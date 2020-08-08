package vazkii.ambience.Screens;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import vazkii.ambience.Util.ModContainerTypes;
import vazkii.ambience.World.Biomes.Area;

public class EditAreaContainer extends Container {

	public static Area currentArea=new Area("Area1");
	public EditAreaContainer(final int windowId, final PlayerInventory playerInventory,final PacketBuffer extraData) {
		super(ModContainerTypes.EDITAREA_CONTAINER.get(), windowId);
	}
	
	 public EditAreaContainer(int id,Area currentArea){
         super(ModContainerTypes.EDITAREA_CONTAINER.get(), id);
         this.currentArea=currentArea;
   }
	
	protected EditAreaContainer(ContainerType<?> type, int id) {
		super(type, id);
		// TODO Auto-generated constructor stub
	}

	public boolean canInteractWith(PlayerEntity player) {
		return true;
	}

	public void onContainerClosed(PlayerEntity playerIn) {
		
	}
}
