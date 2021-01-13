package vazkii.ambience.commands;

import java.util.ArrayList;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
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

public class CreateAreaCommand {

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
		final LiteralArgumentBuilder<CommandSource> root = Commands.literal("area");
		
		root.then(Commands.literal("help").executes(source -> {
					return helpArea(source.getSource(), source.getSource().asPlayer());
		}));
		
		root.then(Commands.literal("list").executes(source -> {
			return listArea(source.getSource(), source.getSource().asPlayer());
		}));
		
		root.then(Commands.literal("showInfo").executes(source -> {
			return showInfoArea(source.getSource(), source.getSource().asPlayer());
		}));

		root.then(Commands.argument("areaName", StringArgumentType.string()).suggests(SUGGEST_AREA)
				.then(Commands.argument("position1", BlockPosArgument.blockPos())
				.then(Commands.argument("position2", BlockPosArgument.blockPos())
				.then(Commands.argument("playInstant", BoolArgumentType.bool())
				.then(Commands.argument("playAtNight", BoolArgumentType.bool()).executes(source -> {
							return createArea(source.getSource(), StringArgumentType.getString(source, "areaName"), source.getSource().asPlayer(), BlockPosArgument.getBlockPos(source, "position1"), BlockPosArgument.getBlockPos(source, "position2"),BoolArgumentType.getBool(source, "playInstant"),BoolArgumentType.getBool(source, "playAtNight"));
						}))))));
		
		dispatcher.register(root);
	}

	public static int createArea(CommandSource source,String areaName, PlayerEntity player, BlockPos pos1, BlockPos pos2, Boolean playInstant,Boolean playAtNight) {
												
		if(pos1.equals(pos2)) {
			source.sendFeedback(new TranslationTextComponent(TextFormatting.RED+"The coordinates can't be the same"), true);
			return 0;
		}
		
		Area currentArea=new Area();		
		currentArea.setOperation(Operation.CREATE);
		currentArea.setName(areaName);
		currentArea.setPlayAtNight(playAtNight);
		currentArea.setInstantPlay(playInstant);
		currentArea.setID(Ambience.getWorldData().listAreas.size());
		currentArea.setDimension(player.world.getDimensionKey().getLocation().getPath());
		currentArea.setPos1(new Vector3d(pos1.getX(),pos1.getY(),pos1.getZ()));
		currentArea.setPos2(new Vector3d(pos2.getX(),pos2.getY(),pos2.getZ()));
		//AmbiencePackageHandler.sendToServer(new MyMessage(currentArea.SerializeThis()));
		Ambience.sync = true;
		
		WorldData data = new WorldData().GetArasforWorld((ServerWorld) player.world);
		CompoundNBT updatedAreas=null;
		data.addArea(currentArea);
		updatedAreas = WorldData.SerializeThis(Ambience.getWorldData().listAreas);
		AmbiencePackageHandler.sendToAll(new MyMessage(updatedAreas));	
		data.saveData();
		Ambience.getWorldData().listAreas = data.listAreas;
		
		source.sendFeedback(new TranslationTextComponent(TextFormatting.GREEN + "Region created with success!"), true);
		return 1;
	}
	
	
	public static int helpArea(CommandSource source, PlayerEntity player) {
		source.sendFeedback(new TranslationTextComponent(TextFormatting.GREEN+"<--- Area Commands --->"), true);
		source.sendFeedback(new TranslationTextComponent(TextFormatting.GREEN+"/area -> creates a new region."), true);
		source.sendFeedback(new TranslationTextComponent(TextFormatting.GREEN+"/area list -> show the information for all the regions created."), true);
		source.sendFeedback(new TranslationTextComponent(TextFormatting.GREEN+"/area showInfo -> show the region informations for the current player standing region."), true);
		source.sendFeedback(new TranslationTextComponent(TextFormatting.GREEN+"/areadel -> delete a region"), true);
		source.sendFeedback(new TranslationTextComponent(TextFormatting.GREEN+"/areaedit -> edit a region"), true);
		source.sendFeedback(new TranslationTextComponent(TextFormatting.GREEN+"<--------------------->"), true);
		return 1;
	}
	
	public static int listArea(CommandSource source, PlayerEntity player) {
		source.sendFeedback(new TranslationTextComponent(TextFormatting.WHITE+"<-------------------------------->"), true);	
		for (Area area : Ambience.getWorldData().listAreas) {					
			source.sendFeedback(new TranslationTextComponent(TextFormatting.YELLOW + "Name: "+area.getName() + " ID: "+ area.getID() + " P1:" + area.getPos1() + " P2:" + area.getPos2()), true);			
		}
		source.sendFeedback(new TranslationTextComponent(TextFormatting.WHITE+"<-------------------------------->"), true);
		return 1;
	}
	
	public static int showInfoArea(CommandSource source, PlayerEntity player) {
		Area area = Area.getBlockStandingArea(player.getPosition());
		
		if(area!=null) 
		{
			source.sendFeedback(new TranslationTextComponent(TextFormatting.WHITE+"<-------------------------------->"), true);
			source.sendFeedback(new TranslationTextComponent(TextFormatting.YELLOW + "ID: "+area.getID()), true);
			source.sendFeedback(new TranslationTextComponent(TextFormatting.YELLOW + "Name: "+area.getName()), true);
			source.sendFeedback(new TranslationTextComponent(TextFormatting.YELLOW + "Pos1: "+area.getPos1()), true);
			source.sendFeedback(new TranslationTextComponent(TextFormatting.YELLOW + "Pos2: "+area.getPos2()), true);
			source.sendFeedback(new TranslationTextComponent(TextFormatting.YELLOW + "Dimension: "+area.getDimension()), true);
			source.sendFeedback(new TranslationTextComponent(TextFormatting.YELLOW + "Instant Play: "+area.isInstantPlay()), true);
			source.sendFeedback(new TranslationTextComponent(TextFormatting.YELLOW + "Play at night: "+area.isPlayatNight()), true);
			source.sendFeedback(new TranslationTextComponent(TextFormatting.WHITE+"<-------------------------------->"), true);
		}else {
			source.sendFeedback(new TranslationTextComponent(TextFormatting.RED + "Must be inside a Region to show the informations"),true);
		}

		return 1;
	}
}
