package vazkii.ambience.Util;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SplashParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@OnlyIn(value = Dist.CLIENT)
public class SplashFactory2 extends SplashParticle.Factory {
    public SplashFactory2(IAnimatedSprite p_i50679_1_) {
        super(p_i50679_1_);
    }

    public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) 
    {      	     
    	if(!worldIn.isRaining()) 
		{
            if(xSpeed == 0 && ySpeed == 0 && zSpeed == 0) {            	
                if (worldIn.getBlockState(new BlockPos(x, y -1, z)).getFluidState().isEmpty()) {
	                // play the sound
	                float vol = MathHelper.clamp(1, 0f, 1f);
	                worldIn.playSound(x, y, z, SoundEvents.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, SoundCategory.AMBIENT, vol, 1f, false);                	
                }   
            }        
		}
    	
	    return super.makeParticle(typeIn, (ClientWorld) worldIn, x, y, z, xSpeed, ySpeed, zSpeed);    	
    }

    public void wrap(SplashParticle.Factory real){
        // do: this.spriteSet = real.spriteSet;    	
        IAnimatedSprite spr = ObfuscationReflectionHelper.getPrivateValue(SplashParticle.Factory.class, real, "field_217547_a");
        ObfuscationReflectionHelper.setPrivateValue(SplashParticle.Factory.class, this, spr, "field_217547_a");

    }
}