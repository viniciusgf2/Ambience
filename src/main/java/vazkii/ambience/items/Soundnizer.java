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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.ambience.Ambience;
import vazkii.ambience.Screens.GuiContainerMod;
import vazkii.ambience.Util.Border;
import vazkii.ambience.World.Biomes.Area;

public class Soundnizer extends ItemBase {

	
	/*public Soundnizer() {
		super();
		maxStackSize = 64;
		
		if (!FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			I18n.format("Soundnizer.Position");
			I18n.format("Soundnizer.Alert");
			I18n.format("Soundnizer.Desc");
		}
	}*/

	private boolean rightclick=false;
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		
		ItemStack item = playerIn.getHeldItem(handIn);

		if (Ambience.selectedArea == null)
			Ambience.selectedArea = new Area("Area1");

		/*RayTraceResult lookingAt = Minecraft.getInstance().objectMouseOver;
		Object Position2 = lookingAt.hitInfo;
		
		if (Position2 != null && lookingAt.getType() == RayTraceResult.Type.BLOCK) {
		    BlockPos pos = (BlockPos)Position2;
		}*/

		RayTraceResult lookingAt = Minecraft.getInstance().objectMouseOver;
	
		if (worldIn.isRemote) {
			rightclick=true;
			
			if (lookingAt != null && lookingAt.getType() == RayTraceResult.Type.BLOCK) {
				BlockPos Position2 =((BlockRayTraceResult)lookingAt).getPos();
				
				if (!playerIn.isSneaking()) {
					((PlayerEntity) playerIn).sendStatusMessage(
							(ITextComponent)new TranslationTextComponent(I18n.format("Soundnizer.Position") + " 2 = x:" + ""
									+ ((int) Position2.getX()) + "" + (" y:") + ""
									+ ((int) Position2.getY()) + "" + (" z:") + ""
									+ ((int) Position2.getZ())),
							(true));

					Ambience.selectedArea.setPos2(new Vec3d(Position2.getX(),Position2.getY(),Position2.getZ()));
					Ambience.selectedArea.setDimension(playerIn.dimension.getId());
					Ambience.previewArea.setPos2(Ambience.selectedArea.getPos2());
					
					

					//Create AREA
				//	Minecraft.getInstance().displayGuiScreen(new CreateAreaGUI());
					
					
				} else {
					if (Ambience.selectedArea != null)
						Ambience.selectedArea.resetSelection();
				}
			}
		}
			
			
			
			if (!worldIn.isRemote & lookingAt.hitInfo == null && lookingAt.getType() != RayTraceResult.Type.BLOCK) {
				Area currentArea = Area.getPlayerStandingArea(playerIn);
								
				if (!playerIn.isSneaking()) {
					if (currentArea == null | Ambience.multiArea>0 & (Ambience.selectedArea.getPos1()!=null & Ambience.selectedArea.getPos2()!=null)) {

						// Check if player is inside the selected area before creating
						if (Ambience.selectedArea.getPos1() != null & Ambience.selectedArea.getPos2() != null) {

							Border border = new Border(Ambience.selectedArea.getPos1(),Ambience.selectedArea.getPos2());

							if (border.contains(playerIn.getPositionVector())) {

								
								/*
								Ambience.selectedArea.setOperation(Operation.CREATE);
								Ambience.selectedArea.setName("Elevador");															      
								AmbiencePackageHandler.sendToServer(new MyMessage(Ambience.selectedArea.SerializeThis()));
								 */							

								//Create AREA
								
								//NetworkHooks.openGui(playerIn, () -> new CreateAreaScreen(null, null, null));
								//playerIn.openContainer(new CreateAreaScreen(null, null, null));
								
								
								
								int id = playerIn.getEntityId();
								
								 NetworkHooks.openGui((ServerPlayerEntity) playerIn, new INamedContainerProvider() {
						                @Override
						                public ITextComponent getDisplayName() {
						                    return (ITextComponent)new StringTextComponent(I18n.format("GUI.CreateArea"));
						                }

						                @Override
						                public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
						                    
						                    return new GuiContainerMod(id);
						                }
						            }, buf -> buf.writeInt(id));
								
								
								
								
								//playerIn.openGui(Ambience.instance, 5, worldIn, MathHelper.floor(playerIn.getPosX()),
								//		MathHelper.floor(playerIn.getPosY()), MathHelper.floor(playerIn.getPosZ()));
							} else {
								((PlayerEntity) playerIn).sendStatusMessage(
										(ITextComponent)new StringTextComponent(I18n.format("Soundnizer.Alert")), true);
							}
						}
					} else {
						/*EditAreaGUI.currentArea = currentArea;
						
						//UPDATE AREA
						playerIn.openGui(Ambience.instance, 2, worldIn, MathHelper.floor(playerIn.getPosX()),
								MathHelper.floor(playerIn.getPosY()), MathHelper.floor(playerIn.getPosZ()));*/
					}
				} else {
					//Clear selected Area
						Ambience.selectedArea = new Area("Area1");
						Ambience.previewArea = new Area("Area1");
				}
			
		}

        return ActionResult.resultSuccess(item);
	}
	
	
	@Override
	public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {		
		if (Ambience.selectedArea == null)
			Ambience.selectedArea = new Area("Area1");
		
		if (entity.world.isRemote & !rightclick) {
			
			RayTraceResult lookingAt = Minecraft.getInstance().objectMouseOver;
			
			if (lookingAt != null && lookingAt.getType() == RayTraceResult.Type.BLOCK) {
				BlockPos Position1 =((BlockRayTraceResult)lookingAt).getPos();
			//if (Position1.getType() != Type.MISS & !testBlock.contains("Speaker") & !testBlock.contains("Alarm")) {			
					((PlayerEntity) entity).sendStatusMessage(
							new TranslationTextComponent(((I18n.format("Soundnizer.Position") + " 1 = x:") + ""
									+ ((int) Position1.getX()) + "" + (" y:") + ""
									+ ((int) Position1.getY()) + "" + (" z:") + ""
									+ ((int) Position1.getZ()))),
							(true));				
				// Defines the selected area Pos 1
				Ambience.selectedArea.setPos1(new Vec3d(Position1.getX(),Position1.getY(),Position1.getZ()));
				Ambience.previewArea.setPos1(Ambience.selectedArea.getPos1());
			}
		}
		
		rightclick=false;
		
		return super.onEntitySwing(stack, entity);
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add((ITextComponent)new StringTextComponent(I18n.format("Soundnizer.Desc")));
	}

}
