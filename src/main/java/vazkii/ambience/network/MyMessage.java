package vazkii.ambience.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.ambience.Ambience;
import vazkii.ambience.Util.WorldData;
import vazkii.ambience.World.Biomes.Area;


public class MyMessage{
	
private CompoundNBT data;
	
	public MyMessage() {
	}
	public MyMessage(PacketBuffer buf) {
	    this.data = buf.readCompoundTag();
	}
	
	public MyMessage(CompoundNBT Data) {
		this.data = Data;
	}
	
	public void encode(PacketBuffer buf) {
	    buf.writeCompoundTag(data);
	}
	
    public void handle(Supplier<NetworkEvent.Context> context) {
    	
    	NetworkEvent.Context ctx = context.get();
    	//System.out.println(ctx.getDirection() + " Side="+FMLEnvironment.dist);
    	 

    	// The value that was sent
    	Area area = Area.DeSerialize(data);
    	 
    	context.get().enqueueWork(() -> {
    		
    		//if (ctx.getDirection().getReceptionSide().isClient() && ctx.getDirection().getOriginationSide().isServer()) {
    		//}
    			//System.out.println(ctx.getDirection());
    		    	
    		//
    		//SERVER SIDE
    		//
    		if(FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
    			ServerWorld world=ctx.getSender().server.getWorld(DimensionType.getById(area.getDimension()));
    		
    			WorldData data = new WorldData().GetArasforWorld(world);

 				switch (area.getOperation()) {
 				case CREATE:
 					data.addArea(area);
 					break;
 				case DELETE:
 					data.removeArea(area);
 					break;
 				case EDIT:
 					data.editArea(area);
 					break;
 				default:
 					data.addArea(area);
 					break;
 				}

 				data.saveData();

 				Ambience.getWorldData().listAreas = data.listAreas;
 				Ambience.sync = true;
    		}
    		
	    });
    	
    	context.get().setPacketHandled(true);    	 
    }    
}
