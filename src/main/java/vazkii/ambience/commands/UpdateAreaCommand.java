package vazkii.ambience.commands;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import vaskii.ambience.network4.MyMessage4;
import vaskii.ambience.network4.NetworkHandler4;
import vazkii.ambience.Ambience;
import vazkii.ambience.Reference;
import vazkii.ambience.Util.WorldData;
import vazkii.ambience.World.Biomes.Area;
import vazkii.ambience.World.Biomes.Area.Operation;

public class UpdateAreaCommand extends CommandBase{

	private final List<String> aliases= Lists.newArrayList(Reference.MOD_ID, "area");
	
	@Override
	public String getName() {
		return "areaedit";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "areaedit <<Area ID or Pos1 '<old x1> <old y1> <old z1>'> <new x1> <new y1> <new z1> <new x2> <new y2> <new z2> <name> <instant play> <play at night>";
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
			BlockPos oldpos=null;
			Vec3d newpos1 = null,newpos2= null;		
			String areaName = "";
			Boolean playInstant = false,playAtNight = false, commandValid=false;					
			int areaID=0;
			
				try {	
					
				if(args.length>=11) {
					if(args[9].isEmpty() | args[9]==null) {
						commandValid=false;
						throw new Exception("Missing a Name for the Region.");
						
					}else {
						areaName= args[9];	
						commandValid=true;
					}
					
					oldpos= new BlockPos(processCoordInput(args[0],sender.getPosition().getX(), args[0].split("~").length>1 ? Integer.parseInt(args[0].split("~")[1]) : 0 ),
							   processCoordInput(args[1],sender.getPosition().getY(), args[1].split("~").length>1 ? Integer.parseInt(args[1].split("~")[1]) : 0 ),
							   processCoordInput(args[2],sender.getPosition().getZ(), args[2].split("~").length>1 ? Integer.parseInt(args[2].split("~")[1]) : 0 ));
		
					
					newpos1= new Vec3d(processCoordInput(args[3],sender.getPosition().getX(), args[3].split("~").length>1 ? Integer.parseInt(args[3].split("~")[1]) : 0 ),
									   processCoordInput(args[4],sender.getPosition().getY(), args[4].split("~").length>1 ? Integer.parseInt(args[4].split("~")[1]) : 0 ),
									   processCoordInput(args[5],sender.getPosition().getZ(), args[5].split("~").length>1 ? Integer.parseInt(args[5].split("~")[1]) : 0 ));
					
					newpos2= new Vec3d(processCoordInput(args[6],sender.getPosition().getX(), args[6].split("~").length>1 ? Integer.parseInt(args[6].split("~")[1]) : 0 ),
							   processCoordInput(args[7],sender.getPosition().getY(), args[7].split("~").length>1 ? Integer.parseInt(args[7].split("~")[1]) : 0 ),
							   processCoordInput(args[8],sender.getPosition().getZ(), args[8].split("~").length>1 ? Integer.parseInt(args[8].split("~")[1]) : 0 ));
					
					if(newpos1.equals(newpos2)) {
						throw new Exception("The coordinates can't be the same");				
					}
					
					if(args.length>10)		
					{
						if(!args[10].equals("true") & !args[10].equals("false")) 
						{
							commandValid=false;
							sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid value '" + args[10] + "' for instant play, accepted values: true|false"));
						}
						else
						{
							commandValid=true;
							playInstant=Boolean.parseBoolean(args[10]);
						}
					}
		
					if(args.length>11) {
						if(!args[11].equals("true") & !args[11].equals("false")) 
						{
							commandValid=false;
							sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid value '" + args[11] + "' for play at night, accepted values: true|false"));
						}
						else
						{
							commandValid=true;
							playAtNight=Boolean.parseBoolean(args[11]);
						}
					}	
					
				}
				else if(args.length>=9) {
					oldpos= null;
					areaID= Integer.parseInt(args[0]);
					if(args[7].isEmpty() | args[7]==null) {
						commandValid=false;
						throw new Exception("Missing a Name for the Region.");
						
					}else {
						areaName= args[7];	
						commandValid=true;
					}
					
					newpos1= new Vec3d(processCoordInput(args[1],sender.getPosition().getX(), args[1].split("~").length>1 ? Integer.parseInt(args[1].split("~")[1]) : 0 ),
									   processCoordInput(args[2],sender.getPosition().getY(), args[2].split("~").length>1 ? Integer.parseInt(args[2].split("~")[1]) : 0 ),
									   processCoordInput(args[3],sender.getPosition().getZ(), args[3].split("~").length>1 ? Integer.parseInt(args[3].split("~")[1]) : 0 ));
					
					newpos2= new Vec3d(processCoordInput(args[4],sender.getPosition().getX(), args[4].split("~").length>1 ? Integer.parseInt(args[4].split("~")[1]) : 0 ),
							   processCoordInput(args[5],sender.getPosition().getY(), args[5].split("~").length>1 ? Integer.parseInt(args[5].split("~")[1]) : 0 ),
							   processCoordInput(args[6],sender.getPosition().getZ(), args[6].split("~").length>1 ? Integer.parseInt(args[6].split("~")[1]) : 0 ));
					
					if(args.length>8)		
					{
						if(!args[8].equals("true") & !args[8].equals("false")) 
						{
							commandValid=false;
							sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid value '" + args[8] + "' for instant play, accepted values: true|false"));
						}
						else
						{
							commandValid=true;
							playInstant=Boolean.parseBoolean(args[8]);
						}
					}
		
					if(args.length>9) {
						if(!args[9].equals("true") & !args[9].equals("false")) 
						{
							commandValid=false;
							sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid value '" + args[9] + "' for play at night, accepted values: true|false"));
						}
						else
						{
							commandValid=true;
							playAtNight=Boolean.parseBoolean(args[9]);
						}
					}
				}
				else {
					throw new Exception("Invalid command");
				}
				
				
				/*oldpos= new BlockPos(processCoordInput(args[0],sender.getPosition().getX(), args[0].split("~").length>1 ? Integer.parseInt(args[0].split("~")[1]) : 0 ),
						   processCoordInput(args[1],sender.getPosition().getY(), args[1].split("~").length>1 ? Integer.parseInt(args[1].split("~")[1]) : 0 ),
						   processCoordInput(args[2],sender.getPosition().getZ(), args[2].split("~").length>1 ? Integer.parseInt(args[2].split("~")[1]) : 0 ));
	
				
				newpos1= new Vec3d(processCoordInput(args[3],sender.getPosition().getX(), args[3].split("~").length>1 ? Integer.parseInt(args[3].split("~")[1]) : 0 ),
								   processCoordInput(args[4],sender.getPosition().getY(), args[4].split("~").length>1 ? Integer.parseInt(args[4].split("~")[1]) : 0 ),
								   processCoordInput(args[5],sender.getPosition().getZ(), args[5].split("~").length>1 ? Integer.parseInt(args[5].split("~")[1]) : 0 ));
				
				newpos2= new Vec3d(processCoordInput(args[6],sender.getPosition().getX(), args[6].split("~").length>1 ? Integer.parseInt(args[6].split("~")[1]) : 0 ),
						   processCoordInput(args[7],sender.getPosition().getY(), args[7].split("~").length>1 ? Integer.parseInt(args[7].split("~")[1]) : 0 ),
						   processCoordInput(args[8],sender.getPosition().getZ(), args[8].split("~").length>1 ? Integer.parseInt(args[8].split("~")[1]) : 0 ));
				*/
				
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
				sender.sendMessage(new TextComponentString("Usage: "+TextFormatting.YELLOW + "/areaedit <<Area ID or Pos1 '<old x1> <old y1> <old z1>'> <new x1> <new y1> <new z1> <new x2> <new y2> <new z2> <name> <instant play> <play at night>"));
			}
					
			if(commandValid==true)
			{
				Area currentArea=null;
				if(args.length>=11) 
				     currentArea = Area.getBlockStandingArea(oldpos);
				else if(args.length>=9) 
					 currentArea = Area.getAreabyID(Integer.parseInt(args[0]));
				
				 if(currentArea!=null) {
				    				    
					currentArea.setName(areaName);
					currentArea.setOperation(Operation.EDIT);
					currentArea.setPos1(newpos1);
					currentArea.setPos2(newpos2);
					currentArea.setPlayAtNight(playAtNight);
					currentArea.setInstantPlay(playInstant);
					// Send the selected area to the server to save it
				//	NetworkHandler4.sendToServer(new MyMessage4(currentArea.SerializeThis()));
					
					WorldData data = new WorldData().GetArasforWorld(sender.getEntityWorld());
					data.editArea(currentArea);
					data.saveData();
					Ambience.getWorldData().listAreas = data.listAreas;
					Ambience.sync = true;
					
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Region updated with success!"));
				}else {
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Region not found."));
				}
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
