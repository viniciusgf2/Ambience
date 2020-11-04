package vazkii.ambience.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.ambience.AmbienceConfig;
import vazkii.ambience.Util.RegistryHandler;
import vazkii.ambience.Util.Utils;
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

		Ocarina Ocarina=RegistryHandler.Ocarina.get();
				
		context.get().enqueueWork(() -> {

			//
			// SERVER SIDE
			//
			// if(FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
			if (ctx.getDirection() == NetworkDirection.PLAY_TO_SERVER) {

				PlayerEntity player=ctx.getSender();
				
				if(data.contains("actualPressedKeys")) {
					int key_id=data.getInt("keyPressed");
					
					//Converts the received string list keys to a Integer list and sets to the Ocarina
					String[] keysList= data.getString("actualPressedKeys").split(",");					
					List<Integer> actualPressedKeys = new ArrayList<Integer>();					
					for(String key :keysList) {
						if(key!="")
							actualPressedKeys.add(Integer.parseInt(key));
					}					
					//Ocarina.actualPressedKeys=actualPressedKeys;
					
					if (data.getBoolean("playing") & !data.getBoolean("runningCommand")) {
						if (key_id != -1 & actualPressedKeys.size() == 1) {
							//Ocarina.playNote(key_id, (PlayerEntity) player);	
							
							player.world.playSound(player, player.getPosition(),
									ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambience:ocarina" + key_id)),
									SoundCategory.BLOCKS, 0.5f, 1);
						}
					}
					
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
			else if (data.contains("setDayTime")) {

					if (player.experienceTotal >= 30) {
						decreaseExp(player, 30);
						
						if (AmbienceConfig.COMMON.sunsong_enabled.get())
							Ocarina.setDayTime = data.getBoolean("setDayTime");
						else
							Ocarina.setDayTime = false;
					}

					Ocarina.songName = data.getString("songName");
					Ocarina.hasMatch = true;
					Ocarina.runningCommand = true;
					Ocarina.pos = Utils.NBTtoBlockPos(data.getCompound("pos"));

					//Damages the Ocarina on Play
					ItemStack itemstack = player.getHeldItem(player.getActiveHand());
					itemstack.damageItem(1, player, (damage) -> {
						damage.sendBreakAnimation(player.getActiveHand());

						Ocarina.stoopedPlayedFadeOut = 100;
						Ocarina.playing = false;
					});

				}
				else if(data.contains("setWeather")) {
						
					if (player.experienceTotal >= 35) {
						decreaseExp(player,35);
						
						if(AmbienceConfig.COMMON.songofstorms_enabled.get())
						{
							boolean chuva=false;
							if(!player.world.isRaining() & !player.world.isThundering()) {	
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
							
						}
					}
					
					//Damages the Ocarina on Play
					ItemStack itemstack = player.getHeldItem(player.getActiveHand());
					itemstack.damageItem(1, player, (damage) -> {
						damage.sendBreakAnimation(player.getActiveHand());

						Ocarina.stoopedPlayedFadeOut = 100;
						Ocarina.playing = false;
					});
					
					Ocarina.hasMatch=true;
					Ocarina.songName=data.getString("songName");
					Ocarina.runningCommand=true;
					Ocarina.pos=Utils.NBTtoBlockPos(data.getCompound("pos"));
				}else if(data.contains("setFireResistance")) {					
					Ocarina.hasMatch=true;
					Ocarina.songName=data.getString("songName");
					Ocarina.runningCommand=true;
					Ocarina.pos=Utils.NBTtoBlockPos(data.getCompound("pos"));
					
					//Damages the Ocarina on Play
					ItemStack itemstack = player.getHeldItem(player.getActiveHand());
					itemstack.damageItem(1, player, (damage) -> {
						damage.sendBreakAnimation(player.getActiveHand());

						Ocarina.stoopedPlayedFadeOut = 100;
						Ocarina.playing = false;
					});
					
					if (player.experienceTotal >= 25) {
						decreaseExp(player,25);
						if(AmbienceConfig.COMMON.bolerooffire_enabled.get())
						{
							player.addPotionEffect(new EffectInstance(Effects.GLOWING, 60*20, 3));
							player.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 90*20, 2));
						}
					}					
				}
				else if(data.contains("callHorse"))
				{			
					Ocarina.hasMatch=true;
					Ocarina.songName=data.getString("songName");
					Ocarina.runningCommand=true;
					Ocarina.pos=Utils.NBTtoBlockPos(data.getCompound("pos"));
					
					BlockPos pos = Utils.NBTtoBlockPos(data.getCompound("pos"));
					
					//Damages the Ocarina on Play
					ItemStack itemstack = player.getHeldItem(player.getActiveHand());
					itemstack.damageItem(1, player, (damage) -> {
						damage.sendBreakAnimation(player.getActiveHand());

						Ocarina.stoopedPlayedFadeOut = 100;
						Ocarina.playing = false;
					});
					
					if (player.experienceTotal >= 15) {
						
						if(AmbienceConfig.COMMON.horsesong_enabled.get())
						{
							List<Entity> entities =player.world.getEntitiesWithinAABB(Entity.class,	new AxisAlignedBB(pos.getX() - 128, pos.getY() - 64, pos.getZ() - 128, pos.getX() + 128,pos.getY() + 64, pos.getZ() + 128));
							
							for (Entity entity : entities) {
								if (entity instanceof HorseEntity) {
		
									HorseEntity horse = ((HorseEntity) entity);
									if(horse.getOwnerUniqueId() != null)
									if (horse.getOwnerUniqueId().equals(player.getUniqueID())) {
										
										decreaseExp(player,15);		
										
										Ocarina.horseName=horse.getName().getFormattedText();
										
										int dir = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
										int dirX=0;
										int dirZ=0;
										switch (dir) {
											case 0: dirZ =7;break;//South
											case 1: dirX =-7;break;//west
											case 2: dirZ =-7;break;//North
											case 3: dirX =7;break;//east
										}
										
										Vec3d vector = new Vec3d(pos.getX()+dirX, 256, pos.getZ()+dirZ);
										BlockRayTraceResult rayTraceResult = player.world
												.rayTraceBlocks(new RayTraceContext(vector, vector.add(new Vec3d(0, 1, 0).scale(-256)),
														RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.ANY, player));
		
										
		
										double distance = Math.sqrt(entity.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()));
										if(distance>20 & distance!=0) 
										{
										
										horse.setPositionAndUpdate(rayTraceResult.getPos().getX(), rayTraceResult.getPos().getY()+2,
												rayTraceResult.getPos().getZ());
										}
										
										break;
									}
								}
							}
						}
					}
				}
				else if(data.contains("playMusic")) {
					player.world.playSound(player, Utils.NBTtoBlockPos(data.getCompound("pos")),
							ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(data.getString("playMusic"))),
							SoundCategory.BLOCKS, 1, 1);
				}else if(data.contains("setLightVision")) {
					Ocarina.hasMatch=true;
					Ocarina.songName=data.getString("songName");
					Ocarina.runningCommand=true;
					Ocarina.pos=Utils.NBTtoBlockPos(data.getCompound("pos"));
					
					//Damages the Ocarina on Play
					ItemStack itemstack = player.getHeldItem(player.getActiveHand());
					itemstack.damageItem(1, player, (damage) -> {
						damage.sendBreakAnimation(player.getActiveHand());

						Ocarina.stoopedPlayedFadeOut = 100;
						Ocarina.playing = false;
					});
					
					if (player.experienceTotal >= 25) {
						decreaseExp(player,25);
						if(AmbienceConfig.COMMON.preludeoflight_enabled.get())
						{
							player.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 180*20, 2));
						}
					}
				}else if(data.contains("setWaterBreathe")) {
					Ocarina.hasMatch=true;
					Ocarina.songName=data.getString("songName");
					Ocarina.runningCommand=true;
					Ocarina.pos=Utils.NBTtoBlockPos(data.getCompound("pos"));
					
					//Damages the Ocarina on Play
					ItemStack itemstack = player.getHeldItem(player.getActiveHand());
					itemstack.damageItem(1, player, (damage) -> {
						damage.sendBreakAnimation(player.getActiveHand());

						Ocarina.stoopedPlayedFadeOut = 100;
						Ocarina.playing = false;
					});
					
					if (player.experienceTotal >= 30) {
						decreaseExp(player,30);
						if(AmbienceConfig.COMMON.serenadeofwater.get())
						{
							player.addPotionEffect(new EffectInstance(Effects.WATER_BREATHING, 300*20, 2));
						}
					}
				}else if(data.contains("heal")) {
					Ocarina.hasMatch=true;
					Ocarina.songName=data.getString("songName");
					Ocarina.runningCommand=true;
					Ocarina.pos=Utils.NBTtoBlockPos(data.getCompound("pos"));
					
					//Damages the Ocarina on Play
					ItemStack itemstack = player.getHeldItem(player.getActiveHand());
					itemstack.damageItem(1, player, (damage) -> {
						damage.sendBreakAnimation(player.getActiveHand());

						Ocarina.stoopedPlayedFadeOut = 100;
						Ocarina.playing = false;
					});
					
					if (player.experienceTotal >= 35) {
						decreaseExp(player,35);
						if(AmbienceConfig.COMMON.minuetofforest.get())
						{
							player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 90*20, 1));
						}
					}
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
				else if (data.contains("ocarinaBreak")) {
					Ocarina.stoopedPlayedFadeOut = 0;
					Ocarina.playing = false;
					Ocarina.hasMatch=false;
					Ocarina.delayMatch=0;
					Ocarina.runningCommand=false;
				}
			}
		});

		context.get().setPacketHandled(true);
	}
	
	@OnlyIn(value = Dist.CLIENT)
	private boolean setWeather(boolean chuva,PlayerEntity player) {
		if(!chuva) {	
			//Thunder
			//player.world.setRainStrength(1);
			player.world.getWorldInfo().setClearWeatherTime(0);
			player.world.getWorldInfo().setRainTime(6000);
			player.world.getWorldInfo().setThunderTime(6000);
			player.world.getWorldInfo().setRaining(true);
			player.world.getWorldInfo().setThundering(true);
			
			return false;
										
		}else {
			//Clear Weather
			//player.world.setRainStrength(0);
			player.world.getWorldInfo().setClearWeatherTime(6000);
			player.world.getWorldInfo().setRainTime(0);
			player.world.getWorldInfo().setThunderTime(0);
			player.world.getWorldInfo().setRaining(false);
			player.world.getWorldInfo().setThundering(false);												
			
			return true;
		}
	}
	
	/** Decreases player's experience properly */
	public static void decreaseExp(PlayerEntity player, float amount)
	{
	        if (player.experienceTotal - amount <= 0)
	        {
	            player.experienceLevel = 0;
	            player.experience = 0;
	            player.experienceTotal = 0;
	            return;
	        }
	        
	        player.experienceTotal -= amount;

	        if (player.experience * (float)player.xpBarCap() <= amount)
	        {
	        	amount -= player.experience * (float)player.xpBarCap();
	        	player.experience = 1.0f;
	        	player.experienceLevel--;
	        }

	        while (player.xpBarCap() < amount)
	        {
	        	amount -= player.xpBarCap();
	            player.experienceLevel--;
	        }
	        
	        player.experience -= amount / (float)player.xpBarCap();
	}
}
