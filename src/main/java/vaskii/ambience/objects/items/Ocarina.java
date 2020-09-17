package vaskii.ambience.objects.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import vaskii.ambience.network4.MyMessage4;
import vaskii.ambience.network4.NetworkHandler4;
import vaskii.ambience.network4.OcarinaNetworkHandler;
import vazkii.ambience.Reference;
import vazkii.ambience.Util.Utils;

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
	public EntityPlayer player = null;
	public boolean hasMatch = false;
	public BlockPos pos= new BlockPos(Vec3d.ZERO);
	public String horseName="";
	
	public String songName = "";
	static int countCorrect = 0;
	
	public int delayMatch = 0;

	public Ocarina(String Name) {
		super(Name);
		setMaxDamage(20);
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
	    return EnumAction.BOW;
	}

	@Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 72000;
    }
	
	@Override
	public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
		super.addInformation(itemstack, world, list, flag);

		list.add(I18n.format("Ocarina.Desc"));
	}
	
	
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
		
		
		if (playing & !runningCommand) {

			if (key_id != -1 & actualPressedKeys.size() == 1) {
				playNote(key_id, (EntityPlayer) entityIn);

				if (worldIn.isRemote)
					checkMusicNotes((EntityPlayer) entityIn);
			}
		}

		if (worldIn.isRemote) {
			
			// Apply a little delay to let the looping play the match song
			if (hasMatch) {
				delayMatch++;
			}

			int scale_time=1;
			//If this is null you are in the single player 
			if(Minecraft.getMinecraft().getCurrentServerData() ==null)
				scale_time=1;
			else
				scale_time=1;
			
			if (delayMatch == 15*scale_time ) {
			//if (delayMatch == 15*20) {
				stoopedPlayedFadeOut = getDelayStopTime();
				//Play correct sound locally
				worldIn.playSound((EntityPlayer) entityIn, entityIn.getPosition(),
						ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambience:match_sound")),
						SoundCategory.RECORDS, 1, 1);
				
				//Play the music to other players in the server
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("playMusic","ambience:match_sound");	
				nbt.setTag("pos", Utils.BlockPosToNBT(entityIn.getPosition()));
				OcarinaNetworkHandler.sendToServer(new MyMessage4(nbt));				
			}
			
			if (delayMatch == 35*scale_time) {
				//Play the music locally
				worldIn.playSound((EntityPlayer) entityIn, entityIn.getPosition(),
						ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambience:" + songName)),
						SoundCategory.RECORDS, 1, 1);
				
				//Play the music to other players in the server
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("playMusic","ambience:" + songName);	
				nbt.setTag("pos", Utils.BlockPosToNBT(entityIn.getPosition()));
				OcarinaNetworkHandler.sendToServer(new MyMessage4(nbt));	
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

	
	
	/*@Override
	public SoundEvent getDrinkSound() {

		return null;
	}*/

	public Object getActualNotes(int key) {

		for (Integer item : actualPressedKeys) {
			if (item == key) {
				return item;
			}
		}

		return null;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		boolean flag = true;
		ItemStack itemstack = playerIn.getHeldItem(handIn);

		ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemstack, worldIn,
				playerIn, handIn, flag);
		if (ret != null)
			return ret;

		//itemstack.damageItem(1, playerIn);
		
		if(itemstack.getItemDamage()>=20) {		
			stoopedPlayedFadeOut = 100;
			itemstack.damageItem(1, playerIn);
			playing = false;
			return new ActionResult(EnumActionResult.SUCCESS, itemstack);
		}

		//if (playerIn.capabilities.isCreativeMode) {
		//	return new ActionResult(EnumActionResult.PASS, itemstack);
		//} else {
			playerIn.setActiveHand(handIn);
			playing = true;
			this.player = playerIn;
			
			/*worldIn.playSound( playerIn, playerIn.getPosition(),
					ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambience:serenadeofwater")),
					SoundCategory.BLOCKS, 1, 1);
			*/
			return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
		//}
	}
	
	public boolean checkMusicNotes(EntityPlayer playerIn) {
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

			if (songsMap.size()>0 & countCorrect >= correctLenght & !runningCommand) {
				old_key_id = -1;
				key_id = -1;
				hasMatch = true;

				ItemStack itemstack = playerIn.getHeldItem(playerIn.getActiveHand());
				itemstack.damageItem(1, playerIn);

				NBTTagCompound nbt = new NBTTagCompound();
				switch (songName) {
				case "sunssong":
					if (setDayTime) {
						setDayTime = false;

						nbt = new NBTTagCompound();
						nbt.setBoolean("setDayTime", setDayTime);
						nbt.setString("songName", songName);
						nbt.setTag("pos", Utils.BlockPosToNBT(player.getPosition()));
						OcarinaNetworkHandler.sendToServer(new MyMessage4(nbt));

					} else {
						setDayTime = true;

						nbt = new NBTTagCompound();
						nbt.setBoolean("setDayTime", setDayTime);
						nbt.setString("songName", songName);
						nbt.setTag("pos", Utils.BlockPosToNBT(player.getPosition()));
						OcarinaNetworkHandler.sendToServer(new MyMessage4(nbt));
					}
					break;
				case "songofstorms":
					nbt.setBoolean("setWeather", player.world.isRaining());
					nbt.setString("songName", songName);
					nbt.setTag("pos", Utils.BlockPosToNBT(player.getPosition()));
					OcarinaNetworkHandler.sendToServer(new MyMessage4(nbt));

					break;
				case "bolerooffire":
					nbt = new NBTTagCompound();
					nbt.setBoolean("setFireResistance", true);
					nbt.setString("songName", songName);
					nbt.setTag("pos", Utils.BlockPosToNBT(player.getPosition()));
					OcarinaNetworkHandler.sendToServer(new MyMessage4(nbt));
					break;
				case "horsesong":
					nbt = new NBTTagCompound();
					nbt.setBoolean("callHorse", true);
					nbt.setString("songName", songName);
					nbt.setTag("pos", Utils.BlockPosToNBT(player.getPosition()));
					OcarinaNetworkHandler.sendToServer(new MyMessage4(nbt));
					break;
				case "preludeoflight":
					nbt = new NBTTagCompound();
					nbt.setBoolean("setLightVision", true);
					nbt.setString("songName", songName);
					nbt.setTag("pos", Utils.BlockPosToNBT(player.getPosition()));
					OcarinaNetworkHandler.sendToServer(new MyMessage4(nbt));
					break;
				case "serenadeofwater":
					nbt = new NBTTagCompound();
					nbt.setBoolean("setWaterBreathe", true);
					nbt.setString("songName", songName);
					nbt.setTag("pos", Utils.BlockPosToNBT(player.getPosition()));
					OcarinaNetworkHandler.sendToServer(new MyMessage4(nbt));
					break;
				case "minuetofforest":
					nbt = new NBTTagCompound();
					nbt.setBoolean("heal", true);
					nbt.setString("songName", songName);
					nbt.setTag("pos", Utils.BlockPosToNBT(player.getPosition()));
					OcarinaNetworkHandler.sendToServer(new MyMessage4(nbt));
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

	public static void playNote(int note, EntityPlayer player) {
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

	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
	{
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
		if(Minecraft.getMinecraft().getCurrentServerData() ==null)
			return stoopedPlayedFadeOut*2;
		else
			return stoopedPlayedFadeOut;
	}

	public static final ResourceLocation Ocarina_OVERLAY_FX = new ResourceLocation(Reference.MOD_ID,"textures/gui/ocarina_overlays_fx.png");
	public static final ResourceLocation Ocarina_OVERLAYS = new ResourceLocation(Reference.MOD_ID,"textures/gui/ocarina_overlays.png");
	public static float fx_rotateCount = 0;
	public static float fx_zoomCount = 70;

	private float startDelayCount=0;
	public void renderFX(RenderGameOverlayEvent.Post event, float zoomCount, float zoomAmount, double zoomSpeed, float startDelayTime) 
	{
		// Renders the Ocarina's cinematic effect
		Minecraft mc = Minecraft.getMinecraft();
		if (event.getType() == ElementType.ALL) {
			ScaledResolution res = event.getResolution();
			if (mc.player != null) {
				ItemStack item = mc.player.getHeldItem(EnumHand.MAIN_HAND) == null ? mc.player.getHeldItem(EnumHand.OFF_HAND) : mc.player.getHeldItem(EnumHand.MAIN_HAND);

				if (item.getItem() instanceof Ocarina) {

					int width = 2048;
					int x = res.getScaledWidth() / 2;
					int y = (int) (1 + res.getScaleFactor());

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


					if (fx_zoomCount != 70) {
					//if (fx_zoomCount == 70) { //test
												
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
						opacity = ((opacity * 1.15f) / 15)-0.01f;
						
						double angle = 2 * Math.PI * fx_rotateCount / 80;
						float x2 = (float) Math.cos(angle);
						float scaleFade = (40 + (fx_zoomCount - 70)) / 40;
						
						// FX2
						GL11.glPushMatrix();
						GL11.glTranslatef(x, res.getScaledHeight() / 2, 0);
						GL11.glRotated(-fx_rotateCount / 2, 0, 0, 10);
						GL11.glScalef((1 + x2 / 7) + scaleFade, (1 + x2 / 7) + scaleFade, 1);
						GlStateManager.color(color.getX(), color.getY(), color.getZ(), Utils.clamp(opacity, 0, 0.98f));

						// rendering
						mc.getTextureManager().bindTexture(Ocarina_OVERLAY_FX);
						GuiScreen.drawModalRectWithCustomSizedTexture(-res.getScaledWidth(), (int) (-res.getScaledHeight() * 1.5f),
								res.getScaledWidth() * 2, res.getScaledHeight() * 3, res.getScaledWidth() * 2,
								res.getScaledHeight() * 3, res.getScaledWidth() * 2, res.getScaledHeight() * 3);

						GlStateManager.color(1F, 1F, 1F, 1);
						GL11.glPopMatrix();

						// FX1
						x2 = (float) Math.cos(angle) * 1.5f;
						GL11.glPushMatrix();
						GL11.glTranslatef(x, res.getScaledHeight() / 2, 0);
						GL11.glRotated(fx_rotateCount, 0, 0, 10);
						GL11.glScalef((1 + x2 / 9) + scaleFade, (1 + x2 / 9) + scaleFade, 1);
						GlStateManager.color(color.getX(), color.getY(), color.getZ(), Utils.clamp(opacity, 0, 0.99f));

						// rendering
						mc.getTextureManager().bindTexture(Ocarina_OVERLAY_FX);
						//this.mc.renderEngine.bindTexture(texture);
						
						GuiScreen.drawModalRectWithCustomSizedTexture(-res.getScaledWidth(), (int) (-res.getScaledHeight() * 1.5f),
								res.getScaledWidth() * 2, res.getScaledHeight() * 3, res.getScaledWidth() * 2,
								res.getScaledHeight() * 3, res.getScaledWidth() * 2, res.getScaledHeight() * 3);

						GlStateManager.color(1F, 1F, 1F, 1);
						GL11.glPopMatrix();

					// ********************************************************


						GL11.glPushMatrix();
						color = new Vector4f(1, 1, 1, 1);
						GlStateManager.color(color.getX(), color.getY(), color.getZ(), 1);

						String s = I18n.format("Ocarina.Played");
						float scale = 1.25F * (int) res.getScaleFactor() / 2.5f;
						GL11.glScalef(scale, scale, scale);

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
						
						
						int px = (int) (x / scale) - 0;
						
						scale = 1.25F * (int) res.getScaleFactor() / 2.5f;
						y = res.getScaledHeight() / 2;
						//int py2 =  (res.getScaledHeight()+120 * res.getScaleFactor()/2) / res.getScaleFactor();
						int py2 =  (res.getScaledHeight()-55*res.getScaleFactor());


						//Test-------------
						//opacity=255;
						//songNameText="Song of Storms";
						//textColor = Utils.colorToInt(opacity, 180, 155, 255);
						//-----------------

						int playedTextLeng = mc.fontRenderer.getStringWidth(s) ;
						int sontTextLeng= mc.fontRenderer.getStringWidth(songNameText);
						int totalTextLeng=playedTextLeng+sontTextLeng;

					
								
						mc.fontRenderer.drawStringWithShadow(s, px-(totalTextLeng/2)+res.getScaledWidth() / 2 / totalTextLeng, py2, Utils.colorToInt(opacity, 255, 255, 255));
						
						mc.fontRenderer.drawStringWithShadow(songNameText, px-(totalTextLeng/2)+res.getScaledWidth() / 2 / totalTextLeng + playedTextLeng +4, py2, textColor);
						GlStateManager.color(1F, 1F, 1F, 1);
						GL11.glPopMatrix();

					}else {
						fx_rotateCount=0;
					}

					//if(playing | fx_zoomCount != 70) 
					{
						y = (int) (1 + res.getScaleFactor());
						int py = (int) Math.abs(zoomCount - 70);
	
						GL11.glPushMatrix();
						color = new Vector4f(1, 1, 1, 1);
						GlStateManager.color(color.getX(), color.getY(), color.getZ(), 1);
	
						// Top Overlay
						mc.getTextureManager().bindTexture(Ocarina_OVERLAYS);
						GuiScreen.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width, y + (int) (py * 1.1) - 10, 256, 256);
	
						// Bottom Overlay
						y = res.getScaledHeight() + 5 / (int) (1 + res.getScaleFactor());
						mc.getTextureManager().bindTexture(Ocarina_OVERLAYS);
						GuiScreen.drawModalRectWithCustomSizedTexture(0, y - (int) (py * 1.1) + 10, 0, 0, width, 100, 256, 256);
	
						GlStateManager.color(1F, 1F, 1F, 1);
						GL11.glPopMatrix();
					}
				}
			}
		}
	}
}
