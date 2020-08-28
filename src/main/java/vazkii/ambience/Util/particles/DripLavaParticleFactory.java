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

    public static int dripsLavaCount=0;
    public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        // the splash particle is used in several ways, so there's some checks to tell if this is a splash from a drip.
    	      	     	
            // the splash when moving in water has speed, while drips and fishing splashes don't
            if(xSpeed == 0 && ySpeed == 0 && zSpeed == 0) {            	
                // check that the block below isn't fluid since fishing splashes have water below
               
            	/*Vec3d vector=new Vec3d(x,y,z);
            	BlockRayTraceResult rayTraceResult = worldIn.rayTraceBlocks(new RayTraceContext(vector, vector.add(new Vec3d(0,1,0).scale(-25)),RayTraceContext.BlockMode.OUTLINE,  RayTraceContext.FluidMode.ANY,worldIn.getClosestPlayer(x, y, z)));
            	
            	// check that the block below isn't fluid since fishing splashes have water below
                if (worldIn.getBlockState(rayTraceResult.getPos()).getBlock().getRegistryName().toString().contains("lava")) {
               
                	if(dripsLavaCount<=10)
                		dripsLavaCount++;
                }*/
                	// play the sound
                float vol = MathHelper.clamp(0.1f, 0f, 1f);
                worldIn.playSound(x, y, z, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.AMBIENT, vol, 1f, false);
                
            }
        
        // make the particle
        return super.makeParticle(typeIn, worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    public void wrap( DripParticle.DrippingLavaFactory real){
        // do: this.spriteSet = real.spriteSet;
        IAnimatedSprite spr = ObfuscationReflectionHelper.getPrivateValue(DripParticle.DrippingLavaFactory.class, real, "field_217547_a");
        ObfuscationReflectionHelper.setPrivateValue(DripParticle.DrippingLavaFactory.class, this, spr, "field_217547_a");

    }
}