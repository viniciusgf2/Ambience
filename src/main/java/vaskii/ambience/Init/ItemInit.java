package vaskii.ambience.Init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import vaskii.ambience.objects.items.Horn;
import vaskii.ambience.objects.items.Ocarina;
import vaskii.ambience.objects.tools.Soundnizer;

public class ItemInit {

	public static final List<Item> ITEMS=new ArrayList<Item>();
	
	public static final Item itemSondnizer=new Soundnizer("soundnizer");
	
	public static final Item itemHorn=new Horn("horn");
	
	public static final Item itemOcarina=new Ocarina("ocarina");
	
	//Tools
	//public static final Item soundnizer=new Soundnizer("sound");
	
}
