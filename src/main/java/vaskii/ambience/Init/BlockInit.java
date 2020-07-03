package vaskii.ambience.Init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import vaskii.ambience.objects.blocks.Alarm;
import vaskii.ambience.objects.blocks.Bell;
import vaskii.ambience.objects.blocks.BlockBase;
import vaskii.ambience.objects.blocks.Speaker;

public class BlockInit {

	public static final List<BlockBase> BLOCKS = new ArrayList<BlockBase>();
	
	public static final BlockBase block_Speaker= new Speaker("speaker", Material.IRON);
	public static final BlockBase block_Alarm= new Alarm("alarm", Material.GLASS,false);
	public static final BlockBase block_Alarm_lit= new Alarm("alarm_lit", Material.GLASS,true);
	public static final BlockBase block_Bell= new Bell("bell", Material.IRON);
}
