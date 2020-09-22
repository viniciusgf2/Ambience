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

public class RemoveAreaCommand extends CommandBase {

	private final List<String> aliases = Lists.newArrayList(Reference.MOD_ID, "areadel");

	@Override
	public String getName() {
		return "areadel";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "areadel <x> <y> <z> or /areadel <area ID>";
	}

	@Override
	public List<String> getAliases() {
		return aliases;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		// return super.checkPermission(server, sender);
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		((WorldServer) sender.getEntityWorld()).addScheduledTask(() -> {
			BlockPos pos = null;
			Area currentArea = null;

			try {
				if (args.length == 1)
					currentArea = Area.getAreabyID(Integer.parseInt(args[0]));

				if (args.length > 2)
					pos = new BlockPos(
							processCoordInput(args[0], sender.getPosition().getX(),	args[0].split("~").length > 1 ? Integer.parseInt(args[0].split("~")[1]) : 0),
							processCoordInput(args[1], sender.getPosition().getY(),	args[1].split("~").length > 1 ? Integer.parseInt(args[1].split("~")[1]) : 0),
							processCoordInput(args[2], sender.getPosition().getZ(),	args[2].split("~").length > 1 ? Integer.parseInt(args[2].split("~")[1]) : 0));

			} catch (Exception e) {

				if (e instanceof ArrayIndexOutOfBoundsException) {
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Missing coordinate"));
				}
				if (e instanceof NumberFormatException) {
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid coordinate"));
				}
				sender.sendMessage(new TextComponentString("Usage: " + TextFormatting.YELLOW + "/areadel <x> <y> <z> or /areadel <area ID>"));
			}

			// Get Area by ID
			if (args.length == 1) {
				if (currentArea != null) {
					currentArea.setOperation(Operation.DELETE);

					WorldData data = new WorldData().GetArasforWorld(sender.getEntityWorld());
					data.removeArea(currentArea);
					data.saveData();
					Ambience.getWorldData().listAreas = data.listAreas;
					Ambience.sync = true;
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Region removed with success!"));
				} else {
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Region not found for the ID '" + args[0]+"'"));
				}
			}

			// Get Area by coord
			if (args.length > 2) {
				if (pos != null) {
					currentArea = Area.getBlockStandingArea(pos);
					if (currentArea != null) {
						currentArea.setOperation(Operation.DELETE);

						WorldData data = new WorldData().GetArasforWorld(sender.getEntityWorld());
						data.removeArea(currentArea);
						data.saveData();
						Ambience.getWorldData().listAreas = data.listAreas;
						Ambience.sync = true;
						sender.sendMessage(
								new TextComponentString(TextFormatting.GREEN + "Region removed with success!"));
					} else {
						sender.sendMessage(new TextComponentString(
								TextFormatting.RED + "Region not found for the selected coordinate."));
					}
				}
			}

		});
	}

	private int processCoordInput(String input, int axisValue, int sumValue) {

		if (input.equals("~")) {
			return Integer.parseInt("" + axisValue);
		} else if (input.equals("-~")) {
			return Integer.parseInt("" + -axisValue);
		} else if (input.equals("~" + sumValue)) {
			return Integer.parseInt("" + (sumValue + axisValue));
		}

		return Integer.parseInt(input);
	}

}
