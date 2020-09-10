package vazkii.ambience.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.rmi.CORBA.Util;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.ambience.Ambience;
import vazkii.ambience.Util.Utils;
import vazkii.ambience.World.Biomes.Area;
import vazkii.ambience.blocks.Speaker;
import vazkii.ambience.network.AmbiencePackageHandler;
import vazkii.ambience.network.MyMessage;
import vazkii.ambience.network.OcarinaMessage;
import vazkii.ambience.network.OcarinaPackageHandler;

public class Ocarina extends ItemBase {

	public enum Operation {
		NIGHTCOMMAND, EDIT, DELETE, SELECT, OPENEDIT;
	}

	public static int stoopedPlayedFadeOut = 0;
	public static boolean playing = false;
	public static boolean runningCommand = false;
	public static int key_id = -1;
	public static int old_key_id = -1;
	public static List<String> pressedKeys = new ArrayList<String>();
	public static List<Integer> actualPressedKeys = new ArrayList<Integer>();
	public static PlayerEntity player = null;
	public static Map<String, String[]> songsMap = new HashMap<String, String[]>();
	public static boolean hasMatch = false;

	public static boolean setDayTime = false;

	public Ocarina(int Maxdamage) {
		super(Maxdamage);

	}

	public UseAction getUseAction(ItemStack stack) {

		/*
		 * if(Minecraft.getInstance().gameSettings.thirdPersonView == 0) return
		 * UseAction.DRINK; else
		 */
		return UseAction.BOW;
	}

	public int getUseDuration(ItemStack stack) {
		return 250;
	}

	@Override
	public SoundEvent getDrinkSound() {

		return null;
	}

	public static Object getActualNotes(int key) {

		for (Integer item : actualPressedKeys) {
			if (item == key) {
				return item;
			}
		}

		return null;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {

		boolean flag = true;
		ItemStack itemstack = playerIn.getHeldItem(handIn);

		ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemstack, worldIn,
				playerIn, handIn, flag);
		if (ret != null)
			return ret;

		itemstack.damageItem(1, playerIn, (damage) -> {
			damage.sendBreakAnimation(playerIn.getActiveHand());
			
			stoopedPlayedFadeOut = 100;			
			playing = false;
		});

		if (!playerIn.abilities.isCreativeMode && !flag) {
			return ActionResult.resultFail(itemstack);
		} else {
			playerIn.setActiveHand(handIn);
			playing = true;					
			this.player = playerIn;
			
			return ActionResult.resultConsume(itemstack);
		}
	}	

	public static String songName = "";
	static int countCorrect = 0;

	public static boolean checkMusicNotes() {

		countCorrect = 0;
		
		if (pressedKeys.size() >= 6) {
			
			songName = "";
			int correctLenght=0;
			for (Entry<String, String[]> entry : songsMap.entrySet()) {
				correctLenght=entry.getValue().length;
				List<String> subList = new ArrayList<String>();
				subList = pressedKeys.subList((int)Utils.clamp(pressedKeys.size() - entry.getValue().length, 0, 16) , pressedKeys.size());
				countCorrect = 0;
				// Compare each entry from the last 5 played notes
				for (int i = 0; i < subList.size(); i++) {
					if (subList.get(i).equals(entry.getValue()[i])) {
						songName = entry.getKey();
						countCorrect++;
					} else {
						countCorrect--;
						break;
					}

				}

				if (countCorrect >= correctLenght)
					break;

			}

			if (countCorrect >= correctLenght & !runningCommand) {
				Ocarina.old_key_id = -1;
				Ocarina.key_id = -1;
				hasMatch = true;

				CompoundNBT nbt = new CompoundNBT();
				switch (songName) {
				case "sunssong":
					if (setDayTime) {
						setDayTime = false;

						nbt = new CompoundNBT();
						nbt.putBoolean("setDayTime", setDayTime);
						nbt.putString("songName", songName);
						OcarinaPackageHandler.sendToServer(new OcarinaMessage(nbt));

					} else {
						setDayTime = true;

						nbt = new CompoundNBT();
						nbt.putBoolean("setDayTime", setDayTime);
						nbt.putString("songName", songName);
						OcarinaPackageHandler.sendToServer(new OcarinaMessage(nbt));
					}
					break;
				case "songofstorms":
					nbt.putBoolean("setWeather", player.world.isRaining());
					nbt.putString("songName", songName);
					OcarinaPackageHandler.sendToServer(new OcarinaMessage(nbt));

					break;
				case "bolerooffire":
					nbt = new CompoundNBT();
					nbt.putBoolean("setFireResistance", true);
					nbt.putString("songName", songName);
					OcarinaPackageHandler.sendToServer(new OcarinaMessage(nbt));
					
					break;
				}

				runningCommand = true;

				return true;
			} else {
				hasMatch = false;
				songName = "";
				return false;
			}
		}

		return false;
	}

	public static void playNote(int note, PlayerEntity player) {
		player.world.playSound(player, player.getPosition(),
				ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambience:ocarina" + note)),
				SoundCategory.PLAYERS, 0.5f, 1);
	}

	public static void addPressedKey(int key) {
		Ocarina.key_id = key;
		if (Ocarina.old_key_id != Ocarina.key_id & Ocarina.key_id != -1) {
			Ocarina.pressedKeys.add("" + Ocarina.key_id);			
		}
		Ocarina.old_key_id = Ocarina.key_id;
	}

	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {

		if (runningCommand) {
			stoopedPlayedFadeOut = getDelayStopTime();
		} else
			playing = false;
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add((ITextComponent) new StringTextComponent(I18n.format("Ocarina.Desc")));
	}

	//Set how much time to close the playing screen depending on the music played
	public static int getDelayStopTime() {
		switch (songName) {
		case "sunssong":
			stoopedPlayedFadeOut = 200;
			break;
		case "songofstorms":
			stoopedPlayedFadeOut = 250;
			break;
		case "bolerooffire":
			stoopedPlayedFadeOut = 900;
			break;
		default:
			stoopedPlayedFadeOut = 200;
			break;
		}

		return stoopedPlayedFadeOut;
	}
	
	public static final ResourceLocation Ocarina_OVERLAY_FX = new ResourceLocation(Ambience.MODID, "textures/gui/ocarina_overlays_fx.png");	   
    public static final ResourceLocation Ocarina_OVERLAYS = new ResourceLocation(Ambience.MODID, "textures/gui/ocarina_overlays.png");
    public static float fx_rotateCount=0;
    public static float fx_zoomCount=70;
	public static void renderFX(RenderGameOverlayEvent.Post event,float zoomCount,float zoomAmount,double zoomSpeed) {

		if(fx_zoomCount!=70)
			fx_rotateCount+=0.1f;
		else
			fx_rotateCount=70;
			
		//Renders the Ocarina's cinematic effect
		Minecraft mc = Minecraft.getInstance();
        if (event.getType() == ElementType.ALL) {
            MainWindow res = event.getWindow();
            if (mc.player != null) 
            {
            	ItemStack item = mc.player.getHeldItem(Hand.MAIN_HAND) == null? mc.player.getHeldItem(Hand.OFF_HAND) : mc.player.getHeldItem(Hand.MAIN_HAND);
            	
    			if (item.getItem() instanceof Ocarina) 
    			{
            	
	            	 int width = 2048;
	            	 int x = res.getScaledWidth() / 2;
	                 int y = (int) (1+event.getWindow().getGuiScaleFactor());
	                                   

	                 Vector4f color=new Vector4f(1,1,0,1);	 
	                 
	                 if(Ocarina.runningCommand) 
	                 {
	                 
	                 //*******************************************************
	                 //FX ------------------------
	                 
	                 if(Ocarina.runningCommand) {
	                	 
	                	 if (fx_zoomCount > zoomAmount) {
	                		 fx_zoomCount -= zoomSpeed;
	     	                if (fx_zoomCount < zoomAmount) {
	     	                	fx_zoomCount = zoomAmount;
	     	                }
	     	            }	        
	                 }else{
	                	 if (fx_zoomCount < 70) {
	                		 fx_zoomCount += zoomSpeed;
	                         if (fx_zoomCount > 70) {
	                        	 fx_zoomCount = 70;
	                         }
	                     }
	                 }	
	                 if(fx_zoomCount!=70)
	                // if(Ocarina.runningCommand) 
	                 {
	     			
	                	 switch (Ocarina.songName) {
		                 	case "sunssong" : color=new Vector4f(1,1,0,1);break;
		                 	case "songofstorms" : color=new Vector4f(0.7f,0.6f,1,1);break;
		                 	case "bolerooffire" : color=new Vector4f(1,0.3f,0,1);break;		                 	
		                 	default : color=new Vector4f(1,1,1,1);break;
		                 }   
	                	             

	                // int opacity=(int)(262-(6.2f*zoomCount-179));
	                 float opacity=(int)(17-(zoomCount/8));	                 
	                 opacity=(opacity * 1.15f)/15;
	                 
	                 double angle = 2 * Math.PI * fx_rotateCount / 150;
	     			 float x2 = (float) Math.cos(angle);	  
	     			 float scaleFade=(40+(fx_zoomCount-70))/20;
	                 	     			 
	     			 //FX2

	                 RenderSystem.pushMatrix();	    
	                 RenderSystem.translatef(x, res.getScaledHeight()/2,0);
	                 RenderSystem.rotatef(-fx_rotateCount/2, 0, 0, 10);
	                 RenderSystem.scalef((1+x2/7)+scaleFade, (1+x2/7)+scaleFade, 1);
	                 RenderSystem.color4f(color.getX(), color.getY(), color.getZ(), opacity );
	                 		                	     
	                 //rendering	                 	                 
	            	 mc.getTextureManager().bindTexture(Ocarina_OVERLAY_FX);            	
	                 AbstractGui.blit(-res.getScaledWidth(), (int)(-res.getScaledHeight()*1.5f), res.getScaledWidth()*2, res.getScaledHeight()*3,  res.getScaledWidth()*2, res.getScaledHeight()*3, res.getScaledWidth()*2, res.getScaledHeight()*3);
	                 	 
	                 RenderSystem.color4f(1F, 1F, 1F, 1);
	                 RenderSystem.popMatrix();
	     			 
	     			 //FX1
	     			 x2 = (float) Math.cos(angle) * 1.5f;
	                 RenderSystem.pushMatrix();	        
	     			 RenderSystem.translatef(x, res.getScaledHeight()/2,0);
	                 RenderSystem.rotatef(fx_rotateCount, 0, 0, 10);
	                 RenderSystem.scalef((1+x2/9)+scaleFade, (1+x2/9)+scaleFade, 1);
	                 RenderSystem.color4f(color.getX(), color.getY(), color.getZ(), opacity );
	                 		                	     
	                 //rendering	                 	                 
	            	 mc.getTextureManager().bindTexture(Ocarina_OVERLAY_FX);            	
	                 AbstractGui.blit(-res.getScaledWidth(), (int)(-res.getScaledHeight()*1.5f), res.getScaledWidth()*2, res.getScaledHeight()*3,  res.getScaledWidth()*2, res.getScaledHeight()*3, res.getScaledWidth()*2, res.getScaledHeight()*3);
	                 	 
	                 RenderSystem.color4f(1F, 1F, 1F, 1);
	                 RenderSystem.popMatrix();
	                 
	                 }
	                 //********************************************************
	                 
	               
	                 	             
	            	
	                   
	                 
	                	 RenderSystem.pushMatrix();	 
	 	                 color=new Vector4f(1,1,1,1);	 
	 	                 RenderSystem.color4f(color.getX(), color.getY(), color.getZ(), 1);
	 	                 
		                 y = res.getScaledHeight()/2;
		             	
		                 String s=I18n.format("Ocarina.Played");
		                 float scale = 1.25F*(int)event.getWindow().getGuiScaleFactor()/2.5f;
		                 RenderSystem.scalef(scale, scale, scale);
		                 
		                 //Text renderer
		                 int opacity=(int)(262-(6.2f*zoomCount-179));
		                // int opacity=(int)((6.2f*zoomCount-179));
		                 int textColor= Utils.colorToInt(opacity,255,255,255);                  
		                 int totalTextLeng= mc.fontRenderer.getStringWidth(s)/2;
		                 int px= (int) (x/scale)-30;
		                 int py2= (int) (y/scale)+70;
		                 
		                 String songNameText="";
		                 mc.fontRenderer.drawStringWithShadow(s, px - totalTextLeng, py2, textColor);
		                 switch (Ocarina.songName) {
		                 	case "sunssong" : songNameText="Sun's Song"; textColor=Utils.colorToInt(opacity,255,255,0);break;
		                 	case "songofstorms" : songNameText="Song of Storms"; textColor=Utils.colorToInt(opacity,180,155,255);break;
		                 	case "bolerooffire" : songNameText="Bolero of Fire"; textColor=Utils.colorToInt(opacity,255,0,0);break;
		                 	
		                 	default : textColor=Utils.colorToInt(opacity,255,0,0);break;
		                 }                 
		                 mc.fontRenderer.drawStringWithShadow(songNameText, px+3 + mc.fontRenderer.getStringWidth("Sun's Song")/1.5f, py2, textColor);
		                 RenderSystem.color4f(1F, 1F, 1F, 1);
	                     RenderSystem.popMatrix();
		                    
	                 }

	                 y = (int) (1+event.getWindow().getGuiScaleFactor());
	                 int py=(int) Math.abs(zoomCount-70);
	                 
	                 RenderSystem.pushMatrix();	 
	                 color=new Vector4f(1,1,1,1);	 
	                 RenderSystem.color4f(color.getX(), color.getY(), color.getZ(), 1);
	                 
	                 
	                 //Top Overlay
	            	 mc.getTextureManager().bindTexture(Ocarina_OVERLAYS);            	
	                 AbstractGui.blit(0, 0, 0, 0, width, y+(int)(py*1.1)-10, 256, 256);
	                                  
	                 //Bottom Overlay
	                 y = res.getScaledHeight()+5 /(int)(1+event.getWindow().getGuiScaleFactor());
	            	 mc.getTextureManager().bindTexture(Ocarina_OVERLAYS);            	
	                 AbstractGui.blit(0, y-(int)(py*1.1)+10, 0, 0, width, 100, 256, 256);
	                 
	                 RenderSystem.color4f(1F, 1F, 1F, 1);
	                 RenderSystem.popMatrix();
	                 
    			}
            }
        }
	}
}
