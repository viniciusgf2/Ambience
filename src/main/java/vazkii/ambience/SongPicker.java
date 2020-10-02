package vazkii.ambience;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.overlay.BossOverlayGui;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.OptionsSoundsScreen;
import net.minecraft.client.gui.screen.SleepInMultiplayerScreen;
import net.minecraft.client.gui.screen.WinGameScreen;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.ambience.Util.RegistryHandler;
import vazkii.ambience.Util.SplashFactory2;
import vazkii.ambience.Util.Handlers.EventHandlers;
import vazkii.ambience.Util.Handlers.EventHandlersServer;
import vazkii.ambience.Util.particles.DripWaterParticleFactory;
import vazkii.ambience.World.Biomes.Area;
import vazkii.ambience.blocks.SpeakerTileEntity;
import vazkii.ambience.items.Ocarina;
import vazkii.ambience.render.CinematicRender;

@OnlyIn(value = Dist.CLIENT)
public final class SongPicker {

	public static final String EVENT_ATTACKED = "attacked";
	public static final String EVENT_MAIN_MENU = "mainMenu";
	public static final String EVENT_PAUSE = "paused";
	public static final String EVENT_CONNECTING = "connecting";
	public static final String EVENT_DISCONNECTED = "disconnected";
	public static final String EVENT_SLEEPING = "sleeping";
	public static final String EVENT_GAMEOVER = "gameover";
	public static final String EVENT_BOSS = "boss";
	public static final String EVENT_BOSS_WITHER = "bossWither";
	public static final String EVENT_BOSS_DRAGON = "bossEnderDragon";
	public static final String EVENT_IN_NETHER = "nether";
	public static final String EVENT_IN_END = "end";
	public static final String EVENT_HORDE = "horde";
	public static final String EVENT_NIGHT = "night";
	public static final String EVENT_RAIN = "rain";
	public static final String EVENT_LAVA = "lava";
	public static final String EVENT_FALLING = "falling";
	public static final String EVENT_ELYTRA = "flyingelytra";
	public static final String EVENT_UNDERWATER = "underwater";
	public static final String EVENT_DRIPWATERINCAVE = "dripwaterincave";
	public static final String EVENT_UNDERGROUND = "underground";
	public static final String EVENT_OCEANMONUMENT = "oceanMonument";
	public static final String EVENT_DEEP_UNDEGROUND = "deepUnderground";
	public static final String EVENT_HIGH_UP = "highUp";
	public static final String EVENT_VILLAGE = "village";
	public static final String EVENT_VILLAGE_NIGHT = "villageNight";
	public static final String EVENT_MINECART = "minecart";
	public static final String EVENT_RANCH = "ranch";
	public static final String EVENT_RANCH_NIGHT = "ranchNight";
	public static final String EVENT_BOAT = "boat";
	public static final String EVENT_HORSE = "horse";
	public static final String EVENT_PIG = "pig";
	public static final String EVENT_FISHING = "fishing";
	public static final String EVENT_DYING = "dying";
	public static final String EVENT_PUMPKIN_HEAD = "pumpkinHead";
	public static final String EVENT_CREDITS = "credits";
	public static final String EVENT_GENERIC = "generic";

	public static Map<String, String[]> eventMap = new HashMap<String, String[]>();
	public static Map<Biome, String[]> biomeMap = new HashMap<Biome, String[]>();
	public static Map<String, String[]> areasMap = new HashMap<String, String[]>();	
	public static Map<String, String[]> mobMap = new HashMap<String, String[]>();
	public static Map<String, String[]> effectMap = new HashMap<String, String[]>();	
	public static Map<String, Long> cinematicMap = new HashMap<String, Long>();	
	public static Map<BiomeDictionary.Type, String[]> primaryTagMap = new HashMap<Type, String[]>();
	public static Map<BiomeDictionary.Type, String[]> secondaryTagMap = new HashMap<Type, String[]>();	
	public static List<String> transitionsMap = new ArrayList<String>();
	
	public static final Random rand = new Random();

	public static int songTimer = 0;
	private static int uncountDripWater=0;
	
	public static boolean areaSongsLoaded = false;
	public static boolean falling = false;
	public static boolean horde=false;
	
	public static int forcePlayID=-1;
	public static boolean ForcePlaying=false;
	public static int musicLenght=0;
	public static Ocarina Ocarina;
	
	public static BlockPos lastPlayerPos=new BlockPos(Vec3d.ZERO);
	public static String StructureName;
			
	public static void reset() {
		eventMap.clear();
		biomeMap.clear();
		areasMap.clear();
		mobMap.clear();
		primaryTagMap.clear();
		secondaryTagMap.clear();
	}

	public static String[] getSongs() {
		Minecraft mc = Minecraft.getInstance();
		PlayerEntity player = mc.player;
		World world = mc.world;
		int dimension=0;
		Ocarina = RegistryHandler.Ocarina.get();
		
		if(world!=null)
			dimension=world.dimension.getType().getId();	
		
		if (mc.currentScreen instanceof OptionsSoundsScreen) {
			GameSettings settings = Minecraft.getInstance().gameSettings;
			EventHandlers.oldVolume=settings.getSoundLevel(SoundCategory.MASTER);
		}
				
		if (mc.currentScreen instanceof ConnectingScreen)
			return getSongsForEvent(EVENT_CONNECTING);
		
		if (mc.currentScreen instanceof DisconnectedScreen)
			return getSongsForEvent(EVENT_DISCONNECTED);
				
		if (player == null || world == null) {
			areaSongsLoaded = false;
			
			Ambience.dimension=-25412;
			
			if(PlayerThread.currentSong!=null)
			{				
				if(getSongsForEvent(EVENT_MAIN_MENU) !=null)
					Ambience.overideBackMusicDimension=true;
				else {
					EventHandlers.fadeIn=true;
					EventHandlers.fadeInTicks= EventHandlers.FADE_DURATION-1;	
					Ambience.overideBackMusicDimension=false;	
				}
			}
						
			return getSongsForEvent(EVENT_MAIN_MENU);
		}
				
		if (mc.currentScreen instanceof IngameMenuScreen)
			return getSongsForEvent(EVENT_PAUSE);
			
		if (mc.currentScreen instanceof SleepInMultiplayerScreen) {
			EventHandlers.playInstant();
			return getSongsForEvent(EVENT_SLEEPING);
		}
		
		if (mc.currentScreen instanceof DeathScreen)
			return getSongsForEvent(EVENT_GAMEOVER);
		
		if (mc.currentScreen instanceof WinGameScreen)
			return getSongsForEvent(EVENT_CREDITS);		
		 				
		BlockPos pos = new BlockPos(player);
		
		/*AmbienceEventEvent event = new AmbienceEventEvent.Pre(world, pos);
		MinecraftForge.EVENT_BUS.post(event);
		String[] eventr = getSongsForEvent(event.event);
		if (eventr != null) {
			
			player.sendStatusMessage((ITextComponent) new StringTextComponent("funcionou"),(true));

			return eventr;
		}*/
		
		//Silences all the musics while playing the ocarina
		if(Ocarina.playing) {
			String[] song={"silent"};
			return song;
		}
		
		if(Ambience.ExternalEvent.event!="")
		{
			return  getSongsForEvent(Ambience.ExternalEvent.event);
		}
				
		// ******************
		if (Ambience.forcePlay ) {
			String[] songs = getSongsForEvent("shortcut"+forcePlayID);
			
			if(songs!=null & !ForcePlaying) {
				String[] song= {songs[0]};	
				ForcePlaying=true;
				musicLenght=getSongLenght(song[0]);					
			}

			if(songs!=null)
			{		
				//Pick only the first music in the shortcut event and ignore the rest	
				String[] song= {songs[0]};	
								
				if(song!=null) {				
					// Para a musica no fim dela
					if (songTimer > musicLenght * 39) {
						Ambience.forcePlay = false;
						ForcePlaying=false;
						songTimer = 0;
					} else
						songTimer++;
				}
				
				return song;
			}
			else {
				Ambience.forcePlay=false;
			}
			
			return null;
		}else {
			ForcePlaying=false;
			songTimer=0;
		}

		// ******************

		double distance = Math.sqrt(player.getDistanceSq(lastPlayerPos.getX(), lastPlayerPos.getY(), lastPlayerPos.getZ()));
				
		if(Ambience.playingJuckebox & distance<50) {
			String[] song={"silent"};
			
			return song;
		}
		
		BossOverlayGui bossOverlay = mc.ingameGUI.getBossOverlay();
		Map<UUID, BossInfo> map = ObfuscationReflectionHelper.getPrivateValue(BossOverlayGui.class, bossOverlay,Ambience.OBF_MAP_BOSS_INFOS);
		if (!map.isEmpty()) {
			try {
				BossInfo first = map.get(map.keySet().iterator().next());
				ITextComponent comp = first.getName();
				String type = "";

				if (comp instanceof StringTextComponent) {
					type = comp.getStyle().getHoverEvent().getValue().getUnformattedComponentText();
					type = type.substring(type.indexOf("type:\"") + 6, type.length() - 2);
				} else if (comp instanceof TranslationTextComponent) {
					type = ((TranslationTextComponent) comp).getKey();
					if (type.startsWith("entity.") && type.endsWith(".name"))
						type = type.substring(7, type.length() - 5);
				}

				if (type.equals("minecraft:wither")) {
					String[] songs = getSongsForEvent(EVENT_BOSS_WITHER);
					if (songs != null)
						return songs;
				} else if (type.equals("EnderDragon")) {
					String[] songs = getSongsForEvent(EVENT_BOSS_DRAGON);
					if (songs != null)
						return songs;
				}
				
				//Makes the mob. event have high priority over the boss
				 String[] tokens = type.split(":");				 
				 if(tokens.length>1) {
					 if (mobMap.containsKey(tokens[1]))
							return mobMap.get(tokens[1]);	
				 }else {
					 if (mobMap.containsKey(type))
							return mobMap.get(type);	
				 }
				 
			} catch (NullPointerException e) {
			}

			String[] songs = getSongsForEvent(EVENT_BOSS);
			if (songs != null)
				return songs;
		}

		// Boss and Enemies Battle Musics******************
		if (Ambience.attacked) {			
				try {
					String[] songs = null;
					int countEntities = 0;
					String mobName = null;
								
					List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class,
						new AxisAlignedBB(player.getPosX() - 16, player.getPosY() - 16, player.getPosZ() - 16, player.getPosX() + 16,player.getPosY() + 16, player.getPosZ() + 16));

				for (LivingEntity mob : entities) {					
					mobName = mob.getName().getString().toLowerCase();
										
					if (!(mob instanceof PlayerEntity) )	
						if (mob.canAttack(player)) {
						countEntities++;
					}
					
					if (mobMap.containsKey(mobName) & countEntities>0 & !(mob instanceof PlayerEntity))
						return mobMap.get(mobName);			
				}
				
				//****************
				
				if (mobName != null & countEntities > 0) {
					//Songs for other dimensions
					if (dimension !=0) {
						songs = getSongsForEvent(EVENT_ATTACKED+"\\"+dimension); 	
						if(songs==null)
							songs = getSongsForEvent(EVENT_ATTACKED);
					}
					else
						songs = getSongsForEvent(EVENT_ATTACKED);
				}
				
				//****************
				//Termina a musica de ataque
				if (mobName == null || countEntities < 1 || EventHandlersServer.attackingTimer-- < 0) {
					Ambience.attacked = false;
				}
								
				//**Play horde musig				
				if (countEntities > 5) {
					horde=true;
					songs=null;
					//Songs for other dimensions
					if (dimension !=0) {
						songs = getSongsForEvent(EVENT_HORDE+"\\"+dimension);
						if(songs==null)
							songs = getSongsForEvent(EVENT_HORDE);
					}
					if (songs != null)
						return songs;
				}else {
					horde=false;
				}

				if (songs != null)
					return songs;
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
		
		
		float hp = player.getHealth();
		if (hp < 7) {
			String[] songs=null;
			//Songs for other dimensions
			if (dimension !=0) {
				songs = getSongsForEvent(EVENT_DYING+"\\"+dimension);
				if(songs==null)
					songs = getSongsForEvent(EVENT_DYING);
			}
			
			if (songs != null)
				return songs;
		}

		/*int monsterCount = world.getEntitiesWithinAABB(MonsterEntity.class, new AxisAlignedBB(player.getPosX() - 16,
				player.getPosY() - 8, player.getPosZ() - 16, player.getPosX() + 16, player.getPosY() + 8, player.getPosZ() + 16)).size();
		if (monsterCount > 5) {
			horde=true;
			String[] songs=null;
			//Songs for other dimensions
			if (dimension <-1 | dimension >1 )
				songs = getSongsForEvent(EVENT_HORDE+"\\"+dimension);
			else
				songs = getSongsForEvent(EVENT_HORDE);
			if (songs != null)
				return songs;
		}else {
			horde=false;
		}*/
		
		//Events for potions
		Collection<EffectInstance> potions= player.getActivePotionEffects();		
		if(potions!=null && potions.size()>0) {			
			String potionName= ((EffectInstance)potions.toArray()[0]).getEffectName().replace("effect.", "");

			if (dimension !=0 ) {
				 if (effectMap.containsKey(potionName+"."+ dimension))
						return effectMap.get(potionName+"."+ dimension);	

				 if (effectMap.containsKey(potionName))
						return effectMap.get(potionName);		
			}else {
				 if (effectMap.containsKey(potionName))
						return effectMap.get(potionName);					
			}			
		}

		if (player.fishingBobber != null) {
			String[] songs=null;
			//Songs for other dimensions
			if (dimension !=0) {
				songs = getSongsForEvent(EVENT_FISHING+"\\"+dimension);
				if(songs==null)
					songs = getSongsForEvent(EVENT_FISHING);
			}
			if (songs != null)
				return songs;
		}

		ItemStack headItem = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
		if (headItem != null && headItem.getItem() == Item.getItemFromBlock(net.minecraft.block.Blocks.CARVED_PUMPKIN)) {
			String[] songs=null;
			//Songs for other dimensions
			if (dimension !=0 ) {
				songs = getSongsForEvent(EVENT_PUMPKIN_HEAD+"\\"+dimension);
				if(songs==null)
					songs = getSongsForEvent(EVENT_PUMPKIN_HEAD);
			}
			else
				songs = getSongsForEvent(EVENT_PUMPKIN_HEAD);
			if (songs != null)
				return songs;
		}

		if (dimension == -1) {
			
			String[] songs = getSongsForEvent(EVENT_IN_NETHER);
			if (songs != null)
				return songs;
		} else if (dimension == 1) {
			String[] songs = getSongsForEvent(EVENT_IN_END);
			if (songs != null)
				return songs;
		}

		Entity riding = player.getRidingEntity();
		if (riding != null) {
			if (riding instanceof MinecartEntity) {
				String[] songs=null;
				//Songs for other dimensions
				if (dimension !=0) {
					songs = getSongsForEvent(EVENT_MINECART+"\\"+dimension);
					if(songs==null)
						songs = getSongsForEvent(EVENT_MINECART);
				}
				else
					songs = getSongsForEvent(EVENT_MINECART);
				if (songs != null)
					return songs;
			}
			if (riding instanceof BoatEntity) {

				String[] songs = null;
				//Songs for other dimensions
				if (dimension !=0) {
					songs= eventMap.get(EVENT_BOAT+"\\"+dimension);
					if(songs==null)
						songs = getSongsForEvent(EVENT_BOAT);
				}			
				
				if (songs != null)
					return songs;
			}
			if (riding instanceof HorseEntity) {
				String[] songs=null;
				//Songs for other dimensions
				if (dimension !=0) {
					songs = getSongsForEvent(EVENT_HORSE+"\\"+dimension);
					if(songs==null)
						songs = getSongsForEvent(EVENT_HORSE);
				}
				else
					songs = getSongsForEvent(EVENT_HORSE);
				if (songs != null)
					return songs;
			}
			if (riding instanceof PigEntity) {
				String[] songs=null;
				//Songs for other dimensions
				if (dimension !=0) {
					songs = getSongsForEvent(EVENT_PIG+"\\"+dimension);
					if(songs==null)
						songs = getSongsForEvent(EVENT_PIG);
				}
				else
					songs = getSongsForEvent(EVENT_PIG);
				if (songs != null)
					return songs;
			}
		}

		
		
		if(DripWaterParticleFactory.dripsCount>0) {
			boolean underground = !world.canSeeSky(pos);

			uncountDripWater++;
			if(uncountDripWater >50 & DripWaterParticleFactory.dripsCount>=0) {
				DripWaterParticleFactory.dripsCount--;
				uncountDripWater=0;
			}
			
			if (underground) {
				String[] songs=null;
				//Songs for other dimensions
				if (dimension !=0) {
					songs = getSongsForEvent(EVENT_DRIPWATERINCAVE+"\\"+dimension);
					if(songs==null)
						songs = getSongsForEvent(EVENT_DRIPWATERINCAVE);
				}
				else
					songs = getSongsForEvent(EVENT_DRIPWATERINCAVE);
				if (songs != null)
					return songs;
			}
		}
				
		if(player.fallDistance>15) 
		{
			falling=true;
			String[] songs=null;
			//Songs for other dimensions
			if (dimension !=0) {
				songs = getSongsForEvent(EVENT_FALLING+"\\"+dimension);
				if(songs==null)
					songs = getSongsForEvent(EVENT_FALLING);
			}
			else
				songs = getSongsForEvent(EVENT_FALLING);
			if (songs != null) {
				EventHandlers.playInstant();
				return songs;	
			}
		}
		else {
			if(falling) {
				falling=false;			
				EventHandlers.playInstant();
			}
		}
		
		if (player.isElytraFlying()) {
			String[] songs=null;
			//Songs for other dimensions
			if (dimension !=0) {
				songs = getSongsForEvent(EVENT_ELYTRA+"\\"+dimension);
				if(songs==null)
					songs = getSongsForEvent(EVENT_ELYTRA);
			}
			else
				songs = getSongsForEvent(EVENT_ELYTRA);
			if (songs != null)
				return songs;
		}
		
		if (player.isInLava()) {
			String[] songs=null;
			//Songs for other dimensions
			if (dimension !=0) {
				songs = getSongsForEvent(EVENT_LAVA+"\\"+dimension);
				if(songs==null)
					songs = getSongsForEvent(EVENT_LAVA);
			}
			else
				songs = getSongsForEvent(EVENT_LAVA);
			if (songs != null)
				return songs;
		}
				
		long time = world.getDayTime() % 24000;
		boolean night = time > 13300 && time < 23200;
		
		// Get Area Songs
		if (world != null) {
			if (Ambience.getWorldData().listAreas != null) {

				for (Area area : Ambience.getWorldData().listAreas) {
					if (area.getDimension() == player.dimension.getId()) {
						
						Area currentArea = Area.getPlayerStandingArea(player);												
						if(currentArea!=null & currentArea==area) {
								if(night & area.isPlayatNight()) {
									if(area.isInstantPlay())
										EventHandlers.playInstant();
								
									getTransition(world,player,area.getName().toLowerCase(),false);
									return areasMap.get(area.getName());
								}else if(!night) {
									if(area.isInstantPlay())
										EventHandlers.playInstant();
																		
									if(area.getRedstoneStrength()==0) {			

										getTransition(world,player,area.getName().toLowerCase(),false);
										return areasMap.get(area.getName());	
									}else {			
										String[] songs= areasMap.get(area.getName()+"."+area.getRedstoneStrength());	
										

										getTransition(world,player,area.getName().toLowerCase(),false);
										if(songs!=null)
											return songs;
										else						
											return areasMap.get(area.getName());	
											
									}
								}
						}						
					}
				}				
			}
		}
				
		//Events for the World Structures
		if(StructureName!="") 
		{								
			String[] songs=null;
			//Songs for other dimensions
			if (dimension !=0) {
				songs = getSongsForEvent(StructureName+"\\"+dimension);
				if(songs==null)
					songs = getSongsForEvent(StructureName);
			}
			else
				songs = getSongsForEvent(StructureName);
					
			getTransition(world,player,StructureName,true);
			
			if (songs != null) 
			{
				return songs;	
			}
		}
		
		if (player.isInWater() & !Ambience.attacked) {
			String[] songs=null;
			//Songs for other dimensions
			if (dimension !=0) {
				songs = getSongsForEvent(EVENT_UNDERWATER+"\\"+dimension);
				if(songs==null)
					songs = getSongsForEvent(EVENT_UNDERWATER);
			}
			else
				songs = getSongsForEvent(EVENT_UNDERWATER);
			if (songs != null)
				return songs;
		}
		
		if (world.dimension.isSurfaceWorld()) {
			boolean underground = !world.canSeeSky(pos);

			if (underground) {
				if (pos.getY() < 20) {
					String[] songs=null;
					//Songs for other dimensions
					if (dimension !=0) {
						songs = getSongsForEvent(EVENT_DEEP_UNDEGROUND+"\\"+dimension);
						if(songs==null)
							songs = getSongsForEvent(EVENT_DEEP_UNDEGROUND);
					}
					else
						songs = getSongsForEvent(EVENT_DEEP_UNDEGROUND);
					if (songs != null)
						return songs;
				}
				if (pos.getY() < 55) {
					String[] songs=null;
					//Songs for other dimensions
					if (dimension !=0) {
						songs = getSongsForEvent(EVENT_UNDERGROUND+"\\"+dimension);
						if(songs==null)
							songs = getSongsForEvent(EVENT_UNDERGROUND);
					}
					else
						songs = getSongsForEvent(EVENT_UNDERGROUND);
					if (songs != null)
						return songs;
				}else if (world.isRaining()) {
					String[] songs=null;
					//Songs for other dimensions
					if (dimension !=0) {
						songs = getSongsForEvent(EVENT_RAIN+"\\"+dimension);
						if(songs==null)
							songs = getSongsForEvent(EVENT_RAIN);
					}
					else
						songs = getSongsForEvent(EVENT_RAIN);
					if (songs != null)
						return songs;
				}
			} else if (world.isRaining()) {
				String[] songs=null;
				//Songs for other dimensions
				if (dimension !=0) {
					songs = getSongsForEvent(EVENT_RAIN+"\\"+dimension);
					if(songs==null)
						songs = getSongsForEvent(EVENT_RAIN);
				}
				else
					songs = getSongsForEvent(EVENT_RAIN);
				if (songs != null)
					return songs;
			}

			if (pos.getY() > 128) {
				String[] songs=null;
				//Songs for other dimensions
				if (dimension !=0) {
					songs = getSongsForEvent(EVENT_HIGH_UP+"\\"+dimension);
					if(songs==null)
						songs = getSongsForEvent(EVENT_HIGH_UP);
				}
				else
					songs = getSongsForEvent(EVENT_HIGH_UP);
				if (songs != null)
					return songs;
			}

			if (night) {
				String[] songs=null;
				//Songs for other dimensions
				if (dimension !=0) {
					songs = getSongsForEvent(EVENT_NIGHT+"\\"+dimension);
					if(songs==null)
						songs = getSongsForEvent(EVENT_NIGHT);
				}
				else
					songs = getSongsForEvent(EVENT_NIGHT);
				if (songs != null)
					return songs;
			}
		}



		List<LivingEntity> EntitiesCount = world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(player.getPosX() - 30,
				player.getPosY() - 8, player.getPosZ() - 30, player.getPosX() + 30, player.getPosY() + 8, player.getPosZ() + 30));

		int villagerCount=0, countPassiveMobs=0;
		
		for (LivingEntity mob : EntitiesCount) {	
			if(mob instanceof VillagerEntity) 
				villagerCount++;		
			
			if (mob instanceof HorseEntity 
				| mob instanceof CowEntity
				| mob instanceof ChickenEntity 
				| mob instanceof SheepEntity
				| mob instanceof PigEntity 
				| mob instanceof BeeEntity
				| mob instanceof RabbitEntity
			)
				countPassiveMobs++;		
		}
		
		if (villagerCount > 3) {
			if (night) {
				String[] songs=null;
				//Songs for other dimensions
				if (dimension !=0) {
					songs = getSongsForEvent(EVENT_VILLAGE_NIGHT+"\\"+dimension);
					if(songs==null)
						songs = getSongsForEvent(EVENT_VILLAGE_NIGHT);
				}
				else
					songs = getSongsForEvent(EVENT_VILLAGE_NIGHT);
				if (songs != null)
					return songs;
			}

			String[] songs=null;
			//Songs for other dimensions
			if (dimension !=0) {
				songs = getSongsForEvent(EVENT_VILLAGE+"\\"+dimension);
				if(songs==null)
					songs = getSongsForEvent(EVENT_VILLAGE);
			}
			else
				songs = getSongsForEvent(EVENT_VILLAGE);
			if (songs != null)
				return songs;
		}
		
		if(countPassiveMobs>15) {
			if (night) {
				String[] songs=null;
				//Songs for other dimensions
				if (dimension !=0) {
					songs = getSongsForEvent(EVENT_RANCH_NIGHT+"\\"+dimension);
					if(songs==null)
						songs = getSongsForEvent(EVENT_RANCH_NIGHT);
				}
				else
					songs = getSongsForEvent(EVENT_RANCH_NIGHT);
				if (songs != null)
					return songs;
			}

			String[] songs=null;
			//Songs for other dimensions
			if (dimension !=0) {
				songs = getSongsForEvent(EVENT_RANCH+"\\"+dimension);
				if(songs==null)
					songs = getSongsForEvent(EVENT_RANCH);
			}
			else
				songs = getSongsForEvent(EVENT_RANCH);
			if (songs != null)
				return songs;
		}
		
		/*event = new AmbienceEventEvent.Post(world, pos);
		MinecraftForge.EVENT_BUS.post(event);
		eventr = getSongsForEvent(event.event);
		if (eventr != null)
			return eventr;	*/

		if (world != null) {
			if(Ambience.instantPlaying) {
				EventHandlers.playInstant();
				Ambience.instantPlaying=false;
			}
			
			if(eventMap.containsKey("dim"+dimension)) {
				
				return eventMap.get("dim"+dimension);
			}
			
			//Chunk chunk = world.getChunkAt(pos);
			Biome biome = world.getBiome(pos);
			if (biomeMap.containsKey(biome)) 
			{
				Ambience.overideBackMusicDimension=true;
				return biomeMap.get(biome);
			}else {
				Ambience.overideBackMusicDimension=false;
			}

			Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(biome);
			for (Type t : types)
				if (primaryTagMap.containsKey(t))
					return primaryTagMap.get(t);
			for (Type t : types)
			{
				if(dimension >=-1 & dimension<=1) {					
					if (secondaryTagMap.containsKey(t))
						return secondaryTagMap.get(t);					
				}else {
					return new String[] {"null"};
				}
			}
		}

		return getSongsForEvent(EVENT_GENERIC);
	}

	public static String getSongsString() {
		return StringUtils.join(getSongs(), ",");
	}

	public static String getRandomSong() {
		String[] songChoices = getSongs();

		try {

			if (songChoices != null) {
				if (songChoices.length > 0)
					return songChoices[rand.nextInt(songChoices.length)];
				else
					return null;
			} else
				return null;

		} catch (Exception ex) {
			return null;
		}
	}
	
	public static Long removeDays(String structureName) {
		
		if (cinematicMap.containsKey(structureName))
			return cinematicMap.remove(structureName);

		return null;
	}
	
	public static Long getDays(String structureName) {
		
		if (cinematicMap.containsKey(structureName))
			return cinematicMap.get(structureName);

		return null;
	}

	public static String[] getSongsForEvent(String event) {
		
		if (eventMap.containsKey(event))
			return eventMap.get(event);

		return null;
	}

	public static String getSongName(String song) {
		return song == null ? "" : song.replaceAll("([^A-Z])([A-Z])", "$1 $2");
	}
	
	
	private static int getSongLenght(String song) {
		
		int songLenght=0;
		// Obtém o tempo do som selecionado********************
		File f = new File(Ambience.ambienceDir+"\\music", song + ".mp3");

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
		// ****************************************************
		return songLenght;
	}
	
	private static void getTransition(World world, PlayerEntity player, String structureName, boolean playSound) {

		if(AmbienceConfig.COMMON.structuresCinematic.get())
			if(getDays(structureName)==null) {										
				if(transitionsMap.contains(structureName)) {
					
					if(!cinematicMap.containsKey(structureName) & playSound)
					world.playSound(player, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(),
							ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("minecraft:block.end_portal.spawn")),
							SoundCategory.BLOCKS, (float) 10, (float) 1);
					
					cinematicMap.put(structureName, world.getGameTime() / 24000);		
					CinematicRender.AREA_LOGO=new ResourceLocation(Ambience.MODID,"textures/transitions/"+structureName.toLowerCase()+".png");						
					CinematicRender.ativated=true;
				}
			}
			else 
			{
				Long days;		
				long daysPassed = world.getGameTime() / 24000;
				days=getDays(structureName);
				
				if(daysPassed-days>=1) {
					removeDays(structureName);
				}						
			}
	}
}
