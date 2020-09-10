package vazkii.ambience.Util;

public class Utils {

	public static int colorToInt(int IAlpha,int IRed, int IGreen, int IBlue) {
		
		int alpha = IAlpha & 0xFF;
		int red = IRed & 0xFF;
		int green = IGreen & 0xFF;
		int blue = IBlue & 0xFF;

		return  (alpha << 24) + (red << 16) + (green << 8) + (blue);		
	}
	
	public static float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}
}
