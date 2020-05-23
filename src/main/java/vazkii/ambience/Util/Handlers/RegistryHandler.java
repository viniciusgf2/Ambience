package vazkii.ambience.Util.Handlers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vaskii.ambience.Init.BiomeInit;
import vaskii.ambience.Init.BlockInit;
import vaskii.ambience.Init.ItemInit;
import vaskii.ambience.objects.blocks.SpeakerTileEntity;
import vaskii.ambience.render.SelectionBoxRenderer;
import vazkii.ambience.Ambience;
import vazkii.ambience.Reference;
import vazkii.ambience.Util.IHasModel;

@EventBusSubscriber
public class RegistryHandler {
	public static void otherRegistries() {
		BiomeInit.registerBiomes();
	}

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
				SelectionBoxRenderer.drawBoundingBox(currentplayer.getPositionVector(), Ambience.previewArea.getPos1(),Ambience.previewArea.getPos2(), true, 2);
			}
	}	
}
