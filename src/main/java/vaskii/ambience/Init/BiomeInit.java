package vaskii.ambience.Init;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import vazkii.ambience.World.Biomes.BiomeHouse;
import vazkii.ambience.World.Biomes.BiomeShop;

public class BiomeInit {
	public static final Biome House = new BiomeHouse();
	public static final Biome Shop = new BiomeShop();
	
	public static void registerBiomes()
	{
		initBiome(House, "House", BiomeType.WARM, Type.RARE);
		initBiome(Shop, "Shop", BiomeType.WARM, Type.RARE);
	}
	
	private static Biome initBiome(Biome biome, String name, BiomeType biomeType,Type type) {
		biome.setRegistryName(name);
		ForgeRegistries.BIOMES.register(biome);
		System.out.println("Biome "+ name + " Registered");
		BiomeDictionary.addTypes(biome, type);
		BiomeManager.addBiome(biomeType, new BiomeEntry(biome,10));
		
		return biome;
	}

}
