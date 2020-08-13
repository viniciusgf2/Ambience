package vazkii.ambience.blocks;

import java.io.File;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.ambience.Ambience;
import vazkii.ambience.Util.ModTileEntityTypes;
import vazkii.ambience.network.AmbiencePackageHandler;
import vazkii.ambience.network.MyMessage;

public class AlarmTileEntity extends TileEntity implements ITickableTileEntity{

	public int cooldown;
	public String selectedSound="";
	public boolean isPowered = false;
	public int delay = 30;
	public boolean loop = true;
	public int distance = 1;
	public int countPlay = 0;
	public boolean sync = false;
	public int songLenght = 0;
	private String old_song = "";
	public String color = "";
	public boolean isOn=false;
	
	
/*	public static AlarmTileEntity AlarmMaker(String color) {
		
		return new AlarmTileEntity();
	}*/
	
	public AlarmTileEntity(final TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);		
		
	}
	
	
	public AlarmTileEntity(List<Block> blocks) {
		this(ModTileEntityTypes.ALARM_RED.get());
	}
	
	public AlarmTileEntity(String Color) {
		this(ModTileEntityTypes.getAlarmByColor(Color));
		
		this.color=Color;
		cooldown = 0;
		delay = 30;
	}
	
	public AlarmTileEntity(String Color,boolean isOn,String selectedSound,int delay,int distance,int cooldown) {		
		this(ModTileEntityTypes.getAlarmByColor(Color));
		
		this.selectedSound=selectedSound;
		
		//this.songLenght=6;
		this.isOn=isOn;
		this.color=Color;
		this.cooldown = cooldown;
		this.delay = delay;
		this.distance=distance;
	}
	
	public AlarmTileEntity(String Color,boolean isOn) {
		//this(ModTileEntityTypes.ALARM_RED.get());
		this(ModTileEntityTypes.getAlarmByColor(Color));
		
		this.isOn=isOn;
		this.color=Color;
		cooldown = 0;
		this.delay = delay;
		this.distance=distance;
	}
	
	public AlarmTileEntity() {
		this(ModTileEntityTypes.ALARM_RED.get());
		//this(ModTileEntityTypes.getAlarmByColor());
		
		cooldown = 0;
		delay = 30;
	} 
	
	public AlarmTileEntity(boolean isAlarmL, String colorL,boolean on) {
		this(ModTileEntityTypes.ALARM_RED.get());
		
		cooldown = 0;
		delay = 30;
		color=colorL;
		isOn=on; //Isso faz a luz saber que esta ligada assim que voce coloca um alarm_lit no chao entao ele apaga (talvez tirar dps se eu quiser manter luzes acessas)
	}
	
	@Override
	public void read(CompoundNBT nbt) {
		this.cooldown = nbt.getInt("cooldown");
		this.delay = nbt.getInt("delay");
		this.selectedSound = nbt.getString("sound");
		this.loop = nbt.getBoolean("loop");
		this.distance = nbt.getInt("distance");
		this.color = nbt.getString("color");
		
		super.read(nbt);

		old_song = selectedSound;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt.putInt("cooldown", this.cooldown);
		nbt.putInt("delay", this.delay);
		nbt.putString("sound", selectedSound);
		nbt.putBoolean("loop", this.loop);
		nbt.putFloat("distance", this.distance);
		nbt.putString("color", this.color);

		return super.write(nbt);
	}
	
	int countLight=0;
	Alarm parent;
	private void UpdateLight(boolean syncWithSound) {
		
		if(parent == null) {
			Block blockAlarm=this.world.getBlockState(pos).getBlock();
			if(blockAlarm instanceof Alarm)
				parent=(Alarm)blockAlarm;
		}else {
		
			if(parent == null) {
				Block blockAlarm=this.world.getBlockState(pos).getBlock();
				if(blockAlarm instanceof Alarm)
					parent=(Alarm)blockAlarm;
			}else {
			
				
					this.countLight++;
					
					if(countLight>40 & isOn) {											
						parent.setState(false, this.world, this.pos, this.color, this.selectedSound);	
						isOn=false;
						countLight=0;
					}	
					
				if(songLenght>2 & !syncWithSound)
				{	
					if((countLight>20 & countLight<40) & world.isBlockPowered(pos) & !isOn) {
						parent.setState(true, this.world, this.pos, this.color, this.selectedSound);		
						isOn=true;
					}
					
				//	if(countLight>30)
					//	countLight=0;
				}
				
				if(songLenght>2 & syncWithSound)
				{	
					if(countLight>20 & world.isBlockPowered(pos) & !isOn) {
						parent.setState(true, this.world, this.pos, this.color, this.selectedSound);		
						isOn=true;
					}
					
				//	if(countLight>30)
					//	countLight=0;
				}
						
				if(syncWithSound & songLenght<=2) {		
					parent.setState(true, this.world, this.pos, this.color, this.selectedSound);
					isOn=true;	
					this.countLight=0;		
				}	
				
				//Desliga a luz caso não receba sinal de redstone
				if(!world.isBlockPowered(pos) & isOn) {
					isOn=false;
					parent.setState(false, this.world, this.pos, this.color, this.selectedSound);	
				}	
			}
			
			
				
			/*	if(syncWithSound) {		
					
					if(countLight>15 & !isOn) {						
						parent.setState(true, this.world, this.pos, this.color, this.selectedSound);	
						isOn=true;
					}
					else {
						parent.setState(false, this.world, this.pos, this.color, this.selectedSound);	
						isOn=false;
						countLight=0;
					}
					
				}	
				*/
				
			/*	if(countLight>57 & isOn & world.isBlockPowered(pos) & syncWithSound) {											
					parent.setState(false, this.world, this.pos, this.color, this.selectedSound);	
					isOn=false;
				}	
				if(countLight>27 & !isOn & world.isBlockPowered(pos) & syncWithSound) {											
					parent.setState(true, this.world, this.pos, this.color, this.selectedSound);	
					isOn=true;
				}
				
			if(songLenght>2 & !syncWithSound)
			{	
				if((countLight>27 & countLight<47) & world.isBlockPowered(pos) & !isOn) {
					parent.setState(true, this.world, this.pos, this.color, this.selectedSound);		
					isOn=true;
				}else if(countLight>67  & world.isBlockPowered(pos) & isOn) {
					parent.setState(false, this.world, this.pos, this.color, this.selectedSound);		
					isOn=false;
				}
				
				System.out.println(""+countLight);
			//	if(countLight>60)
				//	countLight=0;
			}
			else if(songLenght<2 & !syncWithSound & world.isBlockPowered(pos) & isOn & countLight>27 ) {
				isOn=false;
				parent.setState(false, this.world, this.pos, this.color, this.selectedSound);
			}	
					
			if(syncWithSound & songLenght<=2) {		
				parent.setState(true, this.world, this.pos, this.color, this.selectedSound);		
				isOn=true;	
				this.countLight=0;		
			}	
			*/
			
			
			
			//Desliga a luz caso não receba sinal de redstone
			/*if(!world.isBlockPowered(pos)) {
				isOn=false;
				parent.setState(false, this.world, this.pos, this.color, this.selectedSound);
			}*/	
		}
	}

	@Override
	public void tick() {
		
	/*	//Desliga a luz caso não receba sinal de redstone
		if(!world.isBlockPowered(pos) & isOn) {			
			if(parent == null) {
				Block blockAlarm=this.world.getBlockState(pos).getBlock();
				if(blockAlarm instanceof Alarm)
					parent=(Alarm)blockAlarm;
			}else {
				isOn=false;
				parent.setState(false, this.world, this.pos, this.color, this.selectedSound);
			}
		}else{
			if(this.countLight<150) {
				this.countLight++;

				System.out.println(countLight);
			}
		}
		*/
		
		UpdateLight(false);
		
		try {
			if (songLenght == 0 & selectedSound != "")
				getSongLenght();
	
					
			if (!this.getWorld().isRemote & cooldown>0) 
			{
				this.cooldown--;			
			}
						
			if (!this.getWorld().isRemote & cooldown == 0) {
			
				if (loop) // Play infinitly
					if (world.isBlockPowered(pos)) {
						this.cooldown =  delay + (songLenght * 20);


						Alarm.selectedSound=selectedSound;
						UpdateLight(true);
												
						this.getWorld().playSound((PlayerEntity) null, this.pos.getX(), this.pos.getY(), this.pos.getZ(),
								ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambience:" + selectedSound)),
								SoundCategory.NEUTRAL, (float) distance, (float) 1);
					}
	
				if (!loop & countPlay == 0)// Play one time if loop is disabled
				{
					if (world.isBlockPowered(pos)) {

						Alarm.selectedSound=selectedSound;
						
						this.getWorld().playSound((PlayerEntity) null, this.pos.getX(), this.pos.getY(), this.pos.getZ(),
								ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambience:" + selectedSound)),
								SoundCategory.NEUTRAL, (float) distance, (float) 1);
						countPlay++;
					}
				}
	
			}
	
			if (sync & !this.getWorld().isRemote) {
				sync = false;
				// Updates client
				
				CompoundNBT nbt = new CompoundNBT();
				nbt.putString("selectedSound", selectedSound);
				nbt.putInt("delay", delay);
				nbt.putBoolean("loop", loop);
				nbt.putBoolean("sync", true);
				nbt.putString("color", this.color);
				nbt.putInt("distance", distance);

				AmbiencePackageHandler.sendToAll(new MyMessage(nbt));
				Alarm.selectedSound=selectedSound;
				markDirty();
	
				if (!old_song.contains(selectedSound)) {
					old_song = selectedSound;
					cooldown = 0;
				}
	
				// Obtém o tempo do som selecionado********************
				getSongLenght();
				// ****************************************************
				if (cooldown == 0) {
					if (world.isBlockPowered(pos)) {
						this.cooldown = delay + (songLenght* 20);
						this.getWorld().playSound((PlayerEntity) null, this.pos.getX(), this.pos.getY(), this.pos.getZ(),
								ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambience:" + selectedSound)),
								SoundCategory.NEUTRAL, (float) distance, (float) 1);
					}
				}
	
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getSongLenght() {
		
		 // Obtém o tempo do som selecionado********************
		String selectedsound = this.selectedSound;//((AlarmTileEntity) world.getTileEntity(pos)).selectedSound;
		File f = new File(Ambience.resourcesDir+"\\sounds", selectedsound + ".ogg");

		if (f.isFile()) {
			try {
				AudioFile af = AudioFileIO.read(f);
				AudioHeader ah = af.getAudioHeader();
				songLenght =ah.getTrackLength();
			} catch (Exception e) {

			}
		}else {
			songLenght=0;
		}
		// ****************************************************
	}

}
