package vazkii.ambience.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.rmi.CORBA.Util;

import net.minecraft.client.Minecraft;
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

	/*
	 * private static void removePressedKey(int key) {
	 * if(Ocarina.pressedKeys.size()>0 && Ocarina.actualPressedKeys.contains(key)) {
	 * Ocarina.pressedKeys.remove((Object)key); } }
	 */

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
}
