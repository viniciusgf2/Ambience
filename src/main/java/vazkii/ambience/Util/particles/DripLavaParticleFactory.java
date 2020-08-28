package vazkii.ambience.Util.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDrip;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DripLavaParticleFactory extends ParticleDrip.LavaFactory {
   
	public static int dripsCount=0;
	
    @Override
	public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
    	// the splash particle is used in several ways, so there's some checks to tell if this is a splash from a drip.
        // the splash when moving in water has speed, while drips and fishing splashes don't
         	
    	if(xSpeedIn == 0 && ySpeedIn == 0 && ySpeedIn == 0) {            	
           
        	// play the sound
            float vol = MathHelper.clamp(0.1f, 0f, 1f);
                    
            worldIn.playSound(xCoordIn, yCoordIn, zCoordIn, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.AMBIENT, vol, 1f, false);                        
        }
	
		return super.createParticle(particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn,p_178902_15_);  
	}   
}