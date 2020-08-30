package vazkii.ambience.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.ambience.Ambience;
import vazkii.ambience.Screens.SpeakerContainer;
import vazkii.ambience.Util.WorldData;
import vazkii.ambience.World.Biomes.Area;
import vazkii.ambience.World.Biomes.Area.Operation;
import vazkii.ambience.blocks.AlarmTileEntity;
import vazkii.ambience.blocks.SpeakerTileEntity;
import vazkii.ambience.items.Soundnizer;

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

		context.get().enqueueWork(() -> {

		
			//
			// SERVER SIDE
			//
			// if(FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
			if (ctx.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
				// This is the player the packet was sent to the server from

				CompoundNBT EventSound = data;
				ServerWorld world = ctx.getSender().server.getWorld(DimensionType.getById(data.getInt("dimension")));

				//Sync the clicked alarm or speaker to the server
				Soundnizer.clickedSpeakerOrAlarm=EventSound.getBoolean("ClickedSpeakerOrAlarm");
							
				
				//*****************
				/*if(EventSound.getString("InterMod") != null) {			
					((PlayerEntity)context.get().getSender()).sendStatusMessage((ITextComponent) new StringTextComponent("externalEvent"),(true));
				}*/				
				//******************
				
				
				// Save the speaker gui configs
				if (EventSound.getString("SoundEvent") != null & !EventSound.getString("SoundEvent").isEmpty()) {

					if(!SpeakerContainer.isAlarm) {
						BlockPos pos = new BlockPos(EventSound.getInt("x"), EventSound.getInt("y"), EventSound.getInt("z"));
	
						((SpeakerTileEntity) world.getTileEntity(pos)).selectedSound = EventSound.getString("SoundEvent");
						((SpeakerTileEntity) world.getTileEntity(pos)).delay = EventSound.getInt("delay");
						((SpeakerTileEntity) world.getTileEntity(pos)).loop = EventSound.getBoolean("loop");
						((SpeakerTileEntity) world.getTileEntity(pos)).distance = EventSound.getInt("distance");
						((SpeakerTileEntity) world.getTileEntity(pos)).countPlay = 0;
						((SpeakerTileEntity) world.getTileEntity(pos)).sync = true;
	
						context.get().setPacketHandled(true);
					}
				}

				// Save the alarm gui configs
				if (EventSound.getString("SoundEvent") != null & !EventSound.getString("SoundEvent").isEmpty()) {

					if(SpeakerContainer.isAlarm) {
						BlockPos pos = SpeakerContainer.pos;
						//BlockPos pos = new BlockPos(EventSound.getInt("x"), EventSound.getInt("y"), EventSound.getInt("z"));
						
						((AlarmTileEntity) world.getTileEntity(pos)).selectedSound = EventSound.getString("SoundEvent");
						((AlarmTileEntity) world.getTileEntity(pos)).delay = EventSound.getInt("delay");
						((AlarmTileEntity) world.getTileEntity(pos)).loop = EventSound.getBoolean("loop");
						((AlarmTileEntity) world.getTileEntity(pos)).distance = EventSound.getInt("distance");
						((AlarmTileEntity) world.getTileEntity(pos)).countPlay = 0;
						((AlarmTileEntity) world.getTileEntity(pos)).sync = true;
	
						context.get().setPacketHandled(true);
					}
				}

			

				if (EventSound.getString("Name") != null & EventSound.getString("Name")!="") {
					// The value that was sent
					Area area = Area.DeSerialize(data);
					world = ctx.getSender().server.getWorld(DimensionType.getById(area.getDimension()));

					WorldData data = new WorldData().GetArasforWorld(world);
					CompoundNBT updatedAreas=null;
					switch (area.getOperation()) {
					case CREATE:
						data.addArea(area);
						updatedAreas = WorldData.SerializeThis(Ambience.getWorldData().listAreas);
						AmbiencePackageHandler.sendToAll(new MyMessage(updatedAreas));	
						// Clear selected Area
						ClearSelection(ctx);
						/*Ambience.selectedArea = new Area("Area1");
						Ambience.previewArea = new Area("Area1");
						Ambience.selectedArea.setOperation(Operation.SELECT);

						AmbiencePackageHandler.sendToClient(new MyMessage(Ambience.selectedArea.SerializeThis()),ctx.getSender());
						*/
						
						break;
					case DELETE:
						data.removeArea(area);
						updatedAreas = WorldData.SerializeThis(Ambience.getWorldData().listAreas);
						AmbiencePackageHandler.sendToAll(new MyMessage(updatedAreas));
									
						// Clear selected Area
						ClearSelection(ctx);
					/*	Ambience.selectedArea = new Area("Area1");
						Ambience.previewArea = new Area("Area1");
						Ambience.selectedArea.setOperation(Operation.SELECT);

						AmbiencePackageHandler.sendToClient(new MyMessage(Ambience.selectedArea.SerializeThis()),ctx.getSender());
						*/
						
						break;
					case EDIT:
						data.editArea(area);	
						
						updatedAreas = WorldData.SerializeThis(Ambience.getWorldData().listAreas);
						AmbiencePackageHandler.sendToAll(new MyMessage(updatedAreas));
						break;
					case SELECT:
						Ambience.selectedArea = area;
						Ambience.previewArea = area;
						Soundnizer.BlockName= EventSound.getString("BlockName");
						// envia a posição selecionada para o server
						Ambience.selectedArea.setOperation(Operation.SELECT);
						Ambience.selectedArea.setName("Area1");

						if (Ambience.selectedArea.getPos1() != null)
							Ambience.selectedArea.setPos1(new Vec3d(Ambience.selectedArea.getPos1().getX(),
									Ambience.selectedArea.getPos1().getY(), Ambience.selectedArea.getPos1().getZ()));

						Ambience.selectedArea.setPos2(new Vec3d(area.getPos2().getX(), area.getPos2().getY(), area.getPos2().getZ()));
						Ambience.selectedArea.setPos1(new Vec3d(area.getPos1().getX(), area.getPos1().getY(), area.getPos1().getZ()));
						Ambience.selectedArea.setInstantPlay(false);
						Ambience.selectedArea.setPlayAtNight(false);
						Ambience.selectedArea.setSelectedBlock(area.getSelectedBlock());
						AmbiencePackageHandler.sendToAll(new MyMessage(Ambience.selectedArea.SerializeThis()));
						
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
				
				if(data.getString("op")=="") {
					// Update the informations on the client when opening the Speaker Screen
					 if (EventSound.getString("selectedSound") != null /*& !EventSound.getString("selectedSound").isEmpty()*/) {
							SpeakerContainer.selectedSound = EventSound.getString("selectedSound");
							SpeakerContainer.delay = EventSound.getInt("delay");
							SpeakerContainer.loop = EventSound.getBoolean("loop");
							SpeakerContainer.distance = EventSound.getFloat("distance");
							SpeakerContainer.dimension = EventSound.getInt("dimension");
							SpeakerContainer.pos = new BlockPos(EventSound.getCompound("pos").getInt("x"),EventSound.getCompound("pos").getInt("y"), EventSound.getCompound("pos").getInt("z"));
							SpeakerContainer.isAlarm = EventSound.getBoolean("isAlarm");
							context.get().setPacketHandled(true);
							// }
	
							// Stops the playing sound on the client
							if (EventSound.getString("stop").contains("stop")) {
								Minecraft.getInstance().getSoundHandler().stop(new ResourceLocation(EventSound.getString("sound")), SoundCategory.NEUTRAL);
							}
					 }
				 }
				 
				 if (EventSound.getString("Name") != null) {
						Area area = Area.DeSerialize(data);
						
						if(EventSound.getInt("lenght") != 0)
						{						
								Ambience.getWorldData().listAreas = Area.DeSerializeList(data);
								if(Ambience.selectedArea!=null)
									Ambience.selectedArea.resetSelection();							
							
						}else {
						
							switch (area.getOperation()) {						
								case OPENEDIT:
									vazkii.ambience.Screens.EditAreaScreen.currentArea=area;
									if(Ambience.previewArea.getName()=="Area1")
										Ambience.previewArea = new Area("Area1");
									break;
								case EDIT:
									area.setOperation(Operation.EDIT);
									AmbiencePackageHandler.sendToServer(new MyMessage(area.SerializeThis()));
									break;
								case SELECT:Ambience.selectedArea = area;
									Ambience.previewArea = area;
									break;
							}
						}
				 }
			}
		});

		context.get().setPacketHandled(true);
	}
	
	private void ClearSelection(NetworkEvent.Context ctx) {
		// Clear selected Area
		Ambience.selectedArea = new Area("Area1");
		Ambience.previewArea = new Area("Area1");
		Ambience.selectedArea.setOperation(Operation.SELECT);

		AmbiencePackageHandler.sendToClient(new MyMessage(Ambience.selectedArea.SerializeThis()),ctx.getSender());
		
	}
}
