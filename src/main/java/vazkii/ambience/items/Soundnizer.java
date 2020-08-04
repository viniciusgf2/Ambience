package vazkii.ambience.items;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import vazkii.ambience.Ambience;
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

		if (worldIn.isRemote) {
			rightclick=true;
			RayTraceResult Position2 = rayTrace(worldIn, playerIn, FluidMode.NONE);// playerIn.rayTrace(5, 1.0F);
			
			if (Position2.getType() != Type.MISS) {

				
				if (!playerIn.isSneaking()) {
					((PlayerEntity) playerIn).sendStatusMessage(
							(ITextComponent)new TranslationTextComponent(I18n.format("Soundnizer.Position") + " 2 = x: " + ""
									+ ((int) Position2.getHitVec().x) + "" + ("y: ") + ""
									+ ((int) Position2.getHitVec().y) + "" + ("z: ") + ""
									+ ((int) Position2.getHitVec().z)),
							(true));

					Ambience.selectedArea.setPos2(new Vec3d(Position2.getHitVec().x,Position2.getHitVec().y,Position2.getHitVec().z));
					Ambience.selectedArea.setDimension(playerIn.dimension.getId());
					Ambience.previewArea.setPos2(Ambience.selectedArea.getPos2());
				} else {
					if (Ambience.selectedArea != null)
						Ambience.selectedArea.resetSelection();
				}
			} else {
				Area currentArea = Area.getPlayerStandingArea(playerIn);
								
				if (!playerIn.isSneaking()) {
					if (currentArea == null | Ambience.multiArea>0 & (Ambience.selectedArea.getPos1()!=null & Ambience.selectedArea.getPos2()!=null)) {

						// Check if player is inside the selected area before creating
						if (Ambience.selectedArea.getPos1() != null & Ambience.selectedArea.getPos2() != null) {

							Border border = new Border(Ambience.selectedArea.getPos1(),Ambience.selectedArea.getPos2());

							if (border.contains(playerIn.getPositionVector())) {

								//Create AREA
								//playerIn.openGui(Ambience.instance, 5, worldIn, MathHelper.floor(playerIn.getPosX()),
								//		MathHelper.floor(playerIn.getPosY()), MathHelper.floor(playerIn.getPosZ()));
							} else {
								((PlayerEntity) playerIn).sendStatusMessage(
										(ITextComponent)new StringTextComponent(I18n.format("Soundnizer.Alert")), true);
							}
						}
					} /*else {
						EditAreaGUI.currentArea = currentArea;

						//UPDATE AREA
						playerIn.openGui(Ambience.instance, 2, worldIn, MathHelper.floor(playerIn.getPosX()),
								MathHelper.floor(playerIn.getPosY()), MathHelper.floor(playerIn.getPosZ()));
					}*/
				} else {
					//Clear selected Area
						Ambience.selectedArea = new Area("Area1");
						Ambience.previewArea = new Area("Area1");
				}
			}
		}

        return ActionResult.resultSuccess(item);
	}
	
	
	@Override
	public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {		
		if (Ambience.selectedArea == null)
			Ambience.selectedArea = new Area("Area1");
		
		if (entity.world.isRemote & !rightclick) {
			// AreaData data= new AreaData();
			RayTraceResult Position1 = rayTrace(entity.world, (PlayerEntity) entity, FluidMode.NONE);

			String testBlock=entity.world.getBlockState(new BlockPos(Position1.getHitVec())).getBlock().getClass().getName();
			
			if (Position1.getType() != Type.MISS & !testBlock.contains("Speaker") & !testBlock.contains("Alarm")) {			
					((PlayerEntity) entity).sendStatusMessage(
							new TranslationTextComponent(((I18n.format("Soundnizer.Position") + " 1 = x: ") + ""
									+ ((int) Position1.getHitVec().x) + "" + ("y: ") + ""
									+ ((int) Position1.getHitVec().y) + "" + ("z: ") + ""
									+ ((int) Position1.getHitVec().z))),
							(true));				
				// Defines the selected area Pos 1
				Ambience.selectedArea.setPos1(new Vec3d(Position1.getHitVec().x,Position1.getHitVec().y,Position1.getHitVec().z));
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
