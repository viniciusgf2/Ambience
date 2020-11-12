package vazkii.ambience;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager.BiomeEntry;

public class BiomeMapper {
	
	private static Map<String, Biome> biomeMap = null;
	
	public static void applyMappings() {
		/*biomeMap = new HashMap<String, Biome>();
		for(Biome biome : Biome.BIOMES) {
			
			biomeMap.put(biome.getDisplayName().getString(), biome);
		}*/
				
		// Obtains a list of biomes
		biomeMap = new HashMap<String, Biome>();
		
		Collection<BiomeDictionary.Type> biomeTypes = BiomeDictionary.Type.getAll();

		biomeTypes.forEach(bio -> {
			Iterator<BiomeDictionary.Type> iterator1 = bio.getAll().iterator();

			while (iterator1.hasNext()) {
				BiomeDictionary.Type biomeType = iterator1.next();

				Set<RegistryKey<Biome>> biomeT = BiomeDictionary.getBiomes(biomeType);

				Iterator<RegistryKey<Biome>> iterator2 = biomeT.iterator();
				while (iterator2.hasNext()) {
					RegistryKey<Biome> biome = iterator2.next();

					String biomeName = biome.getLocation().getPath();

					if (!biomeMap.containsKey(biomeName)) {						
						biomeMap.put(biome.getLocation().getPath(), BiomeMapper.getBiome(biome.getLocation().getPath()));
											
						System.out.println(biome.getLocation().getPath());
					}
				}
			}
		});
		
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
