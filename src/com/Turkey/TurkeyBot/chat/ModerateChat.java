package com.Turkey.TurkeyBot.chat;

import java.util.ArrayList;
import java.util.List;

import com.Turkey.TurkeyBot.TurkeyBot;

public class ModerateChat
{
	private TurkeyBot bot;

	private String[] message;

	private String[] blackList;

	private List<String> emotes = new ArrayList<String>();

	public ModerateChat(TurkeyBot b)
	{
		bot = b;

		blackList = bot.chatSettings.getSetting("WordBlackList").split(",");
	}

	/**
	 * Returns if the given message is valid to be said in chat or if the chat message should be filtered
	 * @param message The chat message to parse through.
	 * @param sender the username that sent the message
	 * @return if the message is ok or false if it should be filtered
	 */
	public boolean isValidChat(String m, String sender, String perm)
	{
		message = m.split(" ");
		if(perm.equalsIgnoreCase("Mod") || bot.checkForImmunity(sender))
		{
			return true;
		}

		ErrorType error;
		if((error = passesWordCheck()) != ErrorType.None)
		{
			if(error == ErrorType.Caps)
				bot.sendMessage(bot.spamResponseFile.getSetting("CapsMessage"));
			if(error == ErrorType.Length)
				bot.sendMessage(bot.spamResponseFile.getSetting("LengthMessage"));
			if(error == ErrorType.BlockedWord)
				bot.sendMessage(bot.spamResponseFile.getSetting("BlockedWordMessage"));
			if(error == ErrorType.Emotes)
				bot.sendMessage(bot.spamResponseFile.getSetting("EmotesMessage"));
			return false;
		}

		return true;
	}

	/**
	 * Parses through the words of the message and checks for any flags in the chat message.
	 * Checks for Caps, length, Blacklisted words/ Astrixs, Emotes and Symbols.
	 * @return The Error Type of the chat message. ErrorType.None if no flag is raised.
	 */
	public ErrorType passesWordCheck()
	{
		//TODO: Fix emotes check as not all emotes are checked for.
		int caps = 0;
		int letters = 0;
		int symbols = 0;
		int charecters = 0;
		int numofemotes = 0;
		for(String word: message)
		{
			if(word.equalsIgnoreCase("***"))
			{
				return ErrorType.BlockedWord;
			}
			if(emotes.contains(word))
				numofemotes++;
			for(char letter: word.toCharArray())
			{
				if(letter >= 65 && letter <= 90)
					caps++;
				if((letter >= 65 && letter <= 90) || (letter >= 97 && letter <= 122))
				{
					letters++;
				}
				else
				{
					symbols++;
				}
				charecters++;
			}
		}
		int capsMax = Integer.parseInt(bot.chatSettings.getSetting("MaxCaps"));
		int capsMin = Integer.parseInt(bot.chatSettings.getSetting("MinimumCaps"));
		int capsPercent = Integer.parseInt(bot.chatSettings.getSetting("MaxpercentofCaps"));
		if(capsMax != -1 && caps > capsMax)
			return ErrorType.Caps;
		if(( capsMin != -1 && caps > capsMin) && (capsPercent != -1 && (((double) caps / (double)letters)*100) > capsPercent))
			return ErrorType.Caps;

		
		int emotesMax = Integer.parseInt(bot.chatSettings.getSetting("MaxEmotes"));
		int emotesMin = Integer.parseInt(bot.chatSettings.getSetting("MinimumEmotes"));
		int emotesPercent = Integer.parseInt(bot.chatSettings.getSetting("MaxpercentofEmotes"));

		if(emotesMax != -1 && numofemotes > emotesMax)
			return ErrorType.Emotes;
		if(( emotesMin != -1 && numofemotes > emotesMin) && (emotesPercent != -1 && (((double) numofemotes / (double)message.length)*100) > emotesPercent))
			return ErrorType.Emotes;
		
		if(letters > Integer.parseInt(bot.chatSettings.getSetting("MaxMessageLength")))
			return ErrorType.Length;
		
		int symbolsMax = Integer.parseInt(bot.chatSettings.getSetting("MaxSymbols"));
		int symbolsMin = Integer.parseInt(bot.chatSettings.getSetting("MinimumSymbols"));
		int symbolsPercent = Integer.parseInt(bot.chatSettings.getSetting("MaxpercentofSymbols"));
		
		if(symbolsMax != -1 && symbols > symbolsMax)
			return ErrorType.Sybols;
		if(( symbolsMin != -1 && symbols > symbolsMin) && (symbolsPercent != -1 && (((double) symbols / (double)charecters)*100) > symbolsPercent))
			return ErrorType.Sybols;
		
		return ErrorType.None;
	}

	/**
	 * Checks if the given word is on the black list.
	 * @param tocheck The word to check against the black list.
	 * @return If the word is black listed or not.
	 */
	public boolean isBlackListed(String tocheck)
	{
		for(String blackword: blackList)
		{
			if(blackword.equalsIgnoreCase(tocheck))
				return true;
		}
		return false;
	}

	/**
	 * Possible error Types on a given chat message
	 *
	 */
	public enum ErrorType
	{
		Caps,
		Link,
		Length,
		BlockedWord,
		Sybols,
		Emotes,
		None;
	}
}
