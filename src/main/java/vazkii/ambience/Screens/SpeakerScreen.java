package vazkii.ambience.Screens;

import java.util.List;

import com.google.common.primitives.Ints;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.ambience.network.AmbiencePackageHandler;
import vazkii.ambience.network.MyMessage;
import vazkii.ambience.render.ScrollListWidget;

@OnlyIn(Dist.CLIENT)
public class SpeakerScreen extends ContainerScreen<SpeakerContainer> {
		
	private SpeakerContainer screenContainer;
	private Button cancelBtn;
	private Button confirmBtn;	
	private TextFieldWidget DelayInput;
	private CheckboxButton instaPlayChk;
	private CheckboxButton LoopCheckbox;	
	private AbstractSlider distanceSlider;
	int DistanceSliderVal;
	
	public static final ResourceLocation textureBackground = new ResourceLocation("ambience:textures/gui/speaker_gui.png");
	public static final ResourceLocation textureBackground2 = new ResourceLocation("ambience:textures/gui/speaker_gui2.png");
	private boolean error;

	private ScrollListWidget list;
			      	  
	
	public SpeakerScreen(SpeakerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		this.guiLeft = 0;
		this.guiTop = 0;
		this.screenContainer=screenContainer;
		this.xSize = 256;
		this.ySize = 200;
						
		this.cancelBtn = new Button(this.width / 2 - 105, this.height / 4 + 120, 100, 20,
				(ITextComponent) new StringTextComponent(I18n.format("GUI.CancelButton")), (close) -> {											
					this.close();
				});

		this.confirmBtn = new Button(this.xSize / 2 + 5, this.ySize / 4 + 120, 100, 20,
				(ITextComponent) new StringTextComponent(I18n.format("GUI.ConfirmButton")), (confirm) -> {
								
						
					
						int delay=0;
						if(DelayInput.getText() == null |DelayInput.getText().isEmpty()) {						
							delay=0;						
						}else {
							delay=Integer.parseInt(DelayInput.getText());
						}
												
						CompoundNBT nbt = new CompoundNBT();
						nbt.putString("SoundEvent", list.getSelected().getText());
						nbt.putInt("x", SpeakerContainer.pos.getX());
						nbt.putInt("y", SpeakerContainer.pos.getY());
						nbt.putInt("z", SpeakerContainer.pos.getZ());						
						nbt.putInt("delay",delay);
						nbt.putBoolean("loop",LoopCheckbox.isChecked());
						nbt.putInt("distance",DistanceSliderVal);
						nbt.putString("dimension",SpeakerContainer.dimension);
						nbt.putBoolean("isAlarm",SpeakerContainer.isAlarm);
						nbt.putBoolean("ClickedSpeakerOrAlarm", true);

						AmbiencePackageHandler.sendToServer(new MyMessage(nbt));
						
						this.close();				
				});
				
		this.buttons.add(cancelBtn);
		this.buttons.add(confirmBtn);		
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX,  int mouseY,  float partialTicks) {
		this.renderBackground(matrixStack);
		//super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
		renderHoveredTooltip(matrixStack,mouseX, mouseY);

		if (this.DelayInput == null) {
			this.DelayInput = new TextFieldWidget(this.font, this.width / 2 - 80, this.height / 4 + 30, 60, 20, (ITextComponent) new StringTextComponent(I18n.format("0")));
			DelayInput.setText(""+SpeakerContainer.delay);			
			DelayInput.setVisible(true);
			
			this.LoopCheckbox = new CheckboxButton(this.width / 2 - 80, this.height / 4 + 80, 20, 20, (ITextComponent) new StringTextComponent(I18n.format("Loop")), SpeakerContainer.loop);
			
		     this.list = new ScrollListWidget(Minecraft.getInstance(),this.width,this.height,screenContainer, font);				
		   //  this.list.setSelected(SpeakerContainer.selectedSound, font);
		    // this.list = new ScrollListWidget(Minecraft.getInstance(),this.width,this.height);
		     // this.children.add(this.list);
		     
		     DistanceSliderVal=(int)SpeakerContainer.distance;
		     distanceSlider=new AbstractSlider(this.width / 2 - 40,this.height / 2 + 1,180,20,(ITextComponent) new StringTextComponent(I18n.format("")),SpeakerContainer.distance/10) {
			
					@Override
					protected void func_230979_b_() {
						DistanceSliderVal= (int)(this.sliderValue*10);
						
					}

					@Override
					protected void func_230972_a_() {
						// TODO Auto-generated method stub
						
					}
				};
			
		}

		confirmBtn.x = this.width / 2 + 5;
		confirmBtn.y = this.height / 2 + 60;
		cancelBtn.x = this.width / 2 - 105;
		cancelBtn.y = this.height / 2 + 60;
		
		distanceSlider.x=this.width / 2 - 65;
		distanceSlider.y=this.height / 2 + 2;
		
		this.LoopCheckbox.x = this.width / 2 +67;
		this.LoopCheckbox.y = this.height / 2 + 30;

		this.DelayInput.x = this.width / 2 - 85;
		this.DelayInput.y = this.height / 2 + 30;

		this.list.render(matrixStack,mouseX, mouseY, partialTicks);
				
		RenderSystem.color4f(1, 1, 1, 1);

		this.minecraft.getTextureManager().bindTexture(textureBackground2);
		int x=(this.width - this.xSize)/2;
		int y=(this.height - this.ySize)/2;

		this.blit(matrixStack,x, y, 0, 0, this.xSize, this.ySize);
		
		this.DelayInput.render(matrixStack, mouseX, mouseY, partialTicks);
		
		this.LoopCheckbox.render(matrixStack,mouseX, mouseY, partialTicks);

		this.drawCenteredString(matrixStack,this.font, I18n.format("GUI.SelectSoundLbl"), this.width / 2-86, this.height / 2 -88,16777215);
		this.drawCenteredString(matrixStack,this.font, "Delay:", this.width / 2-105, this.height / 2 + 35,16777215);
		this.drawCenteredString(matrixStack,this.font,  I18n.format("GUI.Distance"), this.width / 2-97, this.height / 2 + 8,16777215);


		this.distanceSlider.render(matrixStack,mouseX, mouseY, partialTicks);
		this.drawCenteredString(matrixStack,this.font, ""+ DistanceSliderVal, this.width / 2+30 ,this.height / 2 + 7,16777215);
				
		this.cancelBtn.render(matrixStack,mouseX, mouseY, partialTicks);
		this.confirmBtn.render(matrixStack,mouseX, mouseY, partialTicks);		
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1, 1, 1, 1);

		 this.minecraft.getTextureManager().bindTexture(textureBackground);
		 int x=(this.width - this.xSize)/2;
		 int y=(this.height - this.ySize)/2;

		 this.blit(matrixStack,x, y, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack,int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);		
	}

	// ----------------------------

	private void close() {
		 this.minecraft.player.closeScreen();
	      super.closeScreen();
	}

	public void onClose() {
		  super.onClose();
	}

	@Override
	public boolean changeFocus(boolean p_changeFocus_1_) {
		if (DelayInput.isFocused())
			DelayInput.setFocused2(false);
		else
			DelayInput.setFocused2(true);

		return super.changeFocus(p_changeFocus_1_);
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {

		boolean specialKey = false;
		// user.setFocused2(true);
		if (DelayInput.isFocused()) {
			specialKey = DelayInput.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
		}
		
		//Set the ClickedSpeakerOrAlarm variable in the Soundnizer to sync the variable 
		/*if(p_keyPressed_1_==256) {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putBoolean("ClickedSpeakerOrAlarm", false);
			AmbiencePackageHandler.sendToServer(new MyMessage(nbt));
		}*/

		if (!specialKey) {
			if (p_keyPressed_1_ != 340 & p_keyPressed_1_ != 341) {
				
				if(DelayInput.getText().length()<=5) {
				
					// Allows only numbers
					List<Integer> allowedKeys = Ints.asList(
							new int[] { 48,49,50,51,52,53,54,55,56,57 });																													
	
					if (allowedKeys.contains(p_keyPressed_1_))
						DelayInput.charTyped((char) p_keyPressed_1_, p_keyPressed_2_);
						
					int number=0;
					switch(p_keyPressed_1_) {
						case 320: number=48;break;
						case 321: number=49;break;
						case 322: number=50;break;
						case 323: number=51;break;
						case 324: number=52;break;
						case 325: number=53;break;
						case 326: number=54;break;
						case 327: number=55;break;
						case 328: number=56;break;
						case 329: number=57;break;
					}
					DelayInput.charTyped((char)number, p_keyPressed_2_);
				}
			}
		}
		
		if(p_keyPressed_1_== 69)
			return true;

		return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}

	@Override
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
				
		if (LoopCheckbox.isHovered()) {
			//LoopCheckbox.playDownSound(minecraft.getSoundHandler());
			LoopCheckbox.onClick(p_mouseClicked_1_, p_mouseClicked_3_);
		}
			
		if (cancelBtn.isHovered()) {
			cancelBtn.playDownSound(minecraft.getSoundHandler());
			cancelBtn.onClick(p_mouseClicked_1_, p_mouseClicked_3_);
		}

		if (confirmBtn.isHovered()) {
			confirmBtn.playDownSound(minecraft.getSoundHandler());
			confirmBtn.onClick(p_mouseClicked_1_, p_mouseClicked_3_);
		}
			
		boolean clicked = DelayInput.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
		DelayInput.setFocused2(clicked);
				
		if(list.isMouseOver(p_mouseClicked_1_, p_mouseClicked_3_))
			list.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);

		if(distanceSlider.isHovered())
			distanceSlider.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
		
		return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
	}
		
	@Override
	public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
		list.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
		
		return super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
	}

	@Override
	public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_,
			double p_mouseDragged_6_, double p_mouseDragged_8_) {

		if(list.isMouseOver(p_mouseDragged_1_, p_mouseDragged_3_))
			list.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
		
		if(distanceSlider.isHovered())
			distanceSlider.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
		
		return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_,
				p_mouseDragged_8_);
	}

	@Override
	public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
		 
		list.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
		distanceSlider.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
		
		return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
	}
	
	@Override
	public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
		list.setSize(p_resize_2_, p_resize_3_);		
		list.setLeftPos(23);
		
		super.resize(p_resize_1_, p_resize_2_, p_resize_3_);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
