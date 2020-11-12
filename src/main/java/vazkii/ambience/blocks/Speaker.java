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
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.ambience.Screens.SpeakerContainer;
import vazkii.ambience.Util.ModTileEntityTypes;
import vazkii.ambience.Util.RegistryHandler;
import vazkii.ambience.Util.Handlers.SoundHandler;
import vazkii.ambience.network.AmbiencePackageHandler;
import vazkii.ambience.network.MyMessage;

public class Speaker extends Block {

	private boolean red = false;
	public static String selectedSound = null;
	public static int delaySound = 0;
	public static boolean loop = true;
	public static float Distance = 1;
	public static final DirectionProperty FACING = DirectionalBlock.FACING;
	public static final VoxelShape SHAPE_N = Stream.of(Block.makeCuboidShape(2, 2, 15, 14, 14, 16),
			Block.makeCuboidShape(2, 3, 14, 3, 13, 15), Block.makeCuboidShape(13, 3, 14, 14, 13, 15),
			Block.makeCuboidShape(2, 2, 14, 14, 3, 15), Block.makeCuboidShape(2, 13, 14, 14, 14, 15))
			.reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();

	public static final VoxelShape SHAPE_S = Stream.of(Block.makeCuboidShape(2, 2, 0, 14, 14, 1),
			Block.makeCuboidShape(13, 3, 1, 14, 13, 2), Block.makeCuboidShape(2, 3, 1, 3, 13, 2),
			Block.makeCuboidShape(2, 2, 1, 14, 3, 2), Block.makeCuboidShape(2, 13, 1, 14, 14, 2)).reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();

	public static final VoxelShape SHAPE_E = Stream.of(Block.makeCuboidShape(0, 2, 2, 1, 14, 14),
			Block.makeCuboidShape(1, 3, 2, 2, 13, 3), Block.makeCuboidShape(1, 3, 13, 2, 13, 14),
			Block.makeCuboidShape(1, 2, 2, 2, 3, 14), Block.makeCuboidShape(1, 13, 2, 2, 14, 14)).reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();

	public static final VoxelShape SHAPE_W = Stream.of(Block.makeCuboidShape(15, 2, 2, 16, 14, 14),
			Block.makeCuboidShape(14, 3, 13, 15, 13, 14), Block.makeCuboidShape(14, 3, 2, 15, 13, 3),
			Block.makeCuboidShape(14, 2, 2, 15, 3, 14), Block.makeCuboidShape(14, 13, 2, 15, 14, 14))
			.reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();

	public static final VoxelShape SHAPE_U = Stream.of(Block.makeCuboidShape(2, 0, 2, 14, 1, 14),
			Block.makeCuboidShape(2, 1, 3, 3, 2, 13), Block.makeCuboidShape(13, 1, 3, 14, 2, 13),
			Block.makeCuboidShape(2, 1, 2, 14, 2, 3), Block.makeCuboidShape(2, 1, 13, 14, 2, 14)).reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();

	public static final VoxelShape SHAPE_D = Stream.of(Block.makeCuboidShape(2, 15, 2, 14, 16, 14),
			Block.makeCuboidShape(2, 14, 3, 3, 15, 13), Block.makeCuboidShape(13, 14, 3, 14, 15, 13),
			Block.makeCuboidShape(2, 14, 13, 14, 15, 14), Block.makeCuboidShape(2, 14, 2, 14, 15, 3))
			.reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();

	public static Material material=Material.WOOD;
	public static SoundType soundType=SoundType.WOOD;
	public static int lightValue=0;

	public Speaker(String color) {
		super(Block.Properties.create(material)
				.hardnessAndResistance(2.0f, 5.0f)
				.sound(soundType)
				.harvestLevel(1)
				.harvestTool(ToolType.PICKAXE)
				.lightValue(lightValue));
								
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

	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return red ? 15 : 0;
	}

	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return red ? 15 : 0;
	}

	@Override
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return 0.4f;
	}

	@Override
	public void addInformation(ItemStack stack, IBlockReader worldIn, List<ITextComponent> tooltip,
			ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add((ITextComponent) new StringTextComponent(I18n.format("Speaker.Desc")));
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

	
	// Abre a tela de seleção do som no bloco do speaker
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
				
		if(player instanceof ServerPlayerEntity) {
			
			if (player.getHeldItemMainhand().getDisplayName().getString().contains("oundnizer")) {
												
				CompoundNBT tagCompound = new CompoundNBT();
				
				tagCompound.putInt("delay",((SpeakerTileEntity) worldIn.getTileEntity(pos)).delay); 
				tagCompound.putString("selectedSound",((SpeakerTileEntity) worldIn.getTileEntity(pos)).selectedSound); 
				tagCompound.put("pos",getPosListTag(pos));
				tagCompound.putBoolean("loop",((SpeakerTileEntity) worldIn.getTileEntity(pos)).loop);
				tagCompound.putFloat("distance",((SpeakerTileEntity) worldIn.getTileEntity(pos)).distance); 
				tagCompound.putString("openGui","open");
				tagCompound.putInt("index", getListSelectedIndex(((SpeakerTileEntity) worldIn.getTileEntity(pos)).selectedSound));
				tagCompound.putInt("dimension",player.dimension.getId());
				tagCompound.putBoolean("isAlarm",false);
				
				SpeakerContainer.isAlarm =false;
				SpeakerContainer.pos=pos;
				SpeakerContainer.dimension=player.dimension.getId();

				AmbiencePackageHandler.sendToAll(new MyMessage(tagCompound));
				//AmbiencePackageHandler.sendToClient(new MyMessage(tagCompound), (ServerPlayerEntity) player);;
									
				NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
					@Override
					public ITextComponent getDisplayName() {
						return (ITextComponent) new StringTextComponent("Speaker");
					}

					@Override
					public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {

						return new SpeakerContainer(player.getEntityId(),
								((SpeakerTileEntity) worldIn.getTileEntity(pos)).delay,
								((SpeakerTileEntity) worldIn.getTileEntity(pos)).selectedSound,
								pos,
								((SpeakerTileEntity) worldIn.getTileEntity(pos)).loop,
								((SpeakerTileEntity) worldIn.getTileEntity(pos)).distance, 
								"open",
								getListSelectedIndex(((SpeakerTileEntity) worldIn.getTileEntity(pos)).selectedSound),
								player.dimension.getId(),
								false);
					}
				}, buf -> buf.writeInt(player.getEntityId()));

			}
		}

		//return ActionResultType.FAIL;
		 return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}

	private int getListSelectedIndex(String selectedSound) {

		int SelectedItemIndex = 0;
		if (selectedSound != null)
			for (String sound : SoundHandler.SOUNDS) {

				if (sound.contains(selectedSound)) {
					break;
				}
				SelectedItemIndex++;
			}

		return SelectedItemIndex;
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
			boolean isMoving) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);

		this.selectedSound=((SpeakerTileEntity) worldIn.getTileEntity(pos)).selectedSound;
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		Block block = this;

		if (worldIn.isBlockPowered(new BlockPos(x, y, z))) {

			((SpeakerTileEntity) worldIn.getTileEntity(pos)).isPowered = true;
			((SpeakerTileEntity) worldIn.getTileEntity(pos)).countPlay = 0;

		} else {

			((SpeakerTileEntity) worldIn.getTileEntity(pos)).isPowered = false;
			((SpeakerTileEntity) worldIn.getTileEntity(pos)).cooldown = 0;

			PlayerEntity player = (PlayerEntity) worldIn.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10,false);

			if (player != null & player instanceof ServerPlayerEntity) {
				// Manda msg pedido para parar o som no cliente
				CompoundNBT tagCompound = new CompoundNBT();
				tagCompound.putString("selectedSound", ((SpeakerTileEntity) worldIn.getTileEntity(pos)).selectedSound);
				tagCompound.putString("stop", "stop");
				tagCompound.putString("sound","ambience:"+((SpeakerTileEntity) worldIn.getTileEntity(pos)).selectedSound);
							
				AmbiencePackageHandler.sendToAll(new MyMessage(tagCompound));				
			}

		}
	}

	public CompoundNBT getPosListTag(BlockPos pos) {
		CompoundNBT posCompound = new CompoundNBT();
		posCompound.putInt("x", pos.getX());
		posCompound.putInt("y", pos.getY());
		posCompound.putInt("z", pos.getZ());

		return posCompound;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return ModTileEntityTypes.SPEAKER.get().create();			
	}
	
		

	//Stops the Sound on Player Destroy
	@Override
	public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {

		PlayerEntity player = (PlayerEntity) worldIn.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10,false);

		if(player instanceof ServerPlayerEntity)
			StopSound(selectedSound,pos,player);
		
		super.onPlayerDestroy(worldIn, pos, state);
	}

	//Stops the Sound on Explosion Destroy
	@Override
	public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
	
		PlayerEntity player = (PlayerEntity) worldIn.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10,false);

		if(player instanceof ServerPlayerEntity)
			StopSound(selectedSound,pos,player);
		
		super.onExplosionDestroy(worldIn, pos, explosionIn);
	}
	
	private void StopSound(String sound, BlockPos pos, PlayerEntity player) {
		
		// Manda msg pedido para parar o som no cliente
		CompoundNBT tagCompound = new CompoundNBT();
		tagCompound.putString("selectedSound", sound);
		tagCompound.putString("stop", "stop");
		tagCompound.putString("sound","ambience:"+sound);
					
		AmbiencePackageHandler.sendToClient(new MyMessage(tagCompound), (ServerPlayerEntity) player);
	}	
}
