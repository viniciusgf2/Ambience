package vaskii.ambience.GUI;

import java.util.HashMap;

import net.minecraft.client.gui.GuiTextField;
import vaskii.ambience.network4.MyMessage4;
import vaskii.ambience.network4.NetworkHandler4;
import vazkii.ambience.Ambience;
import vazkii.ambience.World.Biomes.Area.Operation;

public class GuiButtonAction {

	public GuiButtonAction(Ambience instance) {

	}

	public static void executeProcedure(java.util.HashMap<String, Object> dependencies) {
				
		HashMap guiinventory = (HashMap) dependencies.get("guiinventory");
		GuiTextField textField = (GuiTextField) guiinventory.get("text:AreaName");
		if (textField != null) {
			Ambience.selectedArea.setName(textField.getText());
		}

		Ambience.selectedArea.setOperation(Operation.CREATE);
		//Send the selected area to the server to save it
		NetworkHandler4.sendToServer(new MyMessage4(Ambience.selectedArea.SerializeThis()));
		Ambience.sync=true;
		Ambience.selectedArea.resetSelection();
	}

}
