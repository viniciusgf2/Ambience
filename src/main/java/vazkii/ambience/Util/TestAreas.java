package vazkii.ambience.Util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.config.Config.Ignore;
import vazkii.ambience.Reference;

public class TestAreas extends WorldSavedData {
	private static final String DATA_NAME = Reference.MOD_ID + "_AreaData";
	public static boolean IS_GLOBAL = true;

	
	public static String ola="oi";
	

	@Ignore
	private NBTTagCompound data = new NBTTagCompound();

	public TestAreas(String name) {
		super(name);

	}

	public TestAreas() {
		super(DATA_NAME);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		data = nbt.getCompoundTag(DATA_NAME);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag(DATA_NAME, data);
		return compound;
	}

	public static TestAreas get(World world) {
		MapStorage storage = IS_GLOBAL ? world.getMapStorage() : world.getPerWorldStorage();
		TestAreas instance = (TestAreas) storage.getOrLoadData(TestAreas.class, DATA_NAME);

		if (instance == null) {
			instance = new TestAreas();
			storage.setData(DATA_NAME, instance);
		}
		return instance;
	}

	public void saveData(World world) {
		//data.setString("Areas", "teste2");
		
		data=this.serializeNBT();
		
		MapStorage storage = IS_GLOBAL ? world.getMapStorage() : world.getPerWorldStorage();
		TestAreas instance = (TestAreas)storage.getOrLoadData(TestAreas.class, DATA_NAME);

		if (instance == null) {
			instance = new TestAreas();
			storage.setData(DATA_NAME, instance);
		}else {
			storage.setData(DATA_NAME, this);
		}

		this.markDirty();
	}
}
