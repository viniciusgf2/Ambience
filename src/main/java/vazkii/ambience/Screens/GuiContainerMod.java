package vazkii.ambience.Screens;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import vazkii.ambience.Util.ModContainerTypes;

public class GuiContainerMod extends Container {

	
	public GuiContainerMod(final int windowId, final PlayerInventory playerInventory,final PacketBuffer extraData) {
		super(ModContainerTypes.GUI_CONTAINER.get(), windowId);
	}
	
	 public GuiContainerMod(int id){
         super(ModContainerTypes.GUI_CONTAINER.get(), id);
         // more stuff here
   }
	
	protected GuiContainerMod(ContainerType<?> type, int id) {
		super(type, id);
		// TODO Auto-generated constructor stub
	}

	public boolean canInteractWith(PlayerEntity player) {
		return true;
	}

	public void onContainerClosed(PlayerEntity playerIn) {
		
	}
}
