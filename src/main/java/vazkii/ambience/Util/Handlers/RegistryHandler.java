package vazkii.ambience.Util.Handlers;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import vazkii.ambience.Ambience;
import vazkii.ambience.Screens.CreateAreaScreen;
import vazkii.ambience.Util.ModContainerTypes;


@Mod.EventBusSubscriber(modid = Ambience.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RegistryHandler {
	
	@SubscribeEvent
	public static void clientRegistries(FMLClientSetupEvent event){
		//Registra as telas
		ScreenManager.registerFactory(ModContainerTypes.GUI_CONTAINER.get(), CreateAreaScreen::new);			
	}
	
	// Register the Sub-Blocks for the Alarm here
	/*@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void otherRegistries(ModelRegistryEvent event) {
		// BiomeInit.registerBiomes();

		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {

			// Registra os sub-blocks do Alarm e Alarm Lit
			BlockBase block = BlockInit.BLOCKS.get(1);

			int i = 0;
			List<IBlockState> values = block.getBlockState().getValidStates();
			for (IBlockState state : values) {

				StateMapperBase statemapper = new DefaultStateMapper();

				String test = block.getRegistryName().getResourcePath();

				String variant = statemapper.getPropertyString(state.getProperties()).split("variant=")[1];

				ModelLoader.setCustomModelResourceLocation(block.itemBlock, i,
						new ModelResourceLocation(
								block.getRegistryName().getResourceDomain() + ":"
										+ block.getRegistryName().getResourcePath() + "_" + variant,
								statemapper.getPropertyString(state.getProperties())));
				i++;
			}

			block = BlockInit.BLOCKS.get(2);

			i = 0;
			values = block.getBlockState().getValidStates();
			for (IBlockState state : values) {

				StateMapperBase statemapper = new DefaultStateMapper();

				String test = block.getRegistryName().getResourcePath();

				String variant = statemapper.getPropertyString(state.getProperties()).split("variant=")[1];

				ModelLoader.setCustomModelResourceLocation(block.itemBlock, i,
						new ModelResourceLocation(
								block.getRegistryName().getResourceDomain() + ":"
										+ block.getRegistryName().getResourcePath() + "_" + variant,
								statemapper.getPropertyString(state.getProperties())));
				i++;
			}
		}
	}*/

	/*
	@SubscribeEvent
	public static void onSoundRegister(RegistryEvent.Register<SoundEvent> event) {
		SoundHandler.registerSounds();
	}

	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(ItemInit.ITEMS.toArray(new Item[0]));
	}

	@SubscribeEvent
	public static void onBlockRegister(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(BlockInit.BLOCKS.toArray(new Block[0]));
	}

	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event) {
		for (Item item : ItemInit.ITEMS) {
			if (item instanceof IHasModel) {
				((IHasModel) item).registerModels();
			}
		}

		for (Block block : BlockInit.BLOCKS) {
			if (block instanceof IHasModel) {
				((IHasModel) block).registerModels();
			}
		}

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onWorldRenderLast(RenderWorldLastEvent event) {
		EntityPlayerSP currentplayer = Minecraft.getMinecraft().player;

		if (Ambience.previewArea != null)
			if (Ambience.previewArea.getPos1() != null & Ambience.previewArea.getPos2() != null) {
				SelectionBoxRenderer.drawBoundingBox(currentplayer.getPositionVector(), Ambience.previewArea.getPos1(),
						Ambience.previewArea.getPos2(), true, 2,event.getPartialTicks());
			}
	}*/
}
