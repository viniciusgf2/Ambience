package vazkii.ambience.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import vazkii.ambience.Ambience;

public class BlockItemBase extends BlockItem {

	public BlockItemBase(Block block) {
		super(block, new Item.Properties().group(Ambience.customItemGroup));
		// TODO Auto-generated constructor stub
	}

}
