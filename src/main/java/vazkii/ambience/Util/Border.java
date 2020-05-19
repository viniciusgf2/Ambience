package vazkii.ambience.Util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Border {

	public BlockPos p1;
	public BlockPos p2;

	public Border(BlockPos p1, BlockPos p2) {
		if (p1 != null & p2 != null) {
			double x1 = Math.min(p1.getX(), p2.getX());
			double y1 = Math.min(p1.getY(), p2.getY());
			double z1 = Math.min(p1.getZ(), p2.getZ());
			double x2 = Math.max(p1.getX(), p2.getX());
			double y2 = Math.max(p1.getY(), p2.getY());
			double z2 = Math.max(p1.getZ(), p2.getZ());
			this.p1 = new BlockPos(x1, y1, z1);
			this.p2 = new BlockPos(x2, y2, z2);
		}
	}

	public boolean contains(BlockPos loc) {
		if (loc == null) {
			return false;
		}
		return loc.getX() >= p1.getX() && loc.getX() <= p2.getX() && loc.getY() >= p1.getY() && loc.getY() <= p2.getY()
				&& loc.getZ() >= p1.getZ() && loc.getZ() <= p2.getZ();
	}
	
	public int getCubicArea() {
		int lenght=Math.abs(p1.getX() - p2.getX())+1;
		int width=Math.abs(p1.getZ() - p2.getZ())+1;
		int height=Math.abs(p1.getY() - p2.getY()-1);
				
		return lenght*width*height;
	}
}
