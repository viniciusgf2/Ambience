package vaskii.ambience.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import vaskii.ambience.objects.blocks.Alarm.EnumType;
 
public class AlarmItemBLock extends ItemBlock {
    public AlarmItemBLock(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
 
    public int getMetadata(int damage) {
        return damage;
    }
    
    @Override
    public String getUnlocalizedName(ItemStack stack)
    {   
    	if(stack.getItem().getDamage(stack)!=0)
    		return this.block.getUnlocalizedName() + "_" + EnumType.byMetadata(stack.getItem().getDamage(stack)).getName();
    	
        return this.block.getUnlocalizedName();
    }
}