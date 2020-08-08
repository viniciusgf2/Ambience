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
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

public class Speaker extends Block {

	private boolean red = false;
	public static String selectedSound = null;
	public static int delaySound = 0;
	public static boolean loop = true;
	public static float Distance = 1;
	public static final DirectionProperty FACING = DirectionalBlock.FACING;
	public static final VoxelShape SHAPE_N= Stream.of(Block.makeCuboidShape(2, 2, 15, 14, 14, 16),Block.makeCuboidShape(2, 3, 14, 3, 13, 15),Block.makeCuboidShape(13, 3, 14, 14, 13, 15),
			Block.makeCuboidShape(2, 2, 14, 14, 3, 15),Block.makeCuboidShape(2, 13, 14, 14, 14, 15)).reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);}).get();
	
	public static final VoxelShape SHAPE_S=Stream.of(Block.makeCuboidShape(2, 2, 0, 14, 14, 1),Block.makeCuboidShape(13, 3, 1, 14, 13, 2),Block.makeCuboidShape(2, 3, 1, 3, 13, 2),
			Block.makeCuboidShape(2, 2, 1, 14, 3, 2),Block.makeCuboidShape(2, 13, 1, 14, 14, 2)).reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);}).get();
	
	public static final VoxelShape SHAPE_E=Stream.of(Block.makeCuboidShape(0, 2, 2, 1, 14, 14),Block.makeCuboidShape(1, 3, 2, 2, 13, 3),Block.makeCuboidShape(1, 3, 13, 2, 13, 14),
			Block.makeCuboidShape(1, 2, 2, 2, 3, 14),Block.makeCuboidShape(1, 13, 2, 2, 14, 14)).reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);}).get();
	
	public static final VoxelShape SHAPE_W=Stream.of(Block.makeCuboidShape(15, 2, 2, 16, 14, 14),Block.makeCuboidShape(14, 3, 13, 15, 13, 14),Block.makeCuboidShape(14, 3, 2, 15, 13, 3),
			Block.makeCuboidShape(14, 2, 2, 15, 3, 14),Block.makeCuboidShape(14, 13, 2, 15, 14, 14)).reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);}).get();
	
	public static final VoxelShape SHAPE_U=Stream.of(Block.makeCuboidShape(2, 0, 2, 14, 1, 14),Block.makeCuboidShape(2, 1, 3, 3, 2, 13),Block.makeCuboidShape(13, 1, 3, 14, 2, 13),
			Block.makeCuboidShape(2, 1, 2, 14, 2, 3),Block.makeCuboidShape(2, 1, 13, 14, 2, 14)).reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);}).get();
	
	public static final VoxelShape SHAPE_D=Stream.of(Block.makeCuboidShape(2, 15, 2, 14, 16, 14),Block.makeCuboidShape(2, 14, 3, 3, 15, 13),Block.makeCuboidShape(13, 14, 3, 14, 15, 13),
			Block.makeCuboidShape(2, 14, 13, 14, 15, 14),Block.makeCuboidShape(2, 14, 2, 14, 15, 3)).reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);}).get();
	
	public Speaker() {
		super(Block.Properties.create(Material.WOOD)
				.hardnessAndResistance(2.0f, 5.0f)
				.sound(SoundType.WOOD)
				.harvestLevel(1)
				.harvestTool(ToolType.PICKAXE)
				.lightValue(0)				
				);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch (state.get(FACING)) {
			case NORTH: return SHAPE_N;
			case SOUTH: return SHAPE_S;
			case EAST: return SHAPE_E;
			case WEST: return SHAPE_W;
			case UP: return SHAPE_U;
			case DOWN: return SHAPE_D;
			default: return SHAPE_N;
		}
	}
	
	@Override
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {		
		return 0.4f;
	}
	
	@Override
	public void addInformation(ItemStack stack, IBlockReader worldIn, List<ITextComponent> tooltip,	ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add((ITextComponent)new StringTextComponent(I18n.format("Speaker.Desc")));
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
		return this.getDefaultState().with(FACING,context.getFace());
	}
	
	public CompoundNBT getPosListTag(BlockPos pos) {		
		CompoundNBT posCompound = new CompoundNBT();
		posCompound.putInt("x", pos.getX());
		posCompound.putInt("y", pos.getY());
		posCompound.putInt("z", pos.getZ());
		
		return posCompound;
	}
}
