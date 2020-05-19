package vaskii.ambience.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import vazkii.ambience.Util.Border;

public class SelectionBoxRenderer {

	public static void drawBoundingBox(Vec3d player_pos, BlockPos posA, BlockPos posB, boolean smooth, float width) {

		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTranslated(-player_pos.x, -player_pos.y, -player_pos.z);

		Color c = new Color(0, 255, 0, 85);
		GL11.glColor4d(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
		GL11.glLineWidth(width);
		GL11.glDepthMask(false);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

		double dx = Math.abs(posA.getX() - posB.getX());
		double dy = Math.abs(posA.getY() - posB.getY());
		double dz = Math.abs(posA.getZ() - posB.getZ());
		double margin=-0.01;
				
		Border border = new Border(posA, posB);
		 
		//Bottom
				// AB
				bufferBuilder.pos(border.p1.getX(), border.p1.getY() + margin, border.p1.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A
				bufferBuilder.pos(border.p2.getX()+1, border.p1.getY() + margin, border.p1.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
				// BC
				bufferBuilder.pos(border.p2.getX()+1, border.p1.getY() + margin, border.p1.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
				bufferBuilder.pos(border.p2.getX()+1, border.p1.getY() + margin, border.p2.getZ()+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C
				// CD
				bufferBuilder.pos(border.p2.getX()+1, border.p1.getY() + margin, border.p2.getZ()+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C
				bufferBuilder.pos(border.p1.getX(), border.p1.getY() + margin, border.p2.getZ()+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D
				// DA
				bufferBuilder.pos(border.p1.getX(), border.p1.getY() + margin, border.p2.getZ()+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D
				bufferBuilder.pos(border.p1.getX(), border.p1.getY() + margin, border.p1.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A
		
		//Top
				// EF
				bufferBuilder.pos(border.p1.getX(), border.p2.getY()+1 - margin, border.p1.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
				bufferBuilder.pos(border.p2.getX()+1, border.p2.getY()+1 - margin, border.p1.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F
				// FG
				bufferBuilder.pos(border.p2.getX()+1, border.p2.getY()+1 - margin, border.p1.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F
				bufferBuilder.pos(border.p2.getX()+1, border.p2.getY()+1 - margin, border.p2.getZ()+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G
				// GH
				bufferBuilder.pos(border.p2.getX()+1, border.p2.getY()+1 - margin, border.p2.getZ()+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G
				bufferBuilder.pos(border.p1.getX(), border.p2.getY()+1 - margin, border.p2.getZ()+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // H
				// HE
				bufferBuilder.pos(border.p1.getX(), border.p2.getY()+1 - margin, border.p2.getZ()+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); //H
				bufferBuilder.pos(border.p1.getX(), border.p2.getY()+1 - margin, border.p1.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
		
		//Oeste
				//AE
				bufferBuilder.pos(border.p1.getX(), border.p1.getY(), border.p1.getZ() - margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A				
				bufferBuilder.pos(border.p1.getX(), border.p2.getY()+1, border.p1.getZ() - margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
				
				//EF
				bufferBuilder.pos(border.p1.getX(), border.p2.getY()+1, border.p1.getZ() - margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
				bufferBuilder.pos(border.p2.getX()+1, border.p2.getY()+1, border.p1.getZ() - margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F		
			
				//FB
				bufferBuilder.pos(border.p2.getX()+1, border.p2.getY()+1, border.p1.getZ() - margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F
				bufferBuilder.pos(border.p2.getX()+1, border.p1.getY(), border.p1.getZ() - margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
			
				//BA				
				bufferBuilder.pos(border.p2.getX()+1, border.p1.getY(), border.p1.getZ() - margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
				bufferBuilder.pos(border.p1.getX(), border.p1.getY(), border.p1.getZ() - margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A
			
	     
	   //Leste
				//CD
				bufferBuilder.pos(border.p2.getX()+1, border.p1.getY(), border.p2.getZ()+1 + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C
				bufferBuilder.pos(border.p1.getX(), border.p1.getY(), border.p2.getZ()+1 + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D
				//DH
				bufferBuilder.pos(border.p1.getX(), border.p1.getY(), border.p2.getZ()+1 + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D
				bufferBuilder.pos(border.p1.getX(), border.p2.getY()+1, border.p2.getZ()+1 + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); //H
				
				//HG
				bufferBuilder.pos(border.p1.getX(), border.p2.getY()+1, border.p2.getZ()+1 + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); //H
				bufferBuilder.pos(border.p2.getX()+1, border.p2.getY()+1, border.p2.getZ()+1 + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G
				
				//GC
				bufferBuilder.pos(border.p2.getX()+1, border.p2.getY()+1, border.p2.getZ()+1 + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G
				bufferBuilder.pos(border.p2.getX()+1, border.p1.getY(), border.p2.getZ()+1 + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C
			
	     
	   //Norte
				//AD				
		     	bufferBuilder.pos(border.p1.getX() - margin, border.p1.getY(), border.p1.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A 
		     	bufferBuilder.pos(border.p1.getX() - margin, border.p1.getY(), border.p2.getZ()+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D 
				
				//DH
		     	bufferBuilder.pos(border.p1.getX() - margin, border.p1.getY(), border.p2.getZ()+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D 
		     	bufferBuilder.pos(border.p1.getX() - margin, border.p2.getY()+1, border.p2.getZ()+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); //H 
				
				//HE
		     	bufferBuilder.pos(border.p1.getX() - margin, border.p2.getY()+1, border.p2.getZ()+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); //H 
		     	bufferBuilder.pos(border.p1.getX() - margin, border.p2.getY()+1, border.p1.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E 
				
				//EA
		     	bufferBuilder.pos(border.p1.getX() - margin, border.p2.getY()+1, border.p1.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
				bufferBuilder.pos(border.p1.getX() - margin, border.p1.getY(), border.p1.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A 
			
		//Sul
				//BC
				bufferBuilder.pos(border.p2.getX()+1 + margin, border.p1.getY(), border.p1.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
				bufferBuilder.pos(border.p2.getX()+1 + margin, border.p1.getY(), border.p2.getZ()+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C

				//CG
				bufferBuilder.pos(border.p2.getX()+1 + margin, border.p1.getY(), border.p2.getZ()+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C
				bufferBuilder.pos(border.p2.getX()+1 + margin, border.p2.getY()+1, border.p2.getZ()+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G				
						
				//GF
				bufferBuilder.pos(border.p2.getX()+1 + margin, border.p2.getY()+1, border.p2.getZ()+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G	
				bufferBuilder.pos(border.p2.getX()+1 + margin, border.p2.getY()+1, border.p1.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F
				
				//FB
				bufferBuilder.pos(border.p2.getX()+1 + margin, border.p2.getY()+1, border.p1.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F
				bufferBuilder.pos(border.p2.getX()+1 + margin, border.p1.getY(), border.p1.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
	     
		/*
		//Bottom
		// AB
		bufferBuilder.pos(posA.getX(), posA.getY() + margin, posA.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A
		bufferBuilder.pos(posA.getX(), posA.getY() + margin, posA.getZ() + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
		// BC
		bufferBuilder.pos(posA.getX(), posA.getY() + margin, posA.getZ() + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
		bufferBuilder.pos(posA.getX() + dx, posA.getY() + margin, posA.getZ() + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C
		// CD
		bufferBuilder.pos(posA.getX() + dx, posA.getY() + margin, posA.getZ() + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C
		bufferBuilder.pos(posA.getX() + dx, posA.getY() + margin, posA.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D
		// DA
		bufferBuilder.pos(posA.getX() + dx, posA.getY() + margin, posA.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D
		bufferBuilder.pos(posA.getX(), posA.getY() + margin, posA.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A
		
		//Top
		// EF
		bufferBuilder.pos(posA.getX(), posA.getY() + dy - margin, posA.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
		bufferBuilder.pos(posA.getX(), posA.getY() + dy - margin, posA.getZ() + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F		
		// FG
		bufferBuilder.pos(posA.getX(), posA.getY() + dy - margin, posA.getZ() + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F
		bufferBuilder.pos(posA.getX() + dx, posA.getY() + dy - margin, posA.getZ() + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G
		// GH
		bufferBuilder.pos(posA.getX() + dx, posA.getY() + dy - margin, posA.getZ() + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G
		bufferBuilder.pos(posA.getX() + dx, posA.getY() + dy - margin, posA.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // H
		// HE
		bufferBuilder.pos(posA.getX() + dx, posA.getY() + dy - margin, posA.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // H
		bufferBuilder.pos(posA.getX(), posA.getY() + dy - margin, posA.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
		
		
		//Leste
		// AE
		bufferBuilder.pos(posA.getX()- margin, posA.getY(), posA.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A
		bufferBuilder.pos(posA.getX()- margin, posA.getY() + dy, posA.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
		
		// EF
		bufferBuilder.pos(posA.getX()- margin, posA.getY() + dy, posA.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
		bufferBuilder.pos(posA.getX()- margin, posA.getY() + dy, posA.getZ() + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F		
	
		//FB
		bufferBuilder.pos(posA.getX()- margin, posA.getY() + dy, posA.getZ() + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F
		bufferBuilder.pos(posA.getX()- margin, posA.getY(), posA.getZ() + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
	
		//BA				
		bufferBuilder.pos(posA.getX()- margin, posA.getY(), posA.getZ() + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
		bufferBuilder.pos(posA.getX()- margin, posA.getY(), posA.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A
			
		//Norte
		//AD				
		bufferBuilder.pos(posA.getX(), posA.getY(), posA.getZ()- margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A
		bufferBuilder.pos(posA.getX() + dx, posA.getY(), posA.getZ()- margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D
		
		//DH
		bufferBuilder.pos(posA.getX() + dx, posA.getY(), posA.getZ()- margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D
		bufferBuilder.pos(posA.getX() + dx, posA.getY() + dy, posA.getZ()- margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // H
		
		//HE
		bufferBuilder.pos(posA.getX() + dx, posA.getY() + dy, posA.getZ()- margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // H
		bufferBuilder.pos(posA.getX(), posA.getY() + dy, posA.getZ()- margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
		
		//EA
		bufferBuilder.pos(posA.getX(), posA.getY() + dy, posA.getZ()- margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
		bufferBuilder.pos(posA.getX(), posA.getY(), posA.getZ()- margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A
		
		
		
		//Sul
		//BC
		bufferBuilder.pos(posA.getX(), posA.getY(), posA.getZ() + dz + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
		bufferBuilder.pos(posA.getX() + dx, posA.getY(), posA.getZ() + dz + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C	

		//CG
		bufferBuilder.pos(posA.getX() + dx, posA.getY(), posA.getZ() + dz + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C
		bufferBuilder.pos(posA.getX() + dx, posA.getY() + dy, posA.getZ() + dz + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G
				
		//GF
		bufferBuilder.pos(posA.getX() + dx, posA.getY() + dy, posA.getZ() + dz + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G
		bufferBuilder.pos(posA.getX(), posA.getY() + dy, posA.getZ() + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F
		
		//FB
		bufferBuilder.pos(posA.getX(), posA.getY() + dy, posA.getZ() + dz + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F
		bufferBuilder.pos(posA.getX(), posA.getY(), posA.getZ() + dz + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
		
		
		//Oeste
		//CD
		bufferBuilder.pos(posA.getX() + dx + margin, posA.getY(), posA.getZ() + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C
		bufferBuilder.pos(posA.getX() + dx + margin, posA.getY(), posA.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D
		
		//DH
		bufferBuilder.pos(posA.getX() + dx + margin, posA.getY(), posA.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D
		bufferBuilder.pos(posA.getX() + dx + margin, posA.getY() + dy, posA.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // H
		
		//HG
		bufferBuilder.pos(posA.getX() + dx + margin, posA.getY() + dy, posA.getZ()).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // H
		bufferBuilder.pos(posA.getX() + dx + margin, posA.getY() + dy, posA.getZ() + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G
		
		//GC
		bufferBuilder.pos(posA.getX() + dx + margin, posA.getY() + dy, posA.getZ() + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G
		bufferBuilder.pos(posA.getX() + dx + margin, posA.getY(), posA.getZ() + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C
		*/
		
		
		tessellator.draw();
		
		GL11.glDepthMask(true);
		GL11.glPopAttrib();
	}

}