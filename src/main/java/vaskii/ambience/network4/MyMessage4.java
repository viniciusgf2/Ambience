package vaskii.ambience.network4;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MyMessage4 implements IMessage {
	// A default constructor is always required
	public MyMessage4() {
	}

	private NBTTagCompound toSend;

	public MyMessage4(NBTTagCompound toSend) {
		this.toSend = toSend;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// Writes the int into the buf
		ByteBufUtils.writeTag(buf, toSend);
	}

	public NBTTagCompound getToSend() {
		return toSend;
	}

	public void setToSend(NBTTagCompound toSend) {
		this.toSend = toSend;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		// Reads the int back from the buf. Note that if you have multiple values, you
		// must read in the same order you wrote.
		setToSend(ByteBufUtils.readTag(buf));
	}
}
