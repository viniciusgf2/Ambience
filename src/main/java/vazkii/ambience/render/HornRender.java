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
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import vazkii.ambience.Util.Border;
import vazkii.ambience.Util.Utils;
import vazkii.ambience.items.Horn;

public class HornRender {
	static float radius = 1;
	static float count = 0;

	public static void drawBoundingBox(Vec3d pos, float partial_ticks, RenderWorldLastEvent event,World worldIn, PlayerEntity playerIn) {
		
		if (Horn.fadeOutTimer > 0 & Horn.fadeOutTimer<300) {
						
			RenderSystem.lineWidth(16);
			GL11.glLineWidth(12);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			RenderSystem.color4f(0.3f, 1, 0.3f, 0.2f);

			IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
			IVertexBuilder builder = buffer.getBuffer(RenderType.LINES);
			MatrixStack matrixStack = event.getMatrixStack();

			double x, y, z;
			Entity viewing_from = Minecraft.getInstance().getRenderViewEntity();
			x = viewing_from.lastTickPosX + ((viewing_from.getPosX() - viewing_from.lastTickPosX) * partial_ticks);
			y = viewing_from.lastTickPosY + ((viewing_from.getPosY() - viewing_from.lastTickPosY) * partial_ticks);
			z = viewing_from.lastTickPosZ + ((viewing_from.getPosZ() - viewing_from.lastTickPosZ) * partial_ticks);

			// Rotate the matrix to face the player
			matrixStack.rotate(Vector3f.YP.rotationDegrees(-(float) viewing_from.getRotationYawHead()));
			matrixStack.rotate(Vector3f.XP.rotationDegrees((float) viewing_from.getPitch(partial_ticks) - 90));
			matrixStack.rotate(Vector3f.ZP.rotationDegrees(0));

			matrixStack.push();
			matrixStack.translate(-x, -y, -z);
			Matrix4f matrix = matrixStack.getLast().getMatrix();
			Vector4f color = new Vector4f(0.4f, 1, 0.4f, 0.05f);
			Vector4f color2 = new Vector4f(1f, 1f, 1f, 1f);

			drawLines(matrix, builder, pos, color, color2);

			matrixStack.pop();
			RenderSystem.disableDepthTest();
			buffer.finish(RenderType.LINES);

			RenderSystem.color4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
		}
	}

	private static void drawLines(Matrix4f matrix, IVertexBuilder buffer, Vec3d pos, Vector4f color, Vector4f color2) {
		count += 0.01f;
		radius = 1f;
		radius += 0.1f + Math.cos(count) * 2f;

		for (int i = 0; i <= 1000; i++) {
			double angle = 2 * Math.PI * i / 1000;
			float x2 = ((float) Math.cos(angle + count / 2) * radius);
			float y2 = ((float) Math.sin(angle + count / 2) * radius);
			GL11.glVertex2d(x2, y2);

			buffer.pos(matrix, (float) pos.getX() + x2,
					(float) pos.getY() - 1 + (float) Math.cos(i * Math.PI + (float) Math.cos(count)) / 8,
					(float) pos.getZ() + y2)
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

			buffer.pos(matrix, (float) pos.getX() + x2,
					(float) pos.getY() - 1 + (float) Math.cos(i * Math.PI + (float) Math.cos(count)) / 8,
					(float) pos.getZ() + y2)
					.color(Utils.clamp(color2.getX() + (float) Math.cos(count), 0.9f, 1f),
							Utils.clamp(color2.getY() + (float) Math.cos(count), 0.95f, 1f),
							Utils.clamp(color2.getZ() + (float) Math.cos(count), 1f, 1f),
							Utils.clamp(0.8f - (float) Math.cos(count), 0f, 1f))
					.endVertex(); // A
		}
		/*
		 * count += 0.1f; radius = 2f; radius += 0.1f + Math.cos(count) * 2;
		 * 
		 * for (int i = 0; i <= 1000; i++) { double angle = 2 * Math.PI * i / 1000;
		 * float x2 = ((float) Math.cos(angle + count / 2) * radius); float y2 =
		 * ((float) Math.sin(angle + count / 2) * radius); GL11.glVertex2d(x2, y2);
		 * 
		 * buffer.pos(matrix, (float) pos.getX() -0.2f + x2, (float) pos.getY() -1 +
		 * (float) Math.cos(i * Math.PI + (float) Math.cos(count)) / 8 , (float)
		 * pos.getZ() + y2) //+ (float) Math.cos(i * Math.PI + (float) Math.cos(count))
		 * / 8 .color(color2.getX(),color2.getY(),color2.getZ(),clamp(0.8f - (float)
		 * Math.cos(count), 0f, 1f)).endVertex(); // A }
		 */
	}
}
