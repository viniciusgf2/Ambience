package vazkii.ambience.proxy;

import net.minecraft.entity.player.PlayerEntity;

public class ClientProxy extends CommonProxy {

	private static PlayerEntity player;
	
	public static PlayerEntity getPlayer() {
		return player;
	}

	public static void setPlayer(PlayerEntity player) {
		ClientProxy.player = player;
	}
	
	/*
	@Override
	public void registerItemRenderer(Item item, int meta, String id) 
	{
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(),id));
	}
		
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		OBJLoader.INSTANCE.addDomain("ambience");
		player=Minecraft.getInstance().player;
	}*/

}
