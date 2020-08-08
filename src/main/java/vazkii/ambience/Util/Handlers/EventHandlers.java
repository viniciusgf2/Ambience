package vazkii.ambience.Util.Handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.VersionChecker.CheckResult;
import net.minecraftforge.fml.VersionChecker.Status;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import vazkii.ambience.Ambience;
import vazkii.ambience.NilMusicTicker;
import vazkii.ambience.PlayerThread;
import vazkii.ambience.SongLoader;
import vazkii.ambience.SongPicker;
import vazkii.ambience.render.SelectionBoxRenderer;

//@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
@Mod.EventBusSubscriber(modid = Ambience.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class EventHandlers {
	
	public static String currentSong;
	public static String nextSong;
	
	//public static final CreativeTabs AmbienceTab = new AmbienceTab("AmbienceTab");
	private static final int WAIT_DURATION = 25;
	public static final int FADE_DURATION = 25;
	public static final int SILENCE_DURATION = 5;
	
	public static int waitTick = WAIT_DURATION;
	public static int fadeOutTicks = FADE_DURATION;
	public static int fadeInTicks = FADE_DURATION-1;
	public static boolean fadeIn = false;
	public static int silenceTicks = 0;
		
	// public static boolean attacked = false;
	public static KeyBinding[] keyBindings;

	public EventHandlers() {		

	}
	
	public static void playInstant() {		
		fadeOutTicks = FADE_DURATION;
		silenceTicks = SILENCE_DURATION;
		waitTick = 0;
		Ambience.instantPlaying=true;
			
		Ambience.thread.setGain(PlayerThread.fadeGains[0]);	
		fadeIn=false;		
	}
	
	public static void registerKeyBindings() {
		keyBindings = new KeyBinding[2];
		
		keyBindings[0] = new KeyBinding("Options.Reload", 80 , "Ambience");
		keyBindings[1] = new KeyBinding("Force Play", 79, "Ambience");

		// register all the key bindings
		for (int i = 0; i < keyBindings.length; ++i) {
			ClientRegistry.registerKeyBinding(keyBindings[i]);
		}
	}

	/*@SubscribeEvent
	public static void clientRegistries(FMLClientSetupEvent event){
		//Registra as telas
		ScreenManager.registerFactory(ModContainerTypes.GUI_CONTAINER.get(), CreateAreaScreen::new);			
	}*/
	
	@SubscribeEvent
	public static void onTick(final ClientTickEvent event) {
		if(Ambience.thread == null)
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
				Ambience.thread.setGain(PlayerThread.fadeGains[fadeInTicks]);				
			}			
			if(Ambience.thread.gain<Ambience.thread.MAX_GAIN & fadeInTicks>0) {			
				fadeInTicks--;
				fadeIn=true;
			}else {
				fadeIn=false;
				fadeInTicks= FADE_DURATION-1;		
			}
			//***************
			
			if(songs != null && (!songs.equals(PlayerThread.currentSongChoices) || (song == null && PlayerThread.currentSong != null) || !Ambience.thread.playing)) {
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
						Ambience.thread.setGain(PlayerThread.fadeGains[fadeOutTicks]);
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
			
			if(Ambience.thread != null)
				Ambience.thread.setRealGain();
		}
	}
	
	@SubscribeEvent
	public static void onBackgroundMusic(final PlaySoundEvent event) {
		
		ClientWorld world=Minecraft.getInstance().world;
			
		if(world!=null)
			Ambience.dimension=world.dimension.getType().getId();
		
		if(event.getSound().getCategory() == SoundCategory.MUSIC)
			if(Ambience.dimension>=-1 & Ambience.dimension<=1 | Ambience.overideBackMusicDimension) {
							
				if(event.isCancelable()) 
					event.setCanceled(true);
				
				event.setResultSound(null);
			}
	}
	
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		// check each enumerated key binding type for pressed and take appropriate
		// action
		if (keyBindings[0].isPressed()) {
			SongPicker.reset();
			//Ambience.thread.forceKill();			
			//Ambience.thread.run();
			SongLoader.loadFrom(Ambience.ambienceDir);

			//if (SongLoader.enabled)
				//Ambience.thread = new PlayerThread();

			Minecraft mc = Minecraft.getInstance();
			MusicTicker ticker = new NilMusicTicker(mc);

			ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, mc, ticker, Ambience.OBF_MC_MUSIC_TICKER);
		}
		
		if (keyBindings[1].isPressed()) {
			//SongPicker.reset();
			Ambience.thread.forceKill();			
			Ambience.thread.run();
			SongLoader.loadFrom(Ambience.ambienceDir);

			if (SongLoader.enabled)
				Ambience.thread = new PlayerThread();

			Minecraft mc = Minecraft.getInstance();
			MusicTicker ticker = new NilMusicTicker(mc);
			ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, mc, ticker, Ambience.OBF_MC_MUSIC_TICKER);
		}
	}

	public static void changeSongTo(String song) 
	{		
		//para de tocar as musicas caso esteja em outra dimensão
		if(song=="null") {
			Ambience.thread.playing=false;
			Ambience.thread.setGain(0);
		}
		
		currentSong = song;	
		Ambience.thread.play(song);		
		Ambience.thread.setGain(PlayerThread.fadeGains[fadeInTicks]);	
		if(!Ambience.attacked) {
			fadeInTicks= FADE_DURATION-1;		
			fadeIn=true;
		}
	}
		
	@SubscribeEvent
	public static void onWorldRenderLast(RenderWorldLastEvent event) {
		PlayerEntity currentplayer = Minecraft.getInstance().player;

		if (Ambience.previewArea != null)
			if (Ambience.previewArea.getPos1() != null & Ambience.previewArea.getPos2() != null) {
				SelectionBoxRenderer.drawBoundingBox(currentplayer.getPositionVector(), Ambience.previewArea.getPos1(),
						Ambience.previewArea.getPos2(), true, 2,event.getPartialTicks(),event);
			}
	}
	
	public static boolean show=false;
	@SubscribeEvent
	public void firstRender(RenderGameOverlayEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (!mc.isGameFocused() || mc.player == null) {
			return;
		}
		
		if (!show & Ambience.showUpdateNotification) {
			show = true;
			for (ModInfo container : ModList.get().getMods()) {
				if (!container.getModId().startsWith("mcp") && !container.getModId().equalsIgnoreCase("mcp") && !container.getModId().equalsIgnoreCase("FML") && !container.getModId().equalsIgnoreCase("Forge")) {
					CheckResult res= VersionChecker.getResult(container);
					
					if ((res != null && res.status != Status.PENDING) && res.status == Status.BETA_OUTDATED || res.status == Status.OUTDATED) {
						String comp = "\u00a7eNew version (\u00a77" + res.target + "\u00a7e) for\u00a7a " + container.getDisplayName() + " \u00a7eis available for download ";
							
						mc.player.sendMessage((ITextComponent)new StringTextComponent(comp));
					}
				}
			}
		}
	}
	
}



