package vaskii.ambience.network4;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import vazkii.ambience.Reference;

public class OcarinaNetworkHandler {
	public static SimpleNetworkWrapper INSTANCE;

	public static void init() {
		INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID+"ocarina");
		INSTANCE.registerMessage(OcarinaServerHandler.class, MyMessage4.class, 1, Side.SERVER);	
		INSTANCE.registerMessage(OcarinaClientHandler.class, MyMessage4.class, 2, Side.CLIENT);
	}
	
	public static void sendToServer(IMessage message) {
		INSTANCE.sendToServer(message);
	}
		
	public static void sendToClient(IMessage message, EntityPlayerMP player) {
		INSTANCE.sendTo(message, player);
	}
	
	public static void sendToAll(IMessage message) {
		INSTANCE.sendToAll(message);
	}	
}
