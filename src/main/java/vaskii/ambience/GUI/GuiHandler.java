package vaskii.ambience.GUI;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id == CreateAreaGUI.GUIID)
			return new CreateAreaGUI.GuiContainerMod(world, x, y, z, player);
		if (id == EditAreaGUI.GUIID)
			return new EditAreaGUI.GuiContainerMod(world, x, y, z, player);
		if (id == SpeakerEditGUI.GUIID)
			return new SpeakerEditGUI.GuiContainerMod(world, x, y, z, player);
		
	//	if (id == SpeakerGUI.GUIID)
		//	return new SpeakerGUI.GuiContainerMod(world, x, y, z, player);

		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id == CreateAreaGUI.GUIID)
			return new CreateAreaGUI.GuiWindow(world, x, y, z, player);
		if (id == EditAreaGUI.GUIID)
			return new EditAreaGUI.GuiWindow(world, x, y, z, player);
		if (id == SpeakerEditGUI.GUIID)
			return new SpeakerEditGUI.GuiWindow(world, x, y, z, player);
		
		//if (id == SpeakerGUI.GUIID)
			//return new SpeakerGUI.GuiWindow(world, x, y, z, player);

		return null;
	}
}