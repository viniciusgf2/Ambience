package vaskii.ambience.network4;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import vaskii.ambience.Init.ItemInit;
import vaskii.ambience.objects.items.Ocarina;
import vazkii.ambience.AmbienceConfig;
import vazkii.ambience.Util.Utils;

public class OcarinaServerHandler implements IMessageHandler<MyMessage4, IMessage> {
	// Do note that the default constructor is required, but implicitly defined in
	// this case

	@Override
	public IMessage onMessage(MyMessage4 message, MessageContext ctx) {
		// This is the player the packet was sent to the server from
		EntityPlayerMP player = ctx.getServerHandler().player;

		NBTTagCompound data = message.getToSend();
		

		Ocarina Ocarina=(Ocarina) ItemInit.itemOcarina;
		
		if(data.hasKey("actualPressedKeys")) {
			int key_id=data.getInteger("keyPressed");
			
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
			
		}else if(data.hasKey("resetVariables")) {
			Ocarina.pressedKeys.clear();
        	Ocarina.old_key_id=-1;
        	Ocarina.runningCommand=false;
        	//Ocarina.songName="";
		}
		else if(data.hasKey("setDayTime")) {
			
			if (player.experienceTotal >= 30) {
				decreaseExp(player, 30);
				
				if(AmbienceConfig.OcarinaMusics.sunsong_enabled)					
					Ocarina.setDayTime=data.getBoolean("setDayTime");	
				else	
					Ocarina.setDayTime=false;
			}
			
			//Damages the Ocarina on Play
			ItemStack itemstack = player.getHeldItem(player.getActiveHand());
			itemstack.damageItem(1, player);
			breakOcarina(itemstack,Ocarina,player);
					
			Ocarina.songName=data.getString("songName");
			Ocarina.hasMatch=true;
			Ocarina.runningCommand=true;
			Ocarina.pos=Utils.NBTtoBlockPos(data.getCompoundTag("pos"));
			
		}
		else if(data.hasKey("setWeather")) {
			
			if (player.experienceTotal >= 35) 
			{
				decreaseExp(player,35);
				
				if(AmbienceConfig.OcarinaMusics.songofstorms_enabled)
				{
					boolean chuva=false;
					if(!player.world.isRaining() & !player.world.isThundering()) {	
						//Thunder
						player.world.getWorldInfo().setCleanWeatherTime(0);
						player.world.getWorldInfo().setRainTime(6000);
						player.world.getWorldInfo().setThunderTime(6000);
						player.world.getWorldInfo().setRaining(true);
						player.world.getWorldInfo().setThundering(true);
						//player.world.setRainStrength(1);
						chuva=false;
													
					}else {
						//Clear Weather
					//	player.world.setRainStrength(0);
						player.world.getWorldInfo().setCleanWeatherTime(6000);
						player.world.getWorldInfo().setRainTime(0);
						player.world.getWorldInfo().setThunderTime(0);
						player.world.getWorldInfo().setRaining(false);
						player.world.getWorldInfo().setThundering(false);	
						
						chuva=true;
					}	
	
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setBoolean("setWeather", chuva);				
					OcarinaNetworkHandler.sendToAll(new MyMessage4(nbt));
				}
			}
			
			//Damages the Ocarina on Play
			ItemStack itemstack = player.getHeldItem(player.getActiveHand());
			itemstack.damageItem(1, player);
			breakOcarina(itemstack,Ocarina,player);
			
			Ocarina.hasMatch=true;
			Ocarina.songName=data.getString("songName");
			Ocarina.runningCommand=true;
			Ocarina.pos=Utils.NBTtoBlockPos(data.getCompoundTag("pos"));
		}else if(data.hasKey("setFireResistance")) {					
			Ocarina.hasMatch=true;
			Ocarina.songName=data.getString("songName");
			Ocarina.runningCommand=true;
			Ocarina.pos=Utils.NBTtoBlockPos(data.getCompoundTag("pos"));
			
			//Damages the Ocarina on Play
			ItemStack itemstack = player.getHeldItem(player.getActiveHand());
			itemstack.damageItem(1, player);
			breakOcarina(itemstack,Ocarina,player);
			
			if (player.experienceTotal >= 25) 
			{
				decreaseExp(player,25);
				
				if(AmbienceConfig.OcarinaMusics.bolerooffire_enabled)
				{
					player.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 60 * 20, 3));
					player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 90 * 20, 2));
				}
			}
		}
		else if (data.hasKey("callHorse")) {
			Ocarina.hasMatch = true;
			Ocarina.songName = data.getString("songName");
			Ocarina.runningCommand = true;
			Ocarina.pos = Utils.NBTtoBlockPos(data.getCompoundTag("pos"));
			
			
			//Damages the Ocarina on Play
			ItemStack itemstack = player.getHeldItem(player.getActiveHand());
			itemstack.damageItem(1, player);						
			breakOcarina(itemstack,Ocarina,player);
						
			BlockPos pos = Utils.NBTtoBlockPos(data.getCompoundTag("pos"));

			if (AmbienceConfig.OcarinaMusics.horsesong_enabled) {
				List<EntityHorse> entities = player.world.getEntitiesWithinAABB(EntityHorse.class,
						new AxisAlignedBB(pos.getX() - 128, pos.getY() - 64, pos.getZ() - 128, pos.getX() + 128,
								pos.getY() + 64, pos.getZ() + 128));

				for (Entity entity : entities) {

					EntityHorse horse = ((EntityHorse) entity);
					
					if(horse.getOwnerUniqueId() != null)
					if (horse.getOwnerUniqueId() != null && horse.getOwnerUniqueId().equals(player.getUniqueID())) {
						
						decreaseExp(player,15);		
						
						Ocarina.horseName = horse.getName();

						int dir = MathHelper.floor((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
						int dirX = 0;
						int dirZ = 0;
						switch (dir) {
							case 0:	dirZ = 7;break;// South
							case 1:	dirX = -7;break;// west
							case 2:	dirZ = -7;break;// North
							case 3:	dirX = 7;break;// east
						}

						Vec3d vector = new Vec3d(pos.getX() + dirX, 256, pos.getZ() + dirZ);

						RayTraceResult rayTraceResult = player.world.rayTraceBlocks(vector,	vector.add(new Vec3d(0, 1, 0).scale(-256)), true);

						double distance = Math.sqrt(entity.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()));
						if (distance > 20 & distance != 0) {

							horse.setPositionAndUpdate(rayTraceResult.hitVec.x, rayTraceResult.hitVec.y + 2,rayTraceResult.hitVec.z);
						}

						break;
					}
				}
			}
		}
		else if(data.hasKey("playMusic")) {
			player.world.playSound(player, Utils.NBTtoBlockPos(data.getCompoundTag("pos")),
					ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(data.getString("playMusic"))),
					SoundCategory.BLOCKS, 1, 1);
		}else if(data.hasKey("setLightVision")) {
			Ocarina.hasMatch=true;
			Ocarina.songName=data.getString("songName");
			Ocarina.runningCommand=true;
			Ocarina.pos=Utils.NBTtoBlockPos(data.getCompoundTag("pos"));
			
			//Damages the Ocarina on Play
			ItemStack itemstack = player.getHeldItem(player.getActiveHand());
			itemstack.damageItem(1, player);
			breakOcarina(itemstack,Ocarina,player);
			
			if (player.experienceTotal >= 25) 
			{
				decreaseExp(player,25);
				if(AmbienceConfig.OcarinaMusics.preludeoflight_enabled)
				{
					player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 180 * 20, 2));
				}
			}
		}else if(data.hasKey("setWaterBreathe")) {
			Ocarina.hasMatch=true;
			Ocarina.songName=data.getString("songName");
			Ocarina.runningCommand=true;
			Ocarina.pos=Utils.NBTtoBlockPos(data.getCompoundTag("pos"));
			
			//Damages the Ocarina on Play
			ItemStack itemstack = player.getHeldItem(player.getActiveHand());
			itemstack.damageItem(1, player);
			breakOcarina(itemstack,Ocarina,player);
			
			if(AmbienceConfig.OcarinaMusics.serenadeofwater)
			{
				player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 300 * 20, 2));				
			}
		}else if(data.hasKey("heal")) {
			Ocarina.hasMatch=true;
			Ocarina.songName=data.getString("songName");
			Ocarina.runningCommand=true;
			Ocarina.pos=Utils.NBTtoBlockPos(data.getCompoundTag("pos"));
			
			//Damages the Ocarina on Play
			ItemStack itemstack = player.getHeldItem(player.getActiveHand());
			itemstack.damageItem(1, player);
			breakOcarina(itemstack,Ocarina,player);
			
			if (player.experienceTotal >= 30) {
				decreaseExp(player,30);
				
				if(AmbienceConfig.OcarinaMusics.minuetofforest)
				{
					player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 90 * 20, 1));
				}
			}
		}
		
		return null;
	}
	
	private void breakOcarina(ItemStack item,Ocarina ocarina,EntityPlayerMP player ) {
		int damage= item.getItemDamage();
		
		if(damage<=0) {			
			ocarina.stoopedPlayedFadeOut = 0;
			ocarina.playing = false;
			ocarina.hasMatch=false;
			ocarina.delayMatch=0;
			ocarina.runningCommand=false;
			ocarina.songName="";
			
			//Update the client that the ocarina has broken
			NBTTagCompound nbt = new NBTTagCompound();
			nbt = new NBTTagCompound();
			nbt.setBoolean("ocarinaBreak", true);
			OcarinaNetworkHandler.sendToClient(new MyMessage4(nbt), player);			
		}
	}
	
	/** Decreases player's experience properly */
	public static void decreaseExp(EntityPlayer player, float amount)
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
