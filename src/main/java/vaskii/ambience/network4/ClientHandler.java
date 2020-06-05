package vaskii.ambience.network4;

import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vaskii.ambience.GUI.SpeakerEditGUI;
import vaskii.ambience.GUI.SpeakerGUI;
import vaskii.ambience.objects.blocks.Speaker;
import vaskii.ambience.objects.blocks.SpeakerTileEntity;
import vazkii.ambience.Ambience;
import vazkii.ambience.World.Biomes.Area;

public class ClientHandler implements IMessageHandler<MyMessage4, IMessage> {
	// Do note that the default constructor is required, but implicitly defined in
	// this case

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(MyMessage4 message, MessageContext ctx) {

		
		//Sync Areas between all players when created/ updated/ edited
		/*List<Area> areas = Area.DeSerializeList(message.getToSend());
		if (areas.size() > 0) {
			//if (areas.get(0).getOperation() == null)

			Ambience.getWorldData().listAreas = (Area.DeSerializeList(message.getToSend()));
			
			
			return null;
		}*/
		
		NBTTagCompound BlockSelected = message.getToSend();
						
		//If has selectedSound Get the Speaker updated informations from the server and open the window in the client
		//Else get the list of the areas
		if (BlockSelected.getString("selectedSound") != null & BlockSelected.getBoolean("sync")==false) {

		 	EntityPlayer player= Minecraft.getMinecraft().player;
		 	
			Speaker.delaySound=BlockSelected.getInteger("delay");
			Speaker.selectedSound=BlockSelected.getString("selectedSound");
			Speaker.loop=BlockSelected.getBoolean("loop");
			Speaker.Distance=BlockSelected.getFloat("distance");
			SpeakerEditGUI.SelectedItemIndex=BlockSelected.getInteger("index");
			
		//System.out.println(BlockSelected.getString("GUISpeaker") + BlockSelected.getString("selectedSound") +  BlockSelected.getInteger("delay"));
						
			BlockPos pos = new BlockPos(0,0,0);
				
			NBTTagList tagListPos = BlockSelected.getTagList("pos", 10);
			Iterator<NBTBase> iterator2 = tagListPos.iterator();
			while (iterator2.hasNext()) {
				NBTTagCompound posCompound = (NBTTagCompound) iterator2.next();
				BlockPos pos1 = new BlockPos(posCompound.getInteger("x"), posCompound.getInteger("y"),posCompound.getInteger("z"));
				pos=pos1;
			}
			
			//if (player.getHeldItemMainhand().getDisplayName().contains("Soundnizer")) {
			if(BlockSelected.getString("openGui").contains("open")){
				player.openGui(Ambience.instance, 3, player.world,pos.getX(), pos.getY(), pos.getZ());	
					
				//Minecraft.getMinecraft().displayGuiScreen(new SpeakerGUI(null, Minecraft.getMinecraft().gameSettings, Minecraft.getMinecraft().getLanguageManager()));
			}
			
			//Para de tocar um som no cliente se não estiver recebendo sinal de redstone		
			if (BlockSelected.getString("stop").contains("stop")) {
					//Minecraft.getMinecraft().getSoundHandler().stopSounds();
					Minecraft.getMinecraft().getSoundHandler().stop("ambience:"+BlockSelected.getString("selectedSound"), SoundCategory.NEUTRAL);
			}
			
			
		} else {

			if (BlockSelected.getString("selectedSound") == "") {
				Ambience.getWorldData().listAreas = Area.DeSerializeList(message.getToSend());
				if(Ambience.selectedArea!=null)
				Ambience.selectedArea.resetSelection();
			}
		}

		return null;
	}
}
