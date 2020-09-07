package vazkii.ambience.Util.Handlers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.ambience.Ambience;

public class SoundHandler {

	public static final List<String> SOUNDS = new ArrayList<String>();

	public SoundHandler() {
	}

	public static void registerSounds() {

		try {

			InputStream stream = new FileInputStream(Ambience.resourcesDir + "\\sounds.json");

			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			JsonParser parser = new JsonParser();
			JsonElement obj = parser.parse(reader);
			
			if (obj != null) {
				String[] arrOfStr = obj.toString().split("\"name\":\"");
				List<String> result = new ArrayList<String>();

				for (int i = 1; i < arrOfStr.length; i++) {
					String SoundName = arrOfStr[i].split("\",\"")[0].substring(9);
					
					ResourceLocation location = new ResourceLocation(Ambience.MODID, SoundName);
					SoundEvent soundEvent = new SoundEvent(location);
					soundEvent.setRegistryName(SoundName);
					ForgeRegistries.SOUND_EVENTS.register(soundEvent);

					SOUNDS.add(SoundName);					
				}

			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		ResourceLocation location = new ResourceLocation(Ambience.MODID, "horn1");
		SoundEvent soundEvent = new SoundEvent(location);
		soundEvent.setRegistryName("horn1");
		ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		
		location = new ResourceLocation(Ambience.MODID, "horn2");
		soundEvent = new SoundEvent(location);
		soundEvent.setRegistryName("horn2");
		ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		
		location = new ResourceLocation(Ambience.MODID, "horn3");
		soundEvent = new SoundEvent(location);
		soundEvent.setRegistryName("horn3");
		ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		
	}
}
