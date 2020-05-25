package vazkii.ambience.proxy;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vaskii.ambience.objects.blocks.SpeakerTileEntity;

public class CommonProxy {
		
	public static World WorldGlobal;
	
	public void preInit(FMLPreInitializationEvent event) {
	}

	public void init(FMLInitializationEvent event) {
		
	}
	
	public void registerItemRenderer(Item item,int meta,String id) {
		
	}	
	
	public static void registerTileEntities() {
		GameRegistry.registerTileEntity(SpeakerTileEntity.class, new ResourceLocation("ambience:blocks/speaker"));
	}
}
