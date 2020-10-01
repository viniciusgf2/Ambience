package vazkii.ambience.Util.Handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkGeneratorHell;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
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

public class EventHandlerServer {

	int waitTime=0;
	boolean settingDay = false,settingNight = false;

/*	@SubscribeEvent
	public void dungeonDetect(PopulateChunkEvent e)
	{
		
		EntityPlayer player= Minecraft.getMinecraft().player;// w.playerEntities.get(0);
		BlockPos pos = player.getPosition();
			
		World world = e.getWorld();	
		IChunkProvider prov = world.getChunkProvider();
		
		if (prov instanceof ChunkProviderServer) {
			ChunkProviderServer serverProv = (ChunkProviderServer) prov;
			
				System.out.println(serverProv.isInsideStructure(world, "Fortress", pos));			
			
		}
		
		
		
		//--------------------------------------------------
		
		World w = e.getWorld();
		if(w.isRemote)
			return;
		Chunk chunk = w.getChunkFromChunkCoords(e.getChunkX(), e.getChunkZ() );
		IChunkGenerator gen = e.getGenerator();
		

		EntityPlayer player= Minecraft.getMinecraft().player;// w.playerEntities.get(0);
		

		if(player!=null) {
			BlockPos pos = player.getPosition();
			boolean mineshaft = false;
			boolean stronghold = false;
			boolean netherfortress = false;
			boolean mansion = false;//has spawner in secret room here
			
			if(gen instanceof ChunkGeneratorOverworld)
			{
				ChunkGeneratorOverworld gen2 = (ChunkGeneratorOverworld)gen;
				mineshaft = gen2.isInsideStructure(w, "Mineshaft", pos);
				stronghold = gen2.isInsideStructure(w, "Stronghold", pos);
				mansion = gen2.isInsideStructure(w, "Mansion", pos);
			}
			else if(gen instanceof ChunkGeneratorHell)
			{				
				ChunkGeneratorHell gen3 = (ChunkGeneratorHell)gen;
				netherfortress = gen3.isInsideStructure(w, "Fortress", pos);
			}
			
			
			System.out.println("Inside Fortress:" +netherfortress);
		}
	}*/

	
	public String insideStructureName="";
	public String StructureName="";
	public String OldStructureName="";
	@SubscribeEvent
	public void onPlayerTick(TickEvent.WorldTickEvent.PlayerTickEvent event) {

		
		//Check if Player is inside a Structure----------------------------------------------------
		EntityPlayer player= event.player;// w.playerEntities.get(0);
		BlockPos pos = player.getPosition();
			
		World world2 = player.world;	
		IChunkProvider prov = world2.getChunkProvider();
		
		if (prov instanceof ChunkProviderServer) {
			ChunkProviderServer serverProv = (ChunkProviderServer) prov;
			
			if(serverProv.isInsideStructure(world2, "Fortress", pos)) {
				StructureName="fortress";
			}
			else if(serverProv.isInsideStructure(world2, "Stronghold", pos)) {
				StructureName="stronghold";
			}
			else if(serverProv.isInsideStructure(world2, "Mansion", pos)) {
				StructureName="mansion";
			}
			else if(serverProv.isInsideStructure(world2, "Monument", pos)) {
				StructureName="oceanMonument";
			}
			else if(serverProv.isInsideStructure(world2, "Mineshaft", pos)) {
				StructureName="mineshaft";
			}
			else if(serverProv.isInsideStructure(world2, "Temple", pos)) {
				StructureName="desertTemple";
			}
			else if(serverProv.isInsideStructure(world2, "Village", pos)) {
				StructureName="village";
			}
			else {
				StructureName="";
			}
			
		}
				
		if(!OldStructureName.equals(StructureName)) {
		
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("StructureName", StructureName);			
				
			
			if(!player.world.isRemote)
			NetworkHandler4.sendToClient(new MyMessage4(nbt), (EntityPlayerMP) player);
		}

		OldStructureName=StructureName;
		//------------------------------------------------------------------------------------------
		
		
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
