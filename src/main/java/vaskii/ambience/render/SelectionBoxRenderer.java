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

	public static void drawBoundingBox(Vec3d player_pos, Vec3d posA, Vec3d posB, boolean smooth, float width) {

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

		//double dx = Math.abs(posA.x - posB.x);
		//double dy = Math.abs(posA.y - posB.y);
		//double dz = Math.abs(posA.z - posB.z);
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
				bufferBuilder.pos(border.p1.x, border.p1.y + margin, border.p1.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A
				bufferBuilder.pos(border.p2.x+1, border.p1.y + margin, border.p1.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
				// BC
				bufferBuilder.pos(border.p2.x+1, border.p1.y + margin, border.p1.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
				bufferBuilder.pos(border.p2.x+1, border.p1.y + margin, border.p2.z+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C
				// CD
				bufferBuilder.pos(border.p2.x+1, border.p1.y + margin, border.p2.z+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C
				bufferBuilder.pos(border.p1.x, border.p1.y + margin, border.p2.z+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D
				// DA
				bufferBuilder.pos(border.p1.x, border.p1.y + margin, border.p2.z+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D
				bufferBuilder.pos(border.p1.x, border.p1.y + margin, border.p1.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A
		
		//Top
				// EF
				bufferBuilder.pos(border.p1.x, border.p2.y+1 - margin, border.p1.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
				bufferBuilder.pos(border.p2.x+1, border.p2.y+1 - margin, border.p1.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F
				// FG
				bufferBuilder.pos(border.p2.x+1, border.p2.y+1 - margin, border.p1.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F
				bufferBuilder.pos(border.p2.x+1, border.p2.y+1 - margin, border.p2.z+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G
				// GH
				bufferBuilder.pos(border.p2.x+1, border.p2.y+1 - margin, border.p2.z+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G
				bufferBuilder.pos(border.p1.x, border.p2.y+1 - margin, border.p2.z+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // H
				// HE
				bufferBuilder.pos(border.p1.x, border.p2.y+1 - margin, border.p2.z+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); //H
				bufferBuilder.pos(border.p1.x, border.p2.y+1 - margin, border.p1.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
		
		//Oeste
				//AE
				bufferBuilder.pos(border.p1.x, border.p1.y, border.p1.z - margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A				
				bufferBuilder.pos(border.p1.x, border.p2.y+1, border.p1.z - margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
				
				//EF
				bufferBuilder.pos(border.p1.x, border.p2.y+1, border.p1.z - margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
				bufferBuilder.pos(border.p2.x+1, border.p2.y+1, border.p1.z - margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F		
			
				//FB
				bufferBuilder.pos(border.p2.x+1, border.p2.y+1, border.p1.z - margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F
				bufferBuilder.pos(border.p2.x+1, border.p1.y, border.p1.z - margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
			
				//BA				
				bufferBuilder.pos(border.p2.x+1, border.p1.y, border.p1.z - margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
				bufferBuilder.pos(border.p1.x, border.p1.y, border.p1.z - margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A
			
	     
	   //Leste
				//CD
				bufferBuilder.pos(border.p2.x+1, border.p1.y, border.p2.z+1 + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C
				bufferBuilder.pos(border.p1.x, border.p1.y, border.p2.z+1 + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D
				//DH
				bufferBuilder.pos(border.p1.x, border.p1.y, border.p2.z+1 + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D
				bufferBuilder.pos(border.p1.x, border.p2.y+1, border.p2.z+1 + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); //H
				
				//HG
				bufferBuilder.pos(border.p1.x, border.p2.y+1, border.p2.z+1 + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); //H
				bufferBuilder.pos(border.p2.x+1, border.p2.y+1, border.p2.z+1 + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G
				
				//GC
				bufferBuilder.pos(border.p2.x+1, border.p2.y+1, border.p2.z+1 + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G
				bufferBuilder.pos(border.p2.x+1, border.p1.y, border.p2.z+1 + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C
			
	     
	   //Norte
				//AD				
		     	bufferBuilder.pos(border.p1.x - margin, border.p1.y, border.p1.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A 
		     	bufferBuilder.pos(border.p1.x - margin, border.p1.y, border.p2.z+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D 
				
				//DH
		     	bufferBuilder.pos(border.p1.x - margin, border.p1.y, border.p2.z+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D 
		     	bufferBuilder.pos(border.p1.x - margin, border.p2.y+1, border.p2.z+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); //H 
				
				//HE
		     	bufferBuilder.pos(border.p1.x - margin, border.p2.y+1, border.p2.z+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); //H 
		     	bufferBuilder.pos(border.p1.x - margin, border.p2.y+1, border.p1.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E 
				
				//EA
		     	bufferBuilder.pos(border.p1.x - margin, border.p2.y+1, border.p1.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
				bufferBuilder.pos(border.p1.x - margin, border.p1.y, border.p1.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A 
			
		//Sul
				//BC
				bufferBuilder.pos(border.p2.x+1 + margin, border.p1.y, border.p1.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
				bufferBuilder.pos(border.p2.x+1 + margin, border.p1.y, border.p2.z+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C

				//CG
				bufferBuilder.pos(border.p2.x+1 + margin, border.p1.y, border.p2.z+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C
				bufferBuilder.pos(border.p2.x+1 + margin, border.p2.y+1, border.p2.z+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G				
						
				//GF
				bufferBuilder.pos(border.p2.x+1 + margin, border.p2.y+1, border.p2.z+1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G	
				bufferBuilder.pos(border.p2.x+1 + margin, border.p2.y+1, border.p1.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F
				
				//FB
				bufferBuilder.pos(border.p2.x+1 + margin, border.p2.y+1, border.p1.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F
				bufferBuilder.pos(border.p2.x+1 + margin, border.p1.y, border.p1.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
	     
		/*
		//Bottom
		// AB
		bufferBuilder.pos(posA.x, posA.y + margin, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A
		bufferBuilder.pos(posA.x, posA.y + margin, posA.z + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
		// BC
		bufferBuilder.pos(posA.x, posA.y + margin, posA.z + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
		bufferBuilder.pos(posA.x + dx, posA.y + margin, posA.z + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C
		// CD
		bufferBuilder.pos(posA.x + dx, posA.y + margin, posA.z + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C
		bufferBuilder.pos(posA.x + dx, posA.y + margin, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D
		// DA
		bufferBuilder.pos(posA.x + dx, posA.y + margin, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D
		bufferBuilder.pos(posA.x, posA.y + margin, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A
		
		//Top
		// EF
		bufferBuilder.pos(posA.x, posA.y + dy - margin, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
		bufferBuilder.pos(posA.x, posA.y + dy - margin, posA.z + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F		
		// FG
		bufferBuilder.pos(posA.x, posA.y + dy - margin, posA.z + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F
		bufferBuilder.pos(posA.x + dx, posA.y + dy - margin, posA.z + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G
		// GH
		bufferBuilder.pos(posA.x + dx, posA.y + dy - margin, posA.z + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G
		bufferBuilder.pos(posA.x + dx, posA.y + dy - margin, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // H
		// HE
		bufferBuilder.pos(posA.x + dx, posA.y + dy - margin, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // H
		bufferBuilder.pos(posA.x, posA.y + dy - margin, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
		
		
		//Leste
		// AE
		bufferBuilder.pos(posA.x- margin, posA.y, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A
		bufferBuilder.pos(posA.x- margin, posA.y + dy, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
		
		// EF
		bufferBuilder.pos(posA.x- margin, posA.y + dy, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
		bufferBuilder.pos(posA.x- margin, posA.y + dy, posA.z + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F		
	
		//FB
		bufferBuilder.pos(posA.x- margin, posA.y + dy, posA.z + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F
		bufferBuilder.pos(posA.x- margin, posA.y, posA.z + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
	
		//BA				
		bufferBuilder.pos(posA.x- margin, posA.y, posA.z + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
		bufferBuilder.pos(posA.x- margin, posA.y, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A
			
		//Norte
		//AD				
		bufferBuilder.pos(posA.x, posA.y, posA.z- margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A
		bufferBuilder.pos(posA.x + dx, posA.y, posA.z- margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D
		
		//DH
		bufferBuilder.pos(posA.x + dx, posA.y, posA.z- margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D
		bufferBuilder.pos(posA.x + dx, posA.y + dy, posA.z- margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // H
		
		//HE
		bufferBuilder.pos(posA.x + dx, posA.y + dy, posA.z- margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // H
		bufferBuilder.pos(posA.x, posA.y + dy, posA.z- margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
		
		//EA
		bufferBuilder.pos(posA.x, posA.y + dy, posA.z- margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // E
		bufferBuilder.pos(posA.x, posA.y, posA.z- margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // A
		
		
		
		//Sul
		//BC
		bufferBuilder.pos(posA.x, posA.y, posA.z + dz + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
		bufferBuilder.pos(posA.x + dx, posA.y, posA.z + dz + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C	

		//CG
		bufferBuilder.pos(posA.x + dx, posA.y, posA.z + dz + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C
		bufferBuilder.pos(posA.x + dx, posA.y + dy, posA.z + dz + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G
				
		//GF
		bufferBuilder.pos(posA.x + dx, posA.y + dy, posA.z + dz + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G
		bufferBuilder.pos(posA.x, posA.y + dy, posA.z + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F
		
		//FB
		bufferBuilder.pos(posA.x, posA.y + dy, posA.z + dz + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // F
		bufferBuilder.pos(posA.x, posA.y, posA.z + dz + margin).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // B
		
		
		//Oeste
		//CD
		bufferBuilder.pos(posA.x + dx + margin, posA.y, posA.z + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C
		bufferBuilder.pos(posA.x + dx + margin, posA.y, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D
		
		//DH
		bufferBuilder.pos(posA.x + dx + margin, posA.y, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // D
		bufferBuilder.pos(posA.x + dx + margin, posA.y + dy, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // H
		
		//HG
		bufferBuilder.pos(posA.x + dx + margin, posA.y + dy, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // H
		bufferBuilder.pos(posA.x + dx + margin, posA.y + dy, posA.z + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G
		
		//GC
		bufferBuilder.pos(posA.x + dx + margin, posA.y + dy, posA.z + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // G
		bufferBuilder.pos(posA.x + dx + margin, posA.y, posA.z + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex(); // C
		*/
		
		
		tessellator.draw();
		
		GL11.glDepthMask(true);
		GL11.glPopAttrib();
	}

}