package vazkii.ambience.Util.Handlers;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil.Test;
import org.lwjgl.input.Keyboard;

import com.google.common.collect.Maps;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.ParticleDrip;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleRain;
import net.minecraft.client.particle.ParticleSplash;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.CheckResult;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.ambience.Ambience;
import vazkii.ambience.NilMusicTicker;
import vazkii.ambience.PlayerThread;
import vazkii.ambience.SongLoader;
import vazkii.ambience.SongPicker;
import vazkii.ambience.Util.particles.DripLavaParticleFactory;
import vazkii.ambience.Util.particles.DripWaterParticleFactory;

public class EventHandlers {

	public int attackFadeTime = 300;
	public static int attackingTimer;
	Entity currentplayer;
	public static KeyBinding[] keyBindings;
	public Ambience ambience;

	// constructor
	public EventHandlers(Ambience amb) {
		this.ambience = amb;
		attackingTimer = attackFadeTime;

		currentplayer = Minecraft.getMinecraft().player;
		// declare an array of key bindings
		keyBindings = new KeyBinding[2];
		keyBindings[0] = new KeyBinding("Options.Reload", Keyboard.KEY_P, "Ambience");
		keyBindings[1] = new KeyBinding("Force Play", Keyboard.KEY_O, "Ambience");

		// register all the key bindings
		for (int i = 0; i < keyBindings.length; ++i) {
			ClientRegistry.registerKeyBinding(keyBindings[i]);
		}
	}

	int count = 0;
	boolean pressedkey = false;

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (currentplayer == null)
			currentplayer = Minecraft.getMinecraft().player;

		// check each enumerated key binding type for pressed and take appropriate
		// action
		if (keyBindings[0].isPressed()) {
			SongPicker.reset();
			// Ambience.thread.forceKill();
			// Ambience.thread.run();
			SongLoader.loadFrom(ambience.ambienceDir);

			Minecraft mc = Minecraft.getMinecraft();
			MusicTicker ticker = new NilMusicTicker(mc);
			ReflectionHelper.setPrivateValue(Minecraft.class, mc, ticker, Ambience.OBF_MC_MUSIC_TICKER);
		}

		if (keyBindings[1].isPressed()) {
			// SongPicker.reset();
			Ambience.thread.forceKill();
			Ambience.thread.run();
			SongLoader.loadFrom(ambience.ambienceDir);

			if (SongLoader.enabled)
				Ambience.thread = new PlayerThread();

			Minecraft mc = Minecraft.getMinecraft();
			MusicTicker ticker = new NilMusicTicker(mc);
			ReflectionHelper.setPrivateValue(Minecraft.class, mc, ticker, Ambience.OBF_MC_MUSIC_TICKER);
		}

		/*
		 * if (keyBindings[1].isPressed()) { if
		 * (SongPicker.getSongName(PlayerThread.currentSong) != "Boss3") { pressedkey =
		 * true; ambience.forcePlay = true; ambience.nextSong =
		 * SongPicker.getSongName("Boss3"); ambience.fadeOutTicks =
		 * Ambience.FADE_DURATION; ambience.silenceTicks = Ambience.SILENCE_DURATION;
		 * 
		 * playInstant(); } else { Ambience.forcePlay = false; SongPicker.songTimer = 0;
		 * } }
		 */
	}

	String mobName = null;
	Boolean isHostile = false;

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
		mobName = event.getTarget().getName().toLowerCase();

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
		
		if (mc.effectRenderer != null) 
		{
			try {
								
				ParticleManager instance = mc.effectRenderer;								
				Map<Integer, IParticleFactory> facts= ObfuscationReflectionHelper.getPrivateValue(ParticleManager.class, instance, "field_178932_g");				
				IParticleFactory pf = facts.get(EnumParticleTypes.DRIP_WATER.getParticleID());
		
				if (pf instanceof ParticleDrip.WaterFactory) {
				
					instance.registerParticle(EnumParticleTypes.DRIP_WATER.getParticleID(), new DripWaterParticleFactory());
					
					//IParticleFactory npf = facts.get(5);
									
					// check that it worked
					// wrap the original factory to copy the sprite data
					//if(npf instanceof DripWaterParticleFactory)
						//((DripWaterParticleFactory) npf).wrap((ParticleDrip.WaterFactory)pf);
					
				}				
				
				pf = facts.get(EnumParticleTypes.DRIP_LAVA.getParticleID());				
				if (pf instanceof ParticleDrip.LavaFactory) {
				
					instance.registerParticle(EnumParticleTypes.DRIP_LAVA.getParticleID(), new DripLavaParticleFactory());
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
		Ambience.fadeOutTicks = Ambience.FADE_DURATION;
		Ambience.silenceTicks = Ambience.SILENCE_DURATION;
		Ambience.waitTick = 0;
		Ambience.instantPlaying = true;

		Ambience.thread.setGain(PlayerThread.fadeGains[0]);
		Ambience.fadeIn = false;
	}

	public static boolean show = false;

	@SubscribeEvent
	public void firstRender(RenderGameOverlayEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
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
