package vazkii.ambience.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.ambience.items.Ocarina;

public class OcarinaMessage {

	private CompoundNBT data;

	public OcarinaMessage() {
	}

	public OcarinaMessage(PacketBuffer buf) {
		this.data = buf.readCompoundTag();
	}

	public OcarinaMessage(CompoundNBT Data) {
		this.data = Data;
	}

	public void encode(PacketBuffer buf) {
		buf.writeCompoundTag(data);
	}

	public void handle(Supplier<NetworkEvent.Context> context) {

		NetworkEvent.Context ctx = context.get();
		
		context.get().enqueueWork(() -> {

			//
			// SERVER SIDE
			//
			// if(FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
			if (ctx.getDirection() == NetworkDirection.PLAY_TO_SERVER) {

				if(data.contains("actualPressedKeys")) {
					Ocarina.key_id=data.getInt("keyPressed");
					
					//Converts the received string list keys to a Integer list and sets to the Ocarina
					String[] keysList= data.getString("actualPressedKeys").split(",");					
					List<Integer> actualPressedKeys = new ArrayList<Integer>();					
					for(String key :keysList) {
						if(key!="")
							actualPressedKeys.add(Integer.parseInt(key));
					}					
					Ocarina.actualPressedKeys=actualPressedKeys;
					
					//Add the pressedKey to list of keys to check for musics					
					/*if (Ocarina.old_key_id != Ocarina.key_id & Ocarina.key_id != -1) {
						Ocarina.pressedKeys.add("" + Ocarina.key_id);
					}
					Ocarina.old_key_id = Ocarina.key_id;*/
					
				}else if(data.contains("resetVariables")) {
					Ocarina.pressedKeys.clear();
	            	Ocarina.old_key_id=-1;
	            	Ocarina.runningCommand=false;
	            	Ocarina.songName="";
				}
				else if(data.contains("setDayTime")) {
					Ocarina.setDayTime=data.getBoolean("setDayTime");		
					Ocarina.songName=data.getString("songName");
					Ocarina.hasMatch=true;
					Ocarina.runningCommand=true;
				}
				else if(data.contains("setWeather")) {
					
					PlayerEntity player=ctx.getSender();
					//boolean chuva=setWeather(player.world.isRaining(),player);
					boolean chuva=false;
					if(!player.world.isRaining()) {	
						//Thunder
						player.world.getWorldInfo().setClearWeatherTime(0);
						player.world.getWorldInfo().setRainTime(6000);
						player.world.getWorldInfo().setThunderTime(6000);
						player.world.getWorldInfo().setRaining(true);
						player.world.getWorldInfo().setThundering(true);
						//player.world.setRainStrength(1);
						chuva=false;
													
					}else {
						//Clear Weather
					//	player.world.setRainStrength(0);
						player.world.getWorldInfo().setClearWeatherTime(6000);
						player.world.getWorldInfo().setRainTime(0);
						player.world.getWorldInfo().setThunderTime(0);
						player.world.getWorldInfo().setRaining(false);
						player.world.getWorldInfo().setThundering(false);	
						
						chuva=true;
					}

					CompoundNBT nbt = new CompoundNBT();
					nbt.putBoolean("setWeather", chuva);
					OcarinaPackageHandler.sendToAll(new OcarinaMessage(nbt));
					
					Ocarina.hasMatch=true;
					Ocarina.songName=data.getString("songName");
					Ocarina.runningCommand=true;
				}
			}

			//
			// CLIENT SIDE
			//
			else {
				if(data.contains("setDayTime")) {
					Ocarina.setDayTime=data.getBoolean("setDayTime");
				}else if(data.contains("setWeather")) {

					setWeather(data.getBoolean("setWeather"),Ocarina.player);
				}
			}
		});

		context.get().setPacketHandled(true);
	}
	
	@OnlyIn(value = Dist.CLIENT)
	private boolean setWeather(boolean chuva,PlayerEntity player) {
		if(!chuva) {	
			//Thunder
			player.world.setRainStrength(1);
			player.world.getWorldInfo().setClearWeatherTime(0);
			player.world.getWorldInfo().setRainTime(6000);
			player.world.getWorldInfo().setThunderTime(6000);
			player.world.getWorldInfo().setRaining(true);
			player.world.getWorldInfo().setThundering(true);
			
			return false;
										
		}else {
			//Clear Weather
			player.world.setRainStrength(0);
			player.world.getWorldInfo().setClearWeatherTime(6000);
			player.world.getWorldInfo().setRainTime(0);
			player.world.getWorldInfo().setThunderTime(0);
			player.world.getWorldInfo().setRaining(false);
			player.world.getWorldInfo().setThundering(false);												
			
			return true;
		}
	}
}
