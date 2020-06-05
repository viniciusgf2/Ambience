package vaskii.ambience.network4;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vaskii.ambience.objects.blocks.Speaker;
import vaskii.ambience.objects.blocks.SpeakerTileEntity;
import vazkii.ambience.Ambience;
import vazkii.ambience.Util.WorldData;
import vazkii.ambience.World.Biomes.Area;

public class MyMessageHandler4 implements IMessageHandler<MyMessage4, IMessage> {
	// Do note that the default constructor is required, but implicitly defined in
	// this case

	@Override
	public IMessage onMessage(MyMessage4 message, MessageContext ctx) {
		// This is the player the packet was sent to the server from
		EntityPlayerMP serverPlayer = ctx.getServerHandler().player;

		NBTTagCompound EventSound = message.getToSend();

		// Save the speaker gui configs
		if (EventSound.getString("SoundEvent") != null & !EventSound.getString("SoundEvent").isEmpty()) {

			BlockPos pos = new BlockPos(EventSound.getInteger("x"), EventSound.getInteger("y"),	EventSound.getInteger("z"));

			serverPlayer.getServerWorld().addScheduledTask(() -> {
				((SpeakerTileEntity) ctx.getServerHandler().player.world.getTileEntity(pos)).selectedSound = EventSound.getString("SoundEvent");
				((SpeakerTileEntity) ctx.getServerHandler().player.world.getTileEntity(pos)).delay = EventSound.getInteger("delay");
				((SpeakerTileEntity) ctx.getServerHandler().player.world.getTileEntity(pos)).loop = EventSound.getBoolean("loop");
				((SpeakerTileEntity) ctx.getServerHandler().player.world.getTileEntity(pos)).distance = EventSound.getFloat("distance");
				((SpeakerTileEntity) ctx.getServerHandler().player.world.getTileEntity(pos)).countPlay = 0;
				((SpeakerTileEntity) ctx.getServerHandler().player.world.getTileEntity(pos)).sync = true;
				
				//serverPlayer.closeScreen();
			});

			return null;
		}

		if (EventSound.getString("selectedSound") != null & !EventSound.getString("selectedSound").isEmpty()) {
			Speaker.selectedSound = EventSound.getString("selectedSound");
			Speaker.delaySound = EventSound.getInteger("delay");
			Speaker.loop = EventSound.getBoolean("loop");
			Speaker.Distance = EventSound.getFloat("distance");

			return null;
		}

		

		// The value that was sent
		NBTTagCompound amount = message.getToSend();
		Area area = Area.DeSerialize(amount);
		// Execute the action on the main server thread by adding it as a scheduled task
		serverPlayer.getServerWorld().addScheduledTask(() -> {

			WorldData data = new WorldData().GetArasforWorld(ctx.getServerHandler().player.world);

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

			// if(Ambience.getWorldData().listAreas!=null)
			// System.out.println(Ambience.getWorldData().listAreas.size());
			Ambience.getWorldData().listAreas = data.listAreas;
			// Ambience.selectedArea = area;
			Ambience.sync = true;
		});

		return null;
	}
}
