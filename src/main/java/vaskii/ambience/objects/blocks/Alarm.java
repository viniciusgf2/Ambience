package vaskii.ambience.objects.blocks;

import java.util.ArrayList;
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
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
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
	private String color;  
	
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
			return new AxisAlignedBB(0.28D, 0.12D, 0D, 0.12D, 0.28D, 0.12D);
		case NORTH:
			return new AxisAlignedBB(0.25D, 0.25D, 0.43D, 0.75D, 0.75D, 1D);
		case WEST:
			return new AxisAlignedBB(1D, 0.25D, 0.75D, 0.43D, 0.75D, 0.25D);
		case EAST:
			return new AxisAlignedBB(0D, 0.25D, 0.25D, 0.57D, 0.75D, 0.75D);
		case UP:
			return new AxisAlignedBB(0.25D, 0D, 0.25D, 0.75D, 0.57D, 0.75D);
		case DOWN:
			return new AxisAlignedBB(0.75D, 0.43D, 0.75D, 0.25, 1D, 0.25D);
		case SOUTH:
			return new AxisAlignedBB(0.75D, 0.75D, 0D, 0.25D, 0.25D, 0.57D);
		}
	}

	//Acende e apaga a luz
	public void setState(boolean active, World worldIn, BlockPos pos, String color) {		
			IBlockState iblockstate = worldIn.getBlockState(pos);
			TileEntity tileentity = worldIn.getTileEntity(pos);	
						
			this.color=color;
			
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
		
	//Makes the block drop the properly color
	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te,
			ItemStack stack) {
				
		switch(((SpeakerTileEntity)te).color) {
			case "white" : state = BlockInit.block_Alarm_WHITE.getDefaultState();break;
			case "red" :state = BlockInit.block_Alarm_RED.getDefaultState();break;
			case "yellow" :state = BlockInit.block_Alarm_YELLOW.getDefaultState();break;
			case "orange" :state = BlockInit.block_Alarm_ORANGE.getDefaultState();break;
			case "lime" : state = BlockInit.block_Alarm_LIME.getDefaultState();break;
			case "green" :state = BlockInit.block_Alarm_GREEN.getDefaultState();break;
			case "cyan" :state = BlockInit.block_Alarm_CYAN.getDefaultState();break;				
			case "lightblue" :state = BlockInit.block_Alarm_LIGHTBLUE.getDefaultState();break;
			case "blue" :state = BlockInit.block_Alarm_BLUE.getDefaultState();break;
			case "purple" :state = BlockInit.block_Alarm_PURPLE.getDefaultState();break;
			case "magenta" :state = BlockInit.block_Alarm_MAGENTA.getDefaultState();break;
			case "pink" :state = BlockInit.block_Alarm_PINK.getDefaultState();break;
			case "brown" :state = BlockInit.block_Alarm_BROWN.getDefaultState();break;
		}
		
		super.harvestBlock(worldIn, player, pos, state, te, stack);			        
	}
	
	//Makes the block drop the properly color
	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
						
		if (!worldIn.isRemote && !worldIn.restoringBlockSnapshots) // do not drop items while restoring blockstates, prevents item dupe
        {
			List<ItemStack> drops=new ArrayList<ItemStack>();  
			
			switch(state.getBlock().getUnlocalizedName().replace("tile.alarm_", "")) {
				case "white" :drops.add(new ItemStack(BlockInit.block_Alarm_WHITE, 1));break;
				case "red" :drops.add(new ItemStack(BlockInit.block_Alarm_RED, 1));break;
				case "yellow" :drops.add(new ItemStack(BlockInit.block_Alarm_YELLOW, 1));break;
				case "orange" :drops.add(new ItemStack(BlockInit.block_Alarm_ORANGE, 1));break;
				case "lime" : drops.add(new ItemStack(BlockInit.block_Alarm_LIME, 1));break;
				case "green" :drops.add(new ItemStack(BlockInit.block_Alarm_GREEN, 1));break;
				case "cyan" :drops.add(new ItemStack(BlockInit.block_Alarm_CYAN, 1));break;				
				case "lightblue" :drops.add(new ItemStack(BlockInit.block_Alarm_LIGHTBLUE, 1));break;
				case "blue" :drops.add(new ItemStack(BlockInit.block_Alarm_BLUE, 1));break;
				case "purple" :drops.add(new ItemStack(BlockInit.block_Alarm_PURPLE, 1));break;
				case "magenta" :drops.add(new ItemStack(BlockInit.block_Alarm_MAGENTA, 1));break;
				case "pink" :drops.add(new ItemStack(BlockInit.block_Alarm_PINK, 1));break;
				case "brown" :drops.add(new ItemStack(BlockInit.block_Alarm_BROWN, 1));break;
			}
			
            chance = net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(drops, worldIn, pos, state, fortune, chance, false, harvesters.get());

            for (ItemStack drop : drops)
            {
                if (worldIn.rand.nextFloat() <= chance)
                {
                    spawnAsEntity(worldIn, pos, drop);
                }
            }
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
