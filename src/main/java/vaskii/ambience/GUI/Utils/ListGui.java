package vaskii.ambience.GUI.Utils;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vaskii.ambience.GUI.SpeakerGUI;

@SideOnly(Side.CLIENT)
public class ListGui extends GuiSlot
{
	  /** A list containing the many different locale language codes. */
    private final java.util.List<String> langCodeList = Lists.<String>newArrayList();
    /** The map containing the Locale-Language pairs. */
    private final Map<String, Language> languageMap = Maps.<String, Language>newHashMap();

    List<String> items;
    
    private LanguageManager languageManager;
    private ResourceLocation texture;
    private FontRenderer fontRenderer;
    
    public ListGui(Minecraft mcIn, List<String> ItemList, int width, int height, int topIn, int bottomIn, int slotHeightIn,LanguageManager languageManager,ResourceLocation texture,FontRenderer fontRenderer)
    {
        //super(mcIn, SpeakerGUI.this.width, SpeakerGUI.this.height, 32, SpeakerGUI.this.height - 65 + 4, 18);
    	//super(mcIn, 212, 300, 32, height - 48, 18);
    	super(mcIn,width, height, topIn, bottomIn, slotHeightIn);
        
    	this.items=ItemList;
    	this.languageManager= languageManager;
    	this.texture=texture;
    	this.fontRenderer=fontRenderer;
    	
        for (Language language : languageManager.getLanguages())
        {
            this.languageMap.put(language.getLanguageCode(), language);
            this.langCodeList.add(language.getLanguageCode());
        }      
    }

    protected int getSize()
    {
    	 return items.size();
    }

    /**
     * The element in the slot that was clicked, boolean for whether it was double clicked or not
     */
    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY)
    {
    	
      /*  Language language = this.SoundsMap.get(this.langCodeList.get(slotIndex));
        languageManager.setCurrentLanguage(language);
        game_settings_3.language = language.getLanguageCode();
        net.minecraftforge.fml.client.FMLClientHandler.instance().refreshResources(net.minecraftforge.client.resource.VanillaResourceType.LANGUAGES);
        fontRenderer.setUnicodeFlag(languageManager.isCurrentLocaleUnicode() || game_settings_3.forceUnicodeFont);
        fontRenderer.setBidiFlag(languageManager.isCurrentLanguageBidirectional());
        confirmSettingsBtn.displayString = I18n.format("gui.done");
        forceUnicodeFontBtn.displayString = game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
        game_settings_3.saveOptions();*/
    }

    /**
     * Returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int slotIndex)
    {
        return ((String)this.langCodeList.get(slotIndex)).equals(languageManager.getCurrentLanguage().getLanguageCode());
    }

    /**
     * Return the height of the content being scrolled
     */
    protected int getContentHeight()
    {
        return this.getSize() * 18;
    }

    @Override
	protected void drawContainerBackground(Tessellator tessellator) {
		// TODO Auto-generated method stub
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(texture);
		int k = (this.width - this.width) / 2;
		int l = (this.height - this.height) / 2;
		super.drawContainerBackground(tessellator);
	}

	protected void drawBackground()
    {
       // drawDefaultBackground();
        
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        this.mc.getTextureManager().bindTexture(texture);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0D, (double)this.height, 0.0D).tex(0.0D, (double)((float)this.height / 32.0F + (float)1)).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos((double)this.width, (double)this.height, 0.0D).tex((double)((float)this.width / 32.0F), (double)((float)this.height / 32.0F + (float)1)).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos((double)this.width, 0.0D, 0.0D).tex((double)((float)this.width / 32.0F), (double)1).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, (double)1).color(64, 64, 64, 255).endVertex();
        tessellator.draw();
    }

    protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks)
    {
        fontRenderer.setBidiFlag(true);
        drawCenteredString(fontRenderer, items.get(slotIndex) /*((Language)this.languageMap.get(this.langCodeList.get(slotIndex))).toString()*/, this.width / 2, yPos + 1, 16777215);
       // fontRenderer.setBidiFlag(languageManager.getCurrentLanguage().isBidirectional());
    }
    
    public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, (float)(x - fontRendererIn.getStringWidth(text) / 2), (float)y, color);
    }
}
