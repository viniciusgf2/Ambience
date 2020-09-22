package vazkii.ambience.World.Biomes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
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
	private int Dimension, ID, redstoneStrength;
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
	
	public int getRedstoneStrength() {
		return redstoneStrength;
	}

	public void setRedstoneStrength(int Strength) {
		redstoneStrength = Strength;
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

	public NBTTagList getPosListTag() {
		NBTTagList tagList = new NBTTagList();

		NBTTagCompound posCompound = new NBTTagCompound();
		posCompound.setDouble("x1", pos1.x);
		posCompound.setDouble("y1", pos1.y);
		posCompound.setDouble("z1", pos1.z);
		posCompound.setDouble("x2", pos2.x);
		posCompound.setDouble("y2", pos2.y);
		posCompound.setDouble("z2", pos2.z);
		tagList.appendTag(posCompound);

		return tagList;
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
	public static Area getPlayerStandingArea(EntityPlayer player) {	
		List<Area> Areas = new ArrayList<Area>();
		
		//Obtém todas as areas que o player esta dentro
		if (Ambience.getWorldData().listAreas != null) {

			for (Area area : Ambience.getWorldData().listAreas) {
				if (area.getDimension() == player.dimension) {
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
				if (area.getDimension() == player.dimension) {
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
	
	// Returns the area that player is standing
	public static Area getBlockStandingArea(BlockPos pos) 
	{	
		List<Area> Areas = new ArrayList<Area>();
		
		//Obtém todas as areas que o player esta dentro
		if (Ambience.getWorldData().listAreas != null) {

			for (Area area : Ambience.getWorldData().listAreas) {
				
					Border border = new Border(area.getPos1(), area.getPos2());
					if (border.p1 != null & border.p2 != null)
						if (border.contains(new Vec3d(pos.getX(),pos.getY(),pos.getZ()))) {								
							Areas.add(area);
						}					
			}
		}
		
		//Obtém a menor area	
		if(Areas.size()>0) {
			Area minArea=Collections.min(Areas,new Area());
			
			return minArea;
		}

		return null;
	}
	
	public static Area getAreabyID(int id) {
		for (Area area : Ambience.getWorldData().listAreas)
		{	
			if(area.ID==id) 
			{
				return area;
			}
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

	public NBTTagCompound SerializeThis() {

		// if you are creating a new registry it automaticaly creates a new id
		if (getOperation() == Operation.CREATE)
			setID(generateNewID());

		NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setTag("Pos", getPosListTag()); // Coordinates init and final
		tagCompound.setString("Name", getName()); // Area Name
		tagCompound.setInteger("D", getDimension()); // Dimension
		tagCompound.setInteger("ID", getID()); // Unique ID
		tagCompound.setString("op", simplifyOperation()); // Operation
		tagCompound.setBoolean("playNight", isPlayatNight()); // If should play at night over the night music or not
		tagCompound.setBoolean("instP", isInstantPlay()); // If should play instantly on this region or not
		tagCompound.setInteger("RedstoneStrength", getRedstoneStrength()); // RedstoneStrength
		
		return tagCompound;
	}

	// Retorna uma área
	public static Area DeSerialize(NBTTagCompound nbt) {
		Area area = new Area(nbt.getString("Name"));
		area.setDimension(nbt.getInteger("D"));
		area.setID(nbt.getInteger("ID"));
		area.setPlayAtNight(nbt.getBoolean("playNight"));
		area.setInstantPlay(nbt.getBoolean("instP"));
		area.setRedstoneStrength(nbt.getInteger("RedstoneStrength"));

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

		NBTTagList tagListPos = nbt.getTagList("Pos", 10);

		Iterator<NBTBase> iterator2 = tagListPos.iterator();
		while (iterator2.hasNext()) {
			NBTTagCompound posCompound = (NBTTagCompound) iterator2.next();
			Vec3d pos1 = new Vec3d(posCompound.getDouble("x1"), posCompound.getDouble("y1"),
					posCompound.getDouble("z1"));
			area.setPos1(pos1);

			Vec3d pos2 = new Vec3d(posCompound.getDouble("x2"), posCompound.getDouble("y2"),
					posCompound.getDouble("z2"));
			area.setPos2(pos2);
		}

		return area;
	}

	// Retorna uma lista de areas
	public static List<Area> DeSerializeList(NBTTagCompound nbt) {

		List<Area> localListAreas = new ArrayList<Area>();

		for (int i = 0; i < nbt.getInteger("lenght"); i++) {
			NBTTagCompound areaCompound = (NBTTagCompound) nbt.getTag("Area" + i);

			Area area = new Area(areaCompound.getString("Name"));
			area.setDimension(areaCompound.getInteger("D"));
			area.setID(areaCompound.getInteger("ID"));
			area.setPlayAtNight(areaCompound.getBoolean("playNight"));
			area.setInstantPlay(areaCompound.getBoolean("instP"));
			area.setRedstoneStrength(areaCompound.getInteger("RedstoneStrength"));
			localListAreas.add(area);

			NBTTagList tagListPos = areaCompound.getTagList("Pos", 10);
			Iterator<NBTBase> iterator2 = tagListPos.iterator();
			while (iterator2.hasNext()) {
				NBTTagCompound posCompound = (NBTTagCompound) iterator2.next();
				Vec3d pos1 = new Vec3d(posCompound.getDouble("x1"), posCompound.getDouble("y1"),
						posCompound.getDouble("z1"));
				area.setPos1(pos1);

				Vec3d pos2 = new Vec3d(posCompound.getDouble("x2"), posCompound.getDouble("y2"),
						posCompound.getDouble("z2"));
				area.setPos2(pos2);
			}
		}

		return localListAreas;
	}
}