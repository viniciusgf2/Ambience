package vaskii.ambience.GUI.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ImageButtom extends GuiButton
{
	private ResourceLocation resource;
	private float scaleFactor;
	
	public ImageButtom(int id, float sF, int xPos, int yPos, int width, int height, String str, ResourceLocation pResource)
	{
		super(id, xPos, yPos, width, height, str);
		this.resource = pResource;
		this.scaleFactor = sF;
		this.visible = true;
		this.width = width;
		this.height = height;
	}
	
	public void drawScreen(Minecraft mc)
    {
        if (this.visible)
        { 
        	GlStateManager.pushMatrix();
            mc.renderEngine.bindTexture(this.resource);
            GlStateManager.scale(this.scaleFactor, this.scaleFactor, 1.0);
            this.drawModalRectWithCustomSizedTexture(this.x, this.y, 0, -1, this.width, this.height, 16, 16);
            GlStateManager.popMatrix();
        }
    }
	
	public int getId()
	{
		return this.id;
	}
	
}