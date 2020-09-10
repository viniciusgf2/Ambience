package vazkii.ambience.Util.Handlers;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
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
		keyBindings = new KeyBinding[13];
		
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
		
		//Tests
		keyBindings[12] = new KeyBinding("Tests", 78, "Ambience");

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

		//If you have an ocarina in hand you can't interact with other blocks
		if (e.getFace() != null) {

			ItemStack item = e.getPlayer().getHeldItem(e.getHand());

			if (item.getItem() instanceof Ocarina)
				e.setCanceled(true);
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
		
		
		
						
		//
		//  Change camera mode for the ocarina  =================
		//
		//if (keyBindings[12].isPressed())
		if(Ocarina.playing)
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
	
	private static void addPressedKey(int key) {
		Ocarina.key_id=key; 
		if(!Ocarina.actualPressedKeys.contains(Ocarina.key_id)) {
			Ocarina.actualPressedKeys.add(Ocarina.key_id);
			
			//Add the pressedKey to list of keys to check for musics on the client side
			Ocarina.addPressedKey(key);
			
			syncKeysServer(key);
		}
	}
	
	private static void removePressedKey(int key) {
		if(Ocarina.actualPressedKeys.size()>0 && Ocarina.actualPressedKeys.contains(key)) {				
			Ocarina.actualPressedKeys.remove((Object)key);

			syncKeysServer(key);
		}
	}
	
	//Sync the pressed keys with the server
	private static void syncKeysServer(int key) {
		String keys="";
		for(Integer actualKey : Ocarina.actualPressedKeys) {
			keys+=actualKey+",";
		}
			
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("keyPressed", key);
		nbt.putString("actualPressedKeys", keys);
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
				
		if(Ocarina.playing) {
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
	
	/*public static final ResourceLocation Ocarina_OVERLAY_FX = new ResourceLocation(Ambience.MODID, "textures/gui/ocarina_overlays_fx.png");	   
    public static final ResourceLocation Ocarina_OVERLAYS = new ResourceLocation(Ambience.MODID, "textures/gui/ocarina_overlays.png");    
    public static float fx_rotateCount=0;
    public static float fx_zoomCount=0;*/
	@SubscribeEvent
    public static void onOverlayRender(RenderGameOverlayEvent.Post event) 
	{
		
		Ocarina.renderFX(event, zoomCount, zoomAmount, zoomSpeed);
		
		
		/*
		fx_rotateCount+=0.1f;
		//Renders the Ocarina's cinematic effect
		Minecraft mc = Minecraft.getInstance();
        if (event.getType() == ElementType.ALL) {
            MainWindow res = event.getWindow();
            if (mc.player != null) 
            {
            	ItemStack item = mc.player.getHeldItem(Hand.MAIN_HAND) == null? mc.player.getHeldItem(Hand.OFF_HAND) : mc.player.getHeldItem(Hand.MAIN_HAND);
            	
    			if (item.getItem() instanceof Ocarina) 
    			{
            	
	            	 int width = 2048;
	            	 int x = res.getScaledWidth() / 2;
	                 int y = (int) (1+event.getWindow().getGuiScaleFactor());
	                                   
	                 int py=(int) Math.abs(zoomCount-70);

	                 Vector4f color=new Vector4f(1,1,0,1);	 
	                 
	                	 
	                 
	                 //*******************************************************
	                 //FX ------------------------
	                 
	                 if(Ocarina.runningCommand) {
	                	 
	                	 if (fx_zoomCount > zoomAmount) {
	                		 fx_zoomCount -= zoomSpeed;
	     	                if (fx_zoomCount < zoomAmount) {
	     	                	fx_zoomCount = zoomAmount;
	     	                }
	     	            }	        
	                 }else{
	                	 if (fx_zoomCount < 70) {
	                		 fx_zoomCount += zoomSpeed;
	                         if (fx_zoomCount > 70) {
	                        	 fx_zoomCount = 70;
	                         }
	                     }
	                 }	
	                 if(fx_zoomCount!=70)
	                // if(Ocarina.runningCommand) 
	                 {
	     			
	                	 switch (Ocarina.songName) {
		                 	case "sunssong" : color=new Vector4f(1,1,0,1);break;
		                 	case "songofstorms" : color=new Vector4f(0.7f,0.6f,1,1);break;
		                 	case "bolerooffire" : color=new Vector4f(1,0.3f,0,1);break;		                 	
		                 	default : color=new Vector4f(1,1,1,1);break;
		                 }   
	                	 
	                 RenderSystem.pushMatrix();	                

	                // int opacity=(int)(262-(6.2f*zoomCount-179));
	                 float opacity=(int)(17-(zoomCount/8));	                 
	                 opacity=(opacity * 1.15f)/15;
	                 
	                 double angle = 2 * Math.PI * fx_rotateCount / 150;
	     			 float x2 = (float) Math.cos(angle);	  
	     			 float scaleFade=(40+(fx_zoomCount-70))/20;
	                 	     			 
	     			 //FX2
	                 RenderSystem.translatef(x, res.getScaledHeight()/2,0);
	                 RenderSystem.rotatef(-fx_rotateCount/2, 0, 0, 10);
	                 RenderSystem.scalef((1+x2/7)+scaleFade, (1+x2/7)+scaleFade, 1);
	                 RenderSystem.color4f(color.getX(), color.getY(), color.getZ(), opacity );
	                 		                	     
	                 //rendering	                 	                 
	            	 mc.getTextureManager().bindTexture(Ocarina_OVERLAY_FX);            	
	                 AbstractGui.blit(-res.getScaledWidth(), (int)(-res.getScaledHeight()*1.5f), res.getScaledWidth()*2, res.getScaledHeight()*3,  res.getScaledWidth()*2, res.getScaledHeight()*3, res.getScaledWidth()*2, res.getScaledHeight()*3);
	                 	 
	                 RenderSystem.color4f(1F, 1F, 1F, 1);
	                 RenderSystem.popMatrix();
	     			 
	     			 //FX1
	     			 x2 = (float) Math.cos(angle) * 1.5f;
	                 RenderSystem.pushMatrix();	        
	     			 RenderSystem.translatef(x, res.getScaledHeight()/2,0);
	                 RenderSystem.rotatef(fx_rotateCount, 0, 0, 10);
	                 RenderSystem.scalef((1+x2/9)+scaleFade, (1+x2/9)+scaleFade, 1);
	                 RenderSystem.color4f(color.getX(), color.getY(), color.getZ(), opacity );
	                 		                	     
	                 //rendering	                 	                 
	            	 mc.getTextureManager().bindTexture(Ocarina_OVERLAY_FX);            	
	                 AbstractGui.blit(-res.getScaledWidth(), (int)(-res.getScaledHeight()*1.5f), res.getScaledWidth()*2, res.getScaledHeight()*3,  res.getScaledWidth()*2, res.getScaledHeight()*3, res.getScaledWidth()*2, res.getScaledHeight()*3);
	                 	 
	                 RenderSystem.color4f(1F, 1F, 1F, 1);
	                 RenderSystem.popMatrix();
	                 
	                 }
	                 //********************************************************
	                 
	                 RenderSystem.pushMatrix();	 
	                 color=new Vector4f(1,1,1,1);	 
	                 RenderSystem.color4f(color.getX(), color.getY(), color.getZ(), 1);
	                 	                
	            	    
	                 //Bottom Overlay
	            	 mc.getTextureManager().bindTexture(Ocarina_OVERLAYS);            	
	                 AbstractGui.blit(0, 0, 0, 0, width, y+(int)(py*1.1)-10, 256, 256);
	                                  
	                 //Top Overlay
	                 y = res.getScaledHeight()+5 /(int)(1+event.getWindow().getGuiScaleFactor());
	            	 mc.getTextureManager().bindTexture(Ocarina_OVERLAYS);            	
	                 AbstractGui.blit(0, y-(int)(py*1.1)+10, 0, 0, width, 100, 256, 256);
	                   
	                 if(Ocarina.runningCommand) 
	                 {
		                 y = res.getScaledHeight()/2; ///(int)(1+event.getWindow().getGuiScaleFactor());
		             	
		                 String s=I18n.format("Ocarina.Played");
		                 float scale = 1.25F*(int)event.getWindow().getGuiScaleFactor()/2.5f;
		                 RenderSystem.scalef(scale, scale, scale);
		                 
		                 //Text renderer
		                 int opacity=(int)(262-(6.2f*zoomCount-179));
		                // int opacity=(int)((6.2f*zoomCount-179));
		                 int textColor= Utils.colorToInt(opacity,255,255,255);                  
		                 int totalTextLeng= mc.fontRenderer.getStringWidth(s)/2;
		                 int px= (int) (x/scale)-30;
		                 int py2= (int) (y/scale)+70;
		                 
		                 String songNameText="";
		                 mc.fontRenderer.drawStringWithShadow(s, px - totalTextLeng, py2, textColor);
		                 switch (Ocarina.songName) {
		                 	case "sunssong" : songNameText="Sun's Song"; textColor=Utils.colorToInt(opacity,255,255,0);break;
		                 	case "songofstorms" : songNameText="Song of Storms"; textColor=Utils.colorToInt(opacity,180,155,255);break;
		                 	case "bolerooffire" : songNameText="Bolero of Fire"; textColor=Utils.colorToInt(opacity,255,0,0);break;
		                 	
		                 	default : textColor=Utils.colorToInt(opacity,255,0,0);break;
		                 }                 
		                 mc.fontRenderer.drawStringWithShadow(songNameText, px+3 + mc.fontRenderer.getStringWidth("Sun's Song")/1.5f, py2, textColor);
	                 }
	                 RenderSystem.color4f(1F, 1F, 1F, 1);
	                 RenderSystem.popMatrix();
    			}
            }
        }*/
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



