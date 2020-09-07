package vazkii.ambience.Util.Handlers;

import com.sun.jna.platform.KeyboardUtils;

import net.minecraft.client.KeyboardListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.VersionChecker.CheckResult;
import net.minecraftforge.fml.VersionChecker.Status;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLCommonLaunchHandler;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import vazkii.ambience.Ambience;
import vazkii.ambience.NilMusicTicker;
import vazkii.ambience.PlayerThread;
import vazkii.ambience.SongLoader;
import vazkii.ambience.SongPicker;
import vazkii.ambience.items.Horn;
import vazkii.ambience.network.AmbiencePackageHandler;
import vazkii.ambience.network.MyMessage;
import vazkii.ambience.render.HornRender;
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
		keyBindings = new KeyBinding[12];
		
		keyBindings[0] = new KeyBinding("Options.Reload", 80 , "Ambience");
		keyBindings[1] = new KeyBinding("Force Play", 79, "Ambience");
		
		keyBindings[2] = new KeyBinding("Options.Shortcut1", 97, "Ambience");
		keyBindings[3] = new KeyBinding("Options.Shortcut2", 97, "Ambience");
		keyBindings[4] = new KeyBinding("Options.Shortcut3", 98, "Ambience");
		keyBindings[5] = new KeyBinding("Options.Shortcut4", 99, "Ambience");
		keyBindings[6] = new KeyBinding("Options.Shortcut5", 100, "Ambience");
		keyBindings[7] = new KeyBinding("Options.Shortcut6", 101, "Ambience");
		keyBindings[8] = new KeyBinding("Options.Shortcut7", 102, "Ambience");
		keyBindings[9] = new KeyBinding("Options.Shortcut8", 103, "Ambience");
		keyBindings[10] = new KeyBinding("Options.Shortcut9", 104, "Ambience");
		keyBindings[11] = new KeyBinding("Options.Shortcut10", 104, "Ambience");

		// register all the key bindings
		for (int i = 0; i < keyBindings.length; ++i) {
			ClientRegistry.registerKeyBinding(keyBindings[i]);
		}
	}
	
/*	@SubscribeEvent
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
		
		
		//KEYBOARD EVENTS HANDLER
		// check each enumerated key binding type for pressed and take appropriate action
		if (keyBindings[0].isPressed()) {
			SongPicker.reset();
			SongLoader.loadFrom(Ambience.ambienceDir);

			Minecraft mc = Minecraft.getInstance();
			MusicTicker ticker = new NilMusicTicker(mc);

			ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, mc, ticker, Ambience.OBF_MC_MUSIC_TICKER);
			
			SystemToast.addOrUpdate(mc.getToastGui(), SystemToast.Type.TUTORIAL_HINT, (ITextComponent) new TranslationTextComponent("Ambience.ReloadTitle"), (ITextComponent) new TranslationTextComponent("Ambience.Reload"));
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
			
			SystemToast.addOrUpdate(mc.getToastGui(), SystemToast.Type.TUTORIAL_HINT, (ITextComponent) new TranslationTextComponent("Ambience.ReloadTitle"), (ITextComponent) new TranslationTextComponent("Ambience.Force"));
		}
		
		
		//Shortcuts keys
		if (keyBindings[2].isPressed()) { ToggleForcePlay(0); }
		if (keyBindings[3].isPressed()) { ToggleForcePlay(1); }
		if (keyBindings[4].isPressed()) { ToggleForcePlay(2); }
		if (keyBindings[5].isPressed()) { ToggleForcePlay(3); }
		if (keyBindings[6].isPressed()) { ToggleForcePlay(4); }
		if (keyBindings[7].isPressed()) { ToggleForcePlay(5); }
		if (keyBindings[8].isPressed()) { ToggleForcePlay(6); }
		if (keyBindings[9].isPressed()) { ToggleForcePlay(7); }
		if (keyBindings[10].isPressed()) { ToggleForcePlay(8); }
		if (keyBindings[11].isPressed()) { ToggleForcePlay(9); }						
	}
	
	private static void ToggleForcePlay(int id) {		
		Minecraft mc = Minecraft.getInstance();		
		if(Ambience.forcePlay) {
			Ambience.forcePlay=false;
		}else {
			Ambience.forcePlay=true;
			//SystemToast.addOrUpdate(mc.getToastGui(), SystemToast.Type.TUTORIAL_HINT, (ITextComponent) new TranslationTextComponent("ForcePlay.Playing"), (ITextComponent) new StringTextComponent(song[0]));
		}
		SongPicker.forcePlayID=id;
		
		if(mc.player.isSneaking())
		{			
			CompoundNBT nbt = new CompoundNBT();
			nbt.putBoolean("forcedPlay", Ambience.forcePlay);
			nbt.putInt("forcedPlayID", SongPicker.forcePlayID);
			AmbiencePackageHandler.sendToServer(new MyMessage(nbt));
		
		}		
	}
	
	@SubscribeEvent
	public static void onBackgroundMusic(final PlaySoundEvent event) {
		
		if(SongLoader.enabled) {
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
				if(Ambience.previewArea.getPos1().x!=0 & Ambience.previewArea.getPos1().y!=0 & Ambience.previewArea.getPos1().z!=0 &
				   Ambience.previewArea.getPos2().x!=0 & Ambience.previewArea.getPos2().y!=0 & Ambience.previewArea.getPos2().z!=0)
					SelectionBoxRenderer.drawBoundingBox(currentplayer.getPositionVector(), Ambience.previewArea.getPos1(),	Ambience.previewArea.getPos2(), true, 2,event.getPartialTicks(),event);
			}

		//Render the Horn sound effect
		HornRender.drawBoundingBox(currentplayer.getPositionVec(), event.getPartialTicks(),event, currentplayer.world,currentplayer);
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



