package vazkii.ambience.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.ambience.Ambience;
import vazkii.ambience.Screens.SpeakerContainer;
import vazkii.ambience.Util.WorldData;
import vazkii.ambience.World.Biomes.Area;
import vazkii.ambience.blocks.AlarmTileEntity;
import vazkii.ambience.blocks.Speaker;
import vazkii.ambience.blocks.SpeakerTileEntity;

public class MyMessage {

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
		// System.out.println(ctx.getDirection() + " Side="+FMLEnvironment.dist);

		context.get().enqueueWork(() -> {

			// if (ctx.getDirection().getReceptionSide().isClient() &&
			// ctx.getDirection().getOriginationSide().isServer()) {
			// }
			
			//
			// SERVER SIDE
			//
			// if(FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
			 if(ctx.getDirection()== NetworkDirection.PLAY_TO_SERVER) {
			// This is the player the packet was sent to the server from

			CompoundNBT EventSound = data;
			ServerWorld world = ctx.getSender().server.getWorld(DimensionType.getById(data.getInt("dimension")));
			
			// Save the speaker gui configs
			if (EventSound.getString("SoundEvent") != null & !EventSound.getString("SoundEvent").isEmpty() & !EventSound.getBoolean("isAlarm")) {

				BlockPos pos = new BlockPos(EventSound.getInt("x"), EventSound.getInt("y"), EventSound.getInt("z"));

				((SpeakerTileEntity) world.getTileEntity(pos)).selectedSound = EventSound.getString("SoundEvent");
				((SpeakerTileEntity) world.getTileEntity(pos)).delay = EventSound.getInt("delay");
				((SpeakerTileEntity) world.getTileEntity(pos)).loop = EventSound.getBoolean("loop");
				((SpeakerTileEntity) world.getTileEntity(pos)).distance = EventSound.getInt("distance");
				((SpeakerTileEntity) world.getTileEntity(pos)).countPlay = 0;
				((SpeakerTileEntity) world.getTileEntity(pos)).sync = true;

				context.get().setPacketHandled(true);
			}
			
			// Save the alarm gui configs
			if (EventSound.getString("SoundEvent") != null & EventSound.getBoolean("isAlarm") & !EventSound.getString("SoundEvent").isEmpty()) {

				BlockPos pos = new BlockPos(EventSound.getInt("x"), EventSound.getInt("y"), EventSound.getInt("z"));

				((AlarmTileEntity) world.getTileEntity(pos)).selectedSound = EventSound.getString("SoundEvent");
				((AlarmTileEntity) world.getTileEntity(pos)).delay = EventSound.getInt("delay");
				((AlarmTileEntity) world.getTileEntity(pos)).loop = EventSound.getBoolean("loop");
				((AlarmTileEntity) world.getTileEntity(pos)).distance = EventSound.getInt("distance");
				((AlarmTileEntity) world.getTileEntity(pos)).countPlay = 0;
				((AlarmTileEntity) world.getTileEntity(pos)).sync = true;

				context.get().setPacketHandled(true);
			}

			//Update the informations on the client when opening the Speaker Screen
			/*if (EventSound.getString("selectedSound") != null & !EventSound.getString("selectedSound").isEmpty()) {
				SpeakerContainer.selectedSound = EventSound.getString("selectedSound");
				SpeakerContainer.delay = EventSound.getInt("delay");
				SpeakerContainer.loop = EventSound.getBoolean("loop");
				SpeakerContainer.distance = EventSound.getFloat("distance");
				SpeakerContainer.dimension = EventSound.getInt("dimension");

				context.get().setPacketHandled(true);
			}*/

			
			if (EventSound.getString("selectedSound") == null & EventSound.getString("SoundEvent") == null) {
				// The value that was sent
				Area area = Area.DeSerialize(data);
				world = ctx.getSender().server.getWorld(DimensionType.getById(area.getDimension()));
				
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
			
			 }
			 
			 else {

					CompoundNBT EventSound = data;
				//Update the informations on the client when opening the Speaker Screen
					//if (EventSound.getString("selectedSound") != null & !EventSound.getString("selectedSound").isEmpty()) {
						SpeakerContainer.selectedSound = EventSound.getString("selectedSound");
						SpeakerContainer.delay = EventSound.getInt("delay");
						SpeakerContainer.loop = EventSound.getBoolean("loop");
						SpeakerContainer.distance = EventSound.getFloat("distance");
						SpeakerContainer.dimension = EventSound.getInt("dimension");
						SpeakerContainer.pos =new BlockPos(EventSound.getCompound("pos").getInt("x"), EventSound.getCompound("pos").getInt("y"), EventSound.getCompound("pos").getInt("z"));
						SpeakerContainer.isAlarm=EventSound.getBoolean("isAlarm");
						context.get().setPacketHandled(true);
					//}
											
					//Stops the playing sound on the client 
					if (EventSound.getString("stop").contains("stop")) {
						Minecraft.getInstance().getSoundHandler().stop(new ResourceLocation(EventSound.getString("sound")), SoundCategory.NEUTRAL);
					}
			 }
		});

		context.get().setPacketHandled(true);
	}
}
