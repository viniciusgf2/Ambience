package vazkii.ambience.blocks;

import java.awt.Dimension;
import java.util.List;
import java.util.function.ToIntFunction;
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
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.ambience.Screens.SpeakerContainer;
import vazkii.ambience.Util.ModTileEntityTypes;
import vazkii.ambience.Util.RegistryHandler;
import vazkii.ambience.Util.Handlers.SoundHandler;
import vazkii.ambience.network.AmbiencePackageHandler;
import vazkii.ambience.network.MyMessage;

public class Alarm extends Block {

	public static boolean isLit=true;
	public String color;  
	
	private boolean red = false;
	public static String selectedSound = null;
	public static int delaySound = 0;
	public static boolean loop = true;
	public static float Distance = 1;
	public static final DirectionProperty FACING = DirectionalBlock.FACING;
	public static AlarmTileEntity tileEntity;
	
	
	public static final VoxelShape SHAPE_N = VoxelShapes.combineAndSimplify(Block.makeCuboidShape(5, 5, 8, 11, 11, 16), Block.makeCuboidShape(4, 4, 16, 12, 12, 17), IBooleanFunction.OR);
	public static final VoxelShape SHAPE_S = VoxelShapes.combineAndSimplify(Block.makeCuboidShape(5, 5, 0, 11, 11, 8), Block.makeCuboidShape(4, 4, -1, 12, 12, 0), IBooleanFunction.OR);
	public static final VoxelShape SHAPE_E = VoxelShapes.combineAndSimplify(Block.makeCuboidShape(1, 5, 5, 9, 11, 11), Block.makeCuboidShape(0, 4, 4, 1, 12, 12), IBooleanFunction.OR);
	public static final VoxelShape SHAPE_W = VoxelShapes.combineAndSimplify(Block.makeCuboidShape(7, 5, 5, 15, 11, 11), Block.makeCuboidShape(15, 4, 4, 16, 12, 12), IBooleanFunction.OR);
	public static final VoxelShape SHAPE_U = VoxelShapes.combineAndSimplify(Block.makeCuboidShape(5, 1, 5, 11, 9, 11), Block.makeCuboidShape(4, 0, 4, 12, 1, 12), IBooleanFunction.OR);
	public static final VoxelShape SHAPE_D = VoxelShapes.combineAndSimplify(Block.makeCuboidShape(5, 7, 5, 11, 15, 11), Block.makeCuboidShape(4, 15, 4, 12, 16, 12), IBooleanFunction.OR);

	public static Material material=Material.WOOD;
	public static SoundType soundType=SoundType.GLASS;
	
	public Alarm(String color,Boolean isLit) {
		super(Block.Properties.create(material)
				.hardnessAndResistance(2.0f, 5.0f)
				.sound(soundType)
				.harvestLevel(0)
				.harvestTool(ToolType.PICKAXE)
				.setLightLevel(new ToIntFunction<BlockState>() {
					
					@Override
					public int applyAsInt(BlockState value) {
						// TODO Auto-generated method stub
						return isLit? 16:0;
					}					
				})			
				);
		
		
		Alarm.isLit=isLit;
		this.color=color;				
	}

/*	@Override
	public int getLightValue(BlockState state) {		
		//if(this.getRegistryName().getPath().contains("white"))
			//return 16;
		
		if(isLit)
			return 16;
		else
			return 0;
	}*/

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
				
				tagCompound.putInt("delay",((AlarmTileEntity) worldIn.getTileEntity(pos)).delay); 
				tagCompound.putString("selectedSound",((AlarmTileEntity) worldIn.getTileEntity(pos)).selectedSound); 
				tagCompound.put("pos",getPosListTag(pos));
				tagCompound.putBoolean("loop",((AlarmTileEntity) worldIn.getTileEntity(pos)).loop);
				tagCompound.putFloat("distance",((AlarmTileEntity) worldIn.getTileEntity(pos)).distance); 
				tagCompound.putString("openGui","open");
				tagCompound.putInt("index", getListSelectedIndex(((AlarmTileEntity) worldIn.getTileEntity(pos)).selectedSound));
				tagCompound.putInt("dimension",player.dimension.getId());
				tagCompound.putBoolean("isAlarm",true);
								
				SpeakerContainer.isAlarm=true;
				SpeakerContainer.pos=pos;
				SpeakerContainer.dimension=player.dimension.getId();
				
				AmbiencePackageHandler.sendToAll(new MyMessage(tagCompound));
									
				NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
					@Override
					public ITextComponent getDisplayName() {
						return (ITextComponent) new StringTextComponent("Speaker");
					}

					@Override
					public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {

						return new SpeakerContainer(player.getEntityId(),
								((AlarmTileEntity) worldIn.getTileEntity(pos)).delay,
								((AlarmTileEntity) worldIn.getTileEntity(pos)).selectedSound,
								pos,
								((AlarmTileEntity) worldIn.getTileEntity(pos)).loop,
								((AlarmTileEntity) worldIn.getTileEntity(pos)).distance, 
								"open",
								getListSelectedIndex(((AlarmTileEntity) worldIn.getTileEntity(pos)).selectedSound),
								player.dimension.getId(),
								true);
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

	public static BlockPos position;
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
			boolean isMoving) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);

		this.position=pos;
		//this.selectedSound=((AlarmTileEntity) worldIn.getTileEntity(pos)).selectedSound;
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		Block block = this;

		if (worldIn.isBlockPowered(new BlockPos(x, y, z))) {

			((AlarmTileEntity) worldIn.getTileEntity(pos)).isPowered = true;
			((AlarmTileEntity) worldIn.getTileEntity(pos)).countPlay = 0;

		} else {

			((AlarmTileEntity) worldIn.getTileEntity(pos)).isPowered = false;
			((AlarmTileEntity) worldIn.getTileEntity(pos)).cooldown = 0;

			PlayerEntity player = (PlayerEntity) worldIn.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10,false);

			if (player != null & player instanceof ServerPlayerEntity) {
				// Manda msg pedido para parar o som no cliente
				CompoundNBT tagCompound = new CompoundNBT();
				tagCompound.putString("selectedSound", ((AlarmTileEntity) worldIn.getTileEntity(pos)).selectedSound);
				tagCompound.putString("stop", "stop");
				tagCompound.putString("sound","ambience:"+((AlarmTileEntity) worldIn.getTileEntity(pos)).selectedSound);
							
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
		
		if(sound!=null) {
		// Manda msg pedido para parar o som no cliente
		CompoundNBT tagCompound = new CompoundNBT();
			tagCompound.putString("selectedSound", sound);
			tagCompound.putString("stop", "stop");
			tagCompound.putString("sound","ambience:"+sound);
						
			AmbiencePackageHandler.sendToClient(new MyMessage(tagCompound), (ServerPlayerEntity) player);
		}
	}
	
	//Acende e apaga a luz
	public void setState(boolean active, World worldIn, BlockPos pos, String color, String selectedSound) {		
			BlockState iblockstate = worldIn.getBlockState(pos);
			TileEntity tileentity = worldIn.getTileEntity(pos);	
									
			this.tileEntity=(AlarmTileEntity) tileentity;
			this.color=color;
			
			if (active) {	
				
				if(selectedSound!="")
				Alarm.selectedSound=selectedSound;
								
				switch (color) {
					case "white" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_WHITE_lit.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 2); getLightValue(this.getDefaultState(),worldIn,pos);break;
					case "red" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_RED_lit.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 2); getLightValue(this.getDefaultState(),worldIn,pos);break;
					case "yellow" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_YELLOW_lit.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 2); getLightValue(this.getDefaultState(),worldIn,pos);break;
					case "orange" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_ORANGE_lit.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 2); getLightValue(this.getDefaultState(),worldIn,pos);break;
					case "lime" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_LIME_lit.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 2); getLightValue(this.getDefaultState(),worldIn,pos);break;
					case "green" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_GREEN_lit.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 2); getLightValue(this.getDefaultState(),worldIn,pos);break;
					case "cyan" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_CYAN_lit.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 2); getLightValue(this.getDefaultState(),worldIn,pos);break;
					case "lightblue" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_LIGHTBLUE_lit.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 2); getLightValue(this.getDefaultState(),worldIn,pos);break;
					case "blue" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_BLUE_lit.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 2); getLightValue(this.getDefaultState(),worldIn,pos);break;
					case "purple" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_PURPLE_lit.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 2); getLightValue(this.getDefaultState(),worldIn,pos);break;
					case "magenta" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_MAGENTA_lit.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 2); getLightValue(this.getDefaultState(),worldIn,pos);break;
					case "pink" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_PINK_lit.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 2); getLightValue(this.getDefaultState(),worldIn,pos);break;
					case "brown" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_BROWN_lit.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 2); getLightValue(this.getDefaultState(),worldIn,pos);break;
				}	
				
			} else {
				switch (color) {
					case "lit_white" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_WHITE.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 1);break;
					case "lit_red" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_RED.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 1);break;
					case "lit_yellow" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_YELLOW.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 1);break;
					case "lit_orange" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_ORANGE.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 1);break;
					case "lit_lime" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_LIME.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 1);break;
					case "lit_green" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_GREEN.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 1);break;
					case "lit_cyan" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_CYAN.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 1);break;
					case "lit_lightblue" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_LIGHTBLUE.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 1);break;
					case "lit_blue" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_BLUE.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 1);break;
					case "lit_purple" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_PURPLE.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 1);break;
					case "lit_magenta" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_MAGENTA.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 1);break;
					case "lit_pink" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_PINK.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 1);break;
					case "lit_brown" :worldIn.setBlockState(pos,RegistryHandler.block_Alarm_BROWN.get().getDefaultState().with(FACING, iblockstate.get(FACING)), 1);break;
				}
						
			}
	
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
						
		 AlarmTileEntity alarm=null;
		 
		 if(this.tileEntity!=null)
		 {
			 alarm=this.tileEntity;
			 
				switch(color) {
					case "white" : return new AlarmTileEntity("white",false,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "red" : return new AlarmTileEntity("red",false,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);					
					case "yellow" : return new AlarmTileEntity("yellow",false,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "orange" : return new AlarmTileEntity("orange",false,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "lime" :  return new AlarmTileEntity("lime",false,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "green" : return new AlarmTileEntity("green",false,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "cyan" : return new AlarmTileEntity("cyan",false,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);		
					case "lightblue" : return new AlarmTileEntity("lightblue",false,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "blue" : return new AlarmTileEntity("blue",false,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "purple" : return new AlarmTileEntity("purple",false,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "magenta" : return new AlarmTileEntity("magenta",false,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "pink" : return new AlarmTileEntity("pink",false,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "brown" : return new AlarmTileEntity("brown",false,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					
					case "lit_white" : return new AlarmTileEntity("lit_white",true,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "lit_red" : return new AlarmTileEntity("lit_red",true,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "lit_yellow" : return new AlarmTileEntity("lit_yellow",true,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "lit_orange" : return new AlarmTileEntity("lit_orange",true,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "lit_lime" : return new AlarmTileEntity("lit_lime",true,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "lit_green" : return new AlarmTileEntity("lit_green",true,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "lit_cyan" : return new AlarmTileEntity("lit_cyan",true,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "lit_lightblue" : return new AlarmTileEntity("lit_lightblue",true,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "lit_blue" : return new AlarmTileEntity("lit_blue",true,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "lit_purple" : return new AlarmTileEntity("lit_purple",true,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "lit_magenta" : return new AlarmTileEntity("lit_magenta",true,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "lit_pink" : return new AlarmTileEntity("lit_pink",true,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
					case "lit_brown" : return new AlarmTileEntity("lit_brown",true,alarm.selectedSound,alarm.delay,alarm.distance,alarm.cooldown);
			}
		 }else {
				switch(color) {
					case "white" : return new AlarmTileEntity("white",false);
					case "red" : return new AlarmTileEntity("red",false);
					case "yellow" :return new AlarmTileEntity("yellow",false);
					case "orange" : return new AlarmTileEntity("orange",false);
					case "lime" :  return new AlarmTileEntity("lime",false);
					case "green" : return new AlarmTileEntity("green",false);
					case "cyan" :return new AlarmTileEntity("cyan",false);		
					case "lightblue" : return new AlarmTileEntity("lightblue",false);
					case "blue" :return new AlarmTileEntity("blue",false);
					case "purple" :return new AlarmTileEntity("purple",false);
					case "magenta" :return new AlarmTileEntity("magenta",false);
					case "pink" :return new AlarmTileEntity("pink",false);
					case "brown" : return new AlarmTileEntity("brown",false);

					case "lit_white" : return new AlarmTileEntity("lit_white",true);
					case "lit_red" : return new AlarmTileEntity("lit_red",true);
					case "lit_yellow" : return new AlarmTileEntity("lit_yellow",true);
					case "lit_orange" : return new AlarmTileEntity("lit_orange",true);
					case "lit_lime" : return new AlarmTileEntity("lit_lime",true);
					case "lit_green" : return new AlarmTileEntity("lit_green",true);
					case "lit_cyan" : return new AlarmTileEntity("lit_cyan",true);
					case "lit_lightblue" : return new AlarmTileEntity("lit_lightblue",true);
					case "lit_blue" : return new AlarmTileEntity("lit_blue",true);
					case "lit_purple" : return new AlarmTileEntity("lit_purple",true);
					case "lit_magenta" : return new AlarmTileEntity("lit_magenta",true);
					case "lit_pink" : return new AlarmTileEntity("lit_pink",true);
					case "lit_brown" : return new AlarmTileEntity("lit_brown",true);
			}
		 }
		
	
		//RegistryHandler.BLOCKS.getEntries();
		return ModTileEntityTypes.ALARM_RED_LIT.get().create();
	}
	
}
