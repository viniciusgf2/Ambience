package vazkii.ambience.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import vazkii.ambience.Ambience;

public class ItemBase extends Item{

	public ItemBase(int Maxdamage) {
		super(new Item.Properties().group(Ambience.customItemGroup).maxDamage(Maxdamage));
		// TODO Auto-generated constructor stub
	}	
}
