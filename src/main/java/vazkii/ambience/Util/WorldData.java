package vazkii.ambience.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.raid.RaidManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import vazkii.ambience.Ambience;
import vazkii.ambience.World.Biomes.Area;

public class WorldData extends WorldSavedData implements Supplier{
	final static String key = Ambience.MODID;
	public DimensionSavedDataManager storage;
	public List<Area> listAreas = new ArrayList<Area>();
	private CompoundNBT data = new CompoundNBT();
	
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
	
	public WorldData GetArasforWorld(ServerWorld world)
	{
		
		DimensionSavedDataManager storage = world.getSavedData();
		WorldData data = world.getSavedData().getOrCreate(() -> {
	         return new WorldData();
	      }, Ambience.MODID);
		
		storage.set(data);

		listAreas = data.listAreas;	
		
		return data;
		
	/*	DimensionSavedDataManager storage = world.getSavedData();
		Supplier<WorldData> sup = new WorldData();
		WorldData saver = (WorldData) storage.getOrCreate((Supplier<WorldData>) storage, Ambience.MODID);

		if (saver == null) 
		{
			saver = new WorldData();
			storage.set(saver);
		}
		
		listAreas = saver.listAreas;	
		
		return saver;*/
	}	

	public void saveData() {
		// NBTTagCompound tag=data;
		ListNBT tagList = new ListNBT();
		

		// cycle through the list of areas
		Iterator<Area> iteratorArea = listAreas.iterator();
		while (iteratorArea.hasNext()) {
			CompoundNBT tagCompound = new CompoundNBT();
			Area area = iteratorArea.next();
			tagCompound.put("Pos", area.getPosListTag());
			tagCompound.putString("Name", area.getName());
			tagCompound.putInt("D",area.getDimension());
			tagCompound.putInt("ID",area.getID());
			tagCompound.putBoolean("playNight", area.isPlayatNight());
			tagCompound.putBoolean("instP", area.isInstantPlay());
			tagList.add(tagCompound);
		}

		data.put("Areas", tagList);
		
		this.markDirty();
	}

	// Serializa a lista de Areas
	public static CompoundNBT  SerializeThis(List<Area> data) {
		
		CompoundNBT nbt=new CompoundNBT();
		int count=0;
		
		Iterator<Area> iteratorArea = data.iterator();
		while (iteratorArea.hasNext()) {
			CompoundNBT tagCompound = new CompoundNBT();
			Area area = iteratorArea.next();
			tagCompound.put("Pos", area.getPosListTag());
			tagCompound.putString("Name", area.getName());
			tagCompound.putInt("D",area.getDimension());
			tagCompound.putInt("ID",area.getID());
			tagCompound.putBoolean("playNight",area.isPlayatNight());
			tagCompound.putBoolean("instP",area.isInstantPlay());
			nbt.put("Area"+count++, tagCompound);
		}
		
		nbt.putInt("lenght", count);
		
		return nbt;
	}
	
	@Override
	public Object get() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void read(CompoundNBT nbt) {
		data = nbt.getCompound(key);

		listAreas.clear();

		Iterator<INBT> iterator = data.getList("Areas", 10).iterator(); // 10 indicates a list of NBTTagCompound
		while (iterator.hasNext()) {
			CompoundNBT areaCompound = (CompoundNBT) iterator.next();
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

			listAreas.add(area);
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.put(key, data);
		return compound;
	}
}
