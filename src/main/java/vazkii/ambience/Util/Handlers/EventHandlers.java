package vazkii.ambience.Util.Handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler.Loader;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.VersionChecker.CheckResult;
import net.minecraftforge.fml.VersionChecker.Status;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.versions.forge.ForgeVersion;
import vazkii.ambience.Ambience;
import vazkii.ambience.NilMusicTicker;
import vazkii.ambience.PlayerThread;
import vazkii.ambience.SongLoader;
import vazkii.ambience.SongPicker;

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
	
	Entity currentplayer;
	// public static boolean attacked = false;
	public static KeyBinding[] keyBindings;

	public Ambience ambience;

	public EventHandlers(Ambience amb) {
		this.ambience = amb;

		currentplayer = Minecraft.getInstance().player;		
		keyBindings = new KeyBinding[2];
		
		keyBindings[0] = new KeyBinding("Options.Reload", 80 , "Ambience");
		keyBindings[1] = new KeyBinding("Force Play", 79, "Ambience");

		// register all the key bindings
		for (int i = 0; i < keyBindings.length; ++i) {
			ClientRegistry.registerKeyBinding(keyBindings[i]);
		}
	}
	
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
		
		if((event.getSound().getCategory() == SoundCategory.MUSIC) & (Ambience.dimension>=-1 & Ambience.dimension<=1) | Ambience.overideBackMusicDimension) {
						
			if(event.isCancelable()) 
				event.setCanceled(true);
			
			event.setResultSound(null);
		}
	}
	
	public static void playInstant() {		
		fadeOutTicks = FADE_DURATION;
		silenceTicks = SILENCE_DURATION;
		waitTick = 0;
		Ambience.instantPlaying=true;
			
		Ambience.thread.setGain(PlayerThread.fadeGains[0]);	
		fadeIn=false;		
	}
	
	
	

	int count = 0;
	boolean pressedkey = false;

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (currentplayer == null)
			currentplayer = Minecraft.getInstance().player;

		// check each enumerated key binding type for pressed and take appropriate
		// action
		if (keyBindings[0].isPressed()) {
			SongPicker.reset();
			//Ambience.thread.forceKill();			
			//Ambience.thread.run();
			SongLoader.loadFrom(ambience.ambienceDir);

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
			SongLoader.loadFrom(ambience.ambienceDir);

			if (SongLoader.enabled)
				Ambience.thread = new PlayerThread();

			Minecraft mc = Minecraft.getInstance();
			MusicTicker ticker = new NilMusicTicker(mc);
			ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, mc, ticker, Ambience.OBF_MC_MUSIC_TICKER);
		}
	}

	
	// Events when attacking

	String mobName = null;
	Boolean isHostile = false;
/*
	// FUNCIONA Quando player ataca alguma coisa
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onPlayerAttackEvent(AttackEntityEvent event) {
		mobName = event.getTarget().getName().getString().toLowerCase();

		if (event.getTarget() instanceof MobEntity) {
		//if (event.getTarget().isCreatureType(EnumCreatureType.MONSTER, false)) {
			ambience.attacked = true;
			playInstant();
		} 

	}		
	
	// On something dies
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onEntityDeath(LivingDeathEvent event) {
		DamageSource source = event.getSource();

		// When Player kills something
		if (source.getTrueSource() instanceof PlayerEntity & event.getEntity() == currentplayer) {
			ambience.attacked = false;
		}

		// When Player dies
		if (event.getEntity() instanceof PlayerEntity & event.getEntity() == currentplayer) {
			ambience.attacked = false;
		}

	}	
	

	// Quando alguma coisa ataca o player
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onLivingAttackEvent(LivingAttackEvent event) {

		
	//	System.out.println(event.getEntity().getName());
		
		if(currentplayer!=null)
		if (event.getEntity().getName().contains(currentplayer.getName())) {
			// When something get hurts near the player
			List<EntityLivingBase> entities = Minecraft.getMinecraft().world.getEntitiesWithinAABB(
					EntityLivingBase.class,
					new AxisAlignedBB(event.getEntity().posX - 16, event.getEntity().posY - 16,
							event.getEntity().posZ - 16, event.getEntity().posX + 16, event.getEntity().posY + 16,
							event.getEntity().posZ + 16));
			for (EntityLivingBase mob : entities) {
				mobName = mob.getName().toLowerCase();
								
				// Detects when player gets attacked
				if (mobName != null & !event.getSource().isUnblockable())
					if (mobName.toLowerCase().contains("player") || event.getSource().isProjectile()) {
						ambience.attacked = true;
						playInstant();
					}
			}
		}
	}

		*/
	
	
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
