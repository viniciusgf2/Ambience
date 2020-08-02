package vazkii.ambience;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BiomeMapper {
	
	private static Map<String, Biome> biomeMap = null;
	
	public static void applyMappings() {
		biomeMap = new HashMap<String, Biome>();
		for(Biome biome : Biome.BIOMES) {
			
			biomeMap.put(biome.getDisplayName().getString(), biome);
		}
	}
	
	public static Biome getBiome(String s) {
		if(biomeMap == null)
			applyMappings();
		return biomeMap.get(s);
	}
	
	public static Type getBiomeType(String s) {
		return BiomeDictionary.Type.getType(s);
	}
	
}
