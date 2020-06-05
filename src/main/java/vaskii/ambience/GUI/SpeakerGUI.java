package vaskii.ambience.GUI;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.ambience.Util.Handlers.SoundHandler;

@SideOnly(Side.CLIENT)
public class SpeakerGUI extends GuiScreen
{
	public static int GUIID = 4;	
	public static HashMap guiinventory = new HashMap();
    /** The parent Gui screen */
    protected GuiScreen parentScreen;
    /** The List GuiSlot object reference. */
    private SpeakerGUI.ListGui list;
    /** Reference to the GameSettings object. */
    private final GameSettings game_settings_3;
    /** Reference to the LanguageManager object. */
    private final LanguageManager languageManager;
    /** A button which allows the user to determine if the Unicode font should be forced. */
    private GuiOptionButton forceUnicodeFontBtn;
    /** The button to confirm the current settings. */
    private GuiOptionButton confirmSettingsBtn;

    public SpeakerGUI(GuiScreen screen, GameSettings gameSettingsObj, LanguageManager manager)
    {
        this.parentScreen = screen;
        this.game_settings_3 = gameSettingsObj;
        this.languageManager = manager;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.forceUnicodeFontBtn = (GuiOptionButton)this.addButton(new GuiOptionButton(100, this.width / 2 - 155, this.height - 38, GameSettings.Options.FORCE_UNICODE_FONT, this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT)));
        this.confirmSettingsBtn = (GuiOptionButton)this.addButton(new GuiOptionButton(6, this.width / 2 - 155 + 160, this.height - 38, I18n.format("gui.done")));
        this.list = new SpeakerGUI.ListGui(this.mc);
        this.list.registerScrollButtons(7, 8);
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.list.handleMouseInput();
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            switch (button.id)
            {
                case 5:
                    break;
                case 6:
                    this.mc.displayGuiScreen(this.parentScreen);
                    break;
                case 100:

                    if (button instanceof GuiOptionButton)
                    {
                        this.game_settings_3.setOptionValue(((GuiOptionButton)button).getOption(), 1);
                        button.displayString = this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
                        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
                        int i = scaledresolution.getScaledWidth();
                        int j = scaledresolution.getScaledHeight();
                        this.setWorldAndResolution(this.mc, i, j);
                    }

                    break;
                default:
                    this.list.actionPerformed(button);
            }
        }
    }

	int count=0;
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
    	count++;
    	if(count>25)
    		this.list.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, I18n.format("options.language"), this.width / 2, 16, 16777215);
        this.drawCenteredString(this.fontRenderer, "(" + I18n.format("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

	private static final ResourceLocation texture = new ResourceLocation("ambience:textures/gui/edit_window_back.png");
	
    @SideOnly(Side.CLIENT)
    class ListGui extends GuiSlot
    {
    	  /** A list containing the many different locale language codes. */
        private final java.util.List<String> langCodeList = Lists.<String>newArrayList();
        /** The map containing the Locale-Language pairs. */
        private final Map<String, Language> languageMap = Maps.<String, Language>newHashMap();

        List<String> sounds = SoundHandler.SOUNDS;
        
        public ListGui(Minecraft mcIn)
        {
            //super(mcIn, SpeakerGUI.this.width, SpeakerGUI.this.height, 32, SpeakerGUI.this.height - 65 + 4, 18);
        	super(mcIn, 212, 300, 32,  SpeakerGUI.this.height - 48, 18);
            
            for (Language language : languageManager.getLanguages())
            {
                this.languageMap.put(language.getLanguageCode(), language);
                this.langCodeList.add(language.getLanguageCode());
            }      
        }

        protected int getSize()
        {
        	 return SoundHandler.SOUNDS.size();
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
            drawCenteredString(fontRenderer, sounds.get(slotIndex) /*((Language)this.languageMap.get(this.langCodeList.get(slotIndex))).toString()*/, this.width / 2, yPos + 1, 16777215);
           // fontRenderer.setBidiFlag(languageManager.getCurrentLanguage().isBidirectional());
        }
    }
}