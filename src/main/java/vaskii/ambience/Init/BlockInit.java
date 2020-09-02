package vaskii.ambience.Init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import vaskii.ambience.objects.blocks.Alarm;
import vaskii.ambience.objects.blocks.Bell;
import vaskii.ambience.objects.blocks.BlockBase;
import vaskii.ambience.objects.blocks.Speaker;
import vaskii.ambience.objects.blocks.SongSwitcher;

public class BlockInit {

	public static final List<BlockBase> BLOCKS = new ArrayList<BlockBase>();
	
	public static final BlockBase block_Speaker = new Speaker("speaker", Material.IRON);
	public static final BlockBase SongSwitcher = new SongSwitcher("songswitcher", Material.WOOD,false);
	public static final BlockBase SongSwitcher_lit = new SongSwitcher("songswitcher_lit", Material.WOOD,true);
	
	public static final BlockBase block_Alarm_WHITE= new Alarm("alarm_white", Material.GLASS,false,0);
	public static final BlockBase block_Alarm_lit_WHITE= new Alarm("alarm_lit_white", Material.GLASS,true,0);
	public static final BlockBase block_Alarm_RED= new Alarm("alarm_red", Material.GLASS,false,1);
	public static final BlockBase block_Alarm_ORANGE= new Alarm("alarm_orange", Material.GLASS,false,2);	
	public static final BlockBase block_Alarm_YELLOW= new Alarm("alarm_yellow", Material.GLASS,false,3);
	public static final BlockBase block_Alarm_LIME= new Alarm("alarm_lime", Material.GLASS,false,4);
	public static final BlockBase block_Alarm_GREEN= new Alarm("alarm_green", Material.GLASS,false,5);
	public static final BlockBase block_Alarm_LIGHTBLUE= new Alarm("alarm_lightblue", Material.GLASS,false,6);
	public static final BlockBase block_Alarm_CYAN= new Alarm("alarm_cyan", Material.GLASS,false,7);
	public static final BlockBase block_Alarm_BLUE= new Alarm("alarm_blue", Material.GLASS,false,8);
	public static final BlockBase block_Alarm_PURPLE= new Alarm("alarm_purple", Material.GLASS,false,9);
	public static final BlockBase block_Alarm_MAGENTA= new Alarm("alarm_magenta", Material.GLASS,false,10);
	public static final BlockBase block_Alarm_PINK= new Alarm("alarm_pink", Material.GLASS,false,11);
	public static final BlockBase block_Alarm_BROWN= new Alarm("alarm_brown", Material.GLASS,false,12);
	
	public static final BlockBase block_Alarm_lit_RED= new Alarm("alarm_lit_red", Material.GLASS,true,1);
	public static final BlockBase block_Alarm_lit_ORANGE= new Alarm("alarm_lit_orange", Material.GLASS,true,2);
	public static final BlockBase block_Alarm_lit_YELLOW= new Alarm("alarm_lit_yellow", Material.GLASS,true,3);
	public static final BlockBase block_Alarm_lit_LIME= new Alarm("alarm_lit_lime", Material.GLASS,true,4);
	public static final BlockBase block_Alarm_lit_GREEN= new Alarm("alarm_lit_green", Material.GLASS,true,5);
	public static final BlockBase block_Alarm_lit_LIGHTBLUE= new Alarm("alarm_lit_lightblue", Material.GLASS,true,6);
	public static final BlockBase block_Alarm_lit_CYAN= new Alarm("alarm_lit_cyan", Material.GLASS,true,7);
	public static final BlockBase block_Alarm_lit_BLUE= new Alarm("alarm_lit_blue", Material.GLASS,true,8);
	public static final BlockBase block_Alarm_lit_PURPLE= new Alarm("alarm_lit_purple", Material.GLASS,true,9);
	public static final BlockBase block_Alarm_lit_MAGENTA= new Alarm("alarm_lit_magenta", Material.GLASS,true,10);
	public static final BlockBase block_Alarm_lit_PINK= new Alarm("alarm_lit_pink", Material.GLASS,true,11);
	public static final BlockBase block_Alarm_lit_BROWN= new Alarm("alarm_lit_brown", Material.GLASS,true,12);
	
	public static final BlockBase block_Bell= new Bell("bell", Material.IRON);
}
