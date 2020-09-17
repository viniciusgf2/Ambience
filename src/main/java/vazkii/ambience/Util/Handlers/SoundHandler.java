package vazkii.ambience.Util.Handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import vazkii.ambience.Ambience;
import vazkii.ambience.Reference;

public class SoundHandler {
//public class SoundHandler extends MovingSound {

	// public static SoundEvent secret;

	public static final List<String> SOUNDS = new ArrayList<String>();

	public SoundHandler() {
		// super(SoundEvents.ENTITY_MINECART_RIDING, SoundCategory.NEUTRAL);
	}

	public static void registerSounds() {

		/*
		 * ResourceLocation location = new ResourceLocation(Reference.MOD_ID, "secret");
		 * SoundEvent soundEvent = new SoundEvent(location);
		 * soundEvent.setRegistryName("secret");
		 * ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		 */

		// TESTE
		// SOUNDS.add(soundEvent);

		try {

			InputStream stream = new FileInputStream(Ambience.resourcesDir + "\\sounds.json");

			// BufferedReader reader = new BufferedReader(new
			// InputStreamReader(ResourceLocation.class.getClassLoader().getResourceAsStream("assets/ambience/sounds.json"),"UTF-8"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			JsonParser parser = new JsonParser();
			JsonElement obj = parser.parse(reader);
			
			if (obj != null) {
				String[] arrOfStr = obj.toString().split("\"name\":\"");
				List<String> result = new ArrayList<String>();

				for (int i = 1; i < arrOfStr.length; i++) {
					String SoundName = arrOfStr[i].split("\",\"")[0].substring(9);
					
					ResourceLocation location = new ResourceLocation(Reference.MOD_ID, SoundName);
					SoundEvent soundEvent = new SoundEvent(location);
					soundEvent.setRegistryName(SoundName);
					ForgeRegistries.SOUND_EVENTS.register(soundEvent);

					SOUNDS.add(SoundName);					
				}

			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ResourceLocation location = new ResourceLocation(Reference.MOD_ID, "bell1");
		SoundEvent soundEvent = new SoundEvent(location);
		soundEvent.setRegistryName("bell1");
		ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		SOUNDS.add("bell1");
		
		location = new ResourceLocation(Reference.MOD_ID, "bell2");
		soundEvent = new SoundEvent(location);
		soundEvent.setRegistryName("bell2");
		ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		SOUNDS.add("bell2");
		
		location = new ResourceLocation(Reference.MOD_ID, "wdrop1");
		soundEvent = new SoundEvent(location);
		soundEvent.setRegistryName("wdrop1");
		ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		SOUNDS.add("wdrop1");
		
		location = new ResourceLocation(Reference.MOD_ID, "wdrop2");
		soundEvent = new SoundEvent(location);
		soundEvent.setRegistryName("wdrop2");
		ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		SOUNDS.add("wdrop2");
		
		location = new ResourceLocation(Reference.MOD_ID, "wdrop3");
		soundEvent = new SoundEvent(location);
		soundEvent.setRegistryName("wdrop3");
		ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		SOUNDS.add("wdrop3");
		
		location = new ResourceLocation(Reference.MOD_ID, "wdrop4");
		soundEvent = new SoundEvent(location);
		soundEvent.setRegistryName("wdrop4");
		ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		SOUNDS.add("wdrop4");
		
		location = new ResourceLocation(Reference.MOD_ID, "horn1");
		soundEvent = new SoundEvent(location);
		soundEvent.setRegistryName("horn1");
		ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		
		location = new ResourceLocation(Reference.MOD_ID, "horn2");
		soundEvent = new SoundEvent(location);
		soundEvent.setRegistryName("horn2");
		ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		
		location = new ResourceLocation(Reference.MOD_ID, "horn3");
		soundEvent = new SoundEvent(location);
		soundEvent.setRegistryName("horn3");
		ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		
		for(int i=1;i<=5;i++) {
			location = new ResourceLocation(Reference.MOD_ID, "ocarina"+i);
			soundEvent = new SoundEvent(location);
			soundEvent.setRegistryName("ocarina"+i);
			ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		}
		
		location = new ResourceLocation(Reference.MOD_ID, "match_sound");
		soundEvent = new SoundEvent(location);
		soundEvent.setRegistryName("match_sound");
		ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		
		location = new ResourceLocation(Reference.MOD_ID, "sunssong");
		soundEvent = new SoundEvent(location);
		soundEvent.setRegistryName("sunssong");
		ForgeRegistries.SOUND_EVENTS.register(soundEvent);	
		
		location = new ResourceLocation(Reference.MOD_ID, "songofstorms");
		soundEvent = new SoundEvent(location);
		soundEvent.setRegistryName("songofstorms");
		ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		
		location = new ResourceLocation(Reference.MOD_ID, "bolerooffire");
		soundEvent = new SoundEvent(location);
		soundEvent.setRegistryName("bolerooffire");
		ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		
		location = new ResourceLocation(Reference.MOD_ID, "horsesong");
		soundEvent = new SoundEvent(location);
		soundEvent.setRegistryName("horsesong");
		ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		
		location = new ResourceLocation(Reference.MOD_ID, "preludeoflight");
		soundEvent = new SoundEvent(location);
		soundEvent.setRegistryName("preludeoflight");
		ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		
		location = new ResourceLocation(Reference.MOD_ID, "serenadeofwater");
		soundEvent = new SoundEvent(location);
		soundEvent.setRegistryName("serenadeofwater");
		ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		
		location = new ResourceLocation(Reference.MOD_ID, "minuetofforest");
		soundEvent = new SoundEvent(location);
		soundEvent.setRegistryName("minuetofforest");
		ForgeRegistries.SOUND_EVENTS.register(soundEvent);
	}

	/*
	 * try { List<String> result = new ArrayList<String>();
	 * 
	 * File config = new File(Ambience.ambienceDir, "ambience.properties");
	 * 
	 * Properties props = new Properties(); props.load(new FileReader(config));
	 * 
	 * String[] songs = props.getProperty("speaker").split(","); for (int i = 0; i <
	 * songs.length; i++) { result.add(songs[i]); }
	 * 
	 * for (int i = 0; i < result.size(); i++) {
	 * 
	 * ResourceLocation location = new ResourceLocation(Reference.MOD_ID,
	 * result.get(i)); SoundEvent soundEvent = new SoundEvent(location);
	 * soundEvent.setRegistryName(result.get(i));
	 * ForgeRegistries.SOUND_EVENTS.register(soundEvent);
	 * 
	 * SOUNDS.add(result.get(i)); }
	 * 
	 * } catch (IOException e) { System.out.println("###### Error: " +
	 * e.getMessage()); e.printStackTrace(); }
	 */

}
