package vazkii.ambience.Util;

import net.minecraft.util.math.vector.Vector3d;

public class Border {

	public Vector3d p1;
	public Vector3d p2;

	public Border(Vector3d posA, Vector3d p2) {
		if (posA != null & p2 != null) {
			float x1 = (float) Math.min(posA.x, p2.x);
			float y1 = (float) Math.min(posA.y, p2.y);
			float z1 = (float) Math.min(posA.z, p2.z);
			float x2 = (float) Math.max(posA.x, p2.x);
			float y2 = (float) Math.max(posA.y, p2.y);
			float z2 = (float) Math.max(posA.z, p2.z);
			this.p1 = new Vector3d(x1, y1, z1);
			this.p2 = new Vector3d(x2+1, y2, z2+1);			
		
		}
	}

	public boolean contains(Vector3d vec3d) {
		if (vec3d == null | p1==null | p2==null) {
			return false;
		}
		return vec3d.x >= p1.getX() && vec3d.x <= p2.getX() 
			&& vec3d.y >= p1.getY() && vec3d.y <= p2.getY()
			&& vec3d.z >= p1.getZ() && vec3d.z <= p2.getZ();
	}
	
	public int getCubicArea() {
		double lenght=Math.abs(p1.x - p2.x)+1;
		double width=Math.abs(p1.z - p2.z)+1;
		double height=Math.abs(p1.y - p2.y-1);
				
		return (int) (lenght*width*height);
	}
}
