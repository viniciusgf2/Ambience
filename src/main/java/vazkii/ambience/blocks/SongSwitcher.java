package vazkii.ambience.blocks;

import java.util.List;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.ambience.Screens.SpeakerContainer;
import vazkii.ambience.Util.ModTileEntityTypes;
import vazkii.ambience.Util.RegistryHandler;
import vazkii.ambience.Util.Handlers.SoundHandler;
import vazkii.ambience.World.Biomes.Area;
import vazkii.ambience.World.Biomes.Area.Operation;
import vazkii.ambience.network.AmbiencePackageHandler;
import vazkii.ambience.network.MyMessage;

public class SongSwitcher extends Block {

	public static final DirectionProperty FACING = DirectionalBlock.FACING;
	public static final VoxelShape SHAPE_S = VoxelShapes.combineAndSimplify(Block.makeCuboidShape(2, 2, 1, 14, 14, 4),
			Block.makeCuboidShape(1.7763568394002505e-15, 0, 0, 16, 16, 2), IBooleanFunction.OR);

	public static final VoxelShape SHAPE_N = VoxelShapes.combineAndSimplify(Block.makeCuboidShape(2, 2, 12, 14, 14, 15),
			Block.makeCuboidShape(1.7763568394002505e-15, 0, 14, 16, 16, 16), IBooleanFunction.OR);

	public static final VoxelShape SHAPE_E = VoxelShapes.combineAndSimplify(Block.makeCuboidShape(1, 2, 2, 4, 14, 14),
			Block.makeCuboidShape(0, 0, 1.7763568394002505e-15, 2, 16, 16), IBooleanFunction.OR);

	public static final VoxelShape SHAPE_W = VoxelShapes.combineAndSimplify(Block.makeCuboidShape(12, 2, 2, 15, 14, 14),
			Block.makeCuboidShape(14, 0, 1.7763568394002505e-15, 16, 16, 16), IBooleanFunction.OR);

	public static final VoxelShape SHAPE_U = VoxelShapes.combineAndSimplify(Block.makeCuboidShape(2, 1, 2, 14, 4, 14),
			Block.makeCuboidShape(1.7763568394002505e-15, 0, 0, 16, 2, 16), IBooleanFunction.OR);

	public static final VoxelShape SHAPE_D = VoxelShapes.combineAndSimplify(Block.makeCuboidShape(2, 12, 2, 14, 15, 14),
			Block.makeCuboidShape(1.7763568394002505e-15, 14, 0, 16, 16, 16), IBooleanFunction.OR);

	public static Material material = Material.WOOD;
	public static SoundType soundType = SoundType.WOOD;
	public static int lightValue = 0;

	public SongSwitcher(String color) {
		super(Block.Properties.create(material).hardnessAndResistance(2.0f, 5.0f).sound(soundType).harvestLevel(1)
				.harvestTool(ToolType.PICKAXE).lightValue(lightValue));

	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch (state.get(FACING)) {
		case NORTH:
			return SHAPE_N;
		case SOUTH:
			return SHAPE_S;
		case EAST:
			return SHAPE_E;
		case WEST:
			return SHAPE_W;
		case UP:
			return SHAPE_U;
		case DOWN:
			return SHAPE_D;
		default:
			return SHAPE_N;
		}
	}

	/*
	 * @Override public int getWeakPower(BlockState blockState, IBlockReader
	 * blockAccess, BlockPos pos, Direction side) { return red ? 15 : 0; }
	 * 
	 * @Override public int getStrongPower(BlockState blockState, IBlockReader
	 * blockAccess, BlockPos pos, Direction side) { return red ? 15 : 0; }
	 */

	@Override
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return 0.4f;
	}

	@Override
	public void addInformation(ItemStack stack, IBlockReader worldIn, List<ITextComponent> tooltip,
			ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add((ITextComponent) new StringTextComponent(I18n.format("songswitcher.Desc")));
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {		
		return this.getDefaultState().with(FACING, context.getFace());
	}

		
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
			boolean isMoving) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);

		Area currentArea = Area.getBlockStandingArea(pos);

		if (worldIn instanceof ServerWorld) {		
		
			if (currentArea != null) {
				// currentArea.setRedstoneStrength(worldIn.getRedstonePowerFromNeighbors(fromPos));
				currentArea.setRedstoneStrength(getRedstonePowerFromNeighbors(fromPos, worldIn));
				currentArea.setOperation(Operation.EDIT);

				AmbiencePackageHandler.sendToAll(new MyMessage(currentArea.SerializeThis()));

				if (getRedstonePowerFromNeighbors(fromPos, worldIn)!=0) {
					setState(true,worldIn,pos,state);
				}else {
					setState(false,worldIn,pos,state);					
				}
			}
		}
	}

	@Override
	public boolean isEmissiveRendering(BlockState block) {
		
		return block.getBlock().getRegistryName().getPath().contains("lit") ? true : false;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		//Check the surrounding area on placement to see if there is power already or not
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		
		Area currentArea = Area.getBlockStandingArea(pos);


		worldIn.setBlockState(pos,RegistryHandler.SongSwitcher_lit.get().getDefaultState().with(FACING, state.get(FACING)),2);		

		if (worldIn.getPlayers().get(0) instanceof ServerPlayerEntity) {

			
		}else {
			if (currentArea != null) {
				
				int power=0;
				
				power=getRedstonePowerFromNeighbors(new BlockPos(pos.getX()+1,pos.getY(),pos.getZ()), worldIn);
				if(power==0)
					power=getRedstonePowerFromNeighbors(new BlockPos(pos.getX()-1,pos.getY(),pos.getZ()), worldIn);
				if(power==0)
					power=getRedstonePowerFromNeighbors(new BlockPos(pos.getX(),pos.getY(),pos.getZ()+1), worldIn);
				if(power==0)
					power=getRedstonePowerFromNeighbors(new BlockPos(pos.getX(),pos.getY(),pos.getZ()-1), worldIn);
				
				currentArea.setRedstoneStrength(power);
				currentArea.setOperation(Operation.EDIT);

				AmbiencePackageHandler.sendToServer(new MyMessage(currentArea.SerializeThis()));

				if (power !=0) {
					setState(true,worldIn,pos,state);
				}else {
					setState(false,worldIn,pos,state);					
				}
			}
		}
	}

	public int getRedstonePowerFromNeighbors(BlockPos pos, World worldIn) {
		int i = 0;

		for (Direction direction : Direction.values()) {
			if (direction.name() != "") {
				int j = worldIn.getRedstonePower(pos, direction);
				if (j >= 15) {
					return 15;
				}

				if (j > i) {
					i = j;
				}
			}
		}

		return i;
	}

	// Acende e apaga a luz
	public void setState(boolean active, World worldIn, BlockPos pos, BlockState state) {
		
		if (active) {
			worldIn.setBlockState(pos,RegistryHandler.SongSwitcher_lit.get().getDefaultState().with(FACING, state.get(FACING)),2);		

		} else {
			worldIn.setBlockState(pos,RegistryHandler.SongSwitcher.get().getDefaultState().with(FACING, state.get(FACING)), 2);
		}

	}

	// Stops the Sound on Player Destroy
	@Override
	public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {

		PlayerEntity player = (PlayerEntity) worldIn.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10, false);
	
		
		if (player instanceof ServerPlayerEntity) {

			
		}else {
			Area currentArea = Area.getBlockStandingArea(pos);

			if(currentArea !=null) {
				currentArea.setRedstoneStrength(0);
				currentArea.setOperation(Operation.EDIT);
	
				AmbiencePackageHandler.sendToServer(new MyMessage(currentArea.SerializeThis()));
			}
		}
		

		super.onPlayerDestroy(worldIn, pos, state);
	}

	// Stops the Sound on Explosion Destroy
	@Override
	public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
		if (worldIn instanceof ServerWorld) {			
			PlayerEntity player = (PlayerEntity) worldIn.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10, false);
			Area currentArea = Area.getBlockStandingArea(pos);
	
			currentArea.setRedstoneStrength(0);
			currentArea.setOperation(Operation.EDIT);
	
			AmbiencePackageHandler.sendToServer(new MyMessage(currentArea.SerializeThis()));
	
			super.onExplosionDestroy(worldIn, pos, explosionIn);
		}
	}

}
