package vazkii.ambience.Util.particles;

import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.Particle;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class DripWaterParticleFactory extends DripParticle.DrippingWaterFactory {
    public DripWaterParticleFactory(IAnimatedSprite p_i50679_1_) {
		super(p_i50679_1_);
		// TODO Auto-generated constructor stub
	}
	
    public static int dripsCount=0;
    public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        // the splash particle is used in several ways, so there's some checks to tell if this is a splash from a drip.
    	      	     	
            // the splash when moving in water has speed, while drips and fishing splashes don't
            if(xSpeed == 0 && ySpeed == 0 && zSpeed == 0) {            	
              
            	Vec3d vector=new Vec3d(x,y,z);
            	BlockRayTraceResult rayTraceResult = worldIn.rayTraceBlocks(new RayTraceContext(vector, vector.add(new Vec3d(0,1,0).scale(-25)),RayTraceContext.BlockMode.OUTLINE,  RayTraceContext.FluidMode.ANY,worldIn.getClosestPlayer(x, y, z)));
            	
            	// check that the block below isn't fluid since fishing splashes have water below
                if (worldIn.getBlockState(rayTraceResult.getPos()).getBlock().getRegistryName().toString().contains("water")) {
                	
                	if(dripsCount<=10)
                		dripsCount++;
                	// play the sound
                    float vol = MathHelper.clamp(1, 0f, 1f);
                    worldIn.playSound(x, y, z, SoundEvents.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, SoundCategory.AMBIENT, vol, 1f, false);
                }
            }
        
        // make the particle
        return super.makeParticle(typeIn, worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    public void wrap(DripParticle.DrippingWaterFactory real){
        // do: this.spriteSet = real.spriteSet;
        IAnimatedSprite spr = ObfuscationReflectionHelper.getPrivateValue(DripParticle.DrippingWaterFactory.class, real, "field_217547_a");
        ObfuscationReflectionHelper.setPrivateValue(DripParticle.DrippingWaterFactory.class, this, spr, "field_217547_a");

    }
}
