package vaskii.ambience.objects.blocks;

import java.util.List;

import net.minecraft.advancements.DisplayInfo;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vaskii.ambience.Init.BlockInit;
import vaskii.ambience.Init.ItemInit;

public class Alarm extends Speaker {

	private boolean red = false;
	public static String selectedSound = null;
	public static int delaySound = 0;
	public static boolean loop = true;
	public static float Distance = 1;
	public static final PropertyDirection FACING = BlockDirectional.FACING;

	public int meta=0;
	public boolean isLit=false;
	
	public Alarm(String name, Material material, boolean isLit,int meta) {
		super(name, material);
		this.isLit=isLit;
		this.meta=meta;
		setHarvestLevel("pickaxe", 0);
		setHardness(0.3F);
		setResistance(1.5F);
		setSoundType(SoundType.GLASS);
						
		if (isLit) {
			setCreativeTab(null);
			setLightLevel(1F);

			if (!FMLCommonHandler.instance().getEffectiveSide().isServer()) {
				I18n.format("Alarm.Desc");
			}
		} else {
			setLightLevel(0F);

			if (!FMLCommonHandler.instance().getEffectiveSide().isServer()) {
				I18n.format("Speaker.Desc");
			}
		}

		setLightOpacity(0);
		//this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		
	}
	
	@Override
	public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
		//super.addInformation(itemstack, world, list, flag);
		 list.add(I18n.format("Alarm.Desc"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.SOLID;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch ((EnumFacing) state.getValue(BlockDirectional.FACING)) {

		default:
			return new AxisAlignedBB(0.88D, 0.12D, 0D, 0.12D, 0.88D, 0.12D);
		case NORTH:
			return new AxisAlignedBB(0.13D, 0.13D, 1D, 0.87D, 0.87D, 0.13D);
		case WEST:
			return new AxisAlignedBB(1D, 0.13D, 0.87D, 0.12D, 0.88D, 0.13D);
		case EAST:
			return new AxisAlignedBB(0D, 0.13D, 0.13D, 0.87D, 0.87D, 0.87D);
		case UP:
			return new AxisAlignedBB(0.13D, 0D, 0.13D, 0.87D, 0.87D, 0.87D);
		case DOWN:
			return new AxisAlignedBB(0.87D, 1D, 0.88D, 0.13, 0.13D, 0.13D);
		case SOUTH:
			return new AxisAlignedBB(0.87D, 0.87D, 0D, 0.13D, 0.13D, 0.87D);
		}
	}

	//Acende e apaga a luz
	public void setState(boolean active, World worldIn, BlockPos pos, String color) {		
			IBlockState iblockstate = worldIn.getBlockState(pos);
			TileEntity tileentity = worldIn.getTileEntity(pos);	
								
			if (active) {	
				switch (color) {
					case "white" :worldIn.setBlockState(pos,BlockInit.block_Alarm_lit_WHITE.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 2);break;
					case "red" :worldIn.setBlockState(pos,BlockInit.block_Alarm_lit_RED.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 2);break;
					case "yellow" :worldIn.setBlockState(pos,BlockInit.block_Alarm_lit_YELLOW.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 2);break;
					case "orange" :worldIn.setBlockState(pos,BlockInit.block_Alarm_lit_ORANGE.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 2);break;
					case "lime" :worldIn.setBlockState(pos,BlockInit.block_Alarm_lit_LIME.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 2);break;
					case "green" :worldIn.setBlockState(pos,BlockInit.block_Alarm_lit_GREEN.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 2);break;
					case "cyan" :worldIn.setBlockState(pos,BlockInit.block_Alarm_lit_CYAN.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 2);break;				
					case "lightblue" :worldIn.setBlockState(pos,BlockInit.block_Alarm_lit_LIGHTBLUE.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 2);break;
					case "blue" :worldIn.setBlockState(pos,BlockInit.block_Alarm_lit_BLUE.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 2);break;
					case "purple" :worldIn.setBlockState(pos,BlockInit.block_Alarm_lit_PURPLE.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 2);break;
					case "magenta" :worldIn.setBlockState(pos,BlockInit.block_Alarm_lit_MAGENTA.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 2);break;
					case "pink" :worldIn.setBlockState(pos,BlockInit.block_Alarm_lit_PINK.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 2);break;
					case "brown" :worldIn.setBlockState(pos,BlockInit.block_Alarm_lit_BROWN.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 2);break;
				}
				
				
			} else {				
				worldIn.setBlockState(pos,BlockInit.block_Alarm_WHITE.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)));			
			}
	
			if (tileentity != null) {
				tileentity.validate();
				worldIn.setTileEntity(pos, tileentity);
			}	
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{FACING});
	}
	
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
			
	//	itemBlock.getUnlocalizedName().getRegistryName();
		
		IBlockState iblockstate = super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
	    //iblockstate = iblockstate.withProperty(FACING, facing).withProperty(VARIANT, Alarm.EnumType.byMetadata(placer.getHeldItemMainhand().getMetadata()));
		iblockstate = iblockstate.withProperty(FACING, facing);
		
	    //((SpeakerTileEntity)worldIn.getTileEntity(pos)).color = EnumType.byMetadata(placer.getHeldItemMainhand().getMetadata()).name;		
			    
		return iblockstate;// this.getDefaultState().withProperty(FACING, facing).withProperty(VARIANT, Alarm.EnumType.byMetadata(meta));
	}
	
	// Abre a tela de seleção do som no bloco do speaker
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entity, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		super.onBlockActivated(world, pos, state, entity, hand, side, hitX, hitY, hitZ);

		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {		
		return new SpeakerTileEntity(true,itemBlock.getUnlocalizedName().replace("tile.alarm_", ""),isLit);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new SpeakerTileEntity(true,itemBlock.getUnlocalizedName().replace("tile.alarm_", ""),isLit);
	}


}
