package vaskii.ambience.objects.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vaskii.ambience.Init.BlockInit;
import vaskii.ambience.network4.MyMessage4;
import vaskii.ambience.network4.NetworkHandler4;
import vazkii.ambience.Ambience;
import vazkii.ambience.Util.WorldData;
import vazkii.ambience.Util.Handlers.SoundHandler;
import vazkii.ambience.World.Biomes.Area;
import vazkii.ambience.World.Biomes.Area.Operation;

public class SongSwitcher extends BlockBase {

	public static final PropertyDirection FACING = BlockDirectional.FACING;
	public static final PropertyInteger DIRECTION = PropertyInteger.create("direction", 0, 3);

	public SongSwitcher(String name, Material material,boolean isLit) {
		super(name, material);

		setSoundType(SoundType.WOOD);
		setHarvestLevel("pickaxe", 1);
		setHardness(2F);
		setResistance(10F);
		setLightLevel(0F);
		setLightOpacity(0);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));

		if (isLit) {
			setCreativeTab(null);
		}
		
		if (!FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			I18n.format("SongSwitcher.Desc");
		}
	}
	
	

	@Override
	public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
		super.addInformation(itemstack, world, list, flag);
		list.add(I18n.format("SongSwitcher.Desc"));
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
			case SOUTH :
			default :
				return new AxisAlignedBB(1,1,0,0,0,0.24);
			case NORTH :
				return new AxisAlignedBB(1,1,1,0,0,0.76);
			case WEST :
				return new AxisAlignedBB(1,1,1,0.76,0,0);
			case EAST :
				return new AxisAlignedBB(0,1,1,0.24,0,0);
			case UP :
				return new AxisAlignedBB(0, 0, 0, 1, 0.24, 1);
			case DOWN :
				return new AxisAlignedBB(1,1,1,0,0.76,0);
		}
	}
	
	@Override
	protected net.minecraft.block.state.BlockStateContainer createBlockState() {
		return new net.minecraft.block.state.BlockStateContainer(this, new IProperty[]{FACING,DIRECTION});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		
		switch (meta) {
			//Get the metas for the diretions with facing up
			case 7:return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(1)).withProperty(DIRECTION,0);
			case 8:return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(1)).withProperty(DIRECTION,1);
			case 9:return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(1)).withProperty(DIRECTION,2);
			case 10:return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(1)).withProperty(DIRECTION,3);
			//Get the metas for the diretions with facing down
			case 11:return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(0)).withProperty(DIRECTION,0);
			case 12:return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(0)).withProperty(DIRECTION,1);
			case 13:return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(0)).withProperty(DIRECTION,2);
			case 14:return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(0)).withProperty(DIRECTION,3);
			//Get the metas for the diretions with facing for the rest
			default:return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
		}
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		//Set the meta for the directions up
		if(state.getProperties().values().toArray()[1].toString().contains("up")) {
			switch ((int)state.getProperties().values().toArray()[0]) {
				case 0: return 7;
				case 1: return 8;
				case 2: return 9;
				case 3: return 10;
			}
		}
		
		//Set the meta for the directions down
		if(state.getProperties().values().toArray()[1].toString().contains("down")) {
			switch ((int)state.getProperties().values().toArray()[0]) {
				case 0: return 11;
				case 1: return 12;
				case 2: return 13;
				case 3: return 14;
			}
		}
		
		return ((EnumFacing) state.getValue(FACING)).getIndex();
	}

		
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		
		int dir = MathHelper.floor((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	
		IBlockState iblockstate = super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
	  	iblockstate = iblockstate.withProperty(FACING, facing).withProperty(DIRECTION, dir);
			    
		return iblockstate;		
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}
		
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos fromPos) {
		super.neighborChanged(state, world, pos, neighborBlock, fromPos);
		Area currentArea = Area.getBlockStandingArea(pos);

		if (FMLCommonHandler.instance().getEffectiveSide().isServer())  {		
		
			if (currentArea != null) {
					
				currentArea.setRedstoneStrength(getRedstonePowerFromNeighbors(fromPos, world));
				currentArea.setOperation(Operation.EDIT);
				
				WorldData data = new WorldData().GetArasforWorld(world);
				data.editArea(currentArea);
				data.saveData();				
				Ambience.sync=true;
				
				if (getRedstonePowerFromNeighbors(fromPos, world)!=0) {
					setState(true,world,pos,"red","");
				}else {
					setState(false,world,pos,"red","");					
				}
			}
		}
	}
	
	/*
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		//Check the surrounding area on placement to see if there is power already or not
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		
		Area currentArea = Area.getBlockStandingArea(pos);

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
					setState(true,worldIn,pos,"red","");
				}else {
					setState(false,worldIn,pos,"red","");					
				}
			}
		}
	}*/
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		Area currentArea = Area.getBlockStandingArea(pos);
		
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())  {		
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

				NetworkHandler4.sendToServer(new MyMessage4(currentArea.SerializeThis()));
				
				if (power !=0) {
					setState(true,worldIn,pos,"red","");
				}else {
					setState(false,worldIn,pos,"red","");					
				}
			}
		}
		
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}



	public int getRedstonePowerFromNeighbors(BlockPos pos, World worldIn) {
		int i = 0;
		
		for (EnumFacing direction : FACING.getAllowedValues()) {
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
	public void setState(boolean active, World worldIn, BlockPos pos, String color, String selectedSound) {
		IBlockState iblockstate = worldIn.getBlockState(pos);

		if (active) {
			worldIn.setBlockState(pos,BlockInit.SongSwitcher_lit.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)).withProperty(DIRECTION,iblockstate.getValue(DIRECTION)),2);		

		} else {
			worldIn.setBlockState(pos,BlockInit.SongSwitcher.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)).withProperty(DIRECTION,iblockstate.getValue(DIRECTION)), 2);
		}

	}
		
	// Abre a tela de seleção do som no bloco do speaker
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entity, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		
		
		if (FMLCommonHandler.instance().getSide().isServer() | !world.isRemote) {
			
		
			NBTTagCompound tagCompound = new NBTTagCompound();
			
			tagCompound.setInteger("delay",((SpeakerTileEntity) world.getTileEntity(pos)).delay); 
			tagCompound.setString("selectedSound",((SpeakerTileEntity) world.getTileEntity(pos)).selectedSound); 
			tagCompound.setTag("pos",getPosListTag(pos));
			tagCompound.setBoolean("loop",((SpeakerTileEntity) world.getTileEntity(pos)).loop);
			tagCompound.setFloat("distance",((SpeakerTileEntity) world.getTileEntity(pos)).distance); 
			tagCompound.setString("openGui","open");
			tagCompound.setInteger("index", getListSelectedIndex(((SpeakerTileEntity) world.getTileEntity(pos)).selectedSound));
			
			if(entity.getHeldItemMainhand().getDisplayName().contains("Soundnizer")) {			
				NetworkHandler4.sendToClient(new MyMessage4(tagCompound), (EntityPlayerMP) entity);	
			}
		}		
		
		return true;
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
	public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn)
	{		
		if (FMLCommonHandler.instance().getSide().isServer()) {
			EntityPlayer player = (EntityPlayer) worldIn.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10, false);
			Area currentArea = Area.getBlockStandingArea(pos);
	
			currentArea.setRedstoneStrength(0);
			currentArea.setOperation(Operation.EDIT);

			NetworkHandler4.sendToServer(new MyMessage4(currentArea.SerializeThis()));
		}
		
		super.onBlockDestroyedByExplosion(worldIn, pos, explosionIn);
	}

	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
	{				
		if (FMLCommonHandler.instance().getSide().isClient() | worldIn.isRemote) 
		{
			EntityPlayer player = (EntityPlayer) worldIn.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10, false);
			
			Area currentArea = Area.getBlockStandingArea(pos);

			if(currentArea !=null) {
				currentArea.setRedstoneStrength(0);
				currentArea.setOperation(Operation.EDIT);

				NetworkHandler4.sendToServer(new MyMessage4(currentArea.SerializeThis()));
			}
		}
		
		super.onBlockDestroyedByPlayer(worldIn, pos, state);
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
