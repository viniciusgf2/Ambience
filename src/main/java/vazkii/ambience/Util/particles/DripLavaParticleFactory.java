package vazkii.ambience.Util.particles;

import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SplashParticle;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@OnlyIn(value = Dist.CLIENT)
public class DripLavaParticleFactory extends DripParticle.DrippingLavaFactory {
	public DripLavaParticleFactory(IAnimatedSprite p_i50679_1_) {
		super(p_i50679_1_);
	}

	public static int dripsLavaCount = 0;

	public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {

		if (xSpeed == 0 && ySpeed == 0 && zSpeed == 0) {

			float vol = MathHelper.clamp(1f, 0f, 1f);
			worldIn.playSound(x, y, z, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.AMBIENT, vol, 1f, false);
	
			System.out.println("lava drop");
		}
		// make the particle
		return super.makeParticle(typeIn, worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
	}

	public void wrap(DripParticle.DrippingLavaFactory real) {
		
		//this.spriteSet=ObfuscationReflectionHelper.getPrivateValue(DripParticle.LandingLavaFactory.class, real, "spriteSet");
		// do: this.spriteSet = real.spriteSet;
		IAnimatedSprite spr = ObfuscationReflectionHelper.getPrivateValue(DripParticle.DrippingLavaFactory.class, real, "field_217547_a");
	    ObfuscationReflectionHelper.setPrivateValue(DripParticle.DrippingLavaFactory.class, this, spr, "field_217547_a");

	}
}