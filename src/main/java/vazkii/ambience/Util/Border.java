package vazkii.ambience.Util;

import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.math.Vec3d;

public class Border {

	public Vector3f p1;
	public Vector3f p2;

	public Border(Vec3d posA, Vec3d p2) {
		if (posA != null & p2 != null) {
			float x1 = (float) Math.min(posA.x, p2.x);
			float y1 = (float) Math.min(posA.y, p2.y);
			float z1 = (float) Math.min(posA.z, p2.z);
			float x2 = (float) Math.max(posA.x, p2.x);
			float y2 = (float) Math.max(posA.y, p2.y);
			float z2 = (float) Math.max(posA.z, p2.z);
			this.p1 = new Vector3f(x1, y1, z1);
			this.p2 = new Vector3f(x2+1, y2, z2+1);			
		
		}
	}

	public boolean contains(Vec3d vec3d) {
		if (vec3d == null) {
			return false;
		}
		return vec3d.x >= p1.getX() && vec3d.x <= p2.getX() 
			&& vec3d.y >= p1.getY() && vec3d.y <= p2.getY()
			&& vec3d.z >= p1.getZ() && vec3d.z <= p2.getZ();
	}
	
	public int getCubicArea() {
		float lenght=Math.abs(p1.getX() - p2.getX())+1;
		float width=Math.abs(p1.getZ() - p2.getZ())+1;
		float height=Math.abs(p1.getY() - p2.getY()-1);
				
		return (int) (lenght*width*height);
	}
}
