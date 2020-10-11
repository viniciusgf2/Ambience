package vaskii.ambience.render;

import javax.vecmath.Vector4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import vazkii.ambience.Ambience;
import vazkii.ambience.Reference;
import vazkii.ambience.Util.Utils;

public class CinematicRender {
	public static ResourceLocation AREA_LOGO = null;//new ResourceLocation(Ambience.MODID,"textures/transitions/fortress.png");
	private ResourceLocation Ocarina_OVERLAYS = new ResourceLocation(Reference.MOD_ID,"textures/gui/ocarina_overlays.png");
	private float fx_rotateCount = 0;
	public static float fx_zoomCount = 70;
	private float startDelayCount=0;	
	private String songName = "";	
	public static Boolean ativated=false;
	public static int timer=0;

	public void renderFX(RenderGameOverlayEvent.Post event, float zoomCount, float zoomAmount, double zoomSpeed, float startDelayTime) {

		// Renders the Ocarina's cinematic effect
		Minecraft mc = Minecraft.getMinecraft();
		if (event.getType() == ElementType.ALL) {			
			ScaledResolution res = event.getResolution();
			if (mc.player != null) {

					int width = 2048;
					int x = res.getScaledWidth() / 2;
					int y = res.getScaledHeight() / 2;

					Vector4f color = new Vector4f(1, 1, 1, 1);

					if(timer>150){
						ativated=false;
						timer=0;
					}

					// *******************************************************
					// FX ------------------------
					if (ativated) {

						timer++;
						startDelayCount++;
						if(startDelayCount>startDelayTime) {
							if (fx_zoomCount > zoomAmount) {
								fx_zoomCount -= zoomSpeed;
								if (fx_zoomCount < zoomAmount) {
									fx_zoomCount = zoomAmount;
								}
							}
						}
					} else {

						if (fx_zoomCount < 70) {
							fx_zoomCount += zoomSpeed;
							if (fx_zoomCount > 70) {
								fx_zoomCount = 70;
							}
						}
						startDelayCount=0;
					}

					if (fx_zoomCount != 70) {

						float opacity = (int) (17 - (fx_zoomCount / 8));
						opacity = (opacity * 1.15f) / 15;

						float scaleFade = (40 + (fx_zoomCount - 70)) / 20;

						// FX2
						GlStateManager.enableBlend();
						GlStateManager.pushMatrix();
						GlStateManager.translate(x, res.getScaledHeight() / 2, 0);
						GlStateManager.scale(1+ scaleFade,1+ scaleFade, 1);
						GlStateManager.color(color.getX(), color.getY(), color.getZ(), Utils.clamp(opacity, 0, 0.98f));

						// rendering
						int imgsize=256;
						float scale = 1.25F * (int)event.getResolution().getScaleFactor() / 2.5f;
						int px = (int) (x / scale) ;
						mc.getTextureManager().bindTexture(AREA_LOGO);
						GuiScreen.drawModalRectWithCustomSizedTexture((int) (px/1.7f - imgsize), (int) (y / scale) - imgsize-45 ,
								0 , 0 , imgsize,imgsize, imgsize , imgsize);

						GlStateManager.color(1F, 1F, 1F, 1);
						GlStateManager.popMatrix();


					}

					if(fx_zoomCount != 70) 
					{

						y = (int) (1 + event.getResolution().getScaleFactor());
						int py = (int) Math.abs(fx_zoomCount - 70);

						GlStateManager.pushMatrix();
						color = new Vector4f(1, 1, 1, 1);
						GlStateManager.color(color.getX(), color.getY(), color.getZ(), 1);

						// Top Overlay
						mc.getTextureManager().bindTexture(Ocarina_OVERLAYS);
						GuiScreen.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width, y + (int) (py * 1.1) - 10, 256, 256);
						//AbstractGui.blit(0, 0, 0, 0, width, 10, 256, 256);

						// Bottom Overlay
						y = res.getScaledHeight() + 5 / (int) (1 + event.getResolution().getScaleFactor());
						mc.getTextureManager().bindTexture(Ocarina_OVERLAYS);
						GuiScreen.drawModalRectWithCustomSizedTexture(0, y - (int) (py * 1.1) + 10, 0, 0, width, 100, 256, 256);

						GlStateManager.color(1F, 1F, 1F, 1);
						GlStateManager.popMatrix();
					}

			}
		}
	}
}
