package vaskii.ambience.objects.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import vaskii.ambience.Init.ItemInit;
import vazkii.ambience.Ambience;
import vazkii.ambience.Util.IHasModel;

public class ItemBase extends Item implements IHasModel{

	public ItemBase(String name) {
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(Ambience.AmbienceTab);
		
		ItemInit.ITEMS.add(this);
	}
		
	@Override
	public void registerModels() 
	{
		Ambience.proxy.registerItemRenderer(this, 0, "Inventory");
	}
		
}
