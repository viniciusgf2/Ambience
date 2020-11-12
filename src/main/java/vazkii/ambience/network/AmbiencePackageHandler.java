package vazkii.ambience.network;

import java.util.function.BiConsumer;

import com.google.common.base.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.network.simple.SimpleChannel.MessageBuilder;
import vazkii.ambience.Ambience;

public class AmbiencePackageHandler {

	/*private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
	    new ResourceLocation(Ambience.MODID, "main"),
	    () -> PROTOCOL_VERSION,
	    PROTOCOL_VERSION::equals,
	    PROTOCOL_VERSION::equals
	);
	*/
	
	private static final String PROTOCOL_VERSION = Integer.toString(1);
	public static SimpleChannel INSTANCE;

	static int id = 0;
	public static void register()
	{
		INSTANCE = NetworkRegistry.ChannelBuilder
				.named(new ResourceLocation(Ambience.MODID, "main"))
				.clientAcceptedVersions(PROTOCOL_VERSION::equals)
				.serverAcceptedVersions(PROTOCOL_VERSION::equals)
				.networkProtocolVersion(() -> PROTOCOL_VERSION)
				.simpleChannel();
		

		//INSTANCE.registerMessage(id++, MyMessage.class, MyMessage::encode, MyMessage::new, MyMessage::handle);
		
		INSTANCE.messageBuilder(MyMessage.class, id++)
		.decoder(MyMessage::new)
		.encoder(MyMessage::encode)
		.consumer(MyMessage::handle)
		.add();
	}
	
	public static void handle(MyMessage msg, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		ctx.get().enqueueWork(() -> {
	        // Work that needs to be threadsafe (most work)
	        PlayerEntity sender = ctx.get().getSender(); // the client that sent this packet
	        // do stuff
	        
	       // System.out.println(context.getDirection());
	    });
	    ctx.get().setPacketHandled(true);
	}
		
	public static void sendToServer(MyMessage message) {				
		INSTANCE.sendToServer(message);
	}
		
	public static void sendToClient(MyMessage message, ServerPlayerEntity player) {
								
		INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
	}
	
	public static void sendToAll(MyMessage message) {
		// Sending to all connected players
		INSTANCE.send(PacketDistributor.ALL.noArg(), message);		
		
	}	
}


