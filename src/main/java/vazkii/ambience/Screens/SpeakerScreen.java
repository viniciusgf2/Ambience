package vazkii.ambience.Screens;

import java.util.List;

import javax.annotation.Nullable;
import javax.swing.Scrollable;

import com.google.common.primitives.Ints;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.LanguageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.OptionSlider;
import net.minecraft.client.gui.widget.SoundSlider;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.impl.SetWorldSpawnCommand;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.ambience.Ambience;
import vazkii.ambience.Util.Handlers.SoundHandler;
import vazkii.ambience.World.Biomes.Area;
import vazkii.ambience.World.Biomes.Area.Operation;
import vazkii.ambience.network.AmbiencePackageHandler;
import vazkii.ambience.network.MyMessage;
import vazkii.ambience.render.ScrollListWidget;
import vazkii.ambience.render.ScrollListWidget.SoundEntry;

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
		
	/*   @OnlyIn(Dist.CLIENT)
	   class ScrollListWidget extends ExtendedList<SpeakerScreen.ScrollListWidget.SoundEntry> {
	      public ScrollListWidget(Minecraft mcIn,int width,int height) {
	         super(mcIn,width-52, 120, height / 2 - 71, height / 2-4, 15);
	         //		      w            y   h


	         setLeftPos(23);
	        // this.width=width;
	        // this.height=height;
	         
	         for(String sound: SoundHandler.SOUNDS) {
	        	 SpeakerScreen.ScrollListWidget.SoundEntry soundEntry=new SoundEntry(sound);
	        	 this.addEntry(soundEntry);
	        	 
	        	 if(sound==screenContainer.selectedSound) {
	        		 this.setSelected(soundEntry);
	        	 }
	         }

	         if (this.getSelected() != null) {
	            this.centerScrollOn(this.getSelected());
	         }
	      }
	      
	      public void setSize(int width,int height) {
	    	//  this.width=width;
	    	//  this.height=height;
	    	  
	    	  updateSize(width-50, 120, height / 2 - 71, height / 2-4);
	      }

	      protected int getScrollbarPosition() {
	         return super.getScrollbarPosition() + 17;
	      }

	      public int getRowWidth() {
	         return super.getRowWidth() +15;
	      }

	      public void setSelected(@Nullable SpeakerScreen.ScrollListWidget.SoundEntry p_setSelected_1_) {
	         super.setSelected(p_setSelected_1_);
	       
	         //????

	      }
	      
	      
	      

	      @Override
	    protected void renderList(int p_renderList_1_, int p_renderList_2_, int p_renderList_3_, int p_renderList_4_,
	    		float p_renderList_5_) {
	    	// TODO Auto-generated method stub
	    	  
	    	super.renderList(p_renderList_1_, p_renderList_2_, p_renderList_3_, p_renderList_4_, p_renderList_5_);
	    }
	      
	      @Override
	    protected void renderHoleBackground(int p_renderHoleBackground_1_, int p_renderHoleBackground_2_,
	    		int p_renderHoleBackground_3_, int p_renderHoleBackground_4_) {
	    		  	    	  
	    	//super.renderHoleBackground(p_renderHoleBackground_1_, p_renderHoleBackground_2_, p_renderHoleBackground_3_,
	    			//p_renderHoleBackground_4_);
	    	  
	    	  Tessellator tessellator = Tessellator.getInstance();
	          BufferBuilder bufferbuilder = tessellator.getBuffer();
	          this.minecraft.getTextureManager().bindTexture(SpeakerScreen.textureBackground);
	          RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	          float f = 32.0F;
	          bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
	          bufferbuilder.pos((double)this.x0+100, (double)p_renderHoleBackground_2_, 0.0D).tex(0.0F, (float)p_renderHoleBackground_2_ / 153.0F).color(255, 255, 255, p_renderHoleBackground_4_).endVertex();
	          bufferbuilder.pos((double)(this.x0 + this.width)-126, (double)p_renderHoleBackground_2_, 0.0D).tex((float)this.width / 600.0F, (float)p_renderHoleBackground_2_ / 153.0F).color(255, 255, 255, p_renderHoleBackground_4_).endVertex();
	          bufferbuilder.pos((double)(this.x0 + this.width)-126, (double)p_renderHoleBackground_1_, 0.0D).tex((float)this.width / 600.0F, (float)p_renderHoleBackground_1_ / 153.0F).color(255, 255, 255, p_renderHoleBackground_3_).endVertex();
	          bufferbuilder.pos((double)this.x0+100, (double)p_renderHoleBackground_1_, 0.0D).tex(0.0F, (float)p_renderHoleBackground_1_ / 153.0F).color(255, 255, 255, p_renderHoleBackground_3_).endVertex();
	          tessellator.draw();
	    }
	      
	      @Override
	    protected void renderDecorations(int p_renderDecorations_1_, int p_renderDecorations_2_) {
	    	// TODO Auto-generated method stub
	    	super.renderDecorations(p_renderDecorations_1_, p_renderDecorations_2_);
	    }
	      
	  //  @Override
	  //  public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
	  //  	// TODO Auto-generated method stub
	  //  	super.render(p_render_1_, p_render_2_, p_render_3_);
//
//	        this.minecraft.getTextureManager().bindTexture(SpeakerScreen.textureBackground);
	//    }
	    
	    private int getMaxScroll() {
	        return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
	     }
	    
	    
	      
	      public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
	          this.renderBackground();
	          int i = this.getScrollbarPosition();
	          int j = i + 6;
	          Tessellator tessellator = Tessellator.getInstance();
	          BufferBuilder bufferbuilder = tessellator.getBuffer();
	          this.minecraft.getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);
	          //this.minecraft.getTextureManager().bindTexture(SpeakerScreen.textureBackground);
	          RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	         /* float f = 32.0F;
	          bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
	          bufferbuilder.pos((double)this.x0, (double)this.y1, 0.0D).tex((float)this.x0 / 32.0F, (float)(this.y1 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
	          bufferbuilder.pos((double)this.x1, (double)this.y1, 0.0D).tex((float)this.x1 / 32.0F, (float)(this.y1 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
	          bufferbuilder.pos((double)this.x1, (double)this.y0, 0.0D).tex((float)this.x1 / 32.0F, (float)(this.y0 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
	          bufferbuilder.pos((double)this.x0, (double)this.y0, 0.0D).tex((float)this.x0 / 32.0F, (float)(this.y0 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
	          tessellator.draw();
	          int k = 5;
	          int l = this.y0 + 4 - (int)this.getScrollAmount();
	          if (this.renderHeader) {
	             this.renderHeader(k, l, tessellator);
	          }

	          this.renderList(k, l, p_render_1_, p_render_2_, p_render_3_);
	          RenderSystem.disableDepthTest();
	          //this.renderHoleBackground(0, this.y0, 255, 255);
	          //this.renderHoleBackground(this.y1+90, this.y1-150, 255, 255);
	          RenderSystem.enableBlend();
	          RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
	          RenderSystem.disableAlphaTest();
	          RenderSystem.shadeModel(7425);
	          RenderSystem.disableTexture();
	          int i1 = 4;
	         /* bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
	          bufferbuilder.pos((double)this.x0, (double)(this.y0 + 4), 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 0).endVertex();
	          bufferbuilder.pos((double)this.x1, (double)(this.y0 + 4), 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 0).endVertex();
	          bufferbuilder.pos((double)this.x1, (double)this.y0, 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
	          bufferbuilder.pos((double)this.x0, (double)this.y0, 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
	          tessellator.draw();
	          bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
	          bufferbuilder.pos((double)this.x0, (double)this.y1, 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
	          bufferbuilder.pos((double)this.x1, (double)this.y1, 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
	          bufferbuilder.pos((double)this.x1, (double)(this.y1 - 4), 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 0).endVertex();
	          bufferbuilder.pos((double)this.x0, (double)(this.y1 - 4), 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 0).endVertex();
	          tessellator.draw();
	          int j1 = this.getMaxScroll();
	          if (j1 > 0) {
	             int k1 = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
	             k1 = MathHelper.clamp(k1, 32, this.y1 - this.y0 - 8);
	             int l1 = (int)this.getScrollAmount() * (this.y1 - this.y0 - k1) / j1 + this.y0;
	             if (l1 < this.y0) {
	                l1 = this.y0;
	             }

	             bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
	             bufferbuilder.pos((double)i, (double)this.y1, 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
	             bufferbuilder.pos((double)j, (double)this.y1, 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
	             bufferbuilder.pos((double)j, (double)this.y0, 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
	             bufferbuilder.pos((double)i, (double)this.y0, 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
	             tessellator.draw();
	             bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
	             bufferbuilder.pos((double)i, (double)(l1 + k1), 0.0D).tex(0.0F, 1.0F).color(128, 128, 128, 255).endVertex();
	             bufferbuilder.pos((double)j, (double)(l1 + k1), 0.0D).tex(1.0F, 1.0F).color(128, 128, 128, 255).endVertex();
	             bufferbuilder.pos((double)j, (double)l1, 0.0D).tex(1.0F, 0.0F).color(128, 128, 128, 255).endVertex();
	             bufferbuilder.pos((double)i, (double)l1, 0.0D).tex(0.0F, 0.0F).color(128, 128, 128, 255).endVertex();
	             tessellator.draw();
	             bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
	             bufferbuilder.pos((double)i, (double)(l1 + k1 - 1), 0.0D).tex(0.0F, 1.0F).color(192, 192, 192, 255).endVertex();
	             bufferbuilder.pos((double)(j - 1), (double)(l1 + k1 - 1), 0.0D).tex(1.0F, 1.0F).color(192, 192, 192, 255).endVertex();
	             bufferbuilder.pos((double)(j - 1), (double)l1, 0.0D).tex(1.0F, 0.0F).color(192, 192, 192, 255).endVertex();
	             bufferbuilder.pos((double)i, (double)l1, 0.0D).tex(0.0F, 0.0F).color(192, 192, 192, 255).endVertex();
	             tessellator.draw();
	          }

	          this.renderDecorations(p_render_1_, p_render_2_);
	          RenderSystem.enableTexture();
	          RenderSystem.shadeModel(7424);
	          RenderSystem.enableAlphaTest();
	          RenderSystem.disableBlend();
	       }
	      
	      
	      
	      
	      
	      
	      
	      
	      
	      
	      
	      
	      
	      
	      
	      
	      
	      
	      @Override
		public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
			// TODO Auto-generated method stub
			return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
		}

		@Override
		public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_,
				double p_mouseDragged_6_, double p_mouseDragged_8_) {
			// TODO Auto-generated method stub
			return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_,
					p_mouseDragged_8_);
		}

		@Override
		public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
			// TODO Auto-generated method stub
			return super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
		}
		
		
		
		
		
		

		protected void renderBackground() {
	    	 // SpeakerScreen.this.renderBackground();
	      }

	      protected boolean isFocused() {
	         return SpeakerScreen.this.getFocused() == this;
	      }

	      @OnlyIn(Dist.CLIENT)
	      public class SoundEntry extends ExtendedList.AbstractListEntry<SpeakerScreen.ScrollListWidget.SoundEntry> {
	         private final String sound;

	         public SoundEntry(String sound) {
	            this.sound = sound;
	         }

	         public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
	            SpeakerScreen.this.font.setBidiFlag(true);
	            ScrollListWidget.this.drawCenteredString(SpeakerScreen.this.font, this.sound, ScrollListWidget.this.width / 2+20, p_render_2_ + 1, 16777215);
	            	            
	            SpeakerScreen.this.font.setBidiFlag(Minecraft.getInstance().getLanguageManager().getCurrentLanguage().isBidirectional());
	         }

	         public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
	            if (p_mouseClicked_5_ == 0) {
	               this.setSelected();
	               return true;
	            } else {
	               return false;
	            }
	         }

	         private void setSelected() {
	            ScrollListWidget.this.setSelected(this);
	         }
	      }
	  
	   }
	  
	      */
	      
	  
	  
	  
	  
	  
	
	public SpeakerScreen(SpeakerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		this.guiLeft = 0;
		this.guiTop = 0;
		this.screenContainer=screenContainer;
		this.xSize = 256;
		this.ySize = 200;
				
		this.cancelBtn = new Button(this.width / 2 - 105, this.height / 4 + 120, 100, 20,
				I18n.format("GUI.CancelButton"), (close) -> {					
					this.close();
				});

		this.confirmBtn = new Button(this.xSize / 2 + 5, this.ySize / 4 + 120, 100, 20,
				I18n.format("GUI.ConfirmButton"), (confirm) -> {
								
						
					
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
						nbt.putInt("dimension",SpeakerContainer.dimension);
						nbt.putBoolean("isAlarm",SpeakerContainer.isAlarm);

						AmbiencePackageHandler.sendToServer(new MyMessage(nbt));
						
						this.close();				
				});
				
		this.buttons.add(cancelBtn);
		this.buttons.add(confirmBtn);		
	}

	@Override
	public void render(final int mouseX, final int mouseY, final float partialTicks) {
		this.renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);

		if (this.DelayInput == null) {
			this.DelayInput = new TextFieldWidget(this.font, this.width / 2 - 80, this.height / 4 + 30, 60, 20, "0");
			DelayInput.setText(""+SpeakerContainer.delay);			
			DelayInput.setVisible(true);

			
			this.LoopCheckbox = new CheckboxButton(this.width / 2 - 80, this.height / 4 + 80, 20, 20,"Loop", SpeakerContainer.loop);
			


		     this.list = new ScrollListWidget(Minecraft.getInstance(),this.width,this.height,screenContainer, font);				
		   //  this.list.setSelected(SpeakerContainer.selectedSound, font);
		    // this.list = new ScrollListWidget(Minecraft.getInstance(),this.width,this.height);
		     // this.children.add(this.list);
		     
		     DistanceSliderVal=(int)SpeakerContainer.distance;
		     distanceSlider=new AbstractSlider(this.width / 2 - 40,this.height / 2 + 1,180,20,SpeakerContainer.distance/10) {
					
					@Override
					protected void updateMessage() {
								
					}
					
					@Override
					protected void applyValue() {
						DistanceSliderVal= (int)(this.value*10);
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

		this.list.render(mouseX, mouseY, partialTicks);
				
		RenderSystem.color4f(1, 1, 1, 1);

		this.minecraft.getTextureManager().bindTexture(textureBackground2);
		int x=(this.width - this.xSize)/2;
		int y=(this.height - this.ySize)/2;

		this.blit(x, y, 0, 0, this.xSize, this.ySize);
		
		this.DelayInput.render(mouseX, mouseY, partialTicks);
		
		this.LoopCheckbox.render(mouseX, mouseY, partialTicks);

		this.drawCenteredString(this.font, I18n.format("GUI.SelectSoundLbl"), this.width / 2-86, this.height / 2 -88,16777215);
		this.drawCenteredString(this.font, "Delay:", this.width / 2-105, this.height / 2 + 35,16777215);
		this.drawCenteredString(this.font,  I18n.format("GUI.Distance"), this.width / 2-97, this.height / 2 + 8,16777215);
		
		this.distanceSlider.render(mouseX, mouseY, partialTicks);
		this.drawCenteredString(this.font, ""+ DistanceSliderVal, this.width / 2+30 ,this.height / 2 + 7,16777215);
				
		this.cancelBtn.render(mouseX, mouseY, partialTicks);
		this.confirmBtn.render(mouseX, mouseY, partialTicks);		
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1, 1, 1, 1);

		 this.minecraft.getTextureManager().bindTexture(textureBackground);
		 int x=(this.width - this.xSize)/2;
		 int y=(this.height - this.ySize)/2;

		 this.blit(x, y, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);				
	}

	// ----------------------------

	private void close() {
		this.minecraft.displayGuiScreen((Screen) null);
	}

	public void onClose() {
		this.close();
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
