package vazkii.ambience.Util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class Utils {
	public static int colorToInt(float IAlpha, int IRed, int IGreen, int IBlue) {

		int alpha = (int) IAlpha & 0xFF;
		int red = IRed & 0xFF;
		int green = IGreen & 0xFF;
		int blue = IBlue & 0xFF;

		return (alpha << 24) + (red << 16) + (green << 8) + (blue);
	}

	public static float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}

	public static NBTTagCompound BlockPosToNBT(BlockPos pos) {

		NBTTagCompound posCompound = new NBTTagCompound();
		if (pos != null) {
			posCompound.setDouble("x", pos.getX());
			posCompound.setDouble("y", pos.getY());
			posCompound.setDouble("z", pos.getZ());
		}

		return posCompound;
	}

	public static BlockPos NBTtoBlockPos(NBTTagCompound compound) {
		return new BlockPos(compound.getDouble("x"), compound.getDouble("y"), compound.getDouble("z"));
	}
	
	public static int getRandom(int min, int max) {
		int x = (int) ((Math.random() * ((max - min) + 1)) + min);
		return x;
	}
}
