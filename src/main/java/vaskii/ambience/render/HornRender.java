package vaskii.ambience.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import vaskii.ambience.objects.items.Horn;
import vazkii.ambience.Util.Utils;

public class HornRender {
	static float radius = 1;
	static float count = 0;

	public static void drawBoundingBox(Vec3d pos, float partial_ticks, RenderWorldLastEvent event,World worldIn, EntityPlayer playerIn) {
		
		if (Horn.fadeOutTimer > 0 & Horn.fadeOutTimer<300) {
						
			GL11.glLineWidth(6);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GlStateManager.depthMask(false);
			GlStateManager.enableBlend();
			
			GlStateManager.color(0.3f, 1, 0.3f, 0.2f);
			double x, y, z;
			Entity viewing_from = Minecraft.getMinecraft().getRenderViewEntity();
			x = viewing_from.lastTickPosX + ((viewing_from.getPosition().getX() - viewing_from.lastTickPosX) );
			y = viewing_from.lastTickPosY + ((viewing_from.getPosition().getY() - viewing_from.lastTickPosY) );
			z = viewing_from.lastTickPosZ + ((viewing_from.getPosition().getZ() - viewing_from.lastTickPosZ) );

			// Rotate the matrix to face the player
			GL11.glRotated(90, 1, 0, 0);
			GL11.glRotated(viewing_from.rotationYaw+180, 0, 0, 1);
			GL11.glRotated(-viewing_from.rotationPitch, 1, 0, 0);
			
			GL11.glPushMatrix();
			GL11.glTranslatef((float) -x, (float) -y, (float) -z);
			Vector4f color = new Vector4f(0.4f, 1, 0.4f, 0.05f);
			Vector4f color2 = new Vector4f(1f, 1f, 1f, 1f);

			BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
			bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
			
			drawLines(bufferBuilder, pos, color, color2);

			Tessellator.getInstance().draw();

			GlStateManager.color(1, 1, 1, 1);
			GL11.glPopMatrix();
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GlStateManager.depthMask(true);
			GlStateManager.disableBlend();
		}
	}

	private static void drawLines(BufferBuilder buffer, Vec3d pos, Vector4f color, Vector4f color2) {
		count += 0.03f;
		radius = 2f;
		radius += 0.01f + Math.cos(count) * 3f;

		for (int i = 0; i <= 300; i++) {
			double angle = 2 * Math.PI * i / 300;
			float x2 = ((float) Math.cos(angle + count / 2) * radius);
			float y2 = ((float) Math.sin(angle + count / 2) * radius);
			GL11.glVertex2d(x2, y2);

			buffer.pos( (float) pos.x + x2-0.5f,
					(float)  pos.y -3+ (float) Math.cos(i * Math.PI + (float) Math.cos(count)) / 8,
					(float) pos.z -1.4f+ y2)
					.color(Utils.clamp(color2.getX() + (float) Math.cos(count), 0.85f, 1),
							Utils.clamp(color2.getY() + (float) Math.cos(count), 0.85f, 1),
							Utils.clamp(color2.getZ() + (float) Math.cos(count), 1, 1f),
							Utils.clamp(0.8f - (float) Math.cos(count), 0f, 1f))
					.endVertex(); // A
		}

		count += 0.1f;

		for (int i = 0; i <= 1000; i++) {
			double angle = 2 * Math.PI * i / 1000;
			float x2 = (float) Math.cos(angle + count / 2) * radius;
			float y2 = (float) Math.sin(angle + count / 2) * radius;
			GL11.glVertex2d(x2, y2);

			buffer.pos( (float) pos.x + x2-0.5f,
					(float) pos.y -3+ (float) Math.cos(i * Math.PI + (float) Math.cos(count)) / 8,
					(float) pos.z -1.4f+ y2)
					.color(Utils.clamp(color2.getX() + (float) Math.cos(count), 0.9f, 1f),
							Utils.clamp(color2.getY() + (float) Math.cos(count), 0.95f, 1f),
							Utils.clamp(color2.getZ() + (float) Math.cos(count), 1f, 1f),
							Utils.clamp(0.8f - (float) Math.cos(count), 0f, 1f))
					.endVertex(); // A
		}
	}
}
