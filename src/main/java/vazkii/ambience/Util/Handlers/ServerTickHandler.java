package vazkii.ambience.Util.Handlers;

public class ServerTickHandler {

	int waitTime = 0;
/*
	@SubscribeEvent
	public void onServerTick(ServerTickEvent event) { // this
		// most certainly WILL fire, even in single player, see for yourself:
		// Sync data betwen all the players when player create a new area
		if (Ambience.sync) {
			waitTime++;

			if (waitTime > 50) {
				waitTime = 0;
				Ambience.sync = false;

				NBTTagCompound nbt = WorldData.SerializeThis(Ambience.getWorldData().listAreas);
				nbt.setBoolean("sync", true);

				NetworkHandler4.sendToAll(new MyMessage4(nbt));
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

			if (data.listAreas != null)
				Ambience.getWorldData().listAreas = data.listAreas;

			if (data.listAreas.size() > 0) {
				NBTTagCompound nbt = WorldData.SerializeThis(Ambience.getWorldData().listAreas);
				nbt.setBoolean("sync", true);

				NetworkHandler4.sendToClient(new MyMessage4(nbt), (EntityPlayerMP) event.player);
			}
		});
	}*/
}
