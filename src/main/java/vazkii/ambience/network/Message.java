package vazkii.ambience.network;

/*import java.io.IOException;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.fml.network.NetworkEvent;

public class Message implements IPacket<IServerPlayNetHandler>{

	private CompoundNBT data;
	
	public Message(PacketBuffer buf) {
	    this.data = buf.readCompoundTag();
	}
	
	public Message(CompoundNBT Data) {
		this.data = Data;
	}
	
	public void encode(PacketBuffer buf) {
	    buf.writeCompoundTag(data);
	}
	
	 void handle(Message MyMessage,Supplier<NetworkEvent.Context> context) {
	        NetworkEvent.Context ctx = context.get();
	        ctx.enqueueWork(() -> {
	            if (ctx.getDirection().getReceptionSide().isClient() && ctx.getDirection().getOriginationSide().isServer()) {
	                PlayerEntity player = Minecraft.getInstance().player;
	              
	            }
	        });
	        ctx.setPacketHandled(true);
	    }
	/*
	@Override	
	public void readPacketData(PacketBuffer buf) throws IOException {		
		data=buf.readCompoundTag();
	}

	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {		
		buf.writeCompoundTag(data);
	}

	@Override
	public void processPacket(IServerPlayNetHandler handler) {
		 //handler.processConfirmTransaction(this);
	}*/
	
	
	//TEST 	 	
   

	/*public static void handle(Supplier<NetworkEvent.Context> ctx) {
	    ctx.get().enqueueWork(() -> {
	        // Work that needs to be threadsafe (most work)
	        PlayerEntity sender = ctx.get().getSender(); // the client that sent this packet
	        // do stuff
	    });
	    ctx.get().setPacketHandled(true);
	}*/
    
   /* public void handle(Supplier<Context> ctx) {
    	ctx.get().enqueueWork(() -> {
    		  PlayerEntity sender = ctx.get().getSender(); // the client that sent this packet
	    });
    	ctx.get().setPacketHandled(true);
    }
	
    public static void handle(MyMessage msg, Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Work that needs to be threadsafe (most work)
            ServerPlayerEntity sender = ctx.get().getSender(); // the client that sent this packet
            // do stuff
        });
        ctx.get().setPacketHandled(true);
    }
}*/
