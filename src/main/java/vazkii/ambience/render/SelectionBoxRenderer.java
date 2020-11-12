package vazkii.ambience.render;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import vazkii.ambience.Util.Border;


public class SelectionBoxRenderer {

	static float radius=1;
	static float count=0;
	public static void drawBoundingBox(Vector3d player_pos, Vector3d posA, Vector3d posB, boolean smooth, float width, float partial_ticks,RenderWorldLastEvent event) {


        RenderSystem.lineWidth(16);
        GL11.glLineWidth(12);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        RenderSystem.color4f(0.3f,1,0.3f, 0.2f);
        
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        IVertexBuilder builder = buffer.getBuffer(RenderType.LINES);
        MatrixStack matrixStack = event.getMatrixStack();
        
        double x,y,z;  
	    Entity viewing_from =  Minecraft.getInstance().getRenderViewEntity();
        x = viewing_from.lastTickPosX + ((viewing_from.getPosX() - viewing_from.lastTickPosX) * partial_ticks);
        y = viewing_from.lastTickPosY + ((viewing_from.getPosY() - viewing_from.lastTickPosY) * partial_ticks);
        z = viewing_from.lastTickPosZ + ((viewing_from.getPosZ() - viewing_from.lastTickPosZ) * partial_ticks);	      
        
        matrixStack.push();
        matrixStack.translate(-x, -y, -z);
        
        Matrix4f matrix = matrixStack.getLast().getMatrix();
		Vector4f color=new Vector4f(0.4f,1,0.4f,0.05f);
		Vector4f color2=new Vector4f(0.4f,1,0.4f,0.5f);
		
        drawLines(builder, posA, posB,color,color2);
       
   
      
        
        matrixStack.pop();
        RenderSystem.disableDepthTest();
        buffer.finish(RenderType.LINES);

     
    	
    	
    	
    
    	
        RenderSystem.color4f(1,1,1,1);
    	GL11.glDisable(GL11.GL_LINE_SMOOTH);
    	
    	
    	
    	
    	
			 
        //****************************************************************
        //Drawn Quads
        //***************************************************************
    	/*RenderSystem.pushMatrix();
    	//RenderSystem.disableLighting();

    	RenderSystem.disableCull();
		RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
       // RenderSystem.depthMask(false);
        
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        //GL11.glDisable(GL11.GL_LIGHTING);
       // GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPushMatrix();
        
	  	Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer2 = tessellator.getBuffer();
        matrixStack = event.getMatrixStack();
        
	    viewing_from =  Minecraft.getInstance().getRenderViewEntity();
        x = viewing_from.lastTickPosX + ((viewing_from.getPosX() - viewing_from.lastTickPosX) * partial_ticks);
        y = viewing_from.lastTickPosY + ((viewing_from.getPosY() - viewing_from.lastTickPosY) * partial_ticks);
        z = viewing_from.lastTickPosZ + ((viewing_from.getPosZ() - viewing_from.lastTickPosZ) * partial_ticks);	      
        
        matrixStack.push();
        matrixStack.translate(-x, -y, -z);
        RenderSystem.color4f(0.3f,1,0.3f, 0.2f);
        //RenderSystem.enableAlphaTest();
        
        RenderSystem.lineWidth(6);
        buffer2.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        matrix = matrixStack.getLast().getMatrix();
     
        drawQuads( buffer2, posA, posB);
        tessellator.draw();
        matrixStack.pop();
        
		GL11.glPopMatrix();
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	   // GL11.glEnable(GL11.GL_LIGHTING);
	  //  GL11.glEnable(GL11.GL_CULL_FACE);
		RenderSystem.disableBlend();
    	RenderSystem.enableCull();
        //RenderSystem.disableAlphaTest();
        RenderSystem.popMatrix();
		*/
	}
	
	private static void drawLines( IVertexBuilder buffer, Vector3d p1, Vector3d p2,Vector4f color, Vector4f color2) 
	{
    	float margin=-0.01f;	
		Border border = new Border(p1,p2);
        
        float sneak_fix=0;
        if(!Minecraft.getInstance().player.isSneaking()) {    
        	sneak_fix=0.62f;
        }else {
	    	sneak_fix=0.27f;	      
	    }
          
        float opacity=0.01f;
    //Bottom   
        //linhas Z Transparency
        for(double i= border.p1.z; i< border.p2.z;i=i+opacity) {
        	buffer.pos(border.p1.x, border.p1.y -1+ margin-sneak_fix, i).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // A
        	buffer.pos(border.p2.x, border.p1.y -1+ margin-sneak_fix, i).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // B        	
      	}
        
        //linhas Z 
        for(double i= border.p1.z; i< border.p2.z+1;i++) {
        	buffer.pos(border.p1.x, border.p1.y -1+ margin-sneak_fix, i).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // A
        	buffer.pos(border.p2.x, border.p1.y -1+ margin-sneak_fix, i).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // B        	
      	}
        //linhas X
        for(double i= border.p1.x-1; i< border.p2.x;i++) {
        	buffer.pos(i+1, border.p1.y -1+ margin-sneak_fix, border.p1.z).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // B
            buffer.pos(i+1, border.p1.y -1+ margin-sneak_fix, border.p2.z).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // C
        }
                
    //Top
        //linhas Z Transparency
        for(double i= border.p1.z; i< border.p2.z;i=i+opacity) {
        	buffer.pos(border.p1.x, border.p2.y + margin-sneak_fix, i).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // A
         	buffer.pos(border.p2.x, border.p2.y + margin-sneak_fix, i).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // B          	
      	}
        
      //linhas Z
        for(double i= border.p1.z; i< border.p2.z+1;i++) {
        	buffer.pos(border.p1.x, border.p2.y + margin-sneak_fix, i).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // A
         	buffer.pos(border.p2.x, border.p2.y + margin-sneak_fix, i).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // B          	
      	}
        
        //linhas X
        for(double i= border.p1.x; i< border.p2.x+1;i++) {
        	buffer.pos(i, border.p2.y + margin-sneak_fix, border.p1.z).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // B
         	buffer.pos(i, border.p2.y + margin-sneak_fix, border.p2.z).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // C
    	}
        
    //Side Vertical
        //A
       for(double i= border.p1.z; i< border.p2.z+1;i++) {          
    	   buffer.pos(border.p1.x, border.p1.y -1+ margin-sneak_fix, i).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // A
     	   buffer.pos(border.p1.x, border.p2.y + margin-sneak_fix, i).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // A        
        }
         
         //B
        for(double i= border.p1.z; i< border.p2.z+1;i++) {
        	buffer.pos(border.p2.x, border.p1.y -1+ margin-sneak_fix, i).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // B
      		buffer.pos(border.p2.x, border.p2.y + margin-sneak_fix, i).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // B
        }
        
        //C
        for(double i= border.p1.x; i< border.p2.x+1;i++) {
        	buffer.pos(i, border.p1.y -1+ margin-sneak_fix, border.p2.z).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // C
     	    buffer.pos(i, border.p2.y + margin-sneak_fix, border.p2.z).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // C
     	}
        
        //D
        for(double i= border.p1.x; i< border.p2.x+1;i++) {
            buffer.pos(i, border.p1.y -1+ margin-sneak_fix, border.p1.z).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // D
    	    buffer.pos(i, border.p2.y + margin-sneak_fix, border.p1.z).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // D  
    	}
        
      //Walls Grid Transparency
	    for(double i= border.p1.y-1; i< border.p2.y;i=i+opacity) {
        	// AB
            buffer.pos(border.p1.x, i + margin-sneak_fix, border.p1.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // A
            buffer.pos(border.p2.x, i + margin-sneak_fix, border.p1.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // B
            // BC
            buffer.pos(border.p2.x, i + margin-sneak_fix, border.p1.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // B
            buffer.pos(border.p2.x, i + margin-sneak_fix, border.p2.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // C
    		// CD
            buffer.pos(border.p2.x, i + margin-sneak_fix, border.p2.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // C
            buffer.pos(border.p1.x, i + margin-sneak_fix, border.p2.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // D
    	 	// DA
            buffer.pos(border.p1.x, i + margin-sneak_fix, border.p2.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // C
          	buffer.pos(border.p1.x, i + margin-sneak_fix, border.p1.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // D			
    	
        }
        
	    for(double i= border.p1.y; i< border.p2.y;i++) {
        	// AB
            buffer.pos(border.p1.x, i + margin-sneak_fix, border.p1.z).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // A
            buffer.pos(border.p2.x, i + margin-sneak_fix, border.p1.z).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // B
            // BC
            buffer.pos(border.p2.x, i + margin-sneak_fix, border.p1.z).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // B
            buffer.pos(border.p2.x, i + margin-sneak_fix, border.p2.z).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // C
    		// CD
            buffer.pos(border.p2.x, i + margin-sneak_fix, border.p2.z).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // C
            buffer.pos(border.p1.x, i + margin-sneak_fix, border.p2.z).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // D
    	 	// DA
            buffer.pos(border.p1.x, i + margin-sneak_fix, border.p2.z).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // C
          	buffer.pos(border.p1.x, i + margin-sneak_fix, border.p1.z).color(color2.getX(),color2.getY(), color2.getZ(), color2.getW()).endVertex(); // D			
    	
        }

	//MAIN LINES
    //Bottom   
    	// AB
        buffer.pos(border.p1.x, border.p1.y -1+ margin-sneak_fix, border.p1.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // A
        buffer.pos(border.p2.x, border.p1.y -1+ margin-sneak_fix, border.p1.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // B
        // BC
        buffer.pos(border.p2.x, border.p1.y -1+ margin-sneak_fix, border.p1.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // B
        buffer.pos(border.p2.x, border.p1.y -1+ margin-sneak_fix, border.p2.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // C
		// CD
        buffer.pos(border.p2.x, border.p1.y -1+ margin-sneak_fix, border.p2.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // C
        buffer.pos(border.p1.x, border.p1.y -1+ margin-sneak_fix, border.p2.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // D
	 	// DA
        buffer.pos(border.p1.x, border.p1.y -1+ margin-sneak_fix, border.p2.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // C
      	buffer.pos(border.p1.x, border.p1.y -1+ margin-sneak_fix, border.p1.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // D			
	
	//Top	            
	   	// AB
      	buffer.pos(border.p1.x, border.p2.y + margin-sneak_fix, border.p1.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // A
       	buffer.pos(border.p2.x, border.p2.y + margin-sneak_fix, border.p1.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // B
        // BC
      	buffer.pos(border.p2.x, border.p2.y + margin-sneak_fix, border.p1.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // B
      	buffer.pos(border.p2.x, border.p2.y + margin-sneak_fix, border.p2.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // C
		// CD
  	  	buffer.pos(border.p2.x, border.p2.y + margin-sneak_fix, border.p2.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // C
  	  	buffer.pos(border.p1.x, border.p2.y + margin-sneak_fix, border.p2.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // D
		// DA
  	  	buffer.pos(border.p1.x, border.p2.y + margin-sneak_fix, border.p2.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // C
  	  	buffer.pos(border.p1.x, border.p2.y + margin-sneak_fix, border.p1.z).color(color.getX(),color.getY(), color.getZ(), color.getW()).endVertex(); // D			
		
  	  	
  	  	count+=0.1f;
  	  	radius=2f;
  	  	radius+=0.1f+Math.cos(count)*2;
  	  	
  	  for(int i =0; i <= 1000; i++){
          double angle = 2 * Math.PI * i / 1000 ;
          float x2 = ((float) Math.cos(angle+count/2)*radius);
          float y2 = ((float) Math.sin(angle+count/2)*radius);
          GL11.glVertex2d(x2, y2);
                    
          buffer.pos(border.p2.x+x2,
        		  (float) Math.cos(i*Math.PI+(float)Math.cos(count))/8,
        		  border.p2.z+ y2)
          		  .color(
        		  clamp(color2.getX() + (float)Math.cos(count),0.5f,0.5f),
        		  clamp(color2.getY() + (float)Math.cos(count),0.5f,1),
        		  clamp(color2.getZ() + (float)Math.cos(count),0.1f,0.8f), 
        		  clamp(0.8f-(float)Math.cos(count),0f,1f)
        		  ).endVertex(); // A    	  	
      }  	  
  	  
	  	count+=0.1f;
		  	
	  	for(int i =0; i <= 1000; i++){
	        double angle = 2 * Math.PI * i / 1000 ;
	        float x2 = (float) Math.cos(angle+count/2)*radius;
	        float y2 = (float) Math.sin(angle+count/2)*radius;
	        GL11.glVertex2d(x2, y2);
	                  
	        buffer.pos(border.p2.x+x2,(float) Math.cos(i*Math.PI+(float)Math.cos(count))/8, border.p2.z+y2).color(
	      		  clamp(color2.getX() + (float)Math.cos(count),0.5f,0.5f),
	      		  clamp(color2.getY() + (float)Math.cos(count),0.5f,1),
	      		  clamp(color2.getZ() + (float)Math.cos(count),0.1f,0.8f), 
	      		  clamp(0.8f-(float)Math.cos(count),0f,1f)).endVertex(); // A    	  	
	    }
  	  	
	//Sides	
		//A
  		/*buffer.pos(border.p1.x, border.p1.y -1+ margin-sneak_fix, border.p1.z).color(color.x,color.getY(), color.getZ(), color.getW()).endVertex(); // A
  		buffer.pos(border.p1.x, border.p2.y + margin-sneak_fix, border.p1.z).color(color.x,color.getY(), color.getZ(), color.getW()).endVertex(); // A
        
   		//B		
        buffer.pos(border.p2.x, border.p1.y -1+ margin-sneak_fix, border.p1.z).color(color.x,color.getY(), color.getZ(), color.getW()).endVertex(); // B
		buffer.pos(border.p2.x, border.p2.y + margin-sneak_fix, border.p1.z).color(color.x,color.getY(), color.getZ(), color.getW()).endVertex(); // B
		
		//C
		buffer.pos(border.p2.x, border.p1.y -1+ margin-sneak_fix, border.p2.z).color(color.x,color.getY(), color.getZ(), color.getW()).endVertex(); // C
	    buffer.pos(border.p2.x, border.p2.y + margin-sneak_fix, border.p2.z).color(color.x,color.getY(), color.getZ(), color.getW()).endVertex(); // C
		
		//D
	    buffer.pos(border.p1.x, border.p1.y -1+ margin-sneak_fix, border.p2.z).color(color.x,color.getY(), color.getZ(), color.getW()).endVertex(); // D
	    buffer.pos(border.p1.x, border.p2.y + margin-sneak_fix, border.p2.z).color(color.x,color.getY(), color.getZ(), color.getW()).endVertex(); // D  
	*/
  	}
	
	public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

	
	private static void drawQuads(Matrix4f matrix,BufferBuilder buffer, Vector3d p1, Vector3d p2) {
       
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
        buffer.pos(border.p1.x, border.p1.y + margin -1 + margin-sneak_fix, border.p1.z).endVertex(); // A
		buffer.pos(border.p2.x, border.p1.y + margin -1 + margin-sneak_fix, border.p1.z).endVertex(); // B
		// BC
		buffer.pos(border.p2.x, border.p1.y + margin -1 + margin-sneak_fix, border.p1.z).endVertex(); // B
		buffer.pos(border.p2.x, border.p1.y + margin -1 + margin-sneak_fix, border.p2.z).endVertex(); // C
		// CD
		buffer.pos(border.p2.x, border.p1.y + margin -1 + margin-sneak_fix, border.p2.z).endVertex(); // C
		buffer.pos(border.p1.x, border.p1.y + margin -1 + margin-sneak_fix, border.p2.z).endVertex(); // D
		// DA
		buffer.pos(border.p1.x, border.p1.y + margin -1 + margin-sneak_fix, border.p2.z).endVertex(); // D
		buffer.pos(border.p1.x, border.p1.y + margin -1 + margin-sneak_fix, border.p1.z).endVertex(); // A
        
		//Top
		// EF
		buffer.pos(border.p1.x, border.p2.y+1 - margin -1 + margin-sneak_fix, border.p1.z).endVertex(); // E
		buffer.pos(border.p2.x, border.p2.y+1 - margin -1 + margin-sneak_fix, border.p1.z).endVertex(); // F
		// FG
		buffer.pos(border.p2.x, border.p2.y+1 - margin -1 + margin-sneak_fix, border.p1.z).endVertex(); // F
		buffer.pos(border.p2.x, border.p2.y+1 - margin -1 + margin-sneak_fix, border.p2.z).endVertex(); // G
		// GH
		buffer.pos(border.p2.x, border.p2.y+1 - margin -1 + margin-sneak_fix, border.p2.z).endVertex(); // G
		buffer.pos(border.p1.x, border.p2.y+1 - margin -1 + margin-sneak_fix, border.p2.z).endVertex(); // H
		// HE
		buffer.pos(border.p1.x, border.p2.y+1 - margin -1 + margin-sneak_fix, border.p2.z).endVertex(); //H
		buffer.pos(border.p1.x, border.p2.y+1 - margin -1 + margin-sneak_fix, border.p1.z).endVertex(); // E
		
		//Oeste
		//AE
		buffer.pos(border.p1.x, border.p1.y-1 + margin-sneak_fix, border.p1.z - margin).endVertex(); // A				
		buffer.pos(border.p1.x, border.p2.y+1-1 + margin-sneak_fix, border.p1.z - margin).endVertex(); // E
		
		//EF
		buffer.pos(border.p1.x, border.p2.y+1-1 + margin-sneak_fix, border.p1.z - margin).endVertex(); // E
		buffer.pos(border.p2.x, border.p2.y+1-1 + margin-sneak_fix, border.p1.z - margin).endVertex(); // F		
	
		//FB
		buffer.pos(border.p2.x, border.p2.y+1-1 + margin-sneak_fix, border.p1.z - margin).endVertex(); // F
		buffer.pos(border.p2.x, border.p1.y-1 + margin-sneak_fix, border.p1.z - margin).endVertex(); // B
	
		//BA				
		buffer.pos(border.p2.x, border.p1.y-1 + margin-sneak_fix, border.p1.z - margin).endVertex(); // B
		buffer.pos(border.p1.x, border.p1.y-1 + margin-sneak_fix, border.p1.z - margin).endVertex(); // A
		
		//Leste
		//CD
		buffer.pos(border.p2.x, border.p1.y-1 + margin-sneak_fix, border.p2.z + margin).endVertex(); // C
		buffer.pos(border.p1.x, border.p1.y-1 + margin-sneak_fix, border.p2.z + margin).endVertex(); // D
		//DH
		buffer.pos(border.p1.x, border.p1.y-1 + margin-sneak_fix, border.p2.z + margin).endVertex(); // D
		buffer.pos(border.p1.x, border.p2.y+1-1 + margin-sneak_fix, border.p2.z + margin).endVertex(); //H
		
		//HG
		buffer.pos(border.p1.x, border.p2.y+1-1 + margin-sneak_fix, border.p2.z + margin).endVertex(); //H
		buffer.pos(border.p2.x, border.p2.y+1-1 + margin-sneak_fix, border.p2.z + margin).endVertex(); // G
		
		//GC
		buffer.pos(border.p2.x, border.p2.y+1-1 + margin-sneak_fix, border.p2.z + margin).endVertex(); // G
		buffer.pos(border.p2.x, border.p1.y-1 + margin-sneak_fix, border.p2.z + margin).endVertex(); // C
		
		//Norte
		//AD				
		buffer.pos(border.p1.x - margin, border.p1.y-1 + margin-sneak_fix, border.p1.z).endVertex(); // A 
		buffer.pos(border.p1.x - margin, border.p1.y-1 + margin-sneak_fix, border.p2.z).endVertex(); // D 
		
		//DH
		buffer.pos(border.p1.x - margin, border.p1.y-1 + margin-sneak_fix, border.p2.z).endVertex(); // D 
		buffer.pos(border.p1.x - margin, border.p2.y+1-1 + margin-sneak_fix, border.p2.z).endVertex(); //H 
		
		//HE
		buffer.pos(border.p1.x - margin, border.p2.y+1-1 + margin-sneak_fix, border.p2.z).endVertex(); //H 
		buffer.pos(border.p1.x - margin, border.p2.y+1-1 + margin-sneak_fix, border.p1.z).endVertex(); // E 
		
		//EA
		buffer.pos(border.p1.x - margin, border.p2.y+1-1 + margin-sneak_fix, border.p1.z).endVertex(); // E
     	buffer.pos(border.p1.x - margin, border.p1.y-1 + margin-sneak_fix, border.p1.z).endVertex(); // A
     	
     	//Sul
		//BC
     	buffer.pos(border.p2.x + margin, border.p1.y-1 + margin-sneak_fix, border.p1.z).endVertex(); // B
     	buffer.pos(border.p2.x + margin, border.p1.y-1 + margin-sneak_fix, border.p2.z).endVertex(); // C

		//CG
     	buffer.pos(border.p2.x + margin, border.p1.y-1 + margin-sneak_fix, border.p2.z).endVertex(); // C
		buffer.pos(border.p2.x + margin, border.p2.y+1-1 + margin-sneak_fix, border.p2.z).endVertex(); // G				
				
		//GF
		buffer.pos(border.p2.x + margin, border.p2.y+1-1 + margin-sneak_fix, border.p2.z).endVertex(); // G	
		buffer.pos(border.p2.x + margin, border.p2.y+1-1 + margin-sneak_fix, border.p1.z).endVertex(); // F
		
		//FB
		buffer.pos(border.p2.x + margin, border.p2.y+1-1 + margin-sneak_fix, border.p1.z).endVertex(); // F
		buffer.pos(border.p2.x + margin, border.p1.y-1 + margin-sneak_fix, border.p1.z).endVertex(); // B			
    }
}