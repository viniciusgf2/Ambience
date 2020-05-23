package vazkii.ambience.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import vazkii.ambience.Ambience;
import vazkii.ambience.Reference;
import vazkii.ambience.World.Biomes.Area;

public class WorldData extends WorldSavedData {
	final static String key = Reference.MOD_ID;
	public MapStorage storage;
	public List<Area> listAreas = new ArrayList<Area>();
	private NBTTagCompound data = new NBTTagCompound();
	
	public WorldData(String name) {
		super(name);
	}

	public WorldData() {
		super(key);
		// TODO Auto-generated constructor stub
	}

	public List<Area> getAreas() {
		return listAreas;
	}

	public void addArea(Area parArea) {
		listAreas.add(parArea);
		markDirty();
	}

	public void removeArea(Area parArea) {
		
		//listAreas.remove(parArea);
		
		for(Area area : listAreas){
			if(area.getID()== parArea.getID()) 
			{
				listAreas.remove(area);
				break;
			}
		}				
		
		markDirty();
	}
	
	public void editArea(Area parArea) {
		
		//listAreas.remove(parArea);
		int index=0;
		for(Area area : listAreas){
			
			if(area.getID()== parArea.getID()) 
			{
				listAreas.set(index, parArea);
				break;
			}
			index++;
		}				
		
		markDirty();
	}


	/**
	 * Clear the protected areas list.
	 */
	public void clearAreas() {
		listAreas.clear();
		markDirty();
	}

	/**
	 * Gets the area by name.
	 *
	 * @param parName the par name
	 * @return the area by name
	 */
	@Nullable
	public Area getAreaByName(String parName) {
		if (listAreas.size() > 0) {

			Iterator<Area> iterator = listAreas.iterator();
			while (iterator.hasNext()) {
				Area area = iterator.next();
				if (area.getName().equals(parName)) {
					return area;
				}
			}
		}

		return null;
	}
	
	public WorldData GetArasforWorld(World world) {		
		storage = world.getMapStorage();// .getPerWorldStorage(); //world.getMapStorage();//
		WorldData result=new WorldData();
		result = (WorldData) storage.getOrLoadData(WorldData.class, key);
				
		if (result == null) {
			result = new WorldData(key);
			storage.setData(key, result);
		}
		 
		listAreas = result.listAreas;	
		
		return result;
	}

	

	public void saveData() {
		// NBTTagCompound tag=data;
		NBTTagList tagList = new NBTTagList();

		// cycle through the list of areas
		Iterator<Area> iteratorArea = listAreas.iterator();
		while (iteratorArea.hasNext()) {
			NBTTagCompound tagCompound = new NBTTagCompound();
			Area area = iteratorArea.next();
			tagCompound.setTag("Pos", area.getPosListTag());
			tagCompound.setString("Name", area.getName());
			tagCompound.setInteger("D",area.getDimension());
			tagCompound.setInteger("ID",area.getID());
			tagCompound.setBoolean("playNight", area.isPlayatNight());
			tagCompound.setBoolean("instP", area.isInstantPlay());
			tagList.appendTag(tagCompound);
		}

		data.setTag("Areas", tagList);
		
		//System.out.println(tagList.toString());

		this.markDirty();
	}

	// Serializa a lista de Areas
	public static NBTTagCompound  SerializeThis(List<Area> data) {
		
		NBTTagCompound nbt=new NBTTagCompound();
		int count=0;
		
		Iterator<Area> iteratorArea = data.iterator();
		while (iteratorArea.hasNext()) {
			NBTTagCompound tagCompound = new NBTTagCompound();
			Area area = iteratorArea.next();
			tagCompound.setTag("Pos", area.getPosListTag());
			tagCompound.setString("Name", area.getName());
			tagCompound.setInteger("D",area.getDimension());
			tagCompound.setInteger("ID",area.getID());
			tagCompound.setBoolean("playNight",area.isPlayatNight());
			tagCompound.setBoolean("instP",area.isInstantPlay());
			nbt.setTag("Area"+count++, tagCompound);
		}
		
		nbt.setInteger("lenght", count);
		
		return nbt;
	}
	

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		data = compound.getCompoundTag(key);

		listAreas.clear();

		NBTTagList tagListAreas = data.getTagList("Areas", 10); // 10 indicates a list of NBTTagCompound
		Iterator<NBTBase> iterator = tagListAreas.iterator();
		while (iterator.hasNext()) {
			NBTTagCompound areaCompound = (NBTTagCompound) iterator.next();
			Area area = new Area(areaCompound.getString("Name"));
			area.setDimension(areaCompound.getInteger("D"));
			area.setID(areaCompound.getInteger("ID"));
			area.setPlayAtNight(areaCompound.getBoolean("playNight"));
			area.setInstantPlay(areaCompound.getBoolean("instP"));
			
			listAreas.add(area);
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

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag(key, data);
		return compound;
	}
}
