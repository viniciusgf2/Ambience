package vazkii.ambience;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import vazkii.ambience.Util.Handlers.EventHandlers;

@Mod.EventBusSubscriber(modid= Ambience.MODID, bus= Bus.MOD)
public class AmbienceConfig {

	public static class Common{
				
		//public final BooleanValue enabled;
		public final BooleanValue sunsong_enabled;
		public final BooleanValue songofstorms_enabled;
		public final BooleanValue bolerooffire_enabled;
		public final BooleanValue horsesong_enabled;
		
		public final BooleanValue preludeoflight_enabled;
		public final BooleanValue serenadeofwater;
		public final BooleanValue minuetofforest;
		
		public final IntValue fadeDuration;
				
		public Common(ForgeConfigSpec.Builder builder) {
		
			builder.comment("Ambience Mod Configurations")
				   .push("Ambience");
			
			/*enabled =builder.comment("Enables or disables the Ambience music at all [Default:true]")
							.translation("ambience.configgui.enabled")
							.worldRestart()
							.define("enabled", true);*/
			
			fadeDuration = builder.comment("Defines the sound volume fade in/out duration [Default:25]")
					.worldRestart()
					.defineInRange("Fade_Duration",25,1,500);
			
			builder.comment("Ocarina Configurations")
			   .push("Ocarina");
			
			sunsong_enabled =builder.comment("Enables or disables the Sun's Song [Default:true]")
					.translation("ambience.configgui.sunsongenabled")
					.worldRestart()
					.define("Sun_Song", true);
			
			songofstorms_enabled =builder.comment("Enables or disables the Song of Storms [Default:true]")
					.translation("ambience.configgui.songofstorms_enabled")
					.worldRestart()
					.define("Song_of_Storms", true);
			
			bolerooffire_enabled =builder.comment("Enables or disables the Bolero of Fire Song [Default:true]")
					.translation("ambience.configgui.bolerooffire_enabled")
					.worldRestart()
					.define("Fire_Song", true);
			
			horsesong_enabled =builder.comment("Enables or disables the Horse's Song [Default:true]")
					.translation("ambience.configgui.horsesong_enabled")
					.worldRestart()
					.define("Horse_Song", true);
			//----------
			preludeoflight_enabled =builder.comment("Enables or disables the Prelude of Light Song [Default:true]")
					.translation("ambience.configgui.preludeoflight_enabled")
					.worldRestart()
					.define("Prelude_of_light_Song", true);
			
			serenadeofwater =builder.comment("Enables or disables the Serenade of Water Song [Default:true]")
					.translation("ambience.configgui.serenadeofwater_enabled")
					.worldRestart()
					.define("Serenade_of_Water_Song", true);
			
			minuetofforest =builder.comment("Enables or disables the Minuet of Forest Song [Default:true]")
					.translation("ambience.configgui.horsesong_enabled")
					.worldRestart()
					.define("Minuet_of_Forest_Song", true);

			builder.pop(2);			
		}
	}
		
	public static final ForgeConfigSpec COMMON_SPEC;
	public static final Common COMMON;
	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON = specPair.getLeft();
		COMMON_SPEC =  specPair.getRight();
	}
	
	
	@SubscribeEvent
	public static void onLoad(final ModConfig.Loading event) {
		
	}
	
	@SubscribeEvent
	public static void onFileChanged(final ModConfig.Reloading event) {
		ModConfig configs=event.getConfig();
		
		if(configs.getConfigData().get("Ambience.Fade_Duration")!=null) {
			AmbienceConfig.COMMON.fadeDuration.set(configs.getConfigData().get("Ambience.Fade_Duration"));	
			AmbienceConfig.COMMON.sunsong_enabled.set(configs.getConfigData().get("Ambience.Ocarina.Sun_Song"));
			AmbienceConfig.COMMON.songofstorms_enabled.set(configs.getConfigData().get("Ambience.Ocarina.Song_of_Storms"));
			AmbienceConfig.COMMON.bolerooffire_enabled.set(configs.getConfigData().get("Ambience.Ocarina.Fire_Song"));
			AmbienceConfig.COMMON.horsesong_enabled.set(configs.getConfigData().get("Ambience.Ocarina.Horse_Song"));
			
			//Reloads the Thread --------------------
			EventHandlers.FADE_DURATION= AmbienceConfig.COMMON.fadeDuration.get();			
			EventHandlers.fadeOutTicks = EventHandlers.FADE_DURATION;
			EventHandlers.fadeInTicks = EventHandlers.FADE_DURATION-1;
			
			/*Ambience.thread.forceKill();			
			Ambience.thread.run();
			SongLoader.loadFrom(Ambience.ambienceDir);

			if (SongLoader.enabled)
				Ambience.thread = new PlayerThread();

			Minecraft mc = Minecraft.getInstance();
			MusicTicker ticker = new NilMusicTicker(mc);
			ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, mc, ticker, Ambience.OBF_MC_MUSIC_TICKER);*/
			
			PlayerThread.fadeGains = new float[EventHandlers.FADE_DURATION];
			float totaldiff = PlayerThread.MIN_GAIN - PlayerThread.MAX_GAIN;
			float diff = totaldiff / PlayerThread.fadeGains.length;
			for(int i = 0; i < PlayerThread.fadeGains.length; i++)
				PlayerThread.fadeGains[i] = PlayerThread.MAX_GAIN + diff * i;
			
			//---------------------------------------
		}
		System.out.println("Configs File Changed");
	}
}
