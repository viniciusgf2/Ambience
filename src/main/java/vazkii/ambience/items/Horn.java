package vazkii.ambience.items;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class Horn extends ItemBase {

	public static int fadeOutTimer;
	public static boolean shouting=false;
	public static PlayerEntity player;
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
			
			player=playerIn;
			/*List<Entity> entities = worldIn.getEntitiesWithinAABB(Entity.class,	new AxisAlignedBB(playerIn.getPosX() - 20, playerIn.getPosY() - 10, playerIn.getPosZ() - 20, playerIn.getPosX() + 20,playerIn.getPosY() + 10, playerIn.getPosZ() + 20));
			
			for (Entity entity : entities) {
				
				System.out.println(entity.getDisplayName());
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
			}*/
						
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
		
		BlockPos pos=player.getPosition();
				
		//Play the horn and apply some effects to the entities
		if(shouting) {
			shouting=false;
			int rand = getRandom(1, 3);
			worldIn.playSound(playerIn, playerIn.getPosition(),
			ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambience:horn" + rand)),SoundCategory.AMBIENT, 10, 1);			
		}
		
		
		//TESTE TAMED ENTITIES****************************
		/*for (Entity entity : entities) {
			if (entity instanceof HorseEntity) {

				HorseEntity horse = ((HorseEntity) entity);
				if (horse.getOwnerUniqueId().equals(playerIn.getUniqueID())) {

					
					int dir = MathHelper.floor((double)(playerIn.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
					int dirX=0;
					int dirZ=0;
					switch (dir) {
						case 0: dirZ =-5;break;//South
						case 1: dirX =5;break;//west
						case 2: dirZ =5;break;//North
						case 3: dirX =-5;break;//east
					}
					
					Vec3d vector = new Vec3d(playerIn.getPosX()+dirX, 256, playerIn.getPosZ()+dirZ);
					BlockRayTraceResult rayTraceResult = worldIn
							.rayTraceBlocks(new RayTraceContext(vector, vector.add(new Vec3d(0, 1, 0).scale(-256)),
									RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.ANY, playerIn));

					
					
					horse.setPositionAndUpdate(rayTraceResult.getPos().getX(), rayTraceResult.getPos().getY()+2,
							rayTraceResult.getPos().getZ());
					
					System.out.println("X= "+ (rayTraceResult.getPos().getX() + dirX ) +" Y:"+ (rayTraceResult.getPos().getY()) +" Z: " + (rayTraceResult.getPos().getZ()+dirZ));
				}
			}
		}*/
		
		//*****************************
		
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
				Vector3d angleOfAttack = entity.getPositionVec().add(-(pos.getX() + 0.5D), -(pos.getY() - 0.8D), -(pos.getZ() + 0.5D));

				// we use the resultant vector to determine the force to apply.
				double xForce = angleOfAttack.x * knockbackMultiplier * reductionCoefficient;
				double yForce = angleOfAttack.y * knockbackMultiplier * reductionCoefficient;
				double zForce = angleOfAttack.z * knockbackMultiplier * reductionCoefficient;
				entity.setMotion(entity.getMotion().add(xForce, yForce, zForce));
				
				
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
