package vazkii.ambience.Util.Handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.ambience.Ambience;
import vazkii.ambience.PlayerThread;
import vazkii.ambience.SongPicker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod.EventBusSubscriber(modid = Ambience.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandlersServer {

	public int attackFadeTime = 300;
	public static int attackingTimer;

	public EventHandlersServer() {
		attackingTimer = attackFadeTime;
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		// System.out.println("olá");
	}

	String mobName = null;

	// Quando alguma coisa ataca o player
	@SubscribeEvent
	@OnlyIn(value = Dist.CLIENT)
	public void onEntitySetAttackTargetEvent(LivingSetAttackTargetEvent event) {

		if (event.getTarget() instanceof ServerPlayerEntity) {
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
		String timer = "Attacking Timer: " + EventHandlersServer.attackingTimer;
		event.getRight().add(timer);
	}
}
