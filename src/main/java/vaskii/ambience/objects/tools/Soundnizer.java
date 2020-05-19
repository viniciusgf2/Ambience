package vaskii.ambience.objects.tools;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import vaskii.ambience.GUI.EditAreaGUI;
import vaskii.ambience.objects.items.ItemBase;
import vazkii.ambience.Ambience;
import vazkii.ambience.Util.Border;
import vazkii.ambience.World.Biomes.Area;

public class Soundnizer extends ItemBase {

	public Soundnizer(String Name) {
		super(Name);
		setMaxDamage(0);
		maxStackSize = 64;

		if (!FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			I18n.format("Soundnizer.Position");
			I18n.format("Soundnizer.Alert");
			I18n.format("Soundnizer.Desc");
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack item = playerIn.getHeldItem(handIn);

		if (Ambience.selectedArea == null)
			Ambience.selectedArea = new Area("Area1");

		if (worldIn.isRemote) {

			RayTraceResult Position2 = playerIn.rayTrace(5, 1.0F);

			//boolean ola = worldIn.isRemote;

			if (Position2.typeOfHit != Type.MISS) {

				if (!playerIn.isSneaking()) {

					((EntityPlayer) playerIn).sendStatusMessage(
							new TextComponentString(((I18n.format("Soundnizer.Position") + " 2 = x: ") + ""
									+ ((int) Position2.getBlockPos().getX()) + "" + ("y: ") + ""
									+ ((int) Position2.getBlockPos().getY()) + "" + ("z: ") + ""
									+ ((int) Position2.getBlockPos().getZ()))),
							(true));

					Ambience.selectedArea.setPos2(Position2.getBlockPos());
					Ambience.selectedArea.setDimension(playerIn.dimension);
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

							Border border = new Border(Ambience.selectedArea.getPos1(),
									Ambience.selectedArea.getPos2());

							if (border.contains(playerIn.getPosition())) {

								//Create AREA
								playerIn.openGui(Ambience.instance, 5, worldIn, MathHelper.floor(playerIn.posX),
										MathHelper.floor(playerIn.posY), MathHelper.floor(playerIn.posZ));
							} else {
								((EntityPlayer) playerIn).sendStatusMessage(
										new TextComponentString(I18n.format("Soundnizer.Alert")), true);
							}
						}
					} else {
						EditAreaGUI.currentArea = currentArea;

						//UPDATE AREA
						playerIn.openGui(Ambience.instance, 2, worldIn, MathHelper.floor(playerIn.posX),
								MathHelper.floor(playerIn.posY), MathHelper.floor(playerIn.posZ));
					}
				} else {
					//Clear selected Area
						Ambience.selectedArea = new Area("Area1");
				}
			}
		}

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {

		if (Ambience.selectedArea == null)
			Ambience.selectedArea = new Area("Area1");

		if (FMLCommonHandler.instance().getSide().isClient()) {
			// AreaData data= new AreaData();
			RayTraceResult Position1 = entityLiving.rayTrace(5, 1.0F);

			TileEntity teste = entityLiving.world.getTileEntity(Position1.getBlockPos());

			if (Position1.typeOfHit != Type.MISS) {

				if (teste != null) {
					if (!teste.getBlockType().toString().contains("ambience:speaker"))
						((EntityPlayer) entityLiving).sendStatusMessage(
								new TextComponentString(((I18n.format("Soundnizer.Position") + " 1 = x: ") + ""
										+ ((int) Position1.getBlockPos().getX()) + "" + ("y: ") + ""
										+ ((int) Position1.getBlockPos().getY()) + "" + ("z: ") + ""
										+ ((int) Position1.getBlockPos().getZ()))),
								(true));
				} else {
					((EntityPlayer) entityLiving).sendStatusMessage(
							new TextComponentString(((I18n.format("Soundnizer.Position") + " 1 = x: ") + ""
									+ ((int) Position1.getBlockPos().getX()) + "" + ("y: ") + ""
									+ ((int) Position1.getBlockPos().getY()) + "" + ("z: ") + ""
									+ ((int) Position1.getBlockPos().getZ()))),
							(true));
				}
				// Defines the selected area Pos 1
				Ambience.selectedArea.setPos1(Position1.getBlockPos());
			}
		}
		return super.onEntitySwing(entityLiving, stack);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 32;
	}

	@Override
	public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
		super.addInformation(itemstack, world, list, flag);
		//list.add("Configures an area to play a sound");
		list.add(I18n.format("Soundnizer.Desc"));
	}
}
