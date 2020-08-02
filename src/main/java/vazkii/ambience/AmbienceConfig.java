package vazkii.ambience;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid= Ambience.MODID, bus= Bus.MOD)
public class AmbienceConfig {

	public static class Common{
		
		public final BooleanValue enabled;
				
		public Common(ForgeConfigSpec.Builder builder) {
			builder.comment("Ambience Mod Configurations")
				   .push("Ambience");
			
			enabled =builder.comment("Enables or disables the Ambience music at all")
							.translation("ambience.configgui.enabled")
							.worldRestart()
							.define("enabled", true);
			
			builder.pop();
		}
	}
		
	public static final ForgeConfigSpec COMMON_SPEC;
	public static final Common COMMON;
	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC =  specPair.getRight();
		COMMON = specPair.getLeft();
	}
	
	
	@SubscribeEvent
	public static void onLoad(final ModConfig.Loading event) {
		
	}
	
	@SubscribeEvent
	public static void onFileChanged(final ModConfig.Reloading event) {
		
	}
}
