package vaskii.ambience.tabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import vaskii.ambience.Init.BlockInit;
import vaskii.ambience.Init.ItemInit;

public class AmbienceTab extends CreativeTabs {

	public AmbienceTab(String Label) {super("AmbienceTab");
		this.setBackgroundImageName("ambience.png");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(ItemInit.itemSondnizer);
	}
	
}
