package vazkii.ambience;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
public final class SongLoader {

	public static File mainDir;
	public static boolean enabled = false;
	
	
	public static void loadFrom(File f) {
		File config = new File(f, "ambience.properties");
		if (!config.exists())
			initConfig(config);

		Properties props = new Properties();
		try {

			props.load(new FileReader(config));
			
			//Adds the Notification propertie to the ambience.properties file if the player don't have this 
			if(props.getProperty("ShowUpdateNotifications") ==null) 
			{								
				try {
					
					int count=0;
					String data="";
					Scanner myReader = new Scanner(config);
				      while (myReader.hasNextLine()) {
				        data += myReader.nextLine()+"\n";
				        
				        if(count++==3) {
				        	data+="#Enables or disables the notification in the chat that has updates(default=false)\nShowUpdateNotifications=false\n\n";
				        }
				      }
				      myReader.close();
				      
				    BufferedWriter writer = new BufferedWriter(new FileWriter(config));	
				    writer.write(data);
					writer.close();
					
					props.load(new FileReader(config));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			

			enabled = props.getProperty("enabled").equals("true");
			Ambience.showUpdateNotification = props.getProperty("ShowUpdateNotifications").equals("true");
				
			
			if (enabled) {
				//SongPicker.reset();
				Set<Object> keys = props.keySet();
				for (Object obj : keys) {
					String s = (String) obj;
										
					String[] tokens = s.split("\\.");
					if (tokens.length < 2)
						continue;

					String keyType = tokens[0];
					int dimID = tryParse(tokens[1], 0);
					if (keyType.equals("event")) {
						String event = tokens[1];

						SongPicker.eventMap.put(event, props.getProperty(s).split(","));
					} else if (keyType.equals("biome")) {
						String biomeName = joinTokensExceptFirst(tokens).replaceAll("\\+", " ");
						Biome biome = BiomeMapper.getBiome(biomeName);

					if (biome != null)
							SongPicker.biomeMap.put(biome,props.getProperty(s).split(","));
					} else if (keyType.equals("area")) {
						//String event = tokens[1];
						//SongPicker.areasMap.put(event, props.getProperty(s).split(","));
						
						String event = "";
						
						if(tokens.length>2) 							
						{
							//event=tokens[2];
							//SongPicker.eventMap.put(event+"\\"+ dimID, props.getProperty(s).split(","));
							
							event = tokens[2];
							SongPicker.areasMap.put(tokens[1]+"."+event, props.getProperty(s).split(","));
						}
						else
						{
							event = tokens[1];
							SongPicker.areasMap.put(event, props.getProperty(s).split(","));
						}
						
					} else if (keyType.equals("mob")) {
						String event = tokens[1];						
						String mobName = joinTokensExceptFirst(tokens).replaceAll("\\+", " ");
						
						SongPicker.mobMap.put(mobName, props.getProperty(s).split(","));
					}else if (keyType.equals("dimension")) {
						String event = "";
						
						if(tokens.length>2) 							
						{
							event=tokens[2];
							SongPicker.eventMap.put(event+"\\"+ dimID, props.getProperty(s).split(","));
						}
						else
						{
							SongPicker.eventMap.put("dim" + dimID, props.getProperty(s).split(","));
						}
					}

					else if (keyType.matches("primarytag|secondarytag")) {
						boolean primary = keyType.equals("primarytag");
						String tagName = tokens[1].toUpperCase();
						BiomeDictionary.Type type = BiomeMapper.getBiomeType(tagName);

						if (type != null) {
							if (primary)
								SongPicker.primaryTagMap.put(type, props.getProperty(s).split(","));
							else
								SongPicker.secondaryTagMap.put(type, props.getProperty(s).split(","));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		File musicDir = new File(f, "music");
		if (!musicDir.exists())
			musicDir.mkdir();

		mainDir = musicDir;
	}

	public static void initConfig(File f) {
		try {
			f.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			writer.write("# Ambience Config\n");
			writer.write("enabled=false");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static InputStream getStream() {
		if(Ambience.thread!=null) {
			if (PlayerThread.currentSong == null || PlayerThread.currentSong.equals("null"))
				return null;
		}
		else
			return null;

		File f = new File(mainDir, PlayerThread.currentSong + ".mp3");
		if (f.getName().equals("null.mp3"))
			return null;

		try {
			return new FileInputStream(f);
		} catch (FileNotFoundException e) {
			Ambience.LOGGER.error("File " + f + " not found. Fix your Ambience config!");		
			e.printStackTrace();
			return null;
		}
	}

	private static String joinTokensExceptFirst(String[] tokens) {
		String s = "";
		int i = 0;
		for (String token : tokens) {
			i++;
			if (i == 1)
				continue;
			s += token;
		}
		return s;
	}
	
	private static int tryParse(String value, int defaultVal) {
	    try {
	        return Integer.parseInt(value);
	    } catch (NumberFormatException e) {
	        return defaultVal;
	    }
	}
}
