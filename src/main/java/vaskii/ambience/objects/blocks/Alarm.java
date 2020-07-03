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

	public static final PropertyEnum<Alarm.EnumType> VARIANT = PropertyEnum.<Alarm.EnumType>create("variant",
			Alarm.EnumType.class);

	private boolean red = false;
	public static String selectedSound = null;
	public static int delaySound = 0;
	public static boolean loop = true;
	public static float Distance = 1;
	public static final PropertyDirection FACING = BlockDirectional.FACING;

	public boolean isLit=false;
	
	public Alarm(String name, Material material, boolean isLit) {
		super(name, material);
		this.isLit=isLit;
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
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(VARIANT, EnumType.WHITE));
		
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
				worldIn.setBlockState(pos,BlockInit.block_Alarm_lit.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)).withProperty(VARIANT, EnumType.valueOf(color.toUpperCase())), 2);
			} else {				
				worldIn.setBlockState(pos,BlockInit.block_Alarm.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)));			
			}
	
			if (tileentity != null) {
				tileentity.validate();
				worldIn.setTileEntity(pos, tileentity);
			}		
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{FACING,VARIANT});
	}
	
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
				 		
		IBlockState iblockstate = super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
	    iblockstate = iblockstate.withProperty(FACING, facing).withProperty(VARIANT, Alarm.EnumType.byMetadata(placer.getHeldItemMainhand().getMetadata()));

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
		return new SpeakerTileEntity(true,state.getValue(VARIANT).name,isLit);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new SpeakerTileEntity(true,EnumType.byMetadata(meta).name,isLit);
	}

///////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Gets the metadata of the item this Block can drop. This method is called when
	 * the block gets destroyed. It returns the metadata of the dropped item based
	 * on the old metadata of the block.
	 */
	public int damageDropped(IBlockState state) {
		return ((Alarm.EnumType) state.getValue(VARIANT)).getMetadata();
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood
	 * returns 4 blocks)
	 */
	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (Alarm.EnumType Alarm$enumtype : Alarm.EnumType.values()) {
			items.add(new ItemStack(this, 1, Alarm$enumtype.getMetadata()));
		}
	}
	
	/**
	 * Get the MapColor for this Block and the given BlockState
	 */
	public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return ((Alarm.EnumType) state.getValue(VARIANT)).getMapColor();
	}

	public static enum EnumType implements IStringSerializable {
		WHITE(0, "white", MapColor.QUARTZ),
		RED(1, "red", MapColor.RED),
		ORANGE(2, "orange", MapColor.ORANGE_STAINED_HARDENED_CLAY),
		YELLOW(3, "yellow", MapColor.YELLOW),
		LIME(4, "lime", MapColor.LIME),
		GREEN(5, "green", MapColor.GREEN),
		LIGHTBLUE(6, "lightblue", MapColor.LIGHT_BLUE),
		CYAN(7, "cyan", MapColor.CYAN),
		BLUE(8, "blue", MapColor.BLUE),
		PURPLE(9, "purple", MapColor.PURPLE),
		MAGENTA(10, "magenta", MapColor.MAGENTA),
		PINK(11, "pink", MapColor.PINK),
		BROWN(12, "brown", MapColor.BROWN);

		private static final Alarm.EnumType[] META_LOOKUP = new Alarm.EnumType[values().length];
		private final int meta;
		private final String name;
		private final String unlocalizedName;
		/** The color that represents this entry on a map. */
		private final MapColor mapColor;

		private EnumType(int metaIn, String nameIn, MapColor mapColorIn) {
			this(metaIn, nameIn, nameIn, mapColorIn);
		}

		private EnumType(int metaIn, String nameIn, String unlocalizedNameIn, MapColor mapColorIn) {
			this.meta = metaIn;
			this.name = nameIn;
			this.unlocalizedName = unlocalizedNameIn;
			this.mapColor = mapColorIn;
		}

		public int getMetadata() {
			return this.meta;
		}

		/**
		 * The color which represents this entry on a map.
		 */
		public MapColor getMapColor() {
			return this.mapColor;
		}

		public String toString() {
			return this.name;
		}
		
		public static Alarm.EnumType byMetadata(int meta) {
			if (meta < 0 || meta >= META_LOOKUP.length) {
				meta = 0;
			}

			return META_LOOKUP[meta];
		}

		public String getName() {
			return this.name;
		}

		public String getUnlocalizedName() {
			return this.unlocalizedName;
		}

		static {
			for (Alarm.EnumType Alarm$enumtype : values()) {
				META_LOOKUP[Alarm$enumtype.getMetadata()] = Alarm$enumtype;
			}
		}
	}
}
