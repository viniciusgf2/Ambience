package vazkii.ambience.Util.Handlers;

public class SoundHandler {
/*
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
					
					ResourceLocation location = new ResourceLocation(Reference.MOD_ID, SoundName);
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
	}*/
}
