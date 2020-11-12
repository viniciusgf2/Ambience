package vazkii.ambience.commands;

import java.util.ArrayList;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import vazkii.ambience.Ambience;
import vazkii.ambience.Util.WorldData;
import vazkii.ambience.World.Biomes.Area;
import vazkii.ambience.World.Biomes.Area.Operation;
import vazkii.ambience.network.AmbiencePackageHandler;
import vazkii.ambience.network.MyMessage;

public class UpdateAreaCommand {

	private static final SuggestionProvider<CommandSource> SUGGEST_AREA = (source, builder) -> {
		
		ArrayList<String> AreasNames = new ArrayList<String>();
		for (Area entry : Ambience.getWorldData().listAreas) {
			
			if (!entry.getName().contains(".")) {
				AreasNames.add(entry.getName());
			}
		}

		return ISuggestionProvider.suggest(AreasNames, builder);
	};
	
	public static void register(CommandDispatcher<CommandSource> dispatcher) 
	{		
		final LiteralArgumentBuilder<CommandSource> root = Commands.literal("areaedit");
		
		root.then(Commands.argument("areaID",IntegerArgumentType.integer())
				.then(Commands.argument("areaName", StringArgumentType.string()).suggests(SUGGEST_AREA)
				.then(Commands.argument("position1", BlockPosArgument.blockPos())
				.then(Commands.argument("position2", BlockPosArgument.blockPos())
				.then(Commands.argument("playInstant", BoolArgumentType.bool())
				.then(Commands.argument("playAtNight", BoolArgumentType.bool()).executes(source -> {
							return editAreaByID(source.getSource(), source.getSource().asPlayer(), IntegerArgumentType.getInteger(source, "areaID"),StringArgumentType.getString(source, "areaName"), BlockPosArgument.getBlockPos(source, "position1"), BlockPosArgument.getBlockPos(source, "position2"),BoolArgumentType.getBool(source, "playInstant"),BoolArgumentType.getBool(source, "playAtNight"));
						})))))));
		
		root.then(Commands.argument("oldPosition",BlockPosArgument.blockPos())
				.then(Commands.argument("areaName", StringArgumentType.string()).suggests(SUGGEST_AREA)
				.then(Commands.argument("position1", BlockPosArgument.blockPos())
				.then(Commands.argument("position2", BlockPosArgument.blockPos())
				.then(Commands.argument("playInstant", BoolArgumentType.bool())
				.then(Commands.argument("playAtNight", BoolArgumentType.bool()).executes(source -> {
							return editAreaByPosition(source.getSource(), source.getSource().asPlayer(), BlockPosArgument.getBlockPos(source, "oldPosition"),StringArgumentType.getString(source, "areaName"), BlockPosArgument.getBlockPos(source, "position1"), BlockPosArgument.getBlockPos(source, "position2"),BoolArgumentType.getBool(source, "playInstant"),BoolArgumentType.getBool(source, "playAtNight"));
						})))))));
				
		dispatcher.register(root);
	}

	public static int editAreaByID(CommandSource source, PlayerEntity player, int areaID,String areaName, BlockPos pos1, BlockPos pos2, Boolean playInstant,Boolean playAtNight ) {
		
		Area currentArea = null;
		currentArea = Area.getAreabyID(areaID);
		
		if (currentArea != null) {
			currentArea.setOperation(Operation.EDIT);
			
			currentArea.setName(areaName);
			currentArea.setPlayAtNight(playAtNight);
			currentArea.setInstantPlay(playInstant);
			currentArea.setPos1(new Vector3d(pos1.getX(),pos1.getY(),pos1.getZ()));
			currentArea.setPos2(new Vector3d(pos2.getX(),pos2.getY(),pos2.getZ()));
		//	AmbiencePackageHandler.sendToServer(new MyMessage(currentArea.SerializeThis()));
			Ambience.sync = true;

			CompoundNBT updatedAreas=null;
			WorldData data = new WorldData().GetArasforWorld((ServerWorld) player.world);
			data.editArea(currentArea);				
			updatedAreas = WorldData.SerializeThis(Ambience.getWorldData().listAreas);
			AmbiencePackageHandler.sendToAll(new MyMessage(updatedAreas));
			data.saveData();
			Ambience.getWorldData().listAreas = data.listAreas;
			
			source.sendFeedback(new TranslationTextComponent(TextFormatting.GREEN + "Region updated with success!"), true);
			
			return 1;
		} else {
			source.sendFeedback(new TranslationTextComponent(TextFormatting.RED + "Region not found for the ID '" + areaID + "'"), true);

			return 0;
		}
	}
	
	public static int editAreaByPosition(CommandSource source, PlayerEntity player, BlockPos pos,String areaName, BlockPos pos1, BlockPos pos2, Boolean playInstant,Boolean playAtNight ) {
		
		Area currentArea = null;
	    currentArea = Area.getBlockStandingArea(pos);
		
		if (currentArea != null) {
			currentArea.setOperation(Operation.EDIT);
			
			currentArea.setName(areaName);
			currentArea.setPlayAtNight(playAtNight);
			currentArea.setInstantPlay(playInstant);
			currentArea.setPos1(new Vector3d(pos1.getX(),pos1.getY(),pos1.getZ()));
			currentArea.setPos2(new Vector3d(pos2.getX(),pos2.getY(),pos2.getZ()));
			//AmbiencePackageHandler.sendToServer(new MyMessage(currentArea.SerializeThis()));
			Ambience.sync = true;
			
			CompoundNBT updatedAreas=null;
			WorldData data = new WorldData().GetArasforWorld((ServerWorld) player.world);
			data.editArea(currentArea);				
			updatedAreas = WorldData.SerializeThis(Ambience.getWorldData().listAreas);
			AmbiencePackageHandler.sendToAll(new MyMessage(updatedAreas));
			data.saveData();
			Ambience.getWorldData().listAreas = data.listAreas;
			
			source.sendFeedback(new TranslationTextComponent(TextFormatting.GREEN + "Region updated with success!"), true);
			
			return 1;
		} else {
			source.sendFeedback(new TranslationTextComponent(TextFormatting.RED + "Region not found."), true);

			return 0;
		}
	}
	
}
