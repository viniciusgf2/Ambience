package vaskii.ambience.render;

import javax.vecmath.Vector4f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import vazkii.ambience.Util.Border;

public class SelectionBoxRenderer {

	public static void drawBoundingBox(Vec3d player_pos, Vec3d posA, Vec3d posB, boolean smooth, float width, float partial_ticks) {

		GlStateManager.pushMatrix();
	    GlStateManager.enableLighting();
	    GlStateManager.disableLighting();
	    GlStateManager.disableCull();
	    GlStateManager.enableBlend();
	    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);	    
	    GlStateManager.depthMask(false);	    
	    
       // GlStateManager.translate(-player_pos.x, -player_pos.y, -player_pos.z);
        Vector4f color=new Vector4f(1,1,1, 85);
	
        GlStateManager.color(color.x, color.y, color.z, color.w);
        
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glPushMatrix();

        Entity viewing_from =  Minecraft.getMinecraft().getRenderViewEntity();

        double x_fix = viewing_from.lastTickPosX + ((viewing_from.posX - viewing_from.lastTickPosX) * partial_ticks);
        double y_fix = viewing_from.lastTickPosY + ((viewing_from.posY - viewing_from.lastTickPosY) * partial_ticks);
        double z_fix = viewing_from.lastTickPosZ + ((viewing_from.posZ - viewing_from.lastTickPosZ) * partial_ticks);
                
        GL11.glTranslatef((float) -x_fix, (float) -y_fix, (float) -z_fix);
        GL11.glPushMatrix();
        
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();		
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

		double margin=-0.01;				

		Vec3d pA=new Vec3d(posA.x-1,posA.y,posA.z);
		Vec3d pB=new Vec3d(posB.x,posB.y,posB.z-1);
		
		if(posA.x >= posB.x & posA.z >= posB.z) {
			pA=new Vec3d(posA.x-1,posA.y,posA.z-1);
			pB=new Vec3d(posB.x,posB.y,posB.z);
		}			

		if(posA.x <= posB.x & posA.z <= posB.z) {
			pA=new Vec3d(posA.x,posA.y,posA.z);
			pB=new Vec3d(posB.x-1,posB.y,posB.z-1);
		}
			
		if(posA.x <= posB.x & posA.z >= posB.z) {
			pA=new Vec3d(posA.x,posA.y,posA.z-1);
			pB=new Vec3d(posB.x-1,posB.y,posB.z);
		}
		
		if(posA.x >= posB.x & posA.z <= posB.z) {
			pA=new Vec3d(posA.x-1,posA.y,posA.z);
			pB=new Vec3d(posB.x,posB.y,posB.z-1);
		}
		
		Border border = new Border(pA, pB);
		 
		//Bottom
				// AB
				bufferBuilder.pos(border.p1.x, border.p1.y + margin, border.p1.z).color(color.x,color.y, color.z, color.w).endVertex(); // A
				bufferBuilder.pos(border.p2.x+1, border.p1.y + margin, border.p1.z).color(color.x,color.y, color.z, color.w).endVertex(); // B
				// BC
				bufferBuilder.pos(border.p2.x+1, border.p1.y + margin, border.p1.z).color(color.x,color.y, color.z, color.w).endVertex(); // B
				bufferBuilder.pos(border.p2.x+1, border.p1.y + margin, border.p2.z+1).color(color.x,color.y, color.z, color.w).endVertex(); // C
				// CD
				bufferBuilder.pos(border.p2.x+1, border.p1.y + margin, border.p2.z+1).color(color.x,color.y, color.z, color.w).endVertex(); // C
				bufferBuilder.pos(border.p1.x, border.p1.y + margin, border.p2.z+1).color(color.x,color.y, color.z, color.w).endVertex(); // D
				// DA
				bufferBuilder.pos(border.p1.x, border.p1.y + margin, border.p2.z+1).color(color.x,color.y, color.z, color.w).endVertex(); // D
				bufferBuilder.pos(border.p1.x, border.p1.y + margin, border.p1.z).color(color.x,color.y, color.z, color.w).endVertex(); // A
		
		//Top
				// EF
				bufferBuilder.pos(border.p1.x, border.p2.y+1 - margin, border.p1.z).color(color.x,color.y, color.z, color.w).endVertex(); // E
				bufferBuilder.pos(border.p2.x+1, border.p2.y+1 - margin, border.p1.z).color(color.x,color.y, color.z, color.w).endVertex(); // F
				// FG
				bufferBuilder.pos(border.p2.x+1, border.p2.y+1 - margin, border.p1.z).color(color.x,color.y, color.z, color.w).endVertex(); // F
				bufferBuilder.pos(border.p2.x+1, border.p2.y+1 - margin, border.p2.z+1).color(color.x,color.y, color.z, color.w).endVertex(); // G
				// GH
				bufferBuilder.pos(border.p2.x+1, border.p2.y+1 - margin, border.p2.z+1).color(color.x,color.y, color.z, color.w).endVertex(); // G
				bufferBuilder.pos(border.p1.x, border.p2.y+1 - margin, border.p2.z+1).color(color.x,color.y, color.z, color.w).endVertex(); // H
				// HE
				bufferBuilder.pos(border.p1.x, border.p2.y+1 - margin, border.p2.z+1).color(color.x,color.y, color.z, color.w).endVertex(); //H
				bufferBuilder.pos(border.p1.x, border.p2.y+1 - margin, border.p1.z).color(color.x,color.y, color.z, color.w).endVertex(); // E
		
		//Oeste
				//AE
				bufferBuilder.pos(border.p1.x, border.p1.y, border.p1.z - margin).color(color.x,color.y, color.z, color.w).endVertex(); // A				
				bufferBuilder.pos(border.p1.x, border.p2.y+1, border.p1.z - margin).color(color.x,color.y, color.z, color.w).endVertex(); // E
				
				//EF
				bufferBuilder.pos(border.p1.x, border.p2.y+1, border.p1.z - margin).color(color.x,color.y, color.z, color.w).endVertex(); // E
				bufferBuilder.pos(border.p2.x+1, border.p2.y+1, border.p1.z - margin).color(color.x,color.y, color.z, color.w).endVertex(); // F		
			
				//FB
				bufferBuilder.pos(border.p2.x+1, border.p2.y+1, border.p1.z - margin).color(color.x,color.y, color.z, color.w).endVertex(); // F
				bufferBuilder.pos(border.p2.x+1, border.p1.y, border.p1.z - margin).color(color.x,color.y, color.z, color.w).endVertex(); // B
			
				//BA				
				bufferBuilder.pos(border.p2.x+1, border.p1.y, border.p1.z - margin).color(color.x,color.y, color.z, color.w).endVertex(); // B
				bufferBuilder.pos(border.p1.x, border.p1.y, border.p1.z - margin).color(color.x,color.y, color.z, color.w).endVertex(); // A
			
	     
	   //Leste
				//CD
				bufferBuilder.pos(border.p2.x+1, border.p1.y, border.p2.z+1 + margin).color(color.x,color.y, color.z, color.w).endVertex(); // C
				bufferBuilder.pos(border.p1.x, border.p1.y, border.p2.z+1 + margin).color(color.x,color.y, color.z, color.w).endVertex(); // D
				//DH
				bufferBuilder.pos(border.p1.x, border.p1.y, border.p2.z+1 + margin).color(color.x,color.y, color.z, color.w).endVertex(); // D
				bufferBuilder.pos(border.p1.x, border.p2.y+1, border.p2.z+1 + margin).color(color.x,color.y, color.z, color.w).endVertex(); //H
				
				//HG
				bufferBuilder.pos(border.p1.x, border.p2.y+1, border.p2.z+1 + margin).color(color.x,color.y, color.z, color.w).endVertex(); //H
				bufferBuilder.pos(border.p2.x+1, border.p2.y+1, border.p2.z+1 + margin).color(color.x,color.y, color.z, color.w).endVertex(); // G
				
				//GC
				bufferBuilder.pos(border.p2.x+1, border.p2.y+1, border.p2.z+1 + margin).color(color.x,color.y, color.z, color.w).endVertex(); // G
				bufferBuilder.pos(border.p2.x+1, border.p1.y, border.p2.z+1 + margin).color(color.x,color.y, color.z, color.w).endVertex(); // C
			
	     
	   //Norte
				//AD				
		     	bufferBuilder.pos(border.p1.x - margin, border.p1.y, border.p1.z).color(color.x,color.y, color.z, color.w).endVertex(); // A 
		     	bufferBuilder.pos(border.p1.x - margin, border.p1.y, border.p2.z+1).color(color.x,color.y, color.z, color.w).endVertex(); // D 
				
				//DH
		     	bufferBuilder.pos(border.p1.x - margin, border.p1.y, border.p2.z+1).color(color.x,color.y, color.z, color.w).endVertex(); // D 
		     	bufferBuilder.pos(border.p1.x - margin, border.p2.y+1, border.p2.z+1).color(color.x,color.y, color.z, color.w).endVertex(); //H 
				
				//HE
		     	bufferBuilder.pos(border.p1.x - margin, border.p2.y+1, border.p2.z+1).color(color.x,color.y, color.z, color.w).endVertex(); //H 
		     	bufferBuilder.pos(border.p1.x - margin, border.p2.y+1, border.p1.z).color(color.x,color.y, color.z, color.w).endVertex(); // E 
				
				//EA
		     	bufferBuilder.pos(border.p1.x - margin, border.p2.y+1, border.p1.z).color(color.x,color.y, color.z, color.w).endVertex(); // E
				bufferBuilder.pos(border.p1.x - margin, border.p1.y, border.p1.z).color(color.x,color.y, color.z, color.w).endVertex(); // A 
			
		//Sul
				//BC
				bufferBuilder.pos(border.p2.x+1 + margin, border.p1.y, border.p1.z).color(color.x,color.y, color.z, color.w).endVertex(); // B
				bufferBuilder.pos(border.p2.x+1 + margin, border.p1.y, border.p2.z+1).color(color.x,color.y, color.z, color.w).endVertex(); // C

				//CG
				bufferBuilder.pos(border.p2.x+1 + margin, border.p1.y, border.p2.z+1).color(color.x,color.y, color.z, color.w).endVertex(); // C
				bufferBuilder.pos(border.p2.x+1 + margin, border.p2.y+1, border.p2.z+1).color(color.x,color.y, color.z, color.w).endVertex(); // G				
						
				//GF
				bufferBuilder.pos(border.p2.x+1 + margin, border.p2.y+1, border.p2.z+1).color(color.x,color.y, color.z, color.w).endVertex(); // G	
				bufferBuilder.pos(border.p2.x+1 + margin, border.p2.y+1, border.p1.z).color(color.x,color.y, color.z, color.w).endVertex(); // F
				
				//FB
				bufferBuilder.pos(border.p2.x+1 + margin, border.p2.y+1, border.p1.z).color(color.x,color.y, color.z, color.w).endVertex(); // F
				bufferBuilder.pos(border.p2.x+1 + margin, border.p1.y, border.p1.z).color(color.x,color.y, color.z, color.w).endVertex(); // B
	     			
		Tessellator.getInstance().draw();
		
		GL11.glPopMatrix();
	    GL11.glPopMatrix();
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glEnable(GL11.GL_LIGHTING);
	    GL11.glEnable(GL11.GL_CULL_FACE);
	    
	    GlStateManager.depthMask(true);
	    GlStateManager.disableBlend();
	    GlStateManager.enableCull();
	    GlStateManager.enableLighting();
	    GlStateManager.popMatrix();
	}
}