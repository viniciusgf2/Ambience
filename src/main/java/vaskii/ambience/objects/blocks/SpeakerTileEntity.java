package vaskii.ambience.objects.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import vaskii.ambience.network4.MyMessage4;
import vaskii.ambience.network4.NetworkHandler4;

public class SpeakerTileEntity extends TileEntity implements ITickable {

	private int cooldown;
	public String selectedSound = "";
	public boolean isPowered = false;
	public int delay = 30;
	public boolean loop = true;
	public float distance = 1;
	public int countPlay = 0;
	public boolean sync = false;

	public SpeakerTileEntity() {
		cooldown = 0;
		delay = 30;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.cooldown = nbt.getInteger("cooldown");
		this.delay = nbt.getInteger("delay");
		this.selectedSound = nbt.getString("sound");
		this.loop = nbt.getBoolean("loop");
		this.distance = nbt.getFloat("distance");
		super.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("cooldown", this.cooldown);
		nbt.setInteger("delay", this.delay);
		nbt.setString("sound", selectedSound);
		nbt.setBoolean("loop", this.loop);
		nbt.setFloat("distance", this.distance);

		return super.writeToNBT(nbt);
	}

	@Override
	public void update() {
		// if (FMLCommonHandler.instance().getSide().isClient()) {
		this.cooldown++;
		this.cooldown %= (delay == 0 ? 30 : delay);

		if (!this.getWorld().isRemote & cooldown == 0) {
			

			if (loop) //Play infinitly
				if (world.isBlockIndirectlyGettingPowered(pos) > 0) {

					this.getWorld().playSound((EntityPlayer) null, this.pos.getX(), this.pos.getY(), this.pos.getZ(),
							(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
									.getObject(new ResourceLocation("ambience:" + selectedSound)),
							SoundCategory.NEUTRAL, (float) distance, (float) 1);
				}
			
			if(!loop & countPlay==0)//Play one time if loop is disabled
			{
				if (world.isBlockIndirectlyGettingPowered(pos) > 0) {

					this.getWorld().playSound((EntityPlayer) null, this.pos.getX(), this.pos.getY(), this.pos.getZ(),
							(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
									.getObject(new ResourceLocation("ambience:" + selectedSound)),
							SoundCategory.NEUTRAL, (float) distance, (float) 1);
					countPlay++;
				}
			}
			
			
		}

		if (sync & !this.getWorld().isRemote) {
			sync = false;
			// Updates client
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("selectedSound", selectedSound);
			nbt.setInteger("delay", delay);
			nbt.setBoolean("loop", loop);
			nbt.setBoolean("sync",true);
			nbt.setFloat("distance", distance);
			// NetworkHandler4.sendToClient(new MyMessage4(nbt), entity);

			NetworkHandler4.sendToAll(new MyMessage4(nbt));

		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		int metadata = getBlockMetadata();
		return new SPacketUpdateTileEntity(this.pos, metadata, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		this.readFromNBT(tag);
	}

	@Override
	public NBTTagCompound getTileData() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return nbt;
	}
}
