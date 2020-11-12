package vazkii.ambience.Screens;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import vazkii.ambience.Util.ModContainerTypes;

public class SpeakerContainer extends Container {

	public static int id;
	public static int delay;
	public static String selectedSound="";
	public static BlockPos pos;
	public static boolean loop;
	public static float distance;
	public static String openGui;
	public static int index;
	public static String dimension;
	public static boolean isAlarm;

	public SpeakerContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer extraData) {
		super(ModContainerTypes.SPEAKER_CONTAINER.get(), windowId);
	}

	public SpeakerContainer(int id) {
		super(ModContainerTypes.SPEAKER_CONTAINER.get(), id);
	}

	public SpeakerContainer(int id, int delay, String selectedSound, BlockPos pos, boolean loop, float distance,
			String openGui, int index,String dimension,boolean isAlarm) {
		super(ModContainerTypes.SPEAKER_CONTAINER.get(), id);

		this.id = id;
		this.delay = delay;
		this.selectedSound = selectedSound;
		this.pos = pos;
		this.loop = loop;
		this.distance = distance;
		this.openGui = openGui;
		this.index = index;
		this.dimension=dimension;
		this.isAlarm=isAlarm;
	}

	protected SpeakerContainer(ContainerType<?> type, int id) {
		super(type, id);
		// TODO Auto-generated constructor stub
	}

	public boolean canInteractWith(PlayerEntity player) {
		return true;
	}

	public void onContainerClosed(PlayerEntity playerIn) {

	}
}