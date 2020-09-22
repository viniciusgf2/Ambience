package vazkii.ambience.Util.Handlers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import vaskii.ambience.Init.ItemInit;
import vaskii.ambience.network4.MyMessage4;
import vaskii.ambience.network4.NetworkHandler4;
import vaskii.ambience.network4.OcarinaNetworkHandler;
import vaskii.ambience.objects.items.Horn;
import vaskii.ambience.objects.items.Ocarina;
import vazkii.ambience.Ambience;
import vazkii.ambience.Util.WorldData;
import vazkii.ambience.World.Biomes.Area;

public class ServerTickHandler {

	int waitTime=0;
	boolean settingDay = false,settingNight = false;

	@SubscribeEvent
	public void onPlayerTick(TickEvent.WorldTickEvent.PlayerTickEvent event) {

		if (Horn.fadeOutTimer > 0)
			Horn.fadeOutTimer--;

		if (Horn.fadeOutTimer > 0 & Horn.fadeOutTimer < 300) {
			((Horn) ItemInit.itemHorn).repelEntities(event.player.world, event.player, 1D);

		}

		// Set the time of the day when the Ocarina played the Sun's Song
		if (!event.player.world.isRemote) {
			World world = event.player.world;

			long time = world.getWorldTime() % 24000;
			boolean night = time > 13300 && time < 23200;

			if (Ocarina.setDayTime & night) {
				if (!settingNight) {
					settingDay = true;
					event.player.world.setWorldTime(event.player.world.getWorldTime() + 10);
				} else {
					Ocarina.setDayTime = false;

					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setBoolean("setDayTime", false);
					OcarinaNetworkHandler.sendToClient(new MyMessage4(nbt), (EntityPlayerMP) event.player);
				}
			} else if (Ocarina.setDayTime & !night) {

				if (!settingDay) {
					settingNight = true;
					event.player.world.setWorldTime(event.player.world.getWorldTime() + 10);
				} else {
					Ocarina.setDayTime = false;

					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setBoolean("setDayTime", false);
					OcarinaNetworkHandler.sendToClient(new MyMessage4(nbt), (EntityPlayerMP) event.player);
				}
			} else {
				Ocarina.setDayTime = false;
				settingDay = false;
				settingNight = false;
			}
		}
	}
	
	@SubscribeEvent
	public void onServerTick(ServerTickEvent event) { // this
		// most certainly WILL fire, even in single player, see for yourself:

		
		/*if(Speaker.sound!=null)
			if(Speaker.sound.isDonePlaying()){
				System.out.println("oi");
			}
		*/
		//if(Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(Speaker.sound)) {
			//System.out.println("oi");
			
			//Minecraft.getMinecraft().getSoundHandler().stopSound(Speaker.sound);
		//}
		
	//	if(Ambience.getWorldData().listAreas!=null) {
	//		int teste=Ambience.getWorldData().listAreas.size();
			//System.out.println(teste);
	//	}
		// Sync data betwen all the players when player create a new area		
		
		if (Ambience.sync) {

			waitTime++;
			
			if(waitTime>50) {
				waitTime=0;
			Ambience.sync = false;
						
		//	MinecraftServer server =  FMLCommonHandler.instance().getMinecraftServerInstance(); 			
		//	WorldData data = new WorldData();
		//	data.GetArasforWorld( server.getEntityWorld());
					

		    NBTTagCompound nbt= WorldData.SerializeThis(Ambience.getWorldData().listAreas);
			nbt.setBoolean("sync",true);
			
			NetworkHandler4.sendToAll(new MyMessage4(nbt));
			
			
			
			// Send the new data to all the clients
			/*if (Ambience.selectedArea != null) {
				 NetworkHandler.sendToAll(new MyMessage(Ambience.selectedArea.SerializeThis()));
				 Ambience.selectedArea = null;
			}*/

			/*
			 * MinecraftServer server =
			 * FMLCommonHandler.instance().getMinecraftServerInstance(); World world =
			 * server.getEntityWorld();
			 * 
			 * Iterator<EntityPlayer> iteratorPlayer = world.playerEntities.iterator();
			 * while (iteratorPlayer.hasNext()) { EntityPlayer player =
			 * iteratorPlayer.next(); player.setWorld(world);
			 * System.out.println("player found " + player.getDisplayNameString()); }
			 */
		}

	}
		}

	// Server Side
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {

		event.player.getServer().getWorld(0).addScheduledTask(() -> {
			Ambience.selectedArea = null;

			WorldData data = new WorldData();// WorldData.forWorld(event.player.world);
			data.GetArasforWorld(event.player.world);

			
				List<Area> areasList = new ArrayList<Area>();
				areasList.addAll(data.listAreas);

				if(data.listAreas!=null)
					Ambience.getWorldData().listAreas = data.listAreas;

				/*
				 * Iterator<Area> iterator = areasList.iterator(); while (iterator.hasNext()) {
				 * NetworkHandler4.sendToClient(new
				 * MyMessage4(iterator.next().SerializeThis()),(EntityPlayerMP) event.player); }
				 */
				// WorldData.SerializeThis(data.listAreas);

				if(data.listAreas.size()>0) {
					   NBTTagCompound nbt= WorldData.SerializeThis(Ambience.getWorldData().listAreas);
						nbt.setBoolean("sync",true);
						
					NetworkHandler4.sendToClient(new MyMessage4(nbt),(EntityPlayerMP) event.player);
				}
		});
	}
}
