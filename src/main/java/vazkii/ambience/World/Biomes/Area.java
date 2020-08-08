package vazkii.ambience.World.Biomes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.math.Vec3d;
import vazkii.ambience.Ambience;
import vazkii.ambience.Util.Border;

public class Area implements Comparator<Area>{

	// c: creation
	// e: edit
	// d: delete
	public enum Operation {
		CREATE, EDIT, DELETE;
	}

	private Operation operation;
	private int Dimension, ID;
	private String name;
	private boolean instantPlay = false;
	private boolean playAtNight = false;
	private int cubicArea =0;
	
	public boolean isPlayatNight() {
		return playAtNight;
	}
	
	public boolean isInstantPlay() {
		return instantPlay;
	}
	
	public void setPlayAtNight(boolean playAtNight) {
		this.playAtNight = playAtNight;
	}

	public void setInstantPlay(boolean instantPlay) {
		this.instantPlay = instantPlay;
	}

	private Vec3d pos1, pos2;
	
	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public int getDimension() {
		return Dimension;
	}

	public void setDimension(int dimension) {
		Dimension = dimension;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public Area() {

	}

	public Area(String parName) {
		name = parName;
	}

	public String getName() {
		return name;
	}

	public Vec3d getPos1() {
		return pos1;
	}

	public void setPos1(Vec3d pos1) {
		this.pos1 = pos1;
	}

	public Vec3d getPos2() {
		return pos2;
	}

	public void setPos2(Vec3d pos2) {
		this.pos2 = pos2;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void resetSelection() {
		pos1 = null;
		pos2 = null;
	}

	public String simplifyOperation() {
		switch (getOperation()) {
		case CREATE:
			return "c";

		case EDIT:
			return "e";

		case DELETE:
			return "d";

		default:
			return "c";
		}
	}
	
	public CompoundNBT getPosListTag() {

		CompoundNBT posCompound = new CompoundNBT();
		posCompound.putDouble("x1", pos1.x);
		posCompound.putDouble("y1", pos1.y);
		posCompound.putDouble("z1", pos1.z);
		posCompound.putDouble("x2", pos2.x);
		posCompound.putDouble("y2", pos2.y);
		posCompound.putDouble("z2", pos2.z);

		return posCompound;
	}

	// Cycle through the areas to find an unused ID
	private int generateNewID() {

		int newid = 0;

		// if (Ambience.getWorldData().listAreas != null) {
		List<String> idsList = new ArrayList<String>();
		Iterator<Area> iterator = Ambience.getWorldData().listAreas.iterator();
		while (iterator.hasNext()) {
			idsList.add(Integer.toString(iterator.next().ID));
		}

		for (int i = 0; i <= idsList.size(); i++) {
			if (!idsList.contains(Integer.toString(i))) {
				newid = i;
				break;
			}
		}
		// }

		return newid;
	}
	
	// Returns the area that player is standing
	public static Area getPlayerStandingArea(PlayerEntity player) {	
		List<Area> Areas = new ArrayList<Area>();
		
		//Obtém todas as areas que o player esta dentro
		if (Ambience.getWorldData().listAreas != null) {

			for (Area area : Ambience.getWorldData().listAreas) {
				if (area.getDimension() == player.dimension.getId()) {
					Border border = new Border(area.getPos1(), area.getPos2());
					if (border.p1 != null & border.p2 != null)
						if (border.contains(player.getPositionVector())) {
							
							//player.sendStatusMessage(new TextComponentString(""+border.getCubicArea()), true);
							area.getCubicArea();
							Areas.add(area);
						}
				}
			}
		}

		if(Areas.size()>0) {
		//Adiciona a area selecionada na lista de areas
			if(Ambience.selectedArea!=null) {				
				Area area= Ambience.selectedArea;
				if (area.getDimension() == player.dimension.getId()) {
					Border border = new Border(area.getPos1(), area.getPos2());
					if (border.p1 != null & border.p2 != null)
						if (border.contains(player.getPositionVector())) {
							String test=player.getPositionVector().toString();
							//player.sendStatusMessage(new TextComponentString(""+border.getCubicArea()), true);
							area.getCubicArea();
							Areas.add(area);
						}
				}			
			}
		}

		Ambience.multiArea=Areas.size();
		
		//Obtém a menor area	
		if(Areas.size()>0) {
			Area minArea=Collections.min(Areas,new Area());
			
			return minArea;
		}

		return null;
	}
	
	@Override
	public int compare(Area a, Area b) {
		 if (a.cubicArea < b.cubicArea)
	            return -1; // lowest value first
	        if (a.cubicArea == b.cubicArea)
	            return 0;
	        return 1;
	}
	
	public void getCubicArea() {
		double lenght=Math.abs(pos1.x - pos2.x)+1;
		double width=Math.abs(pos1.z - pos2.z)+1;
		double height=Math.abs(pos1.y - pos2.y-1);
			
		cubicArea= (int) (lenght*width*height);
	}
	
	public CompoundNBT SerializeThis() {

		// if you are creating a new registry it automaticaly creates a new id
		if (getOperation() == Operation.CREATE)
			setID(generateNewID());

		CompoundNBT tagCompound = new CompoundNBT();
		tagCompound.put("Pos", getPosListTag()); // Coordinates init and final
		tagCompound.putString("Name", getName()); // Area Name
		tagCompound.putInt("D", getDimension()); // Dimension
		tagCompound.putInt("ID", getID()); // Unique ID
		tagCompound.putString("op", simplifyOperation()); // Operation
		tagCompound.putBoolean("playNight", isPlayatNight()); // If should play at night over the night music or not
		tagCompound.putBoolean("instP", isInstantPlay()); // If should play instantly on this region or not

		return tagCompound;
	}
	
	// Retorna uma área
	public static Area DeSerialize(CompoundNBT nbt) {
		Area area = new Area(nbt.getString("Name"));
		area.setDimension(nbt.getInt("D"));
		area.setID(nbt.getInt("ID"));
		area.setPlayAtNight(nbt.getBoolean("playNight"));
		area.setInstantPlay(nbt.getBoolean("instP"));

		switch (nbt.getString("op")) {
		case "c":
			area.setOperation(Operation.CREATE);
			break;
		case "e":
			area.setOperation(Operation.EDIT);
			break;
		case "d":
			area.setOperation(Operation.DELETE);
			break;
		default:
			area.setOperation(Operation.CREATE);
			break;
		}

		CompoundNBT tagListPos = nbt.getCompound("Pos");		
		Vec3d pos1 = new Vec3d(tagListPos.getDouble("x1"), tagListPos.getDouble("y1"),tagListPos.getDouble("z1"));area.setPos1(pos1);
		Vec3d pos2 = new Vec3d(tagListPos.getDouble("x2"), tagListPos.getDouble("y2"),tagListPos.getDouble("z2"));
		area.setPos1(pos1);
		area.setPos2(pos2);
		
		/*CompoundNBT tagListAreas = nbt.getCompound("Pos"); // 10 indicates a list of NBTTagCompound
		Iterator<INBT> iterator = tagListAreas.getList("Areas", 10).iterator();
		while (iterator.hasNext()) {	
			CompoundNBT tagListPos = nbt.getCompound("Pos");		
			Vec3d pos1 = new Vec3d(tagListPos.getDouble("x1"), tagListPos.getDouble("y1"),tagListPos.getDouble("z1"));area.setPos1(pos1);
			Vec3d pos2 = new Vec3d(tagListPos.getDouble("x2"), tagListPos.getDouble("y2"),tagListPos.getDouble("z2"));
			area.setPos1(pos1);
			area.setPos2(pos2);
		}*/
				
		return area;
	}
	
	// Retorna uma lista de areas
	public static List<Area> DeSerializeList(CompoundNBT nbt) {

		List<Area> localListAreas = new ArrayList<Area>();

		for (int i = 0; i < nbt.getInt("lenght"); i++) {
			CompoundNBT areaCompound = (CompoundNBT) nbt.get("Area" + i);

			Area area = new Area(areaCompound.getString("Name"));
			area.setDimension(areaCompound.getInt("D"));
			area.setID(areaCompound.getInt("ID"));
			area.setPlayAtNight(areaCompound.getBoolean("playNight"));
			area.setInstantPlay(areaCompound.getBoolean("instP"));
						
			CompoundNBT tagListPos = areaCompound.getCompound("Pos");		
			Vec3d pos1 = new Vec3d(tagListPos.getDouble("x1"), tagListPos.getDouble("y1"),tagListPos.getDouble("z1"));area.setPos1(pos1);
			Vec3d pos2 = new Vec3d(tagListPos.getDouble("x2"), tagListPos.getDouble("y2"),tagListPos.getDouble("z2"));
			area.setPos1(pos1);
			area.setPos2(pos2);
			
			localListAreas.add(area);
		}

		return localListAreas;
	}
	
}