package com.nidefawl.Stats;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class MinecraftFontWidthCalculator {
	private static String charWidthIndexIndex = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_'abcdefghijklmnopqrstuvwxyz{|}~‚åÇ√á√º√©√¢√§√†√•√ß√™√´√®√Ø√Æ√¨√Ñ√Ö√â√¶√Ü√¥√∂√≤√ª√π√ø√ñ√ú√�?¬£√ò√ó∆í√°√≠√≥√∫√±√ë¬™¬∫¬ø¬Æ¬¨¬Ω¬º¬°¬´¬ª";
	private static int[] charWidths = { 4, 2, 5, 6, 6, 6, 6, 3, 5, 5, 5, 6, 2, 6, 2, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 2, 2, 5, 6, 5, 6, 7, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 4, 6, 6, 3, 6, 6, 6, 6, 6, 5, 6, 6, 2, 6, 5, 3, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 5, 2,
			5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 3, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 3, 6, 6, 6, 6, 6, 6, 6, 7, 6, 6, 6, 2, 6, 6, 8, 9, 9, 6, 6, 6, 8, 8, 6, 8, 8, 8, 8, 8, 6, 6, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 6, 9, 9, 9, 5, 9, 9, 8, 7, 7,
			8, 7, 8, 8, 8, 7, 8, 8, 7, 9, 9, 6, 7, 7, 7, 7, 7, 9, 6, 7, 8, 7, 6, 6, 9, 7, 6, 7, 1 };

	public static int getStringWidth(CommandSender sender, String s) {
		int i = 0;
		if (s != null) {
			if(sender instanceof ConsoleCommandSender) {
				return s.length()*5;
			}
			for (int j = 0; j < s.length(); j++)
				i += getCharWidth(s.charAt(j));
		}
		return i;
	}

	public static int getCharWidth(char c) {
		int k = charWidthIndexIndex.indexOf(c);
		if ((c != '�') && (k >= 0))
			return charWidths[k];
		return 0;
	}
}

/*
 * Location: C:\Users\Michael\AppData\Local\Temp\Rar$DR01.151\MyWarp.jar
 * Qualified Name:
 * org.angelsl.minecraft.randomshit.fontwidth.MinecraftFontWidthCalculator
 * JD-Core Version: 0.6.0
 */