package vazkii.ambience.Util;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import vazkii.ambience.Ambience;

public class TestAreas extends WorldSavedData implements Supplier{
	
	private CompoundNBT data = new CompoundNBT();
	
	public TestAreas() {
		super(DATA_NAME);
	}
	
	public TestAreas(String name) {
		super(name);
	}

	private static final String DATA_NAME = "Ambience_AreaData";
	public static boolean IS_GLOBAL = true;

	
	@Override
	public void read(CompoundNBT nbt) {
		data = nbt.getCompound(DATA_NAME);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.put(DATA_NAME, data);
		return compound;
	}
	
	public static TestAreas forWorld(ServerWorld world)
	{
		DimensionSavedDataManager storage = world.getSavedData();
		Supplier<TestAreas> sup = new TestAreas();
		TestAreas saver = (TestAreas) storage.getOrCreate(sup, Ambience.MODID);

		if (saver == null) 
		{
			saver = new TestAreas();
			storage.set(saver);
		}
		return saver;
	}
	
	@Override
	public Object get()
	{
		return this;
	}
		
	
	public void saveData(ServerWorld world) {
		//data.setString("Areas", "teste2");
		
		data=this.serializeNBT();

		DimensionSavedDataManager storage = world.getSavedData();
		Supplier<TestAreas> sup = new TestAreas();
		TestAreas instance = (TestAreas) storage.getOrCreate(sup, Ambience.MODID);

		if (instance == null) {
			instance = new TestAreas();
			storage.set(instance);
		}else {
			storage.set(this);
		}

		this.markDirty();
	}
}
