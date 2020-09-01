package vazkii.ambience;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vaskii.ambience.GUI.GuiHandler;
import vaskii.ambience.network4.ClientHandler;
import vaskii.ambience.network4.MyMessage4;
import vaskii.ambience.network4.NetworkHandler4;
import vaskii.ambience.objects.blocks.SpeakerTileEntity;
import vaskii.ambience.tabs.AmbienceTab;
import vazkii.ambience.Util.WorldData;
import vazkii.ambience.Util.Handlers.EventHandlers;
import vazkii.ambience.Util.Handlers.RegistryHandler;
import vazkii.ambience.Util.Handlers.ServerTickHandler;
import vazkii.ambience.World.Biomes.Area;
import vazkii.ambience.proxy.ClientProxy;
import vazkii.ambience.proxy.CommonProxy;

@Mod(modid = Reference.MOD_ID , name = Reference.MOD_NAME , version = Reference.VERSION , dependencies = Reference.DEPENDENCIES, updateJSON = Reference.UPDATEURL )
public class Ambience {

	public static final CreativeTabs AmbienceTab = new AmbienceTab("AmbienceTab");
	
	private static final int WAIT_DURATION = 25;
	public static final int FADE_DURATION = 25;
	public static final int SILENCE_DURATION = 5;

	public static final String[] OBF_MC_MUSIC_TICKER = { "aM", "field_147126_aw", "mcMusicTicker" };
	public static final String[] OBF_MAP_BOSS_INFOS = { "g", "field_184060_g", "mapBossInfos" };

	public static PlayerThread thread;
	
	public static Boolean attacked=false;
	public static Boolean forcePlay=false;
	
	public String currentSong;
	public String nextSong;
	public static int waitTick = WAIT_DURATION;
	public static int fadeOutTicks = FADE_DURATION;
	public static int fadeInTicks = FADE_DURATION-1;
	public static boolean fadeIn = false;
	public static int silenceTicks = 0;
	public static File ambienceDir;
	public static File resourcesDir;
	
	public static Area selectedArea=new Area("Area1");
	public static Area previewArea=new Area("Area1");
	public static int multiArea=0;
	
	private static WorldData worldData=new WorldData();
	
	public static boolean sync=false;	
	public static boolean instantPlaying=false;
	
	public static boolean overideBackMusicDimension=false;
	public static boolean showUpdateNotification=false;
	
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
		
		//proxyClient= new ClientProxy();
		//proxyClient.preInit(event);
		//SongLoader.loadFrom(ambienceDir);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {

		//Server Tick
		FMLCommonHandler.instance().bus().register(new ServerTickHandler());
				
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
		
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if(thread == null)
			return;
		
		if(event.phase == Phase.END) {			
			String songs = SongPicker.getSongsString();
			String song = null;
			
			if(songs != null) {
				if(nextSong == null || !songs.contains(nextSong)) {
					do {
						song = SongPicker.getRandomSong();
					} while(song.equals(currentSong) && songs.contains(","));
				} else
					song = nextSong;
			}
			
			//Fade In gain***************
			if(fadeIn) {
				thread.setGain(PlayerThread.fadeGains[fadeInTicks]);				
			}			
			if(thread.gain<thread.MAX_GAIN & fadeInTicks>0 /*& fadeIn*/) {			
				fadeInTicks--;
				fadeIn=true;
			}else {
				fadeIn=false;
				fadeInTicks= FADE_DURATION-1;		
			}
			//***************
			
			if(songs != null && (!songs.equals(PlayerThread.currentSongChoices) || (song == null && PlayerThread.currentSong != null) || !thread.playing)) {
				if(nextSong != null && nextSong.equals(song))
					waitTick--;				
				
				if (!song.equals(currentSong)) {
					if (currentSong != null && PlayerThread.currentSong != null && !PlayerThread.currentSong.equals(song) && songs.equals(PlayerThread.currentSongChoices))
						currentSong = PlayerThread.currentSong;
					else
						nextSong = song;
				} else if (nextSong != null && !songs.contains(nextSong))
					nextSong = null;
				
				if(waitTick <= 0) {
					if(PlayerThread.currentSong == null) {
						currentSong = nextSong;
						nextSong = null;
						PlayerThread.currentSongChoices = songs;
						changeSongTo(song);
						fadeOutTicks = 0;
						waitTick = WAIT_DURATION;
					} else if(fadeOutTicks < FADE_DURATION) {
						thread.setGain(PlayerThread.fadeGains[fadeOutTicks]);
						fadeOutTicks++;
						silenceTicks = 0;
					} else {
						if(silenceTicks < SILENCE_DURATION) {
							silenceTicks++;
						} else {
							nextSong = null;
							PlayerThread.currentSongChoices = songs;
							changeSongTo(song);
							fadeOutTicks = 0;
							waitTick = WAIT_DURATION;
						}
					}
				}
			} else {
				nextSong = null;
				//thread.setGain(PlayerThread.fadeGains[0]);
				silenceTicks = 0;
				fadeOutTicks = 0;
				waitTick = WAIT_DURATION;
			}
			
			if(thread != null)
				thread.setRealGain();
		}
	}
	
	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
		if(!Minecraft.getMinecraft().gameSettings.showDebugInfo)
			return;
		
		event.getRight().add(null);
		if((dimension>=-1 & dimension<=1) | PlayerThread.currentSong!="null" & nextSong!="null") {
			
			if(PlayerThread.currentSong != null) {
				String name = "Now Playing: " + SongPicker.getSongName(PlayerThread.currentSong);
				event.getRight().add(name);
			}
			if(nextSong != null) {
				String name = "Next Song: " + SongPicker.getSongName(nextSong);
				event.getRight().add(name);
			}
		}
		//String name = "Cooldown: " + SpeakerTileEntity.testCooldown;
		//event.getRight().add(name);
	}
	
	public static int dimension=-25412;
	@SubscribeEvent
	public void onBackgroundMusic(PlaySoundEvent event) {
		
		if(SongLoader.enabled) {
			WorldClient world=Minecraft.getMinecraft().world;
					
			if(world!=null)
				dimension=world.provider.getDimension();
			
			if(event.getSound().getCategory() == SoundCategory.MUSIC)
			if((SongLoader.enabled & (dimension>=-1 & dimension<=1) | overideBackMusicDimension)) {
							
				if(event.isCancelable()) 
					event.setCanceled(true);
				
				event.setResultSound(null);
			}
		}
	}
	
	public void changeSongTo(String song) 
	{		
		//para de tocar as musicas caso esteja em outra dimensão
		if(song=="null") {
			thread.playing=false;
			thread.setGain(0);
		}
		
		currentSong = song;	
		thread.play(song);		
		thread.setGain(PlayerThread.fadeGains[fadeInTicks]);	
		if(!attacked) {
			fadeInTicks= FADE_DURATION-1;		
			fadeIn=true;
		}
	}	
}
