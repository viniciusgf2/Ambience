package vaskii.ambience.objects.blocks;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import vaskii.ambience.Init.BlockInit;
import vaskii.ambience.Init.ItemInit;
//import vaskii.ambience.objects.blocks.Alarm.EnumType;
import vazkii.ambience.Ambience;
import vazkii.ambience.Util.IHasModel;

public class BlockBase extends Block implements IHasModel{

	private static int i=0;
	public Item itemBlock;
	public BlockBase(String name, Material material) {		
		super(material);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(Ambience.AmbienceTab);
		
		BlockInit.BLOCKS.add(this);
		  
		itemBlock = new AlarmItemBLock(this).setRegistryName(this.getRegistryName());		
		ItemInit.ITEMS.add(itemBlock);	
	}
	
	@Override
	public void registerModels() 
	{
		Ambience.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "Inventory");
	}

}
