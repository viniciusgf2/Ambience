package vazkii.ambience.Util.particles;

import net.minecraft.block.material.Material;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDrip;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DripWaterParticleFactory extends ParticleDrip.WaterFactory {

	public static int dripsCount = 0;

	@Override
	public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) 
	{
		// the splash particle is used in several ways, so there's some checks to tell
		// if this is a splash from a drip.
		// the splash when moving in water has speed, while drips and fishing splashes don't
		if (!worldIn.isRaining()) {
			if (xSpeedIn == 0 && ySpeedIn == 0 && ySpeedIn == 0) {

				Vec3d vector = new Vec3d(xCoordIn, yCoordIn, zCoordIn);

				RayTraceResult rayTraceResult = worldIn.rayTraceBlocks(vector,
						vector.add(new Vec3d(0, 1, 0).scale(-25)), true);

				// check that the block below isn't fluid since fishing splashes have water
				// below
				if (worldIn.getBlockState(rayTraceResult.getBlockPos()).getBlock().getRegistryName().toString()
						.contains("water")) {

					if (dripsCount <= 10)
						dripsCount++;
				}
						
				// play the sound
				float vol = MathHelper.clamp(0.1f, 0f, 1f);

				int rand = getRandom(1, 4);
				worldIn.playSound(xCoordIn, yCoordIn, zCoordIn,
						(SoundEvent) SoundEvent.REGISTRY.getObject(new ResourceLocation("ambience:wdrop" + rand)),SoundCategory.AMBIENT, vol, 1F, false);
			}

			return super.createParticle(particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn,p_178902_15_);
		} else {
			return null;
		}
	}
	//field_151586_h

	public static int getRandom(int min, int max) {
		int x = (int) ((Math.random() * ((max - min) + 1)) + min);
		return x;
	}
	
	public void wrap(ParticleDrip.WaterFactory real) {
		// do: this.spriteSet = real.spriteSet;
		String spr = ObfuscationReflectionHelper.getPrivateValue(ParticleDrip.WaterFactory.class, real,"spriteSet");
		ObfuscationReflectionHelper.setPrivateValue(ParticleDrip.WaterFactory.class, this, spr,"spriteSet");

	}
}