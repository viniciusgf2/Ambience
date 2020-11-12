package vazkii.ambience.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import vazkii.ambience.Ambience;
import vazkii.ambience.Util.WorldData;
import vazkii.ambience.World.Biomes.Area;
import vazkii.ambience.World.Biomes.Area.Operation;
import vazkii.ambience.network.AmbiencePackageHandler;
import vazkii.ambience.network.MyMessage;

public class DeleteAreaCommand {

	public static void register(CommandDispatcher<CommandSource> dispatcher) 
	{		
		final LiteralArgumentBuilder<CommandSource> root = Commands.literal("areadel");
		
		root.then(Commands.argument("areaID",IntegerArgumentType.integer()).executes(source -> {
					return deleteAreabyID(source.getSource(), source.getSource().asPlayer(), IntegerArgumentType.getInteger(source, "areaID"));
		}));
		
		root.then(Commands.argument("position", BlockPosArgument.blockPos()).executes(source -> {
			return deleteAreabyPosition(source.getSource(), source.getSource().asPlayer(), BlockPosArgument.getBlockPos(source, "position"));
						}));
		
		dispatcher.register(root);
	}

	public static int deleteAreabyID(CommandSource source, PlayerEntity player, int areaID) {
		
		Area currentArea = null;
		currentArea = Area.getAreabyID(areaID);
		
		if (currentArea != null) {
			currentArea.setOperation(Operation.DELETE);
			//AmbiencePackageHandler.sendToServer(new MyMessage(currentArea.SerializeThis()));			
			Ambience.sync = true;
			
			CompoundNBT updatedAreas=null;
			WorldData data = new WorldData().GetArasforWorld((ServerWorld) player.world);		
			data.removeArea(currentArea);
			updatedAreas = WorldData.SerializeThis(Ambience.getWorldData().listAreas);
			AmbiencePackageHandler.sendToAll(new MyMessage(updatedAreas));
			data.saveData();
			Ambience.getWorldData().listAreas = data.listAreas;
			
			source.sendFeedback(new TranslationTextComponent(TextFormatting.GREEN + "Region removed with success!"), true);

			return 1;
		} else {
			source.sendFeedback(new TranslationTextComponent(TextFormatting.RED + "Region not found for the ID '" + areaID + "'"), true);

			return 0;
		}
	}
	
	public static int deleteAreabyPosition(CommandSource source, PlayerEntity player, BlockPos pos) {
		
		Area currentArea = null;
		currentArea = Area.getBlockStandingArea(pos);
		
		if (currentArea != null) {
			currentArea.setOperation(Operation.DELETE);
			//AmbiencePackageHandler.sendToServer(new MyMessage(currentArea.SerializeThis()));			
			Ambience.sync = true;

			CompoundNBT updatedAreas=null;
			WorldData data = new WorldData().GetArasforWorld((ServerWorld) player.world);		
			data.removeArea(currentArea);
			updatedAreas = WorldData.SerializeThis(Ambience.getWorldData().listAreas);
			AmbiencePackageHandler.sendToAll(new MyMessage(updatedAreas));
			data.saveData();
			Ambience.getWorldData().listAreas = data.listAreas;
			
			source.sendFeedback(new TranslationTextComponent(TextFormatting.GREEN + "Region removed with success!"), true);

			return 1;
		} else {
			source.sendFeedback(new TranslationTextComponent(TextFormatting.RED + "Region not found for the selected coordinate."), true);

			return 0;
		}
	}
	
}
