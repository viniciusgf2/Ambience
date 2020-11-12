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

public class OcarinaPackageHandler {
	
	private static final String PROTOCOL_VERSION = Integer.toString(1);
	public static SimpleChannel INSTANCE;

	static int id = 0;
	public static void register()
	{
		INSTANCE = NetworkRegistry.ChannelBuilder
				.named(new ResourceLocation(Ambience.MODID, "ocarinamain"))
				.clientAcceptedVersions(PROTOCOL_VERSION::equals)
				.serverAcceptedVersions(PROTOCOL_VERSION::equals)
				.networkProtocolVersion(() -> PROTOCOL_VERSION)
				.simpleChannel();
			
		INSTANCE.messageBuilder(OcarinaMessage.class, id++)
		.decoder(OcarinaMessage::new)
		.encoder(OcarinaMessage::encode)
		.consumer(OcarinaMessage::handle)
		.add();
	}
	
	public static void handle(OcarinaMessage msg, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		ctx.get().enqueueWork(() -> {
	        // Work that needs to be threadsafe (most work)
	        PlayerEntity sender = ctx.get().getSender(); // the client that sent this packet
	        // do stuff	        
	    });
	    ctx.get().setPacketHandled(true);
	}
		
	public static void sendToServer(OcarinaMessage message) {				
		INSTANCE.sendToServer(message);
	}
		
	public static void sendToClient(OcarinaMessage message, ServerPlayerEntity player) {
								
		INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
	}
	
	public static void sendToAll(OcarinaMessage message) {
		// Sending to all connected players
		INSTANCE.send(PacketDistributor.ALL.noArg(), message);				
	}	
}


