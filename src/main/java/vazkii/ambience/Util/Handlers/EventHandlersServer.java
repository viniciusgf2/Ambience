package vazkii.ambience.Util.Handlers;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.SplashParticle;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import vazkii.ambience.Ambience;
import vazkii.ambience.PlayerThread;
import vazkii.ambience.SongPicker;
import vazkii.ambience.Util.SplashFactory2;
import vazkii.ambience.Util.WorldData;
import vazkii.ambience.Util.particles.DripLavaParticleFactory;
import vazkii.ambience.Util.particles.DripWaterParticleFactory;
import vazkii.ambience.commands.CreateAreaCommand;
import vazkii.ambience.commands.DeleteAreaCommand;
import vazkii.ambience.commands.UpdateAreaCommand;
import vazkii.ambience.items.Horn;
import vazkii.ambience.items.Ocarina;
import vazkii.ambience.network.AmbiencePackageHandler;
import vazkii.ambience.network.MyMessage;
import vazkii.ambience.network.OcarinaMessage;
import vazkii.ambience.network.OcarinaPackageHandler;

@Mod.EventBusSubscriber(modid = Ambience.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandlersServer {

	public int attackFadeTime = 300;
	public static int attackingTimer;
	String mobName = null;
	
	public int countNote = 0;
	boolean played_match = false;
	boolean settingDay = false,settingNight = false;	
	public Ocarina Ocarina=new Ocarina(20);
	
	public String insideStructureName="";
	
	public static Map<String, String> insideStructureMap = new HashMap<String, String>();
	
	public EventHandlersServer() {
		attackingTimer = attackFadeTime;
	}

	/*@SubscribeEvent
	public void onAdvancement(AdvancementEvent event) 
	{
		if(event.getPhase() == EventPriority.NORMAL) {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putBoolean("playAdvancement", true);			

			AmbiencePackageHandler.sendToClient(new MyMessage(nbt), (ServerPlayerEntity) event.getPlayer());			
		}
	}*/
	
	@SubscribeEvent
	public void onServerStarting(final FMLServerStartingEvent event) {
		CreateAreaCommand.register(event.getCommandDispatcher());
		DeleteAreaCommand.register(event.getCommandDispatcher());
		UpdateAreaCommand.register(event.getCommandDispatcher());
	}
	
	@SubscribeEvent
	public void onPlayerTick(TickEvent.WorldTickEvent.PlayerTickEvent event) {
	
		if(!event.player.world.isRemote)
		{
			//Check if Player is inside a Structure----------------------------------------------------
			PlayerEntity player= event.player;// w.playerEntities.get(0);
			
			World world2 = player.world;	
			AbstractChunkProvider prov = world2.getChunkProvider();
			
			
			if (prov instanceof AbstractChunkProvider) {
									
				if(LocationPredicate.forFeature(Feature.NETHER_BRIDGE).test((ServerWorld) world2, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ())) {					
					updatePlayersInsideStructure(player,"fortress");
				}
				else if(LocationPredicate.forFeature(Feature.STRONGHOLD).test((ServerWorld) world2, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ())) {					
					updatePlayersInsideStructure(player,"stronghold");
				}
				else if(LocationPredicate.forFeature(Feature.WOODLAND_MANSION).test((ServerWorld) world2, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ())) {					
					updatePlayersInsideStructure(player,"mansion");
				}
				else if(LocationPredicate.forFeature(Feature.OCEAN_MONUMENT).test((ServerWorld) world2, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ())) {					
					updatePlayersInsideStructure(player,"oceanmonument");
				}
				else if(LocationPredicate.forFeature(Feature.MINESHAFT).test((ServerWorld) world2, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ())) {											
					updatePlayersInsideStructure(player,"mineshaft");					
				}
				else if(LocationPredicate.forFeature(Feature.DESERT_PYRAMID).test((ServerWorld) world2, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ())) {					
					updatePlayersInsideStructure(player,"deserttemple");
				}
				else if(LocationPredicate.forFeature(Feature.END_CITY).test((ServerWorld) world2, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ())) {					
					updatePlayersInsideStructure(player,"endcity");
				}
				else if(LocationPredicate.forFeature(Feature.IGLOO).test((ServerWorld) world2, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ())) {					
					updatePlayersInsideStructure(player,"igloo");
				}
				else if(LocationPredicate.forFeature(Feature.JUNGLE_TEMPLE).test((ServerWorld) world2, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ())) {					
					updatePlayersInsideStructure(player,"jungletemple");
				}
				else if(LocationPredicate.forFeature(Feature.OCEAN_RUIN).test((ServerWorld) world2, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ())) {					
					updatePlayersInsideStructure(player,"oceanruin");
				}
				else if(LocationPredicate.forFeature(Feature.PILLAGER_OUTPOST).test((ServerWorld) world2, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ())) {					
					updatePlayersInsideStructure(player,"pillageroutpost");
				}
				else if(LocationPredicate.forFeature(Feature.SHIPWRECK).test((ServerWorld) world2, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ())) {					
					updatePlayersInsideStructure(player,"shipwreck");
				}
				else if(LocationPredicate.forFeature(Feature.SWAMP_HUT).test((ServerWorld) world2, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ())) {					
					updatePlayersInsideStructure(player,"swamphut");
				}	
				else 
				{	
					//When leave the structure update the player
					if(insideStructureMap.containsKey(player.getDisplayName().getFormattedText())) {
						insideStructureMap.remove(player.getDisplayName().getFormattedText());
					
						CompoundNBT nbt = new CompoundNBT();
						nbt.putString("StructureName", "");			
			
						AmbiencePackageHandler.sendToClient(new MyMessage(nbt), (ServerPlayerEntity) player);
					}
				}			
			}
		}
		//------------------------------------------------------------------------------------------
		
		if (Horn.fadeOutTimer > 0)
			Horn.fadeOutTimer--;

		if (Horn.fadeOutTimer > 0 & Horn.fadeOutTimer < 300) {
			Horn.repelEntities(event.player.world, event.player, 1D);
		}
				
		//Set the time of the day when the Ocarina played the Sun's Song
		if(!event.player.world.isRemote) {
			World world=event.player.world;
						
			long time = world.getDayTime() % 24000;
			boolean night = time > 13300 && time < 23200;
						
			if(Ocarina.setDayTime & night)
			{
				if(!settingNight) {
					settingDay=true;
					event.player.world.setDayTime(event.player.world.getDayTime()+10);		
				}else {
					Ocarina.setDayTime=false;
					
					CompoundNBT nbt = new CompoundNBT();
					nbt.putBoolean("setDayTime",false);					
					OcarinaPackageHandler.sendToClient(new OcarinaMessage(nbt), (ServerPlayerEntity) event.player);
				}
			}else if(Ocarina.setDayTime & !night){
				
				if(!settingDay) {
					settingNight=true;
					event.player.world.setDayTime(event.player.world.getDayTime()+10);
				}
				else {
					Ocarina.setDayTime=false;
					
					CompoundNBT nbt = new CompoundNBT();
					nbt.putBoolean("setDayTime",false);					
					OcarinaPackageHandler.sendToClient(new OcarinaMessage(nbt), (ServerPlayerEntity) event.player);
				}
			}
			else {
				Ocarina.setDayTime=false;
				settingDay=false;
				settingNight=false;				
			}
		}	
	}	
	
	private void updatePlayersInsideStructure(PlayerEntity player,String StructureName) {		
		String playerName=player.getDisplayName().getFormattedText();
		if(!insideStructureMap.containsKey(playerName)) {
			insideStructureMap.put(playerName, StructureName);
			
			CompoundNBT nbt = new CompoundNBT();
			nbt.putString("StructureName", StructureName);			

			AmbiencePackageHandler.sendToClient(new MyMessage(nbt), (ServerPlayerEntity) player);
		}
	}

	// Quando alguma coisa ataca o player
	@SubscribeEvent
	@OnlyIn(value = Dist.CLIENT)
	public void onEntitySetAttackTargetEvent(LivingSetAttackTargetEvent event) {
		if (event.getTarget() instanceof ServerPlayerEntity) {			
			//if(event.getTarget().getLastDamageSource()!=null)
			//System.out.println(event.getTarget().getLastDamageSource().damageType);						
			Ambience.attacked = true;
			attackingTimer = attackFadeTime;
	
			EventHandlers.playInstant();	
		}
	}

	// FUNCIONA Quando player ataca alguma coisa
	@SubscribeEvent
	@OnlyIn(value = Dist.CLIENT)
	public void onPlayerAttackEvent(AttackEntityEvent event) {
		mobName = event.getTarget().getName().getString().toLowerCase();

		if (event.getTarget() instanceof MobEntity) {
			// if (event.getTarget().isCreatureType(EnumCreatureType.MONSTER, false)) {
			Ambience.attacked = true;

			attackingTimer = attackFadeTime;
			EventHandlers.playInstant();
		}
	}

	// On something dies
	@SubscribeEvent
	@OnlyIn(value = Dist.CLIENT)
	public void onEntityDeath(LivingDeathEvent event) {
		DamageSource source = event.getSource();

		// When Player kills something
		if (source.getTrueSource() instanceof PlayerEntity & event.getEntity() == Minecraft.getInstance().player) {
			Ambience.attacked = false;
		}

		// When Player dies
		if (event.getEntity() instanceof PlayerEntity & event.getEntity() == Minecraft.getInstance().player) {
			Ambience.attacked = false;
		}
	}

	// Injection of events to the particles
	@SubscribeEvent
	@OnlyIn(value = Dist.CLIENT)
	public void onWorldLoad(WorldEvent.Load ev) {

		Minecraft mc = Minecraft.getInstance();
		if (mc.particles != null) {
			try {
				SongPicker.cinematicMap.clear();
				// get existing splash particle factory

				// do Map<ResourceLocation, IParticleFactory<?>> facts =
				// Minecraft.getInstance().particles.factories;
				Map<ResourceLocation, IParticleFactory<?>> facts = ObfuscationReflectionHelper
						.getPrivateValue(ParticleManager.class, mc.particles, "field_178932_g");
				IParticleFactory pf = facts.get(ParticleTypes.SPLASH.getRegistryName());

				// check that it's the vanilla one
				if (pf instanceof SplashParticle.Factory) {
					// inject custom splash particle factory
					mc.particles.registerFactory(ParticleTypes.SPLASH, SplashFactory2::new);
					IParticleFactory npf = facts.get(ParticleTypes.SPLASH.getRegistryName());

					// check that it worked
					if (npf instanceof SplashFactory2) {
						// wrap the original factory to copy the sprite data
						((SplashFactory2) npf).wrap((SplashParticle.Factory) pf);
					}
				}

				// For Dripping Water on water inside caves
				pf = facts.get(ParticleTypes.DRIPPING_WATER.getRegistryName());

				// check that it's the vanilla one
				if (pf instanceof DripParticle.DrippingWaterFactory) {
					// inject custom splash particle factory
					mc.particles.registerFactory(ParticleTypes.DRIPPING_WATER, DripWaterParticleFactory::new);
					IParticleFactory npf = facts.get(ParticleTypes.DRIPPING_WATER.getRegistryName());

					// check that it worked
					if (npf instanceof DripWaterParticleFactory) {
						// wrap the original factory to copy the sprite data
						((DripWaterParticleFactory) npf).wrap((DripParticle.DrippingWaterFactory) pf);
					}
				}

				// For Dripping Lava on ground
				pf = facts.get(ParticleTypes.LANDING_LAVA.getRegistryName());

				// check that it's the vanilla one
				if (pf instanceof DripParticle.LandingLavaFactory) {
					// inject custom splash particle factory
					mc.particles.registerFactory(ParticleTypes.LANDING_LAVA, DripLavaParticleFactory::new);
					IParticleFactory npf = facts.get(ParticleTypes.LANDING_LAVA.getRegistryName());

					// check that it worked
					if (npf instanceof DripLavaParticleFactory) {
						// wrap the original factory to copy the sprite data
						((DripLavaParticleFactory) npf).wrap((DripParticle.LandingLavaFactory) pf);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SubscribeEvent
	@OnlyIn(value = Dist.CLIENT)
	public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
		if (!Minecraft.getInstance().gameSettings.showDebugInfo)
			return;

		event.getRight().add(null);
		if ((Ambience.dimension >= -1 & Ambience.dimension <= 1)
				| PlayerThread.currentSong != "null" & EventHandlers.nextSong != "null") {

			if (PlayerThread.currentSong != null) {
				String name = "Now Playing: " + SongPicker.getSongName(PlayerThread.currentSong);
				event.getRight().add(name);
			}
			if (EventHandlers.nextSong != null) {
				String name = "Next Song: " + SongPicker.getSongName(EventHandlers.nextSong);
				event.getRight().add(name);
			}
		}
	}

	// Server Side
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		Ambience.selectedArea = null;
		
		WorldData data = new WorldData();

		ServerWorld world = (ServerWorld) event.getPlayer().world;
		data.GetArasforWorld(world);

		if (data.listAreas != null)
			Ambience.setWorldData(data.GetArasforWorld(world));

		if (data.listAreas.size() > 0) {
			CompoundNBT nbt = WorldData.SerializeThis(Ambience.getWorldData().listAreas);

			AmbiencePackageHandler.sendToClient(new MyMessage(nbt), (ServerPlayerEntity) event.getPlayer());

		}
	}
}
