package vazkii.ambience.Screens;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.ambience.Ambience;
import vazkii.ambience.World.Biomes.Area.Operation;
import vazkii.ambience.network.AmbiencePackageHandler;
import vazkii.ambience.network.MyMessage;

@OnlyIn(Dist.CLIENT)
public class CreateAreaGUI extends Screen {
	private final int WIDTH = this.width;
	private final int HEIGHT = this.height;

	private TextFieldWidget AreaName;
	private CheckboxButton instaPlayChk;
	private CheckboxButton PlayatNight;

	private static final ResourceLocation texture = new ResourceLocation("ambience:textures/gui/edit_window_back.png");
	
	public CreateAreaGUI() {
		super(new TranslationTextComponent("GUI.CreateArea"));
	}

	@Override
	protected void init() {
		int relX = WIDTH / 2;
		int relY = HEIGHT / 2;

		this.minecraft.keyboardListener.enableRepeatEvents(true);
		addButton(new Button(this.width / 2 - 105, this.height / 4 + 120, 100, 20, I18n.format("GUI.CancelButton"),
				(close) -> {
					this.close();
				}));
		
		addButton(new Button(this.width / 2+5, this.height / 4 + 120, 100, 20, I18n.format("GUI.ConfirmButton"),
				(confirm) -> {
					
					Ambience.selectedArea.setOperation(Operation.CREATE);
					Ambience.selectedArea.setName(AreaName.getText());	
					Ambience.selectedArea.setInstantPlay(instaPlayChk.isChecked());
					Ambience.selectedArea.setPlayAtNight(PlayatNight.isChecked());																      
					//AmbiencePackageHandler.sendToServer(new MyMessage(Ambience.selectedArea.SerializeThis()));
					Ambience.sync = true;
					
					this.close();
				}));
		
		this.AreaName = new TextFieldWidget(font, this.width / 2 - 80, this.height / 4 + 15, 160, 20, "teste");
		AreaName.setText("teste");
		AreaName.setFocused2(true);
		AreaName.setVisible(true);
		
		this.instaPlayChk =new CheckboxButton(this.width / 2 - 80, this.height / 4 + 50, 20, 20,I18n.format("GUI.InstantPlayChk"), false);
		this.PlayatNight =new CheckboxButton(this.width / 2 - 80, this.height / 4 + 80, 20, 20,I18n.format("GUI.PlayAtNight"), false);		
		
	}

	private void close() {
		this.minecraft.displayGuiScreen((Screen) null);
	}

	public void onClose() {
		this.close();
	}
	
	@Override
	public boolean changeFocus(boolean p_changeFocus_1_) {
		if (AreaName.isFocused())
			AreaName.setFocused2(false);
		else
			AreaName.setFocused2(true);
		
		return super.changeFocus(p_changeFocus_1_);
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {

		boolean specialKey=false;
		//user.setFocused2(true);
		if (AreaName.isFocused()) {
			specialKey=AreaName.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
		}

		if(!specialKey)
		{
			if(p_keyPressed_1_!=340 & p_keyPressed_1_!=341)
			{
				if(p_keyPressed_3_==0) 
				{
					char charTyped=(char)p_keyPressed_1_;
					String lowerCaseChar= String.valueOf(charTyped).toLowerCase();
					AreaName.charTyped(lowerCaseChar.charAt(0), p_keyPressed_2_);
				}else {
					AreaName.charTyped((char)p_keyPressed_1_, p_keyPressed_2_);
				}
			}
		}
		
		return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}

	//public void updateScreen() {
		// this.user.moveCursorBy(this.user.getText().length());
	//}

	@Override
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {

		if(instaPlayChk.isHovered())
			instaPlayChk.onClick(p_mouseClicked_1_, p_mouseClicked_3_);

		if(PlayatNight.isHovered())
			PlayatNight.onClick(p_mouseClicked_1_, p_mouseClicked_3_);
		
		boolean clicked=AreaName.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);		
		AreaName.setFocused2(clicked);
				
		return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		  RenderHelper.setupGuiFlatDiffuseLighting();
	      this.renderBackground();
	      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 40, 16777215);
		
		
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int relX = (this.width - WIDTH) / 2;
		int relY = (this.height - HEIGHT) / 2;
		this.blit(relX, relY, 0, 0, WIDTH, HEIGHT);
		this.AreaName.render(mouseX, mouseY, partialTicks);
		
		
		this.instaPlayChk.render(mouseX, mouseY, partialTicks);
		this.PlayatNight.render(mouseX, mouseY, partialTicks);
		
		super.render(mouseX, mouseY, partialTicks);

		//updateScreen();
	}

	public static void open() {
		Minecraft.getInstance().displayGuiScreen(new CreateAreaGUI());
	}
}
