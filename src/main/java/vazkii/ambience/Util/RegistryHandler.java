package vazkii.ambience.Util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.ambience.Ambience;
import vazkii.ambience.items.ItemBase;
import vazkii.ambience.items.Soundnizer;
import vazkii.ambience.blocks.*;

public class RegistryHandler {
	
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Ambience.MODID);
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Ambience.MODID);

	public static void init() {
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
		

		BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	//Items
	public static final RegistryObject<Soundnizer> Soundnizer =  ITEMS.register("soundnizer", Soundnizer::new);
	
	

	//Blocks
	public static final RegistryObject<Block> Speaker =  BLOCKS.register("speaker", Speaker::new);
	
	
	//Block Items
	public static final RegistryObject<Item> Speaker_Item = ITEMS.register("speaker", () -> new BlockItemBase(Speaker.get()));
}
