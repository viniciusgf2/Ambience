package vazkii.ambience;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vazkii.ambience.Util.ModContainerTypes;
import vazkii.ambience.Util.ModTileEntityTypes;
import vazkii.ambience.Util.RegistryHandler;
import vazkii.ambience.Util.WorldData;
import vazkii.ambience.Util.Handlers.EventHandlers;
import vazkii.ambience.Util.Handlers.EventHandlersServer;
import vazkii.ambience.Util.Handlers.SoundHandler;
import vazkii.ambience.World.Biomes.Area;
import vazkii.ambience.network.AmbiencePackageHandler;
import vazkii.ambience.network.OcarinaPackageHandler;
import vazkii.ambience.items.Ocarina;

@Mod(Ambience.MODID)
public class Ambience {	
	public static final String MODID = "ambience";
	 
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
	
	public static final String OBF_MC_MUSIC_TICKER = "field_147126_aw";
	public static final String OBF_MAP_BOSS_INFOS ="field_184060_g";

	public static PlayerThread thread;
	
	public static Boolean attacked=false;
	public static Boolean forcePlay=false;	
	public static boolean playingJuckebox=false;

	public static File ambienceDir;
	public static File resourcesDir;
	
	public static Area selectedArea=new Area("Area1");
	public static Area previewArea=new Area("Area1");
	public static int multiArea=0;
	
	private static WorldData worldData=new WorldData();
	
	public static boolean sync=false;	
	public static boolean instantPlaying=false;	
	public static boolean overideBackMusicDimension=false;//Overide the custom dimensions back music if there is custom music
	public static boolean showUpdateNotification=false;	
	public static String dimension="";
	
	public static AmbienceEventEvent ExternalEvent= new AmbienceEventEvent();
		
	public static WorldData getWorldData() {
		return worldData;
	}

	public static void setWorldData(WorldData worldData) {
		Ambience.worldData = worldData;
	}

	public static Ambience instance;
				
	public Ambience() {
				
		//Register the Config File
		ModLoadingContext.get().registerConfig(Type.COMMON, AmbienceConfig.COMMON_SPEC, "ambience-common.toml");
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        //Registra o container das janelas
        IEventBus bus=FMLJavaModLoadingContext.get().getModEventBus();
        ModContainerTypes.CONTAINER_TYPES.register(bus);
        ModTileEntityTypes.TILE_ENTITY_TYPES.register(bus);
          
        //Registra Items/Blocos
        RegistryHandler.init();        
        
        MinecraftForge.EVENT_BUS.register(new EventHandlersServer());
                
        //Init the Network Handler
        AmbiencePackageHandler.register();
        OcarinaPackageHandler.register();
       
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }
		
	//PREINIT
	private void setup(final FMLCommonSetupEvent event)
    {							
		/*File configDir = new File(Paths.get("").toAbsolutePath().toString());
		ambienceDir = new File(configDir, "ambience_music");
		if(!ambienceDir.exists())
			ambienceDir.mkdir();
		
		resourcesDir = new File(configDir.getParentFile(), "resourcepacks\\AmbienceSounds\\assets\\ambience");
			*/
						
    }

    private void doClientStuff(final FMLClientSetupEvent event) {

		EventHandlers.registerKeyBindings();
			
    	SongLoader.loadFrom(ambienceDir);
		
		if(SongLoader.enabled)
			thread = new PlayerThread();
							
		Minecraft mc = Minecraft.getInstance();
		MusicTicker ticker = new NilMusicTicker(mc);
		ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, mc, ticker, OBF_MC_MUSIC_TICKER);
			    	
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {    	
        // some example code to dispatch IMC to another mod
        //InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {    	
        // some example code to receive and process InterModComms from other mods
      /*  LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));*/
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
      //  LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
       /* @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }*/
        
        @SubscribeEvent
    	public static void onSoundRegister(final RegistryEvent.Register<SoundEvent> event) {
        	File configDir = new File(Paths.get("").toAbsolutePath().toString());
    		ambienceDir = new File(configDir, "ambience_music");
    		if(!ambienceDir.exists())
    			ambienceDir.mkdir();
    		
    		resourcesDir = new File(configDir, "resourcepacks\\AmbienceSounds\\assets\\ambience");
    			
        	
        	SoundHandler.registerSounds();
    	}
    }
	
    //Registra a Creative Tab
	public static final ItemGroup customItemGroup= new ItemGroup("AmbienceTab")
	{
	      @Override
	      public ItemStack createIcon() {
	        return new ItemStack(RegistryHandler.Soundnizer.get());
	      }
	}; 
}
