package vazkii.ambience.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.ambience.Ambience;
import vazkii.ambience.Util.Utils;
import vazkii.ambience.network.OcarinaMessage;
import vazkii.ambience.network.OcarinaPackageHandler;


public class Ocarina extends ItemBase {

	public enum Operation {
		NIGHTCOMMAND, EDIT, DELETE, SELECT, OPENEDIT;
	}
	
	public static boolean setDayTime = false;
	public static Map<String, String[]> songsMap = new HashMap<String, String[]>();	
	public int stoopedPlayedFadeOut = 0;
	public boolean playing = false;
	public boolean runningCommand = false;
	public int key_id = -1;
	public int old_key_id = -1;
	public List<String> pressedKeys = new ArrayList<String>();
	public List<Integer> actualPressedKeys = new ArrayList<Integer>();
	public PlayerEntity player = null;
	public boolean hasMatch = false;
	public BlockPos pos= new BlockPos(Vec3d.ZERO);
	public String horseName="";
	
	public String songName = "";
	static int countCorrect = 0;
	
	public int delayMatch = 0;

	public Ocarina(int Maxdamage) {
		super(Maxdamage);
		/*this.addPropertyOverride(new ResourceLocation("pulling"), (p_210309_0_, p_210309_1_, p_210309_2_) -> {
	         return p_210309_2_ != null && p_210309_2_.isHandActive() && p_210309_2_.getActiveItemStack() == p_210309_0_ ? 1.0F : 0.0F;
	      });*/		
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add((ITextComponent) new StringTextComponent(I18n.format("Ocarina.Desc")));
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (playing & !runningCommand) {

			if (key_id != -1 & actualPressedKeys.size() == 1) {
				playNote(key_id, (PlayerEntity) entityIn);

				if (worldIn.isRemote)
					checkMusicNotes();
			}
		}

		if (worldIn.isRemote) {
			
			// Apply a little delay to let the looping play the match song
			if (hasMatch) {
				delayMatch++;
			}

			int scale_time=1;
			//If this is null you are in the single player 
			if(Minecraft.getInstance().getCurrentServerData() ==null)
				scale_time=8;
			else
				scale_time=1;
			
			if (delayMatch == 15*scale_time ) {
			//if (delayMatch == 15*20) {
				stoopedPlayedFadeOut = getDelayStopTime();
				//Play correct sound locally
				worldIn.playSound((PlayerEntity) entityIn, entityIn.getPosition(),
						ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambience:match_sound")),
						SoundCategory.RECORDS, 1, 1);
				
				//Play the music to other players in the server
				CompoundNBT nbt = new CompoundNBT();
				nbt.putString("playMusic", "ambience:match_sound");
				nbt.put("pos", Utils.BlockPosToNBT(entityIn.getPosition()));
				OcarinaPackageHandler.sendToServer(new OcarinaMessage(nbt));
			}
			
			if (delayMatch == 35*scale_time) {
				//Play the music locally
				worldIn.playSound((PlayerEntity) entityIn, entityIn.getPosition(),
						ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambience:" + songName)),
						SoundCategory.RECORDS, 1, 1);
				
				//Play the music to other players in the server
				CompoundNBT nbt = new CompoundNBT();
				nbt.putString("playMusic", "ambience:" + songName);
				nbt.put("pos", Utils.BlockPosToNBT(entityIn.getPosition()));
				OcarinaPackageHandler.sendToServer(new OcarinaMessage(nbt));
			}
		}

		if (stoopedPlayedFadeOut >= -1)
			stoopedPlayedFadeOut--;

		if (stoopedPlayedFadeOut == 0) {
			playing = false;
			hasMatch = false;
			runningCommand = false;
			// Ocarina.songName="";
			delayMatch = 0;
		}
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

	public Object getActualNotes(int key) {

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
			
			/*worldIn.playSound( playerIn, playerIn.getPosition(),
					ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambience:serenadeofwater")),
					SoundCategory.BLOCKS, 1, 1);
			*/
			return ActionResult.resultConsume(itemstack);
		}
	}
	
	public boolean checkMusicNotes() {

		countCorrect = 0;

		if (pressedKeys.size() >= 5) {

			songName = "";
			int correctLenght = 0;
			for (Entry<String, String[]> entry : songsMap.entrySet()) {
				correctLenght = entry.getValue().length;
				List<String> subList = new ArrayList<String>();
				subList = pressedKeys.subList((int) Utils.clamp(pressedKeys.size() - entry.getValue().length, 0, 16),
						pressedKeys.size());
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
				old_key_id = -1;
				key_id = -1;
				hasMatch = true;

				CompoundNBT nbt = new CompoundNBT();
				switch (songName) {
				case "sunssong":
					if (setDayTime) {
						setDayTime = false;

						nbt = new CompoundNBT();
						nbt.putBoolean("setDayTime", setDayTime);
						nbt.putString("songName", songName);
						nbt.put("pos", Utils.BlockPosToNBT(player.getPosition()));
						OcarinaPackageHandler.sendToServer(new OcarinaMessage(nbt));

					} else {
						setDayTime = true;

						nbt = new CompoundNBT();
						nbt.putBoolean("setDayTime", setDayTime);
						nbt.putString("songName", songName);
						nbt.put("pos", Utils.BlockPosToNBT(player.getPosition()));
						OcarinaPackageHandler.sendToServer(new OcarinaMessage(nbt));
					}
					break;
				case "songofstorms":
					nbt.putBoolean("setWeather", player.world.isRaining());
					nbt.putString("songName", songName);
					nbt.put("pos", Utils.BlockPosToNBT(player.getPosition()));
					OcarinaPackageHandler.sendToServer(new OcarinaMessage(nbt));

					break;
				case "bolerooffire":
					nbt = new CompoundNBT();
					nbt.putBoolean("setFireResistance", true);
					nbt.putString("songName", songName);
					nbt.put("pos", Utils.BlockPosToNBT(player.getPosition()));
					OcarinaPackageHandler.sendToServer(new OcarinaMessage(nbt));
					break;
				case "horsesong":
					nbt = new CompoundNBT();
					nbt.putBoolean("callHorse", true);
					nbt.putString("songName", songName);
					nbt.put("pos", Utils.BlockPosToNBT(player.getPosition()));
					OcarinaPackageHandler.sendToServer(new OcarinaMessage(nbt));
					break;
				case "preludeoflight":
					nbt = new CompoundNBT();
					nbt.putBoolean("setLightVision", true);
					nbt.putString("songName", songName);
					nbt.put("pos", Utils.BlockPosToNBT(player.getPosition()));
					OcarinaPackageHandler.sendToServer(new OcarinaMessage(nbt));
					break;
				case "serenadeofwater":
					nbt = new CompoundNBT();
					nbt.putBoolean("setWaterBreathe", true);
					nbt.putString("songName", songName);
					nbt.put("pos", Utils.BlockPosToNBT(player.getPosition()));
					OcarinaPackageHandler.sendToServer(new OcarinaMessage(nbt));
					break;
				case "minuetofforest":
					nbt = new CompoundNBT();
					nbt.putBoolean("heal", true);
					nbt.putString("songName", songName);
					nbt.put("pos", Utils.BlockPosToNBT(player.getPosition()));
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
				SoundCategory.RECORDS, 0.5f, 1);
	}

	public void addPressedKey(int key) {
		key_id = key;
		if (/*old_key_id != key_id & */key_id != -1) {
			pressedKeys.add("" + key_id);
		}
		old_key_id = key_id;		
	}

	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {

		if (runningCommand) {
			if(worldIn.isRemote)
				stoopedPlayedFadeOut = getDelayStopTime();			
		} else
			playing = false;
	}

	// Set how much time to close the playing screen depending on the music played
	public int getDelayStopTime() {
								
		switch (songName) {
		case "sunssong":
			stoopedPlayedFadeOut = 60;
			break;
		case "songofstorms":
			stoopedPlayedFadeOut = 85;
			break;
		case "bolerooffire":
			stoopedPlayedFadeOut = 300;
			break;
		case "horsesong":
			stoopedPlayedFadeOut = 110;
			break;
			
		case "preludeoflight":
			stoopedPlayedFadeOut = 300;
			break;
		case "serenadeofwater":
			stoopedPlayedFadeOut = 280;
			break;
		case "minuetofforest":
			stoopedPlayedFadeOut = 280;
			break;
			
		default:
			stoopedPlayedFadeOut = 100;
			break;
		}
		
		//If this is null you are in the single player 
		if(Minecraft.getInstance().getCurrentServerData() ==null)
			return stoopedPlayedFadeOut*20;
		else
			return stoopedPlayedFadeOut;
	}

	public static final ResourceLocation Ocarina_OVERLAY_FX = new ResourceLocation(Ambience.MODID,"textures/gui/ocarina_overlays_fx.png");
	public static final ResourceLocation Ocarina_OVERLAYS = new ResourceLocation(Ambience.MODID,"textures/gui/ocarina_overlays.png");
	public static float fx_rotateCount = 0;
	public static float fx_zoomCount = 70;

	private float startDelayCount=0;
	public void renderFX(RenderGameOverlayEvent.Post event, float zoomCount, float zoomAmount, double zoomSpeed, float startDelayTime) {
		
		// Renders the Ocarina's cinematic effect
		Minecraft mc = Minecraft.getInstance();
		if (event.getType() == ElementType.ALL) {
			MainWindow res = event.getWindow();
			if (mc.player != null) {
				ItemStack item = mc.player.getHeldItem(Hand.MAIN_HAND) == null ? mc.player.getHeldItem(Hand.OFF_HAND)
						: mc.player.getHeldItem(Hand.MAIN_HAND);

				if (item.getItem() instanceof Ocarina) {

					int width = 2048;
					int x = res.getScaledWidth() / 2;
					int y = (int) (1 + event.getWindow().getGuiScaleFactor());

					Vector4f color = new Vector4f(1, 1, 0, 1);

					// *******************************************************
					// FX ------------------------

					if (runningCommand) {
						
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

					// if(Ocarina.runningCommand)
					if (fx_zoomCount != 70) {
												
						fx_rotateCount ++;
						
						switch (songName) {
						case "sunssong": color = new Vector4f(1, 1, 0, 0.8f);break;
						case "songofstorms": color = new Vector4f(0.7f, 0.6f, 1, 0.8f);break;
						case "bolerooffire": color = new Vector4f(1, 0.3f, 0, 0.8f);break;
						case "horsesong":color = new Vector4f(1, 0.6f, 0.35f, 0.8f);break;
						
						case "preludeoflight":color = new Vector4f(0.89f, 0.86f, 0.35f, 0.8f);break;
						case "serenadeofwater":color = new Vector4f(0.6f, 0.78f, 0.98f, 0.8f);break;
						case "minuetofforest":color = new Vector4f(0.21f, 0.9f, 0.31f, 0.8f);break;
						default: color = new Vector4f(1, 1, 1, 0.8f);break;
						}

						// int opacity=(int)(262-(6.2f*zoomCount-179));
						float opacity = (int) (17 - (fx_zoomCount / 8));
						opacity = (opacity * 1.15f) / 15;

						double angle = 2 * Math.PI * fx_rotateCount / 150;
						float x2 = (float) Math.cos(angle);
						float scaleFade = (40 + (fx_zoomCount - 70)) / 20;

						
						// FX2
						RenderSystem.pushMatrix();
						RenderSystem.translatef(x, res.getScaledHeight() / 2, 0);
						RenderSystem.rotatef(-fx_rotateCount / 2, 0, 0, 10);
						RenderSystem.scalef((1 + x2 / 7) + scaleFade, (1 + x2 / 7) + scaleFade, 1);
						RenderSystem.color4f(color.getX(), color.getY(), color.getZ(), Utils.clamp(opacity, 0, 0.98f));

						// rendering
						mc.getTextureManager().bindTexture(Ocarina_OVERLAY_FX);
						AbstractGui.blit(-res.getScaledWidth(), (int) (-res.getScaledHeight() * 1.5f),
								res.getScaledWidth() * 2, res.getScaledHeight() * 3, res.getScaledWidth() * 2,
								res.getScaledHeight() * 3, res.getScaledWidth() * 2, res.getScaledHeight() * 3);

						RenderSystem.color4f(1F, 1F, 1F, 1);
						RenderSystem.popMatrix();

						// FX1
						x2 = (float) Math.cos(angle) * 1.5f;
						RenderSystem.pushMatrix();
						RenderSystem.translatef(x, res.getScaledHeight() / 2, 0);
						RenderSystem.rotatef(fx_rotateCount, 0, 0, 10);
						RenderSystem.scalef((1 + x2 / 9) + scaleFade, (1 + x2 / 9) + scaleFade, 1);
						RenderSystem.color4f(color.getX(), color.getY(), color.getZ(), Utils.clamp(opacity, 0, 0.99f));

						// rendering
						mc.getTextureManager().bindTexture(Ocarina_OVERLAY_FX);
						AbstractGui.blit(-res.getScaledWidth(), (int) (-res.getScaledHeight() * 1.5f),
								res.getScaledWidth() * 2, res.getScaledHeight() * 3, res.getScaledWidth() * 2,
								res.getScaledHeight() * 3, res.getScaledWidth() * 2, res.getScaledHeight() * 3);

						RenderSystem.color4f(1F, 1F, 1F, 1);
						RenderSystem.popMatrix();

					// ********************************************************


						RenderSystem.pushMatrix();
						color = new Vector4f(1, 1, 1, 1);
						RenderSystem.color4f(color.getX(), color.getY(), color.getZ(), 1);

						y = res.getScaledHeight() / 2;

						String s = I18n.format("Ocarina.Played");
						float scale = 1.25F * (int) event.getWindow().getGuiScaleFactor() / 2.5f;
						RenderSystem.scalef(scale, scale, scale);

						// Text renderer
						opacity = (int) (262 - (6.2f * zoomCount - 179));
						// int opacity=(int)((6.2f*zoomCount-179));
						int textColor = Utils.colorToInt(opacity, 255, 255, 255);
					
						String songNameText = "";
						
						switch (songName) {
						case "sunssong": songNameText = I18n.format("Ocarina.sunssong");
							textColor = Utils.colorToInt(opacity, 255, 255, 0);break;
						case "songofstorms": songNameText = I18n.format("Ocarina.songofstorms");
							textColor = Utils.colorToInt(opacity, 180, 155, 255);break;
						case "bolerooffire": songNameText = I18n.format("Ocarina.bolerooffire");
							textColor = Utils.colorToInt(opacity, 255, 0, 0);break;
						case "horsesong": 
							if(horseName!="")
								songNameText = horseName + "'s Song";
							else
								songNameText = I18n.format("Ocarina.horsesong");
							textColor = Utils.colorToInt(opacity, 255, 180, 40);break;
							
						case "preludeoflight": songNameText = I18n.format("Ocarina.preludeoflight");
							textColor = Utils.colorToInt(opacity, 245, 238, 180);break;
						case "serenadeofwater": songNameText = I18n.format("Ocarina.serenadeofwater");
							textColor = Utils.colorToInt(opacity, 200, 220, 250);break;
						case "minuetofforest": songNameText = I18n.format("Ocarina.minuetofforest");
							textColor = Utils.colorToInt(opacity, 55, 230, 80);break;
							
						default:
							textColor = Utils.colorToInt(opacity, 255, 0, 0);break;
						}
						
						int px = (int) (x / scale) - 30;
						int py2 = (int) (y / scale) + 70;

						int playedTextLeng = mc.fontRenderer.getStringWidth(s) ;
						int sontTextLeng= mc.fontRenderer.getStringWidth(songNameText);
						int totalTextLeng=playedTextLeng+sontTextLeng;

						mc.fontRenderer.drawStringWithShadow(s, px-(totalTextLeng/3)+res.getScaledWidth() / 2 / totalTextLeng, py2, Utils.colorToInt(opacity, 255, 255, 255));
						
						mc.fontRenderer.drawStringWithShadow(songNameText, px-(totalTextLeng/3)+res.getScaledWidth() / 2 / totalTextLeng +playedTextLeng , py2, textColor);
						RenderSystem.color4f(1F, 1F, 1F, 1);
						RenderSystem.popMatrix();

					}else {
						fx_rotateCount=0;
					}

					if(playing | fx_zoomCount != 70) 
					{
						y = (int) (1 + event.getWindow().getGuiScaleFactor());
						int py = (int) Math.abs(zoomCount - 70);
	
						RenderSystem.pushMatrix();
						color = new Vector4f(1, 1, 1, 1);
						RenderSystem.color4f(color.getX(), color.getY(), color.getZ(), 1);
	
						// Top Overlay
						mc.getTextureManager().bindTexture(Ocarina_OVERLAYS);
						AbstractGui.blit(0, 0, 0, 0, width, y + (int) (py * 1.1) - 10, 256, 256);
	
						// Bottom Overlay
						y = res.getScaledHeight() + 5 / (int) (1 + event.getWindow().getGuiScaleFactor());
						mc.getTextureManager().bindTexture(Ocarina_OVERLAYS);
						AbstractGui.blit(0, y - (int) (py * 1.1) + 10, 0, 0, width, 100, 256, 256);
	
						RenderSystem.color4f(1F, 1F, 1F, 1);
						RenderSystem.popMatrix();
					}

				}
			}
		}
	}
}
