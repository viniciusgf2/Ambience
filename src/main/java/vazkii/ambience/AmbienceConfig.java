package vazkii.ambience;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Reference.MOD_ID)
@Mod.EventBusSubscriber
public class AmbienceConfig {
	
	@Config.Name("Water Dripping")
	@Config.Comment("Enables or disables the Water Dripping Sound Effect [Default:true]")			
	public static Boolean waterDripping_enabled = true;
	
	@Config.Name("Lava Dripping")
	@Config.Comment("Enables or disables the Lava Dripping Sound Effect [Default:true]")			
	public static Boolean lavaDripping_enabled = true;
	
	@Config.Name("Fade Duration")
	@Config.Comment("Defines the sound volume fade in/out duration [Default:25]")			
	public static int fadeDuration = 25;

	@Config.Name("Fade Duration")
	@Config.Comment("Defines the distance in blocks between the player and hostile mobs to determine if still in combat or not [Default:16,Range:10~128]")	
	public static int attackedDistance = 16;
	
	@Config.Name("Lost Focus Fade Out")
	@Config.Comment("Fade Out Sound Volume on Game Lost Focus[Default:true]")			
	public static Boolean lostFocusEnabled = true;

	@Config.Name("Cinematic Transitions")
	@Config.Comment("Show a cinematic enty with a image on enter a structure[Default:true]")		
	public static Boolean structuresCinematic = true;

		@Config(category = "Ocarina", modid = Reference.MOD_ID)
		public static class OcarinaMusics {
			
			@Config.Name("Sun's Song")
			@Config.Comment("Enables or disables the Sun's Song Effects [Default:true]")			
			public static Boolean sunsong_enabled = true;

			@Config.Name("Song of Storms")
			@Config.Comment("Enables or disables the Song of Storms Effects [Default:true]")
			public static Boolean songofstorms_enabled = true;

			@Config.Name("Bolero of Fire")
			@Config.Comment("Enables or disables the Bolero of Fire Song Effects [Default:true]")
			public static  Boolean bolerooffire_enabled = true;

			@Config.Name("Horse's Song")
			@Config.Comment("Enables or disables the Horse's Song Effects [Default:true]")
			public static Boolean horsesong_enabled = true;

			@Config.Name("Prelude of Light")
			@Config.Comment("Enables or disables the Prelude of Light Song Effects [Default:true]")
			public static Boolean preludeoflight_enabled = true;

			@Config.Name("Serenade of Water")
			@Config.Comment("Enables or disables the Serenade of Water Song Effects [Default:true]")
			public static Boolean serenadeofwater = true;

			@Config.Name("Minuet of Forest")
			@Config.Comment("Enables or disables the Minuet of Forest Song Effects [Default:true]")
			public static Boolean minuetofforest = true;

		}

		@SubscribeEvent
		public static void configChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
			if (e.getModID().equals(Reference.MOD_ID)) {
				ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
			}
		}
	
}
