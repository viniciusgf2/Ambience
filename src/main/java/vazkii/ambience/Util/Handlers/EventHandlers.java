package vazkii.ambience.Util.Handlers;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Block;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.client.GameSettings;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.event.sound.SoundEvent.SoundSourceEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
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
import vazkii.ambience.AmbienceConfig;
import vazkii.ambience.NilMusicTicker;
import vazkii.ambience.PlayerThread;
import vazkii.ambience.SongLoader;
import vazkii.ambience.SongPicker;
import vazkii.ambience.Util.Utils;
import vazkii.ambience.items.Ocarina;
import vazkii.ambience.network.AmbiencePackageHandler;
import vazkii.ambience.network.MyMessage;
import vazkii.ambience.network.OcarinaMessage;
import vazkii.ambience.network.OcarinaPackageHandler;
import vazkii.ambience.render.CinematicRender;
import vazkii.ambience.render.HornRender;
import vazkii.ambience.render.SelectionBoxRenderer;
import vazkii.ambience.Util.RegistryHandler;

//@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
@Mod.EventBusSubscriber(modid = Ambience.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class EventHandlers {
	
	public static String currentSong;
	public static String nextSong;
	
	//public static final CreativeTabs AmbienceTab = new AmbienceTab("AmbienceTab");
	private static final int WAIT_DURATION = 25;
	public static int FADE_DURATION = AmbienceConfig.COMMON.fadeDuration.get();
	public static final int SILENCE_DURATION = 5;
	
	public static int waitTick = WAIT_DURATION;
	public static int fadeOutTicks = FADE_DURATION;
	public static int fadeInTicks = FADE_DURATION-1;
	public static boolean fadeIn = false;
	public static int silenceTicks = 0;
	private static float masterAudioCount=0;
		
	public static KeyBinding[] keyBindings;
	public static float oldVolume;
	

	public static CinematicRender cinematic=new CinematicRender();

	public EventHandlers() {		

	}
	
	//add the items to the Loot tables
	@SubscribeEvent
	public static void onLootLoad(LootTableLoadEvent event) {
	    if (event.getName().equals(new ResourceLocation("minecraft:chests/simple_dungeon"))) {
	       event.getTable().addPool(LootPool.builder().addEntry(TableLootEntry.builder(new ResourceLocation("ambience:chests/simple_dungeon"))).build());
	    }
	    else if (event.getName().equals(new ResourceLocation("minecraft:chests/pillager_outpost"))) {
	       event.getTable().addPool(LootPool.builder().addEntry(TableLootEntry.builder(new ResourceLocation("ambience:chests/pillager_outpost"))).build());
	    }
	    else if (event.getName().equals(new ResourceLocation("minecraft:chests/underwater_ruin_big")))
	    {
		       event.getTable().addPool(LootPool.builder().addEntry(TableLootEntry.builder(new ResourceLocation("ambience:chests/underwater_ruin_big"))).build());
		}
	    else if (event.getName().equals(new ResourceLocation("minecraft:chests/buried_treasure")))
	    {
		       event.getTable().addPool(LootPool.builder().addEntry(TableLootEntry.builder(new ResourceLocation("ambience:chests/buried_treasure"))).build());
		}
	    else if (event.getName().equals(new ResourceLocation("minecraft:chests/abandoned_mineshaft")))
	    {
		       event.getTable().addPool(LootPool.builder().addEntry(TableLootEntry.builder(new ResourceLocation("ambience:chests/abandoned_mineshaft"))).build());
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
	
	public static void registerKeyBindings() {
		GameSettings settings = Minecraft.getInstance().gameSettings;
		oldVolume=settings.getSoundLevel(SoundCategory.MASTER);
		
		keyBindings = new KeyBinding[12];
		
		keyBindings[0] = new KeyBinding("Options.Reload", 80 , "Ambience");
		keyBindings[1] = new KeyBinding("Force Play", 79, "Ambience");
		
		//Shorcuts
		keyBindings[2] = new KeyBinding("Options.Shortcut1", 97, "Ambience");
		keyBindings[3] = new KeyBinding("Options.Shortcut2", 97, "Ambience");
		keyBindings[4] = new KeyBinding("Options.Shortcut3", 98, "Ambience");
		keyBindings[5] = new KeyBinding("Options.Shortcut4", 99, "Ambience");
		keyBindings[6] = new KeyBinding("Options.Shortcut5", 100, "Ambience");		
		//Ocarina
		keyBindings[7] = new KeyBinding("Options.Ocarina1", 265, "Ambience");
		keyBindings[8] = new KeyBinding("Options.Ocarina2", 264, "Ambience");
		keyBindings[9] = new KeyBinding("Options.Ocarina3", 262, "Ambience");
		keyBindings[10] = new KeyBinding("Options.Ocarina4", 263, "Ambience");
		keyBindings[11] = new KeyBinding("Options.Ocarina5", 345, "Ambience");
			
		// register all the key bindings
		for (int i = 0; i < keyBindings.length; ++i) {
			ClientRegistry.registerKeyBinding(keyBindings[i]);
		}
	}
	
	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent e) {
		if (e.isCanceled()) {
			return;
		}

		if (e.getFace() != null) {
			
			//If you have an ocarina in hand you can't interact with other blocks
			ItemStack item = e.getPlayer().getHeldItem(e.getHand());
			if (item.getItem() instanceof Ocarina)
				e.setCanceled(true);
			
			//If interact with a playing juckebox it will stop the music if playing (works only for 1 at a time)
			if(e.getPlayer().isServerWorld()) {
				Block juckebox=e.getPlayer().world.getBlockState(e.getPos()).getBlock();
				if(juckebox instanceof JukeboxBlock){
					if(Ambience.playingJuckebox)
					{						
						//Update the juckebox state everyone in the server
						CompoundNBT nbt = new CompoundNBT();
						nbt.putBoolean("playingJuckebox",false);
						AmbiencePackageHandler.sendToServer(new MyMessage(nbt));
						
						SongPicker.lastPlayerPos=e.getPlayer().getPosition();
					}
				}
			}
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

			GameSettings settings = Minecraft.getInstance().gameSettings;			
			if (!Minecraft.getInstance().isGameFocused()) {
				if (masterAudioCount >= 0.1f)
					masterAudioCount -= 0.05f;

				if (AmbienceConfig.COMMON.lostFocusEnabled.get()) {
					// Mute the gameaudio on lost focus
					settings.setSoundLevel(SoundCategory.MASTER, masterAudioCount);
				}
			} else {

				if (AmbienceConfig.COMMON.lostFocusEnabled.get()) {
					// Return the game audio to the previous one
					if (masterAudioCount <= Utils.clamp(oldVolume, 0.1f, 1f)) {
						masterAudioCount += 0.05f;
						settings.setSoundLevel(SoundCategory.MASTER, masterAudioCount);
					}
				}
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
										
		//
		//  Change camera mode for the ocarina when starting playing the Ocarina =================
		//		
		if(RegistryHandler.Ocarina.get().playing)
		{
			if(cameraChanged==false) {
				cameraChanged=true;			
				oldCameraMode = Minecraft.getInstance().gameSettings.thirdPersonView;
				oldFOV=Minecraft.getInstance().gameSettings.fov;
			}
			
			setCameraMode(2);//Enters Third Person			
		}			
	}

	@SubscribeEvent
	public static void keyEvent(InputEvent.KeyInputEvent event) {
				
		if(Minecraft.getInstance().isGameFocused()) {
			
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
							
			//Ocarina keys
			if (keyBindings[7].isPressed()) { addPressedKey(1);	}else {	removePressedKey(1); }	
			if (keyBindings[8].isPressed()) { addPressedKey(2);	}else {	removePressedKey(2); }	
			if (keyBindings[9].isPressed()) { addPressedKey(3);	}else {	removePressedKey(3); }	
			if (keyBindings[10].isPressed()) { addPressedKey(4);}else {	removePressedKey(4); }	
			if (keyBindings[11].isPressed()) { addPressedKey(5);}else {	removePressedKey(5); }				
		}
	}
	
	public static boolean key_released;
	
	private static void addPressedKey(int key) {
		Ocarina Ocarina=RegistryHandler.Ocarina.get();		
		
		Ocarina.key_id=key; 
		if(!Ocarina.actualPressedKeys.contains(Ocarina.key_id) | Ocarina.actualPressedKeys.size()==0) {
			Ocarina.actualPressedKeys.add(Ocarina.key_id);
						
			//Add the pressedKey to list of keys to check for musics on the client side
			Ocarina.addPressedKey(key);			
		}
		
		syncKeysServer(key);
	}
	
	private static void removePressedKey(int key) {
				
		Ocarina Ocarina=RegistryHandler.Ocarina.get();		
		if(Ocarina.actualPressedKeys.size()>0 && Ocarina.actualPressedKeys.contains(key)) {				
			Ocarina.actualPressedKeys.remove((Object)key);			
		}
		//syncKeysServer(key);
	}
	
	//Sync the pressed keys with the server
	private static void syncKeysServer(int key) {
		Ocarina Ocarina=RegistryHandler.Ocarina.get();		
		String keys="";
		for(Integer actualKey : Ocarina.actualPressedKeys) {
			keys+=actualKey+",";
		}
			
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("keyPressed", key);
		nbt.putString("actualPressedKeys", keys);
		nbt.putBoolean("playing", Ocarina.playing);
		nbt.putBoolean("runningCommand", Ocarina.runningCommand);
		OcarinaPackageHandler.sendToServer(new OcarinaMessage(nbt));
	}
	
	static int oldCameraMode = 0;
	private static float zoomCount = 70.0f;
	public static float zoomAmount = 30.0f;
	public static double zoomSpeed = 0.5;
	public static double oldFOV=0;
	public static boolean cameraChanged=false;
	
	private static void setCameraMode(int mode) {
		Minecraft.getInstance().gameSettings.thirdPersonView = mode;		
	}
	
	@SubscribeEvent
    public static void onFOVModifierEvent(EntityViewRenderEvent.FOVModifier event) {
				
		if(RegistryHandler.Ocarina.get().playing) {
			Minecraft mc = Minecraft.getInstance();			     
			ItemStack item = mc.player.getHeldItem(Hand.MAIN_HAND) == null? mc.player.getHeldItem(Hand.OFF_HAND) : mc.player.getHeldItem(Hand.MAIN_HAND);
        	
			if (item!=null && item.getItem() instanceof Ocarina) 
			{	
		        Minecraft.getInstance().gameSettings.smoothCamera = true;
		        
		        if (zoomCount > zoomAmount) {
	                zoomCount -= zoomSpeed;
	                if (zoomCount < zoomAmount) {
	                    zoomCount = zoomAmount;
	                }
	            }	        	        
		        event.setFOV(zoomCount);
	            return;
			}else {
				//Return the camera mode to the old one if the item breaks in your hand
            	setCameraMode(oldCameraMode);				
			}
		}		
				
		if(zoomCount!=70 ) {
	        Minecraft.getInstance().gameSettings.smoothCamera = false;
               
            if (zoomCount < event.getFOV()) {
                zoomCount += zoomSpeed;
                if (zoomCount > event.getFOV()) {
                    zoomCount = (float) event.getFOV();
                }
            }
            event.setFOV(zoomCount);
            
            //reset to the old camera when stopped playing the ocarina
            if(zoomCount>oldFOV-1)
            {
            	Ocarina Ocarina=RegistryHandler.Ocarina.get();

				//Return the camera mode to the old one before starting playing
            	setCameraMode(oldCameraMode);
            	cameraChanged=false;
            	Ocarina.pressedKeys.clear();
            	Ocarina.old_key_id=-1;
            	Ocarina.runningCommand=false;
            	
            	Ocarina.songName="";
            	
            	//Syncs with the server
            	CompoundNBT nbt = new CompoundNBT();
        		nbt.putBoolean("resetVariables", true);
        		OcarinaPackageHandler.sendToServer(new OcarinaMessage(nbt));
            }
            
		}
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
			
			if(event.getSound().getCategory() == SoundCategory.MUSIC) {

				if(Ambience.dimension>=-1 & Ambience.dimension<=1 | Ambience.overideBackMusicDimension) {
								
					if(event.isCancelable()) 
						event.setCanceled(true);
					
					event.setResultSound(null);
				}
			}else if(event.getSound().getCategory() == SoundCategory.RECORDS) {				
				//Update the juckebox state everyone in the server
				CompoundNBT nbt = new CompoundNBT();
				nbt.putBoolean("playingJuckebox",true);
				AmbiencePackageHandler.sendToServer(new MyMessage(nbt));
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
	
	//Renders the Ocarina Overlays Cinematic Effects
	@SubscribeEvent
    public static void onOverlayRender(RenderGameOverlayEvent.Post event) 
	{
		Ocarina Ocarina=RegistryHandler.Ocarina.get();
		Ocarina.renderFX(event, zoomCount, zoomAmount, zoomSpeed,20);		
		
		//Render the Transitions
		cinematic.renderFX(event, zoomCount, zoomAmount, zoomSpeed, 20);
	}
		
	public static boolean show=false;
	@SubscribeEvent
	public static void firstRender(RenderGameOverlayEvent event) {
		Minecraft mc = Minecraft.getInstance();
		
		int py=(int) Math.abs(zoomCount-70);         
		if (event.getType() == ElementType.HOTBAR & py>10)
		{
			event.setCanceled(true);
		}
		
		if (event.getType() == ElementType.HOTBAR &  Math.abs(CinematicRender.fx_zoomCount-70)> 10)
		{
			event.setCanceled(true);
		}
		
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