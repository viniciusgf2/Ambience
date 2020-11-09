package vaskii.ambience.network4;

import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vaskii.ambience.GUI.SpeakerEditGUI;
import vaskii.ambience.GUI.SpeakerGUI;
import vaskii.ambience.Init.ItemInit;
import vaskii.ambience.objects.blocks.Speaker;
import vaskii.ambience.objects.blocks.SpeakerTileEntity;
import vaskii.ambience.objects.items.Ocarina;
import vazkii.ambience.Ambience;
import vazkii.ambience.SongPicker;
import vazkii.ambience.World.Biomes.Area;

public class OcarinaClientHandler implements IMessageHandler<MyMessage4, IMessage> {
	// Do note that the default constructor is required, but implicitly defined in
	// this case

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(MyMessage4 message, MessageContext ctx) {

		NBTTagCompound data = message.getToSend();

		Ocarina Ocarina=(Ocarina) ItemInit.itemOcarina;
						
		if(data.hasKey("setDayTime")) {
			Ocarina.setDayTime=data.getBoolean("setDayTime");
		}else if(data.hasKey("setWeather")) {
			setWeather(data.getBoolean("setWeather"),Ocarina.player);
		}
		else if (data.hasKey("ocarinaBreak")) {
			Ocarina.stoopedPlayedFadeOut = 0;
			Ocarina.playing = false;
			Ocarina.hasMatch=false;
			Ocarina.delayMatch=0;
			Ocarina.runningCommand=false;
		}

		return null;
	}
	
	private boolean setWeather(boolean chuva,EntityPlayer player) {
		if(!chuva) {	
			//Thunder
			//player.world.setRainStrength(1);
			player.world.getWorldInfo().setCleanWeatherTime(0);
			player.world.getWorldInfo().setRainTime(6000);
			player.world.getWorldInfo().setThunderTime(6000);
			player.world.getWorldInfo().setRaining(true);
			player.world.getWorldInfo().setThundering(true);
			
			return false;
										
		}else {
			//Clear Weather
			//player.world.setRainStrength(0);
			player.world.getWorldInfo().setCleanWeatherTime(6000);
			player.world.getWorldInfo().setRainTime(0);
			player.world.getWorldInfo().setThunderTime(0);
			player.world.getWorldInfo().setRaining(false);
			player.world.getWorldInfo().setThundering(false);												
			
			return true;
		}
	}
}
