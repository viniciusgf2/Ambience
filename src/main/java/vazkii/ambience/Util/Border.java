package vazkii.ambience.Util;

import net.minecraft.util.math.Vec3d;

public class Border {

	public Vec3d p1;
	public Vec3d p2;

	public Border(Vec3d posA, Vec3d p2) {
		if (posA != null & p2 != null) {
			double x1 = Math.min(posA.x, p2.x);
			double y1 = Math.min(posA.y, p2.y);
			double z1 = Math.min(posA.z, p2.z);
			double x2 = Math.max(posA.x, p2.x);
			double y2 = Math.max(posA.y, p2.y);
			double z2 = Math.max(posA.z, p2.z);
			this.p1 = new Vec3d(x1, y1, z1);
			this.p2 = new Vec3d(x2+1, y2, z2+1);
		}
	}

	public boolean contains(Vec3d vec3d) {
		if (vec3d == null) {
			return false;
		}
		return vec3d.x >= p1.x && vec3d.x <= p2.x 
			&& vec3d.y >= p1.y && vec3d.y <= p2.y
			&& vec3d.z >= p1.z && vec3d.z <= p2.z;
	}
	
	public int getCubicArea() {
		double lenght=Math.abs(p1.x - p2.x)+1;
		double width=Math.abs(p1.z - p2.z)+1;
		double height=Math.abs(p1.y - p2.y-1);
				
		return (int) (lenght*width*height);
	}
}
