package vazkii.ambience.Util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public class Utils {

	public static int colorToInt(float IAlpha,int IRed, int IGreen, int IBlue) {
		
		int alpha = (int)IAlpha & 0xFF;
		int red = IRed & 0xFF;
		int green = IGreen & 0xFF;
		int blue = IBlue & 0xFF;

		return  (alpha << 24) + (red << 16) + (green << 8) + (blue);		
	}
	
	public static float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}
	
	public static CompoundNBT BlockPosToNBT(BlockPos pos) {

		CompoundNBT posCompound = new CompoundNBT();
		if(pos!=null) {
			posCompound.putDouble("x", pos.getX());
			posCompound.putDouble("y", pos.getY());
			posCompound.putDouble("z", pos.getZ());
		}		
		
		return posCompound;
	}
	
	public static BlockPos NBTtoBlockPos(CompoundNBT compound) {
		return new BlockPos(compound.getDouble("x"), compound.getDouble("y"),compound.getDouble("z"));
	}
}
