package com.nidefawl.Stats.ItemResolver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import com.nidefawl.Stats.Stats;

public class hModItemResolver implements itemResolver {
	static final Logger log = Logger.getLogger("Minecraft");
	protected Map<String, Integer> items;
	String location = null;
	

	public hModItemResolver(File itemsFile) {
		location = itemsFile.getPath();
		loadItems(itemsFile);
	}

	public void loadItems(File itemsFile) {
		if (!itemsFile.exists()) {
			FileWriter writer = null;
			try {
				writer = new FileWriter(location);
				writer.write("#Add your items in here (When adding your entry DO NOT include #!)\r\n");
				writer.write("#The format is:\r\n");
				writer.write("#NAME:ID\r\n");
				writer.write("#Default Items:\r\n");
				writer.write("air:0\r\n");
				writer.write("rock:1\r\n");
				writer.write("stone:1\r\n");
				writer.write("grass:2\r\n");
				writer.write("dirt:3\r\n");
				writer.write("cobblestone:4\r\n");
				writer.write("cobble:4\r\n");
				writer.write("wood:5\r\n");
				writer.write("sapling:6\r\n");
				writer.write("adminium:7\r\n");
				writer.write("bedrock:7\r\n");
				writer.write("water:8\r\n");
				writer.write("stillwater:9\r\n");
				writer.write("swater:9\r\n");
				writer.write("lava:10\r\n");
				writer.write("stilllava:11\r\n");
				writer.write("slava:11\r\n");
				writer.write("sand:12\r\n");
				writer.write("gravel:13\r\n");
				writer.write("goldore:14\r\n");
				writer.write("ironore:15\r\n");
				writer.write("coalore:16\r\n");
				writer.write("tree:17\r\n");
				writer.write("log:17\r\n");
				writer.write("leaves:18\r\n");
				writer.write("sponge:19\r\n");
				writer.write("glass:20\r\n");
				writer.write("lazuliore:21\r\n");
				writer.write("lapislazuliore:21\r\n");
				writer.write("lazuliblock:22\r\n");
				writer.write("lapislazuliblock:22\r\n");
				writer.write("dispenser:23\r\n");
				writer.write("sandstone:24\r\n");
				writer.write("musicblock:25\r\n");
				writer.write("noteblock:25\r\n");
				writer.write("bedblock:26\r\n");
				writer.write("wool:35\r\n");
				writer.write("cloth:35\r\n");
				writer.write("flower:37\r\n");
				writer.write("rose:38\r\n");
				writer.write("brownmushroom:39\r\n");
				writer.write("redmushroom:40\r\n");
				writer.write("gold:41\r\n");
				writer.write("goldblock:41\r\n");
				writer.write("iron:42\r\n");
				writer.write("ironblock:42\r\n");
				writer.write("doublestair:43\r\n");
				writer.write("stair:44\r\n");
				writer.write("step:44\r\n");
				writer.write("brickblock:45\r\n");
				writer.write("brickwall:45\r\n");
				writer.write("tnt:46\r\n");
				writer.write("bookshelf:47\r\n");
				writer.write("bookcase:47\r\n");
				writer.write("mossycobblestone:48\r\n");
				writer.write("mossy:48\r\n");
				writer.write("obsidian:49\r\n");
				writer.write("torch:50\r\n");
				writer.write("fire:51\r\n");
				writer.write("mobspawner:52\r\n");
				writer.write("woodstairs:53\r\n");
				writer.write("chest:54\r\n");
				writer.write("redstonedust:55\r\n");
				writer.write("redstonewire:55\r\n");
				writer.write("diamondore:56\r\n");
				writer.write("diamondblock:57\r\n");
				writer.write("workbench:58\r\n");
				writer.write("crop:59\r\n");
				writer.write("crops:59\r\n");
				writer.write("soil:60\r\n");
				writer.write("furnace:61\r\n");
				writer.write("litfurnace:62\r\n");
				writer.write("signblock:63\r\n");
				writer.write("wooddoorblock:64\r\n");
				writer.write("ladder:65\r\n");
				writer.write("rails:66\r\n");
				writer.write("rail:66\r\n");
				writer.write("track:66\r\n");
				writer.write("tracks:66\r\n");
				writer.write("cobblestonestairs:67\r\n");
				writer.write("stairs:67\r\n");
				writer.write("signblocktop:68\r\n");
				writer.write("wallsign:68\r\n");
				writer.write("lever:69\r\n");
				writer.write("rockplate:70\r\n");
				writer.write("stoneplate:70\r\n");
				writer.write("irondoorblock:71\r\n");
				writer.write("woodplate:72\r\n");
				writer.write("redstoneore:73\r\n");
				writer.write("redstoneorealt:74\r\n");
				writer.write("redstonetorchoff:75\r\n");
				writer.write("redstonetorchon:76\r\n");
				writer.write("button:77\r\n");
				writer.write("snow:78\r\n");
				writer.write("ice:79\r\n");
				writer.write("snowblock:80\r\n");
				writer.write("cactus:81\r\n");
				writer.write("clayblock:82\r\n");
				writer.write("reedblock:83\r\n");
				writer.write("jukebox:84\r\n");
				writer.write("fence:85\r\n");
				writer.write("pumpkin:86\r\n");
				writer.write("netherstone:87\r\n");
				writer.write("slowsand:88\r\n");
				writer.write("lightstone:89\r\n");
				writer.write("portal:90\r\n");
				writer.write("jackolantern:91\r\n");
				writer.write("jacko:91\r\n");
				writer.write("cakeblock:92\r\n");
				writer.write("repeateron:93\r\n");
				writer.write("repeateroff:94\r\n");
				writer.write("ironshovel:256\r\n");
				writer.write("ironspade:256\r\n");
				writer.write("ironpickaxe:257\r\n");
				writer.write("ironpick:257\r\n");
				writer.write("ironaxe:258\r\n");
				writer.write("flintandsteel:259\r\n");
				writer.write("lighter:259\r\n");
				writer.write("apple:260\r\n");
				writer.write("bow:261\r\n");
				writer.write("arrow:262\r\n");
				writer.write("coal:263\r\n");
				writer.write("diamond:264\r\n");
				writer.write("ironbar:265\r\n");
				writer.write("goldbar:266\r\n");
				writer.write("ironsword:267\r\n");
				writer.write("woodsword:268\r\n");
				writer.write("woodshovel:269\r\n");
				writer.write("woodspade:269\r\n");
				writer.write("woodpickaxe:270\r\n");
				writer.write("woodpick:270\r\n");
				writer.write("woodaxe:271\r\n");
				writer.write("stonesword:272\r\n");
				writer.write("stoneshovel:273\r\n");
				writer.write("stonespade:273\r\n");
				writer.write("stonepickaxe:274\r\n");
				writer.write("stonepick:274\r\n");
				writer.write("stoneaxe:275\r\n");
				writer.write("diamondsword:276\r\n");
				writer.write("diamondshovel:277\r\n");
				writer.write("diamondspade:277\r\n");
				writer.write("diamondpickaxe:278\r\n");
				writer.write("diamondpick:278\r\n");
				writer.write("diamondaxe:279\r\n");
				writer.write("stick:280\r\n");
				writer.write("bowl:281\r\n");
				writer.write("bowlwithsoup:282\r\n");
				writer.write("soupbowl:282\r\n");
				writer.write("soup:282\r\n");
				writer.write("goldsword:283\r\n");
				writer.write("goldshovel:284\r\n");
				writer.write("goldspade:284\r\n");
				writer.write("goldpickaxe:285\r\n");
				writer.write("goldpick:285\r\n");
				writer.write("goldaxe:286\r\n");
				writer.write("string:287\r\n");
				writer.write("feather:288\r\n");
				writer.write("gunpowder:289\r\n");
				writer.write("woodhoe:290\r\n");
				writer.write("stonehoe:291\r\n");
				writer.write("ironhoe:292\r\n");
				writer.write("diamondhoe:293\r\n");
				writer.write("goldhoe:294\r\n");
				writer.write("seeds:295\r\n");
				writer.write("wheat:296\r\n");
				writer.write("bread:297\r\n");
				writer.write("leatherhelmet:298\r\n");
				writer.write("leatherchestplate:299\r\n");
				writer.write("leatherpants:300\r\n");
				writer.write("leatherboots:301\r\n");
				writer.write("chainmailhelmet:302\r\n");
				writer.write("chainmailchestplate:303\r\n");
				writer.write("chainmailpants:304\r\n");
				writer.write("chainmailboots:305\r\n");
				writer.write("ironhelmet:306\r\n");
				writer.write("ironchestplate:307\r\n");
				writer.write("ironpants:308\r\n");
				writer.write("ironboots:309\r\n");
				writer.write("diamondhelmet:310\r\n");
				writer.write("diamondchestplate:311\r\n");
				writer.write("diamondpants:312\r\n");
				writer.write("diamondboots:313\r\n");
				writer.write("goldhelmet:314\r\n");
				writer.write("goldchestplate:315\r\n");
				writer.write("goldpants:316\r\n");
				writer.write("goldboots:317\r\n");
				writer.write("flint:318\r\n");
				writer.write("meat:319\r\n");
				writer.write("pork:319\r\n");
				writer.write("cookedmeat:320\r\n");
				writer.write("cookedpork:320\r\n");
				writer.write("painting:321\r\n");
				writer.write("paintings:321\r\n");
				writer.write("goldenapple:322\r\n");
				writer.write("sign:323\r\n");
				writer.write("wooddoor:324\r\n");
				writer.write("bucket:325\r\n");
				writer.write("waterbucket:326\r\n");
				writer.write("lavabucket:327\r\n");
				writer.write("minecart:328\r\n");
				writer.write("saddle:329\r\n");
				writer.write("irondoor:330\r\n");
				writer.write("redstonedust:331\r\n");
				writer.write("snowball:332\r\n");
				writer.write("boat:333\r\n");
				writer.write("leather:334\r\n");
				writer.write("milkbucket:335\r\n");
				writer.write("brick:336\r\n");
				writer.write("clay:337\r\n");
				writer.write("reed:338\r\n");
				writer.write("paper:339\r\n");
				writer.write("book:340\r\n");
				writer.write("slimeorb:341\r\n");
				writer.write("storageminecart:342\r\n");
				writer.write("poweredminecart:343\r\n");
				writer.write("egg:344\r\n");
				writer.write("compass:345\r\n");
				writer.write("fishingrod:346\r\n");
				writer.write("watch:347\r\n");
				writer.write("lightstonedust:348\r\n");
				writer.write("lightdust:348\r\n");
				writer.write("rawfish:349\r\n");
				writer.write("fish:349\r\n");
				writer.write("cookedfish:350\r\n");
				writer.write("inksac:351\r\n");
				writer.write("bone:352\r\n");
				writer.write("sugar:353\r\n");
				writer.write("cake:354\r\n");
				writer.write("bed:355\r\n");
				writer.write("repeater:356\r\n");
				writer.write("goldrecord:2256\r\n");
				writer.write("greenrecord:2257\r\n");
			} catch (Exception e) {
				Stats.LogError("Exception while creating " + location + " " + e);
				e.printStackTrace();
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						Stats.LogError("Exception while closing writer for " + location + " " + e);
						e.printStackTrace();
					}
				}
			}
		}
		items = new HashMap<String, Integer>();
		try {
			Scanner scanner = new Scanner(itemsFile);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.startsWith("#")) {
					continue;
				}
				if (line.equals("")) {
					continue;
				}
				String[] split = line.split(":");
				String name = split[0];

				this.items.put(name, Integer.parseInt(split[1]));
			}
			scanner.close();
		} catch (Exception e) {
			Stats.LogError("Exception while reading " + location + " (Are you sure you formatted it correctly?)"+ e);
			e.printStackTrace();
		}
	}


	@Override
	public int getItem(String name) {
		if (items.containsKey(name)) {
			return items.get(name);
		}
		try {
			int i = Integer.valueOf(name);
			if(i>0 && i < 3000)  {
				if(!getItem(i).equals(name)) {
					return i;
				}
			}
		} catch (Exception e) {
		}
		return 0;
	}


	@Override
	public String getItem(int id) {
		for (String name : items.keySet()) {
			if (items.get(name) == id) {
				return name;
			}
		}
		return String.valueOf(id);
	}

}