package vazkii.ambience.Util.Handlers;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vaskii.ambience.Init.BiomeInit;
import vaskii.ambience.Init.BlockInit;
import vaskii.ambience.Init.ItemInit;
import vaskii.ambience.objects.blocks.Alarm;
import vaskii.ambience.objects.blocks.BlockBase;
import vaskii.ambience.render.HornRender;
import vaskii.ambience.render.SelectionBoxRenderer;
import vazkii.ambience.Ambience;
import vazkii.ambience.Util.IHasModel;

@EventBusSubscriber
public class RegistryHandler {

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

	
}
