package vazkii.ambience.World.Biomes;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class BiomeShop extends Biome 
{	
	public BiomeShop()
	{
		super(new Biome.BiomeProperties("Shop").setRainfall(0.5F).setBaseHeight(0.1F).setHeightVariation(0.2F).setTemperature(0.5F));
		
		topBlock = Blocks.GRASS.getDefaultState();
		fillerBlock = Blocks.DIRT.getDefaultState();
		decorator.generateFalls = false;
		decorator.treesPerChunk = 3;
		decorator.flowersPerChunk = 10;
		decorator.grassPerChunk = 10;
		decorator.deadBushPerChunk = 0;
		decorator.mushroomsPerChunk = 0;
		decorator.bigMushroomsPerChunk = 0;
		decorator.reedsPerChunk = 0;
		decorator.cactiPerChunk = 0;
		decorator.sandPatchesPerChunk = 0;
		decorator.gravelPatchesPerChunk = 0;
	}
	
	@Override
	public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
		return super.getRandomTreeFeature(rand);
	}

}
