package vazkii.ambience;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vaskii.ambience.GUI.GuiHandler;
import vaskii.ambience.network4.ClientHandler;
import vaskii.ambience.network4.MyMessage4;
import vaskii.ambience.network4.NetworkHandler4;
import vaskii.ambience.network4.OcarinaNetworkHandler;
import vaskii.ambience.tabs.AmbienceTab;
import vazkii.ambience.Util.WorldData;
import vazkii.ambience.Util.Handlers.EventHandlers;
import vazkii.ambience.Util.Handlers.RegistryHandler;
import vazkii.ambience.Util.Handlers.EventHandlerServer;
import vazkii.ambience.World.Biomes.Area;
import vazkii.ambience.proxy.ClientProxy;
import vazkii.ambience.proxy.CommonProxy;

@Mod(modid = Reference.MOD_ID , name = Reference.MOD_NAME , version = Reference.VERSION , dependencies = Reference.DEPENDENCIES, updateJSON = Reference.UPDATEURL )
public class Ambience {

	public static final CreativeTabs AmbienceTab = new AmbienceTab("AmbienceTab");
	
	//private static final int WAIT_DURATION = 25;
	//public static final int FADE_DURATION = 25;
	//public static final int SILENCE_DURATION = 5;

	public static final String[] OBF_MC_MUSIC_TICKER = { "aM", "field_147126_aw", "mcMusicTicker" };
	public static final String[] OBF_MAP_BOSS_INFOS = { "g", "field_184060_g", "mapBossInfos" };

	public static PlayerThread thread;
	
	public static Boolean attacked=false;
	public static Boolean forcePlay=false;
	public static boolean playingJuckebox=false;

	public static File ambienceDir;
	public static File resourcesDir;
	
	public static Area selectedArea=new Area("Area1");
	public static Area previewArea=new Area("Area1");
	public static int multiArea=0;
	public static int dimension=-25412;
	
	private static WorldData worldData=new WorldData();
	
	public static boolean sync=false;	
	public static boolean instantPlaying=false;
	
	public static boolean overideBackMusicDimension=false;
	public static boolean showUpdateNotification=false;
	
	//The API for the creation of external events from other mods
	public static AmbienceEventEvent ExternalEvent= new AmbienceEventEvent();
	
	public static WorldData getWorldData() {
		return worldData;
	}

	public static void setWorldData(WorldData worldData) {
		Ambience.worldData = worldData;
	}

	@Instance
	public static Ambience instance;
	
	@SidedProxy(clientSide = Reference.CLIENT , serverSide= Reference.COMMON)
	public static CommonProxy proxy;
	
	
	@SideOnly(Side.CLIENT)
	public static ClientProxy proxyClient;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		//Registra os Biomas
		//RegistryHandler.otherRegistries();

		NetworkHandler4.init();
		NetworkHandler4.INSTANCE.registerMessage(ClientHandler.class, MyMessage4.class, 2, Side.CLIENT);
		
		OcarinaNetworkHandler.init();
				
		//Registra a GUI
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		
		//SyncHandler.init();
		
		proxy.preInit(event);
		proxy.registerTileEntities();
		
		File configDir = event.getSuggestedConfigurationFile().getParentFile();
		ambienceDir = new File(configDir.getParentFile(), "ambience_music");
		if(!ambienceDir.exists())
			ambienceDir.mkdir();
		
		resourcesDir = new File(configDir.getParentFile(), "resourcepacks\\AmbienceSounds\\assets\\ambience");
		
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		
		EventHandlers eventHand=new EventHandlers(this);

		FMLCommonHandler.instance().bus().register(eventHand);
		MinecraftForge.EVENT_BUS.register(eventHand);

		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {

		//Server Tick
		FMLCommonHandler.instance().bus().register(new EventHandlerServer());
				
		
		
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		else
		{			
			SongLoader.loadFrom(ambienceDir);
									
			if(SongLoader.enabled)
				thread = new PlayerThread();
			
			proxy.init(event);
		}
		
		Minecraft mc = Minecraft.getMinecraft();
		MusicTicker ticker = new NilMusicTicker(mc);
		ReflectionHelper.setPrivateValue(Minecraft.class, mc, ticker, OBF_MC_MUSIC_TICKER);		
		
	}	
	
	@EventHandler
	public void initServer(FMLServerStartingEvent event) {
	 	RegistryHandler.serverRegistries(event);
	}
}
