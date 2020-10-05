package vazkii.ambience.Util.Handlers;

import java.io.File;
import java.util.Map;
import java.util.Random;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import net.minecraft.block.Block;
import net.minecraft.block.BlockJukebox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.toasts.AdvancementToast;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.ParticleDrip;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleSplash;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.CheckResult;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vaskii.ambience.Init.ItemInit;
import vaskii.ambience.network4.MyMessage4;
import vaskii.ambience.network4.NetworkHandler4;
import vaskii.ambience.network4.OcarinaNetworkHandler;
import vaskii.ambience.objects.blocks.SpeakerTileEntity;
import vaskii.ambience.objects.items.Ocarina;
import vaskii.ambience.render.CinematicRender;
import vaskii.ambience.render.HornRender;
import vaskii.ambience.render.SelectionBoxRenderer;
import vazkii.ambience.Ambience;
import vazkii.ambience.AmbienceConfig;
import vazkii.ambience.NilMusicTicker;
import vazkii.ambience.PlayerThread;
import vazkii.ambience.SongLoader;
import vazkii.ambience.SongPicker;
import vazkii.ambience.Util.Utils;
import vazkii.ambience.Util.particles.DripLavaParticleFactory;
import vazkii.ambience.Util.particles.DripWaterParticleFactory;

public class EventHandlers {

	public static String currentSong;
	public static String nextSong;
	
	//public static final CreativeTabs AmbienceTab = new AmbienceTab("AmbienceTab");
	private static final int WAIT_DURATION = 25;
	public static int FADE_DURATION = AmbienceConfig.fadeDuration;
	public static final int SILENCE_DURATION = 5;
	
	public static int waitTick = WAIT_DURATION;
	public static int fadeOutTicks = FADE_DURATION;
	public static int fadeInTicks = FADE_DURATION-1;
	public static boolean fadeIn = false;
	public static int silenceTicks = 0;
	
	public int attackFadeTime = 300;
	public static int attackingTimer;
	static Entity currentplayer;
	public static KeyBinding[] keyBindings;
	public static float oldVolume;

	public static CinematicRender cinematic=new CinematicRender();
	
	public Ambience ambience;
	
	static int oldCameraMode = 0;
	private static float zoomCount = 70.0f;
	public static float zoomAmount = 30.0f;
	public static double zoomSpeed = 0.5;
	public static double oldFOV=0;
	public static boolean cameraChanged=false;

	//add the items to the Loot tables
	@SubscribeEvent
	public void onLootLoad(LootTableLoadEvent event) {
	    if (event.getName().equals(new ResourceLocation("minecraft:chests/simple_dungeon"))) {	    	
	        event.getTable().addPool(event.getLootTableManager().getLootTableFromLocation(new ResourceLocation("ambience:chests/simple_dungeon")).getPool("simpleDungeon"));				   
	    }	    
	    else if (event.getName().equals(new ResourceLocation("minecraft:chests/desert_pyramid")))
	    {
	    	 event.getTable().addPool(event.getLootTableManager().getLootTableFromLocation(new ResourceLocation("ambience:chests/desert_pyramid")).getPool("desertPyramid"));
		}
	    else if (event.getName().equals(new ResourceLocation("minecraft:chests/abandoned_mineshaft")))
	    {
	    	 event.getTable().addPool(event.getLootTableManager().getLootTableFromLocation(new ResourceLocation("ambience:chests/abandoned_mineshaft")).getPool("abandonedMineshaft"));
		}
	    else if (event.getName().equals(new ResourceLocation("minecraft:chests/end_city_treasure")))
	    {
	    	 event.getTable().addPool(event.getLootTableManager().getLootTableFromLocation(new ResourceLocation("ambience:chests/end_city_treasure")).getPool("endCity"));
		} 
	    else if (event.getName().equals(new ResourceLocation("minecraft:chests/jungle_temple")))
	    {
	    	 event.getTable().addPool(event.getLootTableManager().getLootTableFromLocation(new ResourceLocation("ambience:chests/jungle_temple")).getPool("jungleTemple"));
		} 
	    else if (event.getName().equals(new ResourceLocation("minecraft:chests/village_blacksmith")))
	    {
	    	 event.getTable().addPool(event.getLootTableManager().getLootTableFromLocation(new ResourceLocation("ambience:chests/village_blacksmith")).getPool("villageBlocksmith"));
		}
	}
	
	// constructor
	public EventHandlers(Ambience amb) {
		this.ambience = amb;
		attackingTimer = attackFadeTime;
		GameSettings settings = Minecraft.getMinecraft().gameSettings;
		oldVolume=settings.getSoundLevel(SoundCategory.MASTER);

		currentplayer = Minecraft.getMinecraft().player;
		// declare an array of key bindings
		keyBindings = new KeyBinding[12];
		keyBindings[0] = new KeyBinding("Options.Reload", Keyboard.KEY_P, "Ambience");
		keyBindings[1] = new KeyBinding("Force Play", Keyboard.KEY_O, "Ambience");
		
		//Shorcuts
		keyBindings[2] = new KeyBinding("Options.Shortcut1", 0, "Ambience");
		keyBindings[3] = new KeyBinding("Options.Shortcut2", 0, "Ambience");
		keyBindings[4] = new KeyBinding("Options.Shortcut3", 0, "Ambience");
		keyBindings[5] = new KeyBinding("Options.Shortcut4", 0, "Ambience");
		keyBindings[6] = new KeyBinding("Options.Shortcut5", 0, "Ambience");		
		//Ocarina
		keyBindings[7] = new KeyBinding("Options.Ocarina1", 200, "Ambience");
		keyBindings[8] = new KeyBinding("Options.Ocarina2", 208, "Ambience");
		keyBindings[9] = new KeyBinding("Options.Ocarina3", 205, "Ambience");
		keyBindings[10] = new KeyBinding("Options.Ocarina4", 203, "Ambience");
		keyBindings[11] = new KeyBinding("Options.Ocarina5", 157, "Ambience");

		// register all the key bindings
		for (int i = 0; i < keyBindings.length; ++i) {
			ClientRegistry.registerKeyBinding(keyBindings[i]);
		}
	}

	int count = 0;
	boolean pressedkey = false;
	boolean played_match = false;
	boolean settingDay = false,settingNight = false;
	
	
	// Advancements ****************************************************************************
	static boolean playingAdvancement=false;
	int adcancementTimer=0;
	static PlayerThread thread2;
	static String AdvancementSong="";
	static int songLenght;

	public static void onAdvancement() 
	{
		if (SongPicker.eventMap.containsKey("advancement") & !playingAdvancement) {
			thread2 = new PlayerThread();
			Random rand = new Random();
			
			String[] songChoices = SongPicker.eventMap.get("advancement");
			if (songChoices != null & !playingAdvancement) {
				if (songChoices.length > 0) 
				{
					playingAdvancement=true;
					AdvancementSong=songChoices[rand.nextInt(songChoices.length)];
					getSongLenght();
					thread2.play(AdvancementSong);	
					
					System.out.println("playing");
				}
			}
		}		
	}
	
	@SubscribeEvent
	public void onPlayerTick(TickEvent.WorldTickEvent.PlayerTickEvent event) {
		if (currentplayer == null)
			currentplayer = Minecraft.getMinecraft().player;		
		
		if(playingAdvancement) {
			adcancementTimer++;
			
			int scale_time=20;
			//If this is null you are in the single player 
			if(Minecraft.getMinecraft().getCurrentServerData() ==null)
				scale_time=80;
			else
				scale_time=20;
			
			if(adcancementTimer>songLenght*scale_time) {
				adcancementTimer=0;
				playingAdvancement=false;
				thread2.forceKill();
			}
		}
	}
	
	private static void getSongLenght() {
		// Obtém o tempo do som selecionado
		File f = new File(Ambience.ambienceDir+"\\music\\", AdvancementSong + ".mp3");

		if (f.isFile()) {
			try {
				AudioFile af = AudioFileIO.read(f);
				AudioHeader ah = af.getAudioHeader();
				songLenght = ah.getTrackLength();
			} catch (Exception e) {

			}
		}else {
			songLenght=0;
		}
		
	}
	// ******************************************************************************************
	
	@SubscribeEvent
	public void onRightClick(PlayerInteractEvent e) {
		if (e.isCanceled()) {
			return;
		}

		if (e.getFace() != null) {
			
			//If you have an ocarina in hand you can't interact with other blocks
			ItemStack item = e.getEntityPlayer().getHeldItem(e.getHand());
			if (item.getItem() instanceof Ocarina)
				e.setCanceled(true);
			
			//If interact with a playing juckebox it will stop the music if playing (works only for 1 at a time)
			if(e.getEntityPlayer().isServerWorld()) {
				Block juckebox=e.getEntityPlayer().world.getBlockState(e.getPos()).getBlock();
				if(juckebox instanceof BlockJukebox){
					if(Ambience.playingJuckebox)
					{						
						//Update the juckebox state everyone in the server
						NBTTagCompound nbt = new NBTTagCompound();
						nbt.setBoolean("playingJuckebox",false);							
						NetworkHandler4.sendToServer(new MyMessage4(nbt));
												
						SongPicker.lastPlayerPos=e.getEntityPlayer().getPosition();
					}
				}
			}
		}
	}
	
	//Renders the Ocarina Overlays Cinematic Effects
	@SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent.Post event) 
	{
		Ocarina Ocarina=(Ocarina) ItemInit.itemOcarina;
		Ocarina.renderFX(event, zoomCount, zoomAmount, zoomSpeed,20);		
		
		//Render the Transitions
		cinematic.renderFX(event, zoomCount, zoomAmount, zoomSpeed, 20);
	}
	
	private void setCameraMode(int mode) {
		Minecraft.getMinecraft().gameSettings.thirdPersonView = mode;		
	}
		
	@SubscribeEvent
	public void keyEvent(InputEvent.KeyInputEvent event) {
				
		if(Minecraft.getMinecraft().inGameHasFocus) {
			
			//KEYBOARD EVENTS HANDLER
			// check each enumerated key binding type for pressed and take appropriate action
			if (keyBindings[0].isPressed()) {
				SongPicker.reset();
				SongLoader.loadFrom(Ambience.ambienceDir);

				Minecraft mc = Minecraft.getMinecraft();
				MusicTicker ticker = new NilMusicTicker(mc);

				ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, mc, ticker, Ambience.OBF_MC_MUSIC_TICKER);
				
				SystemToast.addOrUpdate(mc.getToastGui(), SystemToast.Type.TUTORIAL_HINT, (ITextComponent) new TextComponentString(I18n.format("Ambience.ReloadTitle")), (ITextComponent) new TextComponentString(I18n.format("Ambience.Reload")));
			}
			
			if (keyBindings[1].isPressed()) {
				//SongPicker.reset();
				Ambience.thread.forceKill();			
				Ambience.thread.run();
				SongLoader.loadFrom(Ambience.ambienceDir);

				if (SongLoader.enabled)
					Ambience.thread = new PlayerThread();

				Minecraft mc = Minecraft.getMinecraft();
				MusicTicker ticker = new NilMusicTicker(mc);
				ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, mc, ticker, Ambience.OBF_MC_MUSIC_TICKER);
				
				SystemToast.addOrUpdate(mc.getToastGui(), SystemToast.Type.TUTORIAL_HINT, (ITextComponent) new TextComponentString(I18n.format("Ambience.ReloadTitle")), (ITextComponent) new TextComponentString(I18n.format("Ambience.Force")));
			}
			
			//Shortcuts keys
			if (keyBindings[2].isPressed()) { 
				ToggleForcePlay(0); 
				}
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
	
	private void addPressedKey(int key) {
		Ocarina Ocarina=(Ocarina) ItemInit.itemOcarina;		
		
		Ocarina.key_id=key; 
		if(!Ocarina.actualPressedKeys.contains(Ocarina.key_id) | Ocarina.actualPressedKeys.size()==0) {
			Ocarina.actualPressedKeys.add(Ocarina.key_id);
						
			//Add the pressedKey to list of keys to check for musics on the client side
			Ocarina.addPressedKey(key);			
		}
		
		syncKeysServer(key);
	}
	
	private void removePressedKey(int key) {
				
		Ocarina Ocarina=(Ocarina) ItemInit.itemOcarina;	
		if(Ocarina.actualPressedKeys.size()>0 && Ocarina.actualPressedKeys.contains(key)) {				
			Ocarina.actualPressedKeys.remove((Object)key);			
		}
		//syncKeysServer(key);
	}
	
	//Sync the pressed keys with the server
	private void syncKeysServer(int key) {
		Ocarina Ocarina=(Ocarina) ItemInit.itemOcarina;		
		String keys="";
		for(Integer actualKey : Ocarina.actualPressedKeys) {
			keys+=actualKey+",";
		}
		
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("keyPressed", key);
		nbt.setString("actualPressedKeys", keys);
		nbt.setBoolean("playing", Ocarina.playing);
		nbt.setBoolean("runningCommand", Ocarina.runningCommand);		
		OcarinaNetworkHandler.sendToServer(new MyMessage4(nbt));		
	}
	
	private static void ToggleForcePlay(int id) {
		Minecraft mc = Minecraft.getMinecraft();
		if (Ambience.forcePlay) {
			Ambience.forcePlay = false;
		} else {
			Ambience.forcePlay = true;
			// SystemToast.addOrUpdate(mc.getToastGui(), SystemToast.Type.TUTORIAL_HINT,
			// (ITextComponent) new TranslationTextComponent("ForcePlay.Playing"),
			// (ITextComponent) new StringTextComponent(song[0]));
		}
		SongPicker.forcePlayID = id;

		if (Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown()) {

			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setBoolean("forcedPlay", Ambience.forcePlay);
			nbt.setInteger("forcedPlayID", SongPicker.forcePlayID);
			NetworkHandler4.sendToServer(new MyMessage4(nbt));

		}
	}
	
	boolean playing=true;
	
	@SubscribeEvent
    public void onFOVModifierEvent(EntityViewRenderEvent.FOVModifier event) {
		
		if(((Ocarina) ItemInit.itemOcarina).playing) {
		
			Minecraft mc = Minecraft.getMinecraft();			     
			ItemStack item = mc.player.getHeldItem(EnumHand.MAIN_HAND) == null? mc.player.getHeldItem(EnumHand.OFF_HAND) : mc.player.getHeldItem(EnumHand.MAIN_HAND);
        	
			if (item!=null && item.getItem() instanceof Ocarina) 
			{	
		        Minecraft.getMinecraft().gameSettings.smoothCamera = true;

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
				
		
		if(zoomCount!= 70 ) {
	        Minecraft.getMinecraft().gameSettings.smoothCamera = false;

            if (zoomCount < 70) {
                zoomCount += zoomSpeed;
                if (zoomCount > event.getFOV()) {
                    zoomCount = (float) event.getFOV();
                }
            }
            event.setFOV(zoomCount);
            
            //reset to the old camera when stopped playing the ocarina
            if(zoomCount>oldFOV-1)
            {
            	Ocarina Ocarina=(Ocarina) ItemInit.itemOcarina;

				//Return the camera mode to the old one before starting playing
            	setCameraMode(oldCameraMode);
            	cameraChanged=false;
            	Ocarina.pressedKeys.clear();
            	Ocarina.old_key_id=-1;
            	Ocarina.runningCommand=false;
            	
            	Ocarina.songName="";
            	
            	NBTTagCompound nbt = new NBTTagCompound();
        		nbt.setBoolean("resetVariables", true);
    			OcarinaNetworkHandler.sendToServer(new MyMessage4(nbt));
            }            
		}
	}

	// Quando alguma coisa ataca o player
	@SubscribeEvent
	public void onEntitySetAttackTargetEvent(LivingAttackEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) {			
			Ambience.attacked = true;
			attackingTimer = attackFadeTime;

			EventHandlers.playInstant();			
		}
	}

	// FUNCIONA Quando player ataca alguma coisa
	@SubscribeEvent
	public void onPlayerAttackEvent(AttackEntityEvent event) {
		String mobName = event.getTarget().getName().toLowerCase();

		if (event.getTarget() instanceof EntityMob) {
			// if (event.getTarget().isCreatureType(EnumCreatureType.MONSTER, false)) {
			Ambience.attacked = true;

			attackingTimer = attackFadeTime;
			EventHandlers.playInstant();
		}
	}

	// On something dies
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onEntityDeath(LivingDeathEvent event) {
		DamageSource source = event.getSource();

		// When Player kills something
		if (source.getTrueSource() instanceof EntityPlayer & event.getEntity() == currentplayer) {
			ambience.attacked = false;
		}

		// When Player dies
		if (event.getEntity() instanceof EntityPlayer & event.getEntity() == currentplayer) {
			ambience.attacked = false;
		}

	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onWorldLoad(WorldEvent.Load ev) {
		Minecraft mc = Minecraft.getMinecraft();
		SongPicker.cinematicMap.clear();
		
		if (mc.effectRenderer != null) 
		{
			try {
								
				ParticleManager instance = mc.effectRenderer;								
				Map<Integer, IParticleFactory> facts= ObfuscationReflectionHelper.getPrivateValue(ParticleManager.class, instance, "field_178932_g");				
				IParticleFactory pf = facts.get(EnumParticleTypes.WATER_SPLASH.getParticleID());
		
				if(AmbienceConfig.waterDripping_enabled)
				if (pf instanceof ParticleSplash.Factory) {
				
					instance.registerParticle(EnumParticleTypes.WATER_SPLASH.getParticleID(), new DripWaterParticleFactory());
					
				/*	IParticleFactory npf = facts.get(5);
									
					// check that it worked
					// wrap the original factory to copy the sprite data
					if(npf instanceof DripWaterParticleFactory)
						((DripWaterParticleFactory) npf).wrap((ParticleSplash.Factory)pf);*/
					
				}				
				
				if(AmbienceConfig.lavaDripping_enabled) {
					pf = facts.get(EnumParticleTypes.DRIP_LAVA.getParticleID());				
					if (pf instanceof ParticleDrip.LavaFactory) {
					
						instance.registerParticle(EnumParticleTypes.DRIP_LAVA.getParticleID(), new DripLavaParticleFactory());
					}	
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// FUNCIONA Quando player ataca alguma coisa
	/*
	 * @SubscribeEvent(priority = EventPriority.NORMAL) public void
	 * onPlayerAttackEvent(AttackEntityEvent event) { mobName =
	 * event.getTarget().getName().toLowerCase();
	 * 
	 * //if (event.getTarget() instanceof EntityMob) { if
	 * (event.getTarget().isCreatureType(EnumCreatureType.MONSTER, false)) {
	 * ambience.attacked = true; playInstant(); }
	 * 
	 * } // Quando alguma coisa ataca o player
	 * 
	 * @SubscribeEvent(priority = EventPriority.NORMAL) public void
	 * onLivingAttackEvent(LivingAttackEvent event) {
	 * 
	 * 
	 * // System.out.println(event.getEntity().getName());
	 * 
	 * if(currentplayer!=null) if
	 * (event.getEntity().getName().contains(currentplayer.getName())) { // When
	 * something get hurts near the player List<EntityLivingBase> entities =
	 * Minecraft.getMinecraft().world.getEntitiesWithinAABB( EntityLivingBase.class,
	 * new AxisAlignedBB(event.getEntity().posX - 16, event.getEntity().posY - 16,
	 * event.getEntity().posZ - 16, event.getEntity().posX + 16,
	 * event.getEntity().posY + 16, event.getEntity().posZ + 16)); for
	 * (EntityLivingBase mob : entities) { mobName = mob.getName().toLowerCase();
	 * 
	 * // Detects when player gets attacked if (mobName != null &
	 * !event.getSource().isUnblockable()) if
	 * (mobName.toLowerCase().contains("player") ||
	 * event.getSource().isProjectile()) { ambience.attacked = true; playInstant();
	 * } } } }
	 */

	public static void playInstant() {
		EventHandlers.fadeOutTicks = EventHandlers.FADE_DURATION;
		EventHandlers.silenceTicks = EventHandlers.SILENCE_DURATION;
		EventHandlers.waitTick = 0;
		Ambience.instantPlaying = true;

		Ambience.thread.setGain(PlayerThread.fadeGains[0]);
		EventHandlers.fadeIn = false;
	}

	boolean focused=false;
	private float masterAudioCount=0;
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if(Ambience.thread == null)
			return;
					
			if(event.phase == Phase.END) {		
				
				//Checks if the screen is focused or not
				if (Display.isCreated())
			    {
			        if (focused && !Display.isActive())
			        {
			            focused = false;
			            //shutdown lighting
			        }
			        else if (!focused && Display.isActive())
			        {
			            focused = true;
			            //restart lighting
			        }
			    }
				
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
				
				GameSettings settings = Minecraft.getMinecraft().gameSettings;			
				if (!focused) {
					if (masterAudioCount >= 0.1f)
						masterAudioCount -= 0.05f;

					if (AmbienceConfig.lostFocusEnabled) {
						// Mute the gameaudio on lost focus
						settings.setSoundLevel(SoundCategory.MASTER, masterAudioCount);
					}
				} else {

					if (AmbienceConfig.lostFocusEnabled) {
						// Return the game audio to the previous one
						if (masterAudioCount < Utils.clamp(oldVolume, 0.1f, 1f)) {
							masterAudioCount += 0.05f;
							settings.setSoundLevel(SoundCategory.MASTER, masterAudioCount);
						}
					}
				}
				
					//Fade In gain***************
					if(fadeIn) {
						Ambience.thread.setGain(PlayerThread.fadeGains[fadeInTicks]);				
					}			
					if(Ambience.thread.gain<Ambience.thread.MAX_GAIN & fadeInTicks>0 /*& fadeIn*/) {			
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
		if(((Ocarina)ItemInit.itemOcarina).playing)
		{
			if(cameraChanged==false) {
				oldCameraMode = Minecraft.getMinecraft().gameSettings.thirdPersonView;
				oldFOV=Minecraft.getMinecraft().gameSettings.fovSetting;

				setCameraMode(2);//Enters Third Person	
				cameraChanged=true;				
			}

		}	
	}
	
	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
		if(!Minecraft.getMinecraft().gameSettings.showDebugInfo)
			return;
		
		event.getRight().add(null);
		if((Ambience.dimension>=-1 & Ambience.dimension<=1) | PlayerThread.currentSong!="null" & nextSong!="null") {
			
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
	
	@SubscribeEvent
	public void onBackgroundMusic(PlaySoundEvent event) {
		
		if(SongLoader.enabled) {
			WorldClient world=Minecraft.getMinecraft().world;
					
			if(world!=null)
				Ambience.dimension=world.provider.getDimension();
			
			if(event.getSound().getCategory() == SoundCategory.MUSIC)
			if((SongLoader.enabled & (Ambience.dimension>=-1 & Ambience.dimension<=1) | Ambience.overideBackMusicDimension)) {
							
				if(event.isCancelable()) 
					event.setCanceled(true);
				
				event.setResultSound(null);
			}

			if(event.getSound().getCategory() == SoundCategory.RECORDS) {		
				//Update the juckebox state everyone in the server
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setBoolean("playingJuckebox",true);			
				NetworkHandler4.sendToServer(new MyMessage4(nbt));
			}
		}
	}
	
	public static void changeSongTo(String song) 
	{		
		//para de tocar as musicas caso esteja em outra dimensao
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
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onWorldRenderLast(RenderWorldLastEvent event) {
		EntityPlayerSP currentplayer = Minecraft.getMinecraft().player;

		if (Ambience.previewArea != null)
			if (Ambience.previewArea.getPos1() != null & Ambience.previewArea.getPos2() != null) {
				SelectionBoxRenderer.drawBoundingBox(currentplayer.getPositionVector(), Ambience.previewArea.getPos1(),
						Ambience.previewArea.getPos2(), true, 2,event.getPartialTicks());				
			}

		//Render the Horn sound effect
		HornRender.drawBoundingBox(currentplayer.getPositionVector(), event.getPartialTicks(), event, currentplayer.world, currentplayer);
		
	}

	public static boolean show = false;

	@SubscribeEvent
	public void firstRender(RenderGameOverlayEvent event) {		
		Minecraft mc = Minecraft.getMinecraft();
		
		int py=(int) Math.abs(zoomCount-70);         
		if (event.getType() == ElementType.HOTBAR & py>10)
		{
			event.setCanceled(true);
		}
		
		if (event.getType() == ElementType.HOTBAR &  Math.abs(CinematicRender.fx_zoomCount-70)> 10)
		{
			event.setCanceled(true);
		}
		
		if (!mc.inGameHasFocus || mc.player == null) {
			return;
		}

		if (!show & Ambience.showUpdateNotification) {
			show = true;
			for (ModContainer container : Loader.instance().getActiveModList()) {
				if (!container.getModId().startsWith("mcp") && !container.getModId().equalsIgnoreCase("mcp")
						&& !container.getModId().equalsIgnoreCase("FML")
						&& !container.getModId().equalsIgnoreCase("Forge")) {
					CheckResult res = ForgeVersion.getResult(container);
					if ((res != null && res.status != Status.PENDING) && res.status == Status.BETA_OUTDATED
							|| res.status == Status.OUTDATED) {
						String comp = "\u00a7eNew version (\u00a77" + res.target + "\u00a7e) for\u00a7a "
								+ container.getName() + " \u00a7eis available for download ";

						/*
						 * ChatStyle style = comp.getChatStyle();
						 * style.setColor(EnumChatFormatting.YELLOW); style.setChatHoverEvent(new
						 * HoverEvent(net.minecraft.event.HoverEvent.Action.SHOW_TEXT, new
						 * ChatComponentText("\u00a7cClick to open download page.")));
						 * style.setChatClickEvent(new
						 * ClickEvent(net.minecraft.event.ClickEvent.Action.OPEN_URL, res.url));
						 * comp.setChatStyle(style);
						 */
						mc.player.sendMessage(new TextComponentString(comp));
						// mc.player.add.addChatComponentMessage(comp);
					}
				}
			}
		}
	}

}
