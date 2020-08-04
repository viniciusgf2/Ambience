package vazkii.ambience.render;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import vazkii.ambience.Util.Border;


public class SelectionBoxRenderer {

	public static void drawBoundingBox(Vec3d player_pos, Vec3d posA, Vec3d posB, boolean smooth, float width, float partial_ticks,RenderWorldLastEvent event) {

        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        IVertexBuilder builder = buffer.getBuffer(RenderType.LINES);
        MatrixStack matrixStack = event.getMatrixStack();
        
        double x,y,z;  
	    Entity viewing_from =  Minecraft.getInstance().getRenderViewEntity();
        x = viewing_from.lastTickPosX + ((viewing_from.getPosX() - viewing_from.lastTickPosX) * partial_ticks);
        y = viewing_from.lastTickPosY + ((viewing_from.getPosY() - viewing_from.lastTickPosY) * partial_ticks);
        z = viewing_from.lastTickPosZ + ((viewing_from.getPosZ() - viewing_from.lastTickPosZ) * partial_ticks);	      
        
        RenderSystem.lineWidth(6);
        matrixStack.push();
        matrixStack.translate(-x, -y, -z);
        Matrix4f matrix = matrixStack.getLast().getMatrix();
		Vector4f color=new Vector4f(1,1,1,1f);
                       
        drawLines(matrix, builder, posA, posB,color);
       
        matrixStack.pop();
        RenderSystem.disableDepthTest();
        buffer.finish(RenderType.LINES);
			 
        //****************************************************************
        //Drawn Quads
        //***************************************************************
    	RenderSystem.pushMatrix();
    	//RenderSystem.disableLighting();

    	RenderSystem.disableCull();
		RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.depthMask(false);
        
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPushMatrix();
        
	  	Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer2 = tessellator.getBuffer();
        matrixStack = event.getMatrixStack();
        
	    viewing_from =  Minecraft.getInstance().getRenderViewEntity();
        x = viewing_from.lastTickPosX + ((viewing_from.getPosX() - viewing_from.lastTickPosX) * partial_ticks);
        y = viewing_from.lastTickPosY + ((viewing_from.getPosY() - viewing_from.lastTickPosY) * partial_ticks);
        z = viewing_from.lastTickPosZ + ((viewing_from.getPosZ() - viewing_from.lastTickPosZ) * partial_ticks);	      
        
        RenderSystem.lineWidth(6);
        matrixStack.push();
        matrixStack.translate(-x, -y, -z);
        RenderSystem.color4f(1,1,1, 0.2f);
        //RenderSystem.enableAlphaTest();
        
        RenderSystem.lineWidth(6);
        buffer2.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        matrix = matrixStack.getLast().getMatrix();
     
        drawQuads(matrix, buffer2, posA, posB);
        tessellator.draw();
        matrixStack.pop();
        
		GL11.glPopMatrix();
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glEnable(GL11.GL_LIGHTING);
	    GL11.glEnable(GL11.GL_CULL_FACE);
		//RenderSystem.disableBlend();
        //RenderSystem.disableAlphaTest();
        RenderSystem.popMatrix();
		
	}
	
	private static void drawLines(Matrix4f matrix, IVertexBuilder buffer, Vec3d p1, Vec3d p2,Vector4f color) 
	{
    	float margin=-0.01f;	
		Border border = new Border(p1,p2);
        
        float sneak_fix=0;
        if(!Minecraft.getInstance().player.isSneaking()) {    
        	sneak_fix=0.62f;
        }else {
	    	sneak_fix=0.27f;	      
	    }
        
    //Bottom   
    	// AB
        buffer.pos(matrix,border.p1.getX(), border.p1.getY() -1+ margin-sneak_fix, border.p1.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // A
        buffer.pos(matrix,border.p2.getX(), border.p1.getY() -1+ margin-sneak_fix, border.p1.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // B
        // BC
        buffer.pos(matrix,border.p2.getX(), border.p1.getY() -1+ margin-sneak_fix, border.p1.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // B
        buffer.pos(matrix,border.p2.getX(), border.p1.getY() -1+ margin-sneak_fix, border.p2.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // C
		// CD
        buffer.pos(matrix,border.p2.getX(), border.p1.getY() -1+ margin-sneak_fix, border.p2.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // C
        buffer.pos(matrix,border.p1.getX(), border.p1.getY() -1+ margin-sneak_fix, border.p2.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // D
	 	// DA
        buffer.pos(matrix,border.p1.getX(), border.p1.getY() -1+ margin-sneak_fix, border.p2.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // C
     	buffer.pos(matrix,border.p1.getX(), border.p1.getY() -1+ margin-sneak_fix, border.p1.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // D			
	
	//Top	
	   	// AB
     	buffer.pos(matrix,border.p1.getX(), border.p2.getY() + margin-sneak_fix, border.p1.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // A
     	buffer.pos(matrix,border.p2.getX(), border.p2.getY() + margin-sneak_fix, border.p1.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // B
        // BC
     	buffer.pos(matrix,border.p2.getX(), border.p2.getY() + margin-sneak_fix, border.p1.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // B
     	buffer.pos(matrix,border.p2.getX(), border.p2.getY() + margin-sneak_fix, border.p2.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // C
		// CD
  		buffer.pos(matrix,border.p2.getX(), border.p2.getY() + margin-sneak_fix, border.p2.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // C
  		buffer.pos(matrix,border.p1.getX(), border.p2.getY() + margin-sneak_fix, border.p2.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // D
		// DA
  		buffer.pos(matrix,border.p1.getX(), border.p2.getY() + margin-sneak_fix, border.p2.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // C
  		buffer.pos(matrix,border.p1.getX(), border.p2.getY() + margin-sneak_fix, border.p1.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // D			
		
	//Sides	
		//A
  		buffer.pos(matrix,border.p1.getX(), border.p1.getY() -1+ margin-sneak_fix, border.p1.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // A
  		buffer.pos(matrix,border.p1.getX(), border.p2.getY() + margin-sneak_fix, border.p1.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // A
        
   		//B		
        buffer.pos(matrix,border.p2.getX(), border.p1.getY() -1+ margin-sneak_fix, border.p1.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // B
		buffer.pos(matrix,border.p2.getX(), border.p2.getY() + margin-sneak_fix, border.p1.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // B
		
		//C
		buffer.pos(matrix,border.p2.getX(), border.p1.getY() -1+ margin-sneak_fix, border.p2.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // C
	    buffer.pos(matrix,border.p2.getX(), border.p2.getY() + margin-sneak_fix, border.p2.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // C
		
		//D
	    buffer.pos(matrix,border.p1.getX(), border.p1.getY() -1+ margin-sneak_fix, border.p2.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // D
	    buffer.pos(matrix,border.p1.getX(), border.p2.getY() + margin-sneak_fix, border.p2.getZ()).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // D
	}
	
	private static void drawQuads(Matrix4f matrix, BufferBuilder buffer, Vec3d p1, Vec3d p2) {
       
    	float margin=-0.01f;	
		Border border = new Border(p1, p2);
        
        float sneak_fix=0;
        if(!Minecraft.getInstance().player.isSneaking()) {    
        	sneak_fix=0.62f;
        }else {
	    	sneak_fix=0.27f;	      
	    }
		
      //Bottom
		// AB
        buffer.pos(matrix,border.p1.getX(), border.p1.getY() + margin -1 + margin-sneak_fix, border.p1.getZ()).endVertex(); // A
		buffer.pos(matrix,border.p2.getX(), border.p1.getY() + margin -1 + margin-sneak_fix, border.p1.getZ()).endVertex(); // B
		// BC
		buffer.pos(matrix,border.p2.getX(), border.p1.getY() + margin -1 + margin-sneak_fix, border.p1.getZ()).endVertex(); // B
		buffer.pos(matrix,border.p2.getX(), border.p1.getY() + margin -1 + margin-sneak_fix, border.p2.getZ()).endVertex(); // C
		// CD
		buffer.pos(matrix,border.p2.getX(), border.p1.getY() + margin -1 + margin-sneak_fix, border.p2.getZ()).endVertex(); // C
		buffer.pos(matrix,border.p1.getX(), border.p1.getY() + margin -1 + margin-sneak_fix, border.p2.getZ()).endVertex(); // D
		// DA
		buffer.pos(matrix,border.p1.getX(), border.p1.getY() + margin -1 + margin-sneak_fix, border.p2.getZ()).endVertex(); // D
		buffer.pos(matrix,border.p1.getX(), border.p1.getY() + margin -1 + margin-sneak_fix, border.p1.getZ()).endVertex(); // A
        
		//Top
		// EF
		buffer.pos(matrix,border.p1.getX(), border.p2.getY()+1 - margin -1 + margin-sneak_fix, border.p1.getZ()).endVertex(); // E
		buffer.pos(matrix,border.p2.getX(), border.p2.getY()+1 - margin -1 + margin-sneak_fix, border.p1.getZ()).endVertex(); // F
		// FG
		buffer.pos(matrix,border.p2.getX(), border.p2.getY()+1 - margin -1 + margin-sneak_fix, border.p1.getZ()).endVertex(); // F
		buffer.pos(matrix,border.p2.getX(), border.p2.getY()+1 - margin -1 + margin-sneak_fix, border.p2.getZ()).endVertex(); // G
		// GH
		buffer.pos(matrix,border.p2.getX(), border.p2.getY()+1 - margin -1 + margin-sneak_fix, border.p2.getZ()).endVertex(); // G
		buffer.pos(matrix,border.p1.getX(), border.p2.getY()+1 - margin -1 + margin-sneak_fix, border.p2.getZ()).endVertex(); // H
		// HE
		buffer.pos(matrix,border.p1.getX(), border.p2.getY()+1 - margin -1 + margin-sneak_fix, border.p2.getZ()).endVertex(); //H
		buffer.pos(matrix,border.p1.getX(), border.p2.getY()+1 - margin -1 + margin-sneak_fix, border.p1.getZ()).endVertex(); // E
		
		//Oeste
		//AE
		buffer.pos(matrix,border.p1.getX(), border.p1.getY()-1 + margin-sneak_fix, border.p1.getZ() - margin).endVertex(); // A				
		buffer.pos(matrix,border.p1.getX(), border.p2.getY()+1-1 + margin-sneak_fix, border.p1.getZ() - margin).endVertex(); // E
		
		//EF
		buffer.pos(matrix,border.p1.getX(), border.p2.getY()+1-1 + margin-sneak_fix, border.p1.getZ() - margin).endVertex(); // E
		buffer.pos(matrix,border.p2.getX(), border.p2.getY()+1-1 + margin-sneak_fix, border.p1.getZ() - margin).endVertex(); // F		
	
		//FB
		buffer.pos(matrix,border.p2.getX(), border.p2.getY()+1-1 + margin-sneak_fix, border.p1.getZ() - margin).endVertex(); // F
		buffer.pos(matrix,border.p2.getX(), border.p1.getY()-1 + margin-sneak_fix, border.p1.getZ() - margin).endVertex(); // B
	
		//BA				
		buffer.pos(matrix,border.p2.getX(), border.p1.getY()-1 + margin-sneak_fix, border.p1.getZ() - margin).endVertex(); // B
		buffer.pos(matrix,border.p1.getX(), border.p1.getY()-1 + margin-sneak_fix, border.p1.getZ() - margin).endVertex(); // A
		
		//Leste
		//CD
		buffer.pos(matrix,border.p2.getX(), border.p1.getY()-1 + margin-sneak_fix, border.p2.getZ() + margin).endVertex(); // C
		buffer.pos(matrix,border.p1.getX(), border.p1.getY()-1 + margin-sneak_fix, border.p2.getZ() + margin).endVertex(); // D
		//DH
		buffer.pos(matrix,border.p1.getX(), border.p1.getY()-1 + margin-sneak_fix, border.p2.getZ() + margin).endVertex(); // D
		buffer.pos(matrix,border.p1.getX(), border.p2.getY()+1-1 + margin-sneak_fix, border.p2.getZ() + margin).endVertex(); //H
		
		//HG
		buffer.pos(matrix,border.p1.getX(), border.p2.getY()+1-1 + margin-sneak_fix, border.p2.getZ() + margin).endVertex(); //H
		buffer.pos(matrix,border.p2.getX(), border.p2.getY()+1-1 + margin-sneak_fix, border.p2.getZ() + margin).endVertex(); // G
		
		//GC
		buffer.pos(matrix,border.p2.getX(), border.p2.getY()+1-1 + margin-sneak_fix, border.p2.getZ() + margin).endVertex(); // G
		buffer.pos(matrix,border.p2.getX(), border.p1.getY()-1 + margin-sneak_fix, border.p2.getZ() + margin).endVertex(); // C
		
		//Norte
		//AD				
		buffer.pos(matrix,border.p1.getX() - margin, border.p1.getY()-1 + margin-sneak_fix, border.p1.getZ()).endVertex(); // A 
		buffer.pos(matrix,border.p1.getX() - margin, border.p1.getY()-1 + margin-sneak_fix, border.p2.getZ()).endVertex(); // D 
		
		//DH
		buffer.pos(matrix,border.p1.getX() - margin, border.p1.getY()-1 + margin-sneak_fix, border.p2.getZ()).endVertex(); // D 
		buffer.pos(matrix,border.p1.getX() - margin, border.p2.getY()+1-1 + margin-sneak_fix, border.p2.getZ()).endVertex(); //H 
		
		//HE
		buffer.pos(matrix,border.p1.getX() - margin, border.p2.getY()+1-1 + margin-sneak_fix, border.p2.getZ()).endVertex(); //H 
		buffer.pos(matrix,border.p1.getX() - margin, border.p2.getY()+1-1 + margin-sneak_fix, border.p1.getZ()).endVertex(); // E 
		
		//EA
		buffer.pos(matrix,border.p1.getX() - margin, border.p2.getY()+1-1 + margin-sneak_fix, border.p1.getZ()).endVertex(); // E
     	buffer.pos(matrix,border.p1.getX() - margin, border.p1.getY()-1 + margin-sneak_fix, border.p1.getZ()).endVertex(); // A
     	
     	//Sul
		//BC
     	buffer.pos(matrix,border.p2.getX() + margin, border.p1.getY()-1 + margin-sneak_fix, border.p1.getZ()).endVertex(); // B
     	buffer.pos(matrix,border.p2.getX() + margin, border.p1.getY()-1 + margin-sneak_fix, border.p2.getZ()).endVertex(); // C

		//CG
     	buffer.pos(matrix,border.p2.getX() + margin, border.p1.getY()-1 + margin-sneak_fix, border.p2.getZ()).endVertex(); // C
		buffer.pos(matrix,border.p2.getX() + margin, border.p2.getY()+1-1 + margin-sneak_fix, border.p2.getZ()).endVertex(); // G				
				
		//GF
		buffer.pos(matrix,border.p2.getX() + margin, border.p2.getY()+1-1 + margin-sneak_fix, border.p2.getZ()).endVertex(); // G	
		buffer.pos(matrix,border.p2.getX() + margin, border.p2.getY()+1-1 + margin-sneak_fix, border.p1.getZ()).endVertex(); // F
		
		//FB
		buffer.pos(matrix,border.p2.getX() + margin, border.p2.getY()+1-1 + margin-sneak_fix, border.p1.getZ()).endVertex(); // F
		buffer.pos(matrix,border.p2.getX() + margin, border.p1.getY()-1 + margin-sneak_fix, border.p1.getZ()).endVertex(); // B			
    }
}