package vazkii.ambience.render;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import vazkii.ambience.Ambience;
import vazkii.ambience.Util.Utils;

public class CinematicRender {
	public static ResourceLocation AREA_LOGO = null;//new ResourceLocation(Ambience.MODID,"textures/transitions/fortress.png");
	private ResourceLocation Ocarina_OVERLAYS = new ResourceLocation(Ambience.MODID,"textures/gui/ocarina_overlays.png");
	private float fx_rotateCount = 0;
	public static float fx_zoomCount = 70;
	private float startDelayCount=0;	
	private String songName = "";	
	public static Boolean ativated=false;
	public static int timer=0;
	
	public void renderFX(RenderGameOverlayEvent.Post event, float zoomCount, float zoomAmount, double zoomSpeed, float startDelayTime) {
		
		// Renders the Ocarina's cinematic effect
		Minecraft mc = Minecraft.getInstance();
		if (event.getType() == ElementType.ALL) {
			MainWindow res = event.getWindow();
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
						RenderSystem.enableBlend();
						RenderSystem.pushMatrix();
						RenderSystem.translatef(x, res.getScaledHeight() / 2, 0);
						RenderSystem.scalef(1+ scaleFade,1+ scaleFade, 1);
						RenderSystem.color4f(color.getX(), color.getY(), color.getZ(), Utils.clamp(opacity, 0, 0.98f));

						// rendering
						int imgsize=256;
						float scale = 1.25F * (int) event.getWindow().getGuiScaleFactor() / 2.5f;
						int px = (int) (x / scale) ;
						mc.getTextureManager().bindTexture(AREA_LOGO);
						AbstractGui.blit(event.getMatrixStack(),(int) (px/1.7f - imgsize), (int) (y / scale) - imgsize-45 ,
								0 , 0 , imgsize,imgsize, imgsize , imgsize);

						RenderSystem.color4f(1F, 1F, 1F, 1);
						RenderSystem.popMatrix();

					
					}

					if(fx_zoomCount != 70) 
					{
						
						y = (int) (1 + event.getWindow().getGuiScaleFactor());
						int py = (int) Math.abs(fx_zoomCount - 70);
	
						RenderSystem.pushMatrix();
						color = new Vector4f(1, 1, 1, 1);
						RenderSystem.color4f(color.getX(), color.getY(), color.getZ(), 1);
	
						// Top Overlay
						mc.getTextureManager().bindTexture(Ocarina_OVERLAYS);
						AbstractGui.blit(event.getMatrixStack(),0, 0, 0, 0, width, y + (int) (py * 1.1) - 10, 256, 256);
						//AbstractGui.blit(0, 0, 0, 0, width, 10, 256, 256);
	
						// Bottom Overlay
						y = res.getScaledHeight() + 5 / (int) (1 + event.getWindow().getGuiScaleFactor());
						mc.getTextureManager().bindTexture(Ocarina_OVERLAYS);
						AbstractGui.blit(event.getMatrixStack(),0, y - (int) (py * 1.1) + 10, 0, 0, width, 100, 256, 256);
	
						RenderSystem.color4f(1F, 1F, 1F, 1);
						RenderSystem.popMatrix();
					}
				
			}
		}
	}
}
