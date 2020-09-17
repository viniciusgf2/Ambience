package vaskii.ambience.objects.items;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import vaskii.ambience.network4.MyMessage4;
import vaskii.ambience.network4.NetworkHandler4;

public class Horn extends ItemBase {
	public static int fadeOutTimer;
	public boolean shouting=false;
	public static EntityPlayer player;
	
	public Horn(String Name) {
		super(Name);
		setMaxDamage(20);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
	    return EnumAction.BOW;
	}

	@Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 72000;
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack itemstack = playerIn.getHeldItem(handIn);
	
		if (!playerIn.capabilities.isCreativeMode) {
			return new ActionResult(EnumActionResult.PASS, itemstack);
		} else {
			playerIn.setActiveHand(handIn);

			shouting = true;
			// This timer is used to activate the horn effects (repels entities etc...)
			fadeOutTimer = 380;
			player = playerIn;			
			
			/*if(worldIn.isRemote) {
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setBoolean("shouting",shouting);							
				NetworkHandler4.sendToServer(new MyMessage4(nbt));
				

				System.out.println(shouting);
			}
*/
			itemstack.damageItem(1, playerIn);

			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
		}
	}

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
	{
		Horn.fadeOutTimer=0;
		shouting=false;
		
		/*NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("shouting",shouting);							
		NetworkHandler4.sendToServer(new MyMessage4(nbt));*/
	}

	public void repelEntities(World worldIn, EntityPlayer playerIn, double force) {

		BlockPos pos = player.getPosition();

		// Play the horn and apply some effects to the entities
		if (shouting & worldIn.isRemote) {
			shouting = false;
			int rand = getRandom(1, 3);
			worldIn.playSound(playerIn, playerIn.getPosition(),
					ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambience:horn" + rand)),
					SoundCategory.NEUTRAL, 10, 1);
		}

		// Calcs the knockback
		if (worldIn.isRemote) {
			return;
		}
		int radius = 8;
		List<Entity> entities = worldIn.getEntitiesWithinAABB(Entity.class,
				new AxisAlignedBB(playerIn.getPosition().getX() - 20, playerIn.getPosition().getY() - 10,
						playerIn.getPosition().getZ() - 20, playerIn.getPosition().getX() + 20,
						playerIn.getPosition().getY() + 10, playerIn.getPosition().getZ() + 20));

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
				Vec3d angleOfAttack = entity.getPositionVector().addVector(-(pos.getX() + 0.5D), -(pos.getY() - 0.8D),
						-(pos.getZ() + 0.5D));

				// we use the resultant vector to determine the force to apply.
				double xForce = angleOfAttack.x * knockbackMultiplier * reductionCoefficient;
				double yForce = angleOfAttack.y * knockbackMultiplier * reductionCoefficient;
				double zForce = angleOfAttack.z * knockbackMultiplier * reductionCoefficient;
				entity.addVelocity(xForce, yForce, zForce);

				// Encourages players nearby
				if (entity instanceof EntityPlayer) {
					playerIn.addPotionEffect(new PotionEffect(MobEffects.SPEED, 60 * 20, 0));
					playerIn.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 60 * 20, 3));
				}
				if (entity instanceof EntityMob) {
					// Add fear to the heart of the enemies
					((EntityMob) entity).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 5 * 20, 1));
					((EntityMob) entity).tasks.addTask(3,new EntityAIAvoidEntity((EntityMob) entity, EntityPlayer.class, 16.0F, 3.5D, 2.2D));
				}
			}

		}
	}

	public static int getRandom(int min, int max) {
		int x = (int) ((Math.random() * ((max - min) + 1)) + min);
		return x;
	}

	@Override
	public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
		super.addInformation(itemstack, world, list, flag);

		list.add(I18n.format("Horn.Desc"));
	}
}
