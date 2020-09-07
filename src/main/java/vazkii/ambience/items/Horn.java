package vazkii.ambience.items;

import java.beans.EventHandler;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.ambience.Ambience;
import vazkii.ambience.Screens.EditAreaContainer;
import vazkii.ambience.Screens.GuiContainerMod;
import vazkii.ambience.Screens.SpeakerContainer;
import vazkii.ambience.Util.Border;
import vazkii.ambience.Util.WorldData;
import vazkii.ambience.Util.Handlers.EventHandlers;
import vazkii.ambience.Util.Handlers.EventHandlersServer;
import vazkii.ambience.World.Biomes.Area;
import vazkii.ambience.World.Biomes.Area.Operation;
import vazkii.ambience.blocks.Speaker;
import vazkii.ambience.network.AmbiencePackageHandler;
import vazkii.ambience.network.MyMessage;
import vazkii.ambience.render.HornRender;

public class Horn extends ItemBase {

	public static int fadeOutTimer;
	public static boolean shouting=false;
	public Horn(int Maxdamage) {
		super(Maxdamage);

	}

	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}

	public int getUseDuration(ItemStack stack) {
		return 2000;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {

		boolean flag = true;
		ItemStack itemstack = playerIn.getHeldItem(handIn);

		ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemstack, worldIn,
				playerIn, handIn, flag);
		if (ret != null)
			return ret;

		itemstack.damageItem(1, playerIn, (damage) -> {
			damage.sendBreakAnimation(playerIn.getActiveHand());
		});
		
		if (!playerIn.abilities.isCreativeMode && !flag) {
			return ActionResult.resultFail(itemstack);
		} else {
			playerIn.setActiveHand(handIn);
				
			shouting=true;
			//This timer is used to activate the horn effects (repels entities etc...)
			fadeOutTimer=380;
						
			return ActionResult.resultConsume(itemstack);
		}
	}

	

	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) 
	{
		Horn.fadeOutTimer=0;
		shouting=false;
	}

	public static void repelEntities(World worldIn, PlayerEntity playerIn,double force) {		
		List<Entity> entities = worldIn.getEntitiesWithinAABB(Entity.class,	new AxisAlignedBB(playerIn.getPosX() - 20, playerIn.getPosY() - 10, playerIn.getPosZ() - 20, playerIn.getPosX() + 20,playerIn.getPosY() + 10, playerIn.getPosZ() + 20));
		
		BlockPos pos=playerIn.getPosition();
		
		//Play the horn and apply some effects to the entities
		if(shouting) {
			shouting=false;
			int rand = getRandom(1, 3);
			worldIn.playSound(playerIn, playerIn.getPosition(),
			ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambience:horn" + rand)),SoundCategory.AMBIENT, 10, 1);			
		}
		
		//Calcs the knockback
		if (worldIn.isRemote) {
			return;
		}
		int radius = 8;

		for (Entity entity : entities) {
			
			double distance = Math.sqrt(entity.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()));
			if (distance < radius && distance != 0) {
				
				if (distance < 1D) {
					distance = 1D;
				}
				double knockbackMultiplier = force + (force / distance);
				double reductionCoefficient = 0.04D;

				// the resultant vector between the two 3d coordinates is the
				// difference of each coordinate pair			
				Vec3d angleOfAttack = entity.getPositionVec().add(-(pos.getX() + 0.5D), -(pos.getY() - 0.8D), -(pos.getZ() + 0.5D));

				// we use the resultant vector to determine the force to apply.
				double xForce = angleOfAttack.x * knockbackMultiplier * reductionCoefficient;
				double yForce = angleOfAttack.y * knockbackMultiplier * reductionCoefficient;
				double zForce = angleOfAttack.z * knockbackMultiplier * reductionCoefficient;
				entity.setMotion(entity.getMotion().add(xForce, yForce, zForce));
			}
		
			//Encourages players nearby
			if (entity instanceof PlayerEntity) {			      
				playerIn.addPotionEffect(new EffectInstance(Effects.SPEED, 60*20, 0));
				playerIn.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 60*20, 3));
			}
			if (entity instanceof MonsterEntity)
			{	
				//Add fear to the heart of the enemies					
				((MonsterEntity)entity).addPotionEffect(new EffectInstance(Effects.WEAKNESS, 5*20, 1));							      
				((MonsterEntity)entity).goalSelector.addGoal(3, new AvoidEntityGoal<>((CreatureEntity)entity, PlayerEntity.class, 16.0F, 3.5D, 2.2D));					
			}				
		}
	}

	public static int getRandom(int min, int max) {
		int x = (int) ((Math.random() * ((max - min) + 1)) + min);
		return x;
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add((ITextComponent) new StringTextComponent(I18n.format("Horn.Desc")));
	}

}
