package vazkii.ambience.Util.Handlers;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class EventHandlersServer {

	@SubscribeEvent
	public void onPlayerTick(TickEvent.ServerTickEvent event) {
		System.out.println("oi");
	}
}
