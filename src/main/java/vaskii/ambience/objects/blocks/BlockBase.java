package vaskii.ambience.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import vaskii.ambience.Init.BlockInit;
import vaskii.ambience.Init.ItemInit;
import vazkii.ambience.Ambience;
import vazkii.ambience.Util.IHasModel;

public class BlockBase extends Block implements IHasModel{

	public BlockBase(String name, Material material) {		
		super(material);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.REDSTONE);
		
		BlockInit.BLOCKS.add(this);
		ItemInit.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
	}
	
	@Override
	public void registerModels() 
	{
		Ambience.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "Inventory");
	}

}
