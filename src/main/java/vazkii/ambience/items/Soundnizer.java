package vazkii.ambience.items;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.ambience.Ambience;
import vazkii.ambience.Screens.EditAreaContainer;
import vazkii.ambience.Screens.GuiContainerMod;
import vazkii.ambience.Util.Border;
import vazkii.ambience.World.Biomes.Area;
import vazkii.ambience.World.Biomes.Area.Operation;
import vazkii.ambience.network.AmbiencePackageHandler;
import vazkii.ambience.network.MyMessage;

public class Soundnizer extends ItemBase {

	public Soundnizer(int Maxdamage) {
		super(Maxdamage);

	}

	private boolean rightclick = false;
	public static boolean firstclick = false;
	public static boolean clickedSpeakerOrAlarm=true;
	public static String BlockName;
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {

		ItemStack item = playerIn.getHeldItem(handIn);

		if (Ambience.selectedArea == null)
			Ambience.selectedArea = new Area("Area1");

		
		// Client
		if (worldIn.isRemote) {								
			BlockPos Position2=null;
			RayTraceResult lookingAt = Minecraft.getInstance().objectMouseOver;
			rightclick = true;
			firstclick = true;

			String blockName = "";
			if (lookingAt.hitInfo == null && lookingAt.getType() != RayTraceResult.Type.BLOCK) {
				firstclick = false;
			} else {
				blockName = worldIn.getBlockState(((BlockRayTraceResult) lookingAt).getPos()).getBlock().getRegistryName().getPath();				
			}
			
			if (lookingAt != null && lookingAt.getType() == RayTraceResult.Type.BLOCK & !blockName.contains("speaker")	& !blockName.contains("alarm")) {
				Position2 = ((BlockRayTraceResult) lookingAt).getPos();

				if (!playerIn.isSneaking()) {
					((PlayerEntity) playerIn)
							.sendStatusMessage(
									(ITextComponent) new TranslationTextComponent(I18n.format("Soundnizer.Position")
											+ " 2 = x:" + "" + ((int) Position2.getX()) + "" + (" y:") + ""
											+ ((int) Position2.getY()) + "" + (" z:") + "" + ((int) Position2.getZ())),
									(true));

					Ambience.selectedArea.setPos2(new Vector3d(Position2.getX(), Position2.getY(), Position2.getZ()));
					Ambience.selectedArea.setDimension(playerIn.world.getDimensionKey().getLocation().getPath());
					Ambience.previewArea.setPos2(Ambience.selectedArea.getPos2());				

				} else {
					if (Ambience.selectedArea != null)
						Ambience.selectedArea.resetSelection();
				}
			}
			else if(blockName.contains("alarm") | blockName.contains("speaker") ) {
				CompoundNBT nbt = new CompoundNBT();
				nbt.putBoolean("ClickedSpeakerOrAlarm", true);
				AmbiencePackageHandler.sendToServer(new MyMessage(nbt));
			}
			

			//Border border = new Border(Ambience.selectedArea.getPos1(), Ambience.selectedArea.getPos2());
			

			if ( !playerIn.isSneaking() & !blockName.contains("alarm") & !blockName.contains("speaker")) {
				// envia a posição selecionada para o server
				Ambience.selectedArea.setOperation(Operation.SELECT);
				Ambience.selectedArea.setName("Area1");
	
				if (Ambience.selectedArea.getPos1() != null)
					Ambience.selectedArea.setPos1(new Vector3d(Ambience.selectedArea.getPos1().getX(),	Ambience.selectedArea.getPos1().getY(), Ambience.selectedArea.getPos1().getZ()));
	
				if(Position2!=null)
					Ambience.selectedArea.setPos2(new Vector3d(Position2.getX(), Position2.getY(), Position2.getZ()));
				Ambience.selectedArea.setInstantPlay(false);
				Ambience.selectedArea.setPlayAtNight(false);
				Ambience.selectedArea.setSelectedBlock(blockName);
				AmbiencePackageHandler.sendToServer(new MyMessage(Ambience.selectedArea.SerializeThis()));	
			}
			
		}

		//
		// Server
		//
		if (!worldIn.isRemote) {
					
			
			Area currentArea = Area.getPlayerStandingArea(playerIn);
			
			if(!clickedSpeakerOrAlarm ) {
				if (!playerIn.isSneaking()) {
					if ((currentArea == null & !firstclick) | Ambience.multiArea > 0 & (Ambience.selectedArea.getPos1() != null & Ambience.selectedArea.getPos2() != null
							)) {
	
							Border border = new Border(Ambience.selectedArea.getPos1(), Ambience.selectedArea.getPos2());
		
							if (Ambience.selectedArea.getPos1() != null & Ambience.selectedArea.getPos2() != null) {
								// Check if player is inside the selected area before creating
								
								if (Ambience.selectedArea.getPos1().x != 0 & Ambience.selectedArea.getPos1().y != 0
										& Ambience.selectedArea.getPos1().z != 0 & Ambience.selectedArea.getPos2().x != 0
										& Ambience.selectedArea.getPos2().y != 0 & Ambience.selectedArea.getPos2().z != 0) {
										if (border.contains(playerIn.getPositionVec())) {
													
												// Create AREA Screen
												int id = playerIn.getEntityId();
												NetworkHooks.openGui((ServerPlayerEntity) playerIn, new INamedContainerProvider() {
													@Override
													public ITextComponent getDisplayName() {
														// return (ITextComponent)new
														// StringTextComponent(I18n.format("GUI.CreateArea"));
														return (ITextComponent) new StringTextComponent("CreateArea");
													}
				
													@Override
													public Container createMenu(int i, PlayerInventory playerInventory,
															PlayerEntity playerEntity) {
				
														return new GuiContainerMod(id);
													}
												}, buf -> buf.writeInt(id));
											
										} else {
											if(Ambience.selectedArea.getSelectedBlock()=="" & !firstclick) {
												((PlayerEntity) playerIn).sendStatusMessage((ITextComponent) new StringTextComponent(
														"You must be inside a selected region to create!"), true);
											}
										}
									
									}
									else {
									// EditAreaGUI.currentArea = currentArea;
									if (Ambience.selectedArea.getSelectedBlock()=="" & currentArea!=null) {
				
										// Ambience.selectedArea = currentArea;
										currentArea.setOperation(Operation.OPENEDIT);
										currentArea.setSelectedBlock("");
										AmbiencePackageHandler.sendToClient(new MyMessage(currentArea.SerializeThis()),(ServerPlayerEntity) playerIn);
				
										int id = playerIn.getEntityId();
										NetworkHooks.openGui((ServerPlayerEntity) playerIn, new INamedContainerProvider() {
											@Override
											public ITextComponent getDisplayName() {
												return (ITextComponent) new StringTextComponent("EditArea");
											}
				
											@Override
											public Container createMenu(int i, PlayerInventory playerInventory,
													PlayerEntity playerEntity) {
				
												return new EditAreaContainer(id, currentArea);
											}
										}, buf -> buf.writeInt(id));
									}
								}
							}
						
					} else {
						// EditAreaGUI.currentArea = currentArea;
						if (!firstclick & Ambience.selectedArea.getSelectedBlock()=="") {
	
							// Ambience.selectedArea = currentArea;
							currentArea.setOperation(Operation.OPENEDIT);
							currentArea.setSelectedBlock("");
							AmbiencePackageHandler.sendToClient(new MyMessage(currentArea.SerializeThis()),(ServerPlayerEntity) playerIn);
	
							int id = playerIn.getEntityId();
							NetworkHooks.openGui((ServerPlayerEntity) playerIn, new INamedContainerProvider() {
								@Override
								public ITextComponent getDisplayName() {
									return (ITextComponent) new StringTextComponent("EditArea");
								}
	
								@Override
								public Container createMenu(int i, PlayerInventory playerInventory,
										PlayerEntity playerEntity) {
	
									return new EditAreaContainer(id, currentArea);
								}
							}, buf -> buf.writeInt(id));
						}
					}
				} else {
					// Clear selected Area
					Ambience.selectedArea = new Area("Area1");
					Ambience.previewArea = new Area("Area1");
					Ambience.selectedArea.setOperation(Operation.SELECT);
	
					Ambience.selectedArea.resetSelection();
					//Ambience.selectedArea.setPos1(Vec3d.ZERO);
					//Ambience.selectedArea.setPos2(Vec3d.ZERO);
					AmbiencePackageHandler.sendToAll(new MyMessage(Ambience.selectedArea.SerializeThis()));
					
					//AmbiencePackageHandler.sendToClient(new MyMessage(Ambience.selectedArea.SerializeThis()),
						//	(ServerPlayerEntity) playerIn);
				}
				firstclick = false;
			}
		}else {
			clickedSpeakerOrAlarm=false;
		}

		clickedSpeakerOrAlarm=false;
		
		return ActionResult.resultSuccess(item);
	}

	@Override
	public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
		if (Ambience.selectedArea == null)
			Ambience.selectedArea = new Area("Area1");

		if (entity.world.isRemote & !rightclick) {

			RayTraceResult lookingAt = Minecraft.getInstance().objectMouseOver;

			String blockName = "";
			if (lookingAt.hitInfo == null && lookingAt.getType() != RayTraceResult.Type.BLOCK) {
				firstclick = false;
			} else {
				blockName = entity.world.getBlockState(((BlockRayTraceResult) lookingAt).getPos()).getBlock().getRegistryName().getPath();
			}
			
			if (lookingAt != null && lookingAt.getType() == RayTraceResult.Type.BLOCK & !blockName.contains("Speaker") & !blockName.contains("Alarm")) {
				BlockPos Position1 = ((BlockRayTraceResult) lookingAt).getPos();
				
				((PlayerEntity) entity).sendStatusMessage(new TranslationTextComponent(
						((I18n.format("Soundnizer.Position") + " 1 = x:") + "" + ((int) Position1.getX()) + "" + (" y:")
								+ "" + ((int) Position1.getY()) + "" + (" z:") + "" + ((int) Position1.getZ()))),
						(true));
				// Defines the selected area Pos 1
				Ambience.selectedArea.setPos1(new Vector3d(Position1.getX(), Position1.getY(), Position1.getZ()));
				Ambience.previewArea.setPos1(Ambience.selectedArea.getPos1());
				if(Ambience.selectedArea.getPos2()!=null)
					Ambience.selectedArea.setPos2(new Vector3d(Ambience.selectedArea.getPos2().getX(),	Ambience.selectedArea.getPos2().getY(), Ambience.selectedArea.getPos2().getZ()));				
			
				// envia a posição selecionada para o server
				Ambience.selectedArea.setOperation(Operation.SELECT);
				Ambience.selectedArea.setName("Area1");	
				Ambience.selectedArea.setInstantPlay(false);
				Ambience.selectedArea.setPlayAtNight(false);
				Ambience.selectedArea.setSelectedBlock(blockName);
				AmbiencePackageHandler.sendToServer(new MyMessage(Ambience.selectedArea.SerializeThis()));				
			}
		}

		rightclick = false;

		return super.onEntitySwing(stack, entity);
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add((ITextComponent) new StringTextComponent(I18n.format("Soundnizer.Desc")));
	}

}
