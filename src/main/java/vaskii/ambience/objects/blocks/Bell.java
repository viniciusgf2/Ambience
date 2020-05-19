package vaskii.ambience.objects.blocks;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockWall;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vaskii.ambience.Init.BlockInit;
import vaskii.ambience.Init.ItemInit;
import vaskii.ambience.network4.MyMessage4;
import vaskii.ambience.network4.NetworkHandler4;
import vazkii.ambience.Ambience;
import vazkii.ambience.Util.IHasModel;

public class Bell extends BlockBase {

	private boolean red = false;

	public static final PropertyBool IN_WALL = PropertyBool.create("in_wall");
	public static final PropertyBool SINGLE_WALL = PropertyBool.create("single_wall");
	public static final PropertyDirection FACING = BlockDirectional.FACING;
	
	
	public Bell(String name, Material material) {
		super(name, material);

		setSoundType(SoundType.WOOD);
		setHarvestLevel("pickaxe", 1);
		setHardness(2F);
		setResistance(10F);
		setLightLevel(0F);
		setLightOpacity(0);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(IN_WALL,
				Boolean.valueOf(false)));

		if (!FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			I18n.format("Bell.Desc");
		}
	}


	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
	}

	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing) state.getValue(FACING)).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING, IN_WALL, SINGLE_WALL });
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer) {

		boolean inWall = worldIn.getBlockState(pos.down()).getMaterial().isSolid() ? false : true;

		boolean betweenWalls = false;

		if (/* worldIn.getBlockState(pos.down()).getMaterial().isSolid() & */
		((worldIn.getBlockState(pos.west()).getMaterial().isSolid()
				& worldIn.getBlockState(pos.east()).getMaterial().isSolid())
				| (worldIn.getBlockState(pos.north()).getMaterial().isSolid()
						& worldIn.getBlockState(pos.south()).getMaterial().isSolid())))
			betweenWalls = false;
		else
			betweenWalls = true;

		return this.getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer))
				.withProperty(IN_WALL, inWall).withProperty(SINGLE_WALL, betweenWalls);
	}

	@Override
	public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
		super.addInformation(itemstack, world, list, flag);
		list.add(I18n.format("Bell.Desc"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.SOLID;
	}

	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		EnumFacing.Axis enumfacingAxis = ((EnumFacing) state.getValue(FACING)).getAxis();

		/*if (enumfacingAxis == EnumFacing.Axis.Z
				&& (worldIn.getBlockState(pos.west()).getBlock() instanceof BlockWall
						|| worldIn.getBlockState(pos.east()).getBlock() instanceof BlockWall)
				|| enumfacingAxis == EnumFacing.Axis.X
						&& (worldIn.getBlockState(pos.north()).getBlock() instanceof BlockWall
								|| worldIn.getBlockState(pos.south()).getBlock() instanceof BlockWall)) {
			state = state.withProperty(IN_WALL, Boolean.valueOf(true));
		}*/

		boolean betweenWalls = false;

		if (((worldIn.getBlockState(pos.west()).getMaterial().isSolid()
				& worldIn.getBlockState(pos.east()).getMaterial().isSolid())
				| (worldIn.getBlockState(pos.north()).getMaterial().isSolid()
						& worldIn.getBlockState(pos.south()).getMaterial().isSolid())))
			betweenWalls = false;
		else
			betweenWalls = true;

		boolean inWall = worldIn.getBlockState(pos.down()).getMaterial().isSolid() ? false : true;
		state = state.withProperty(FACING, state.getValue(FACING)).withProperty(IN_WALL, inWall)
				.withProperty(SINGLE_WALL, betweenWalls);

		return state;
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess baccess, BlockPos pos, EnumFacing side) {
		return red ? 15 : 0;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess baccess, BlockPos pos, EnumFacing side) {
		return red ? 15 : 0;
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}

	public static Boolean powered = false;

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos fromPos) {
		super.neighborChanged(state, world, pos, neighborBlock, fromPos);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		Block block = this;

		if (world.isBlockPowered(new BlockPos(x, y, z))) {
			{
				if (!powered) {
					world.playSound((EntityPlayer) null, pos.getX(), pos.getY(), pos.getZ(),
							(SoundEvent) SoundEvent.REGISTRY.getObject(
									new ResourceLocation("ambience:bell" + (new Random().nextInt(3 - 1) + 1))),
							SoundCategory.NEUTRAL, (float) 1, (float) 1);

					powered = true;
				}
			}

		} else {
			powered = false;
		}
	}

	// Abre a tela de seleção do som no bloco do speaker
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entity, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {

		// Sets the Scoreboard System for the WaterSheep
		if (!world.isRemote) {
			
			List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class,
					new AxisAlignedBB(pos.getX() - 3, pos.getY() - 3, pos.getZ() - 3, pos.getX() + 4,
							pos.getY() + 5, pos.getZ() + 4));

			
			for (EntityLivingBase mob : entities) {
				if (mob.getName().toLowerCase().trim().contains("watersheep")) {
					
					Scoreboard scoreboard = world.getScoreboard();

					// Get the oejective or create if doesn't exists
					ScoreObjective objective = scoreboard.getObjective("WaterSheepBell");
					if (objective == null)
						world.getScoreboard().addScoreObjective("WaterSheepBell", IScoreCriteria.DUMMY);

					// Gets the points
					int Points = world.getScoreboard()
							.getOrCreateScore(entity.getName(), scoreboard.getObjective("WaterSheepBell"))
							.getScorePoints();

					// Sum the points +1
					world.getScoreboard().getOrCreateScore(entity.getName(), scoreboard.getObjective("WaterSheepBell"))
							.setScorePoints(Points + 1);

					
				}
				break;
			}

		}

		//----------------------
		
		world.playSound((EntityPlayer) null, pos.getX(), pos.getY(), pos.getZ(),
				(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
						.getObject(new ResourceLocation("ambience:bell" + (new Random().nextInt(3 - 1) + 1))),
				SoundCategory.NEUTRAL, (float) 1, (float) 1);

		return true;
	}

	public NBTTagList getPosListTag(BlockPos pos) {
		NBTTagList tagList = new NBTTagList();

		NBTTagCompound posCompound = new NBTTagCompound();
		posCompound.setInteger("x", pos.getX());
		posCompound.setInteger("y", pos.getY());
		posCompound.setInteger("z", pos.getZ());

		tagList.appendTag(posCompound);

		return tagList;
	}

}
