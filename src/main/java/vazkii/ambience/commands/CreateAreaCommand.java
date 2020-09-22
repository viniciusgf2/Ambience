package vazkii.ambience.commands;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import vazkii.ambience.Ambience;
import vazkii.ambience.Reference;
import vazkii.ambience.Util.WorldData;
import vazkii.ambience.World.Biomes.Area;
import vazkii.ambience.World.Biomes.Area.Operation;

public class CreateAreaCommand extends CommandBase{

	private final List<String> aliases= Lists.newArrayList(Reference.MOD_ID, "area");
	
	@Override
	public String getName() {
		return "area";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "area <name> <x1> <y1> <z1> <x2> <y2> <z2> <instant play> <play at night>";
	}
	
	@Override
	public List<String> getAliases() {		
		return aliases;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {		
	//	return super.checkPermission(server, sender);
		return true;
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		((WorldServer) sender.getEntityWorld()).addScheduledTask(() -> {

			Vec3d pos1 = null,pos2= null;		
			String areaName = "";
			Boolean playInstant = false,playAtNight = false, commandValid=false;
					
			try {	
				if(args.length==0 || args[0].equals("help")) {
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "<--- Area Commands --->"));
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/area -> creates a new region."));
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/area list -> show the information for all the regions created."));
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/area showInfo -> show the region informations for the current player standing region."));					
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/areadel -> delete a region"));
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/areaedit -> edit a region"));
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "<--------------------->"));					
				}
				else if(args[0].equals("showInfo")) {

						Area area = Area.getBlockStandingArea(sender.getPosition());
						
						if(area!=null) 
						{
							sender.sendMessage(new TextComponentString(TextFormatting.WHITE + "<-------------------------------->"));
							sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "ID: "+area.getID()));
							sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Name: "+area.getName()));
							sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Pos1: "+area.getPos1()));
							sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Pos2: "+area.getPos2()));
							sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Name: "+area.getDimension()));
							sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Instant Play: "+area.isInstantPlay()));
							sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Play at night: "+area.isPlayatNight()));
							sender.sendMessage(new TextComponentString(TextFormatting.WHITE + "<-------------------------------->"));		
						}else {
							sender.sendMessage(new TextComponentString(TextFormatting.RED + "Must be inside a Region to show the informations"));
						}
				}
				else if(args[0].equals("list")) {
					
					for (Area area : Ambience.getWorldData().listAreas) {
						sender.sendMessage(new TextComponentString(TextFormatting.WHITE + "<-------------------------------->"));
						sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "ID: "+area.getID()));
						sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Name: "+area.getName()));
						sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Pos1: "+area.getPos1()));
						sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Pos2: "+area.getPos2()));
						sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Name: "+area.getDimension()));
						sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Instant Play: "+area.isInstantPlay()));
						sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Play at night: "+area.isPlayatNight()));
						sender.sendMessage(new TextComponentString(TextFormatting.WHITE + "<-------------------------------->"));
					}
					
						
				}else {
				
					if(args[0].isEmpty() | args[0]==null) {
						commandValid=false;
						throw new Exception("Missing a Name for the Region.");
						
					}else {
						areaName= args[0];	
						commandValid=true;
					}
					
					pos1= new Vec3d(processCoordInput(args[1],sender.getPosition().getX(), args[1].split("~").length>1 ? Integer.parseInt(args[1].split("~")[1]) : 0 ),
									   processCoordInput(args[2],sender.getPosition().getY(), args[2].split("~").length>1 ? Integer.parseInt(args[2].split("~")[1]) : 0 ),
									   processCoordInput(args[3],sender.getPosition().getZ(), args[3].split("~").length>1 ? Integer.parseInt(args[3].split("~")[1]) : 0 ));
					
					pos2= new Vec3d(processCoordInput(args[4],sender.getPosition().getX(), args[4].split("~").length>1 ? Integer.parseInt(args[4].split("~")[1]) : 0 ),
							   processCoordInput(args[5],sender.getPosition().getY(), args[5].split("~").length>1 ? Integer.parseInt(args[5].split("~")[1]) : 0 ),
							   processCoordInput(args[6],sender.getPosition().getZ(), args[6].split("~").length>1 ? Integer.parseInt(args[6].split("~")[1]) : 0 ));
					
					if(pos1.equals(pos2)) {
						throw new Exception("The coordinates can't be the same");				
					}
					
					if(args.length>7)		
					{
						if(!args[7].equals("true") & !args[7].equals("false")) 
						{
							commandValid=false;
							sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid value '" + args[7] + "' for instant play, accepted values: true|false"));
						}
						else
						{
							commandValid=true;
							playInstant=Boolean.parseBoolean(args[7]);
						}
					}
		
					if(args.length>8) {
						if(!args[8].equals("true") & !args[8].equals("false")) 
						{
							commandValid=false;
							sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid value '" + args[8] + "' for play at night, accepted values: true|false"));
						}
						else
						{
							commandValid=true;
							playAtNight=Boolean.parseBoolean(args[8]);
						}
					}	
				}
			}
			catch(Exception e) {
				commandValid=false;
				if(args.length < 1) {
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Missing the Region Name"));
				}
				if(e instanceof Exception & e.getMessage().length()>2) {
					sender.sendMessage(new TextComponentString(TextFormatting.RED + ""+ e.getMessage()));
				}
				if(e instanceof ArrayIndexOutOfBoundsException) {
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Missing coordinates"));
				}
				if(e instanceof NumberFormatException) {
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid coordinates"));				
				}
				sender.sendMessage(new TextComponentString("Usage: "+TextFormatting.YELLOW + "/area <name> <x1> <y1> <z1> <x2> <y2> <z2> <instant play> <play at night>"));
			}
					
			if(commandValid==true)
			{
				
					Area currentArea=new Area();
					currentArea.setName(areaName);
					currentArea.setOperation(Operation.CREATE);
					currentArea.setPos1(pos1);
					currentArea.setPos2(pos2);
					currentArea.setPlayAtNight(playAtNight);
					currentArea.setInstantPlay(playInstant);
					// Send the selected area to the server to save it
					//NetworkHandler4.sendToServer(new MyMessage4(currentArea.SerializeThis()));
					WorldData data = new WorldData().GetArasforWorld(sender.getEntityWorld());
					data.addArea(currentArea);
					data.saveData();
					Ambience.getWorldData().listAreas = data.listAreas;
					Ambience.sync = true;
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Region created with success!"));
				
			}
		});
	}

	private int processCoordInput(String input,int axisValue, int sumValue) {
				
		if(input.equals("~")) {
			return Integer.parseInt("" + axisValue);
		}
		else if(input.equals("-~")) {
			return Integer.parseInt("" + -axisValue);
		}
		else if(input.equals("~"+sumValue)) {
			return Integer.parseInt(""+ (sumValue + axisValue));
		}
		
		return Integer.parseInt(input);
	}
	
}
