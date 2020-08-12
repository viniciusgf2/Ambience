package vazkii.ambience.Util.Handlers;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import vazkii.ambience.Ambience;
import vazkii.ambience.Screens.CreateAreaScreen;
import vazkii.ambience.Screens.EditAreaScreen;
import vazkii.ambience.Screens.SpeakerScreen;
import vazkii.ambience.Util.ModContainerTypes;


@Mod.EventBusSubscriber(modid = Ambience.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RegistryHandler {
	
	@SubscribeEvent
	public static void clientRegistries(FMLClientSetupEvent event){
		//Registra as telas
		ScreenManager.registerFactory(ModContainerTypes.CREATEAREA_CONTAINER.get(), CreateAreaScreen::new);		
		ScreenManager.registerFactory(ModContainerTypes.EDITAREA_CONTAINER.get(), EditAreaScreen::new);	
		ScreenManager.registerFactory(ModContainerTypes.SPEAKER_CONTAINER.get(), SpeakerScreen::new);
	}
	
	
}
