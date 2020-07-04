package vaskii.ambience.objects.blocks;

import java.io.File;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;

import net.minecraft.advancements.critereon.BredAnimalsTrigger.Instance;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import vaskii.ambience.network4.MyMessage4;
import vaskii.ambience.network4.NetworkHandler4;
import vazkii.ambience.Ambience;

public class SpeakerTileEntity extends TileEntity implements ITickable {

	public int cooldown;
	public String selectedSound = "";
	public boolean isPowered = false;
	public int delay = 30;
	public boolean loop = true;
	public float distance = 1;
	public int countPlay = 0;
	public boolean sync = false;
	public int songLenght = 0;
	private String old_song = "";
	private boolean isAlarm=false;
	public String color = "";
	public boolean isOn=false;
	
	//public static int testCooldown = 0;

	public SpeakerTileEntity() {
		cooldown = 0;
		delay = 30;
	}
	
	public SpeakerTileEntity(boolean isAlarmL, String colorL,boolean on) {
		cooldown = 0;
		delay = 30;
		color=colorL;
		isAlarm=isAlarmL;
		isOn=on; //Isso faz a luz saber que esta ligada assim que voce coloca um alarm_lit no chao entao ele apaga (talvez tirar dps se eu quiser manter luzes acessas)
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.cooldown = nbt.getInteger("cooldown");
		this.delay = nbt.getInteger("delay");
		this.selectedSound = nbt.getString("sound");
		this.loop = nbt.getBoolean("loop");
		this.distance = nbt.getFloat("distance");
		this.color = nbt.getString("color");
		this.isAlarm = nbt.getBoolean("isAlarm");
		super.readFromNBT(nbt);

		old_song = selectedSound;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("cooldown", this.cooldown);
		nbt.setInteger("delay", this.delay);
		nbt.setString("sound", selectedSound);
		nbt.setBoolean("loop", this.loop);
		nbt.setFloat("distance", this.distance);
		nbt.setString("color", this.color);
		nbt.setBoolean("isAlarm", this.isAlarm);

		return super.writeToNBT(nbt);
	}
	

	int countLight=0;
	Alarm parent;
	private void UpdateLight(boolean syncWithSound) {
		
		if(parent == null) {
			Block blockAlarm=this.world.getBlockState(pos).getBlock();
			if(blockAlarm instanceof Alarm)
				parent=(Alarm)blockAlarm;
		}else {
		
			if(isAlarm)	
				this.countLight++;
				
				if(countLight>17 & isOn & isAlarm) {											
					parent.setState(false, this.world, this.pos, this.color);	
					isOn=false;
				}	
				
			if(isAlarm & songLenght>2 & !syncWithSound)
			{	
				if((countLight>0 & countLight<17) & world.isBlockIndirectlyGettingPowered(pos) > 0 & !isOn) {
					parent.setState(true, this.world, this.pos, this.color);		
					isOn=true;
				}
				
				if(countLight>30)
					countLight=0;
			}
					
			if(syncWithSound & songLenght<=2) {		
				parent.setState(true, this.world, this.pos, this.color);		
				isOn=true;	
				this.countLight=0;		
			}	
			
			//Desliga a luz caso não receba sinal de redstone
			if(world.isBlockIndirectlyGettingPowered(pos) <= 0 & isOn) {
				isOn=false;
				parent.setState(false, this.world, this.pos, this.color);
			}	
		}
	}

	@Override
	public void update() {
		
		/*if(isAlarm)			
		{
			this.countLight++;
			 System.out.println(countLight);
		}*/
		//UpdateLight(false);
		
		
		UpdateLight(false);
		
		try {
			if (songLenght == 0 & selectedSound != "")
				getSongLenght();
	
			
			/*if(isAlarm & songLenght<=2) {
				countLight=0;
				Alarm.setState(true, this.world, this.pos, this.color);	
			}*/
			
			//IF the speaker is a alarm do the lighting code flicker
			/*if(isAlarm & songLenght>2) {
						
				if (world.isBlockIndirectlyGettingPowered(pos) > 0) {	
					
					if(countLight>30 & countLight < 60) {
						Alarm.setState(true, this.world, this.pos, this.color);										
					}
					else if(countLight > 60) {
						countLight=0;
					}
					else {
						Alarm.setState(false, this.world, this.pos, this.color);	
					}	
					
				}else {
					Alarm.setState(false, this.world, this.pos, this.color);				
				}
			}*/
			
			// if (FMLCommonHandler.instance().getSide().isClient()) {
			if (!this.getWorld().isRemote & cooldown>0) 
			{
				this.cooldown--;
			//	testCooldown = cooldown;				
			}
						
			if (!this.getWorld().isRemote & cooldown == 0) {
	
				if (loop) // Play infinitly
					if (world.isBlockIndirectlyGettingPowered(pos) > 0) {
						this.cooldown =  delay + (songLenght * 20);

						
						UpdateLight(true);
												
						this.getWorld().playSound((EntityPlayer) null, this.pos.getX(), this.pos.getY(), this.pos.getZ(),
								(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
										.getObject(new ResourceLocation("ambience:" + selectedSound)),
								SoundCategory.NEUTRAL, (float) distance, (float) 1);
					}
	
				if (!loop & countPlay == 0)// Play one time if loop is disabled
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
				nbt.setBoolean("sync", true);
				nbt.setString("color", this.color);
				nbt.setFloat("distance", distance);
				// NetworkHandler4.sendToClient(new MyMessage4(nbt), entity);
				NetworkHandler4.sendToAll(new MyMessage4(nbt));
				markDirty();
	
				//if (old_song+"" != selectedSound+"") {
				if (!old_song.contains(selectedSound)) {
					old_song = selectedSound;
					cooldown = 0;
				}
	
				// Obtém o tempo do som selecionado********************
				getSongLenght();
				// ****************************************************
				if (cooldown == 0) {
					if (world.isBlockIndirectlyGettingPowered(pos) > 0) {
						this.cooldown = delay + (songLenght* 20);
						this.getWorld().playSound((EntityPlayer) null, this.pos.getX(), this.pos.getY(), this.pos.getZ(),
								(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("ambience:" + selectedSound)),SoundCategory.NEUTRAL, (float) distance, (float) 1);
					}
				}
	
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void getSongLenght() {
		// Obtém o tempo do som selecionado********************
		String selectedsound = ((SpeakerTileEntity) world.getTileEntity(pos)).selectedSound;
		File f = new File(Ambience.resourcesDir+"\\sounds", selectedsound + ".ogg");

		if (f.isFile()) {
			try {
				AudioFile af = AudioFileIO.read(f);
				AudioHeader ah = af.getAudioHeader();
				songLenght = ah.getTrackLength();
			} catch (Exception e) {

			}
		}else {
			songLenght=0;
		}
		// ****************************************************
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
