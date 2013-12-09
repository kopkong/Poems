package com.ck.poems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Poem {
	public int ID;
	public String Title;
	public String Author;
	public String Snapshot;
	public String Context;
	public String Genre;
	
	private ArrayList<String> linesContent;
	private HashMap<Integer,ArrayList<String>> lineOptionWords;
	private int lineCount;
	private String noPunctutationContext;
	
	final static int MAX_OPTION_WORDS = 14;
	
	// Init poem data for a quiz
	// Always call this method first if want to use this poem as a quiz.
	public void InitQuiz()
	{
		if(Context.length() > 0 )
		{
			linesContent = new ArrayList<String>();
			noPunctutationContext = "";
			String ch = "";
			String line = "";
			String puncutations ="，。！？";
			
			for(int i = 0; i < Context.length();i++)
			{
				ch = Context.substring(i,i+1);
				line += ch;
				
				if(puncutations.contains(ch))
				{
					// Push into Array and start a new line
					linesContent.add(line);
					
					// Remove last punctuation 
					noPunctutationContext += line.substring(0,line.length() - 1);
					
					// Clear line
					line = "";
				}
			}
		}
		
		// calculate the lines of poem
		lineCount = linesContent.size();
		
		// compute option words for every line
		InitOptionsWords();
	}
	
	private void InitOptionsWords()
	{
		if(lineCount <= 0)
			return;
		
		lineOptionWords = new HashMap<Integer,ArrayList<String>>();
				
		for(int rIndex = 0 ; rIndex<lineCount ; rIndex++)
		{
			// all words that have possibility to become one option
			String allOptionWords = "";
			
			// Store no random options
			String noRandomOptionWords = "";
			
			// Random options and store into final results
			ArrayList<String> finalOptionWords = new ArrayList<String>(MAX_OPTION_WORDS);
			
			// Every words in line text should be added in final Options
			String line = GetPoemLineText(rIndex);
			// Cut the punctuation
			String lineText = line.substring(0,line.length() - 1);
			
			noRandomOptionWords += lineText;
			
			// allOptionWords = all poems text - line text
			allOptionWords = noPunctutationContext.replace(lineText, "");
			
			// Calculate how many options are needed
			int remainedOptions = MAX_OPTION_WORDS - lineText.length();
			
			// Random class
			Random ran = new Random(19841231 + rIndex * System.currentTimeMillis());
			
			// Fill no random options
			for(int i = remainedOptions; i> 0; i--)
			{
				// Pick one random word from all options
				int num = ran.nextInt(allOptionWords.length() - 1);
				String pickedWord = allOptionWords.substring(num, num+1);
				noRandomOptionWords += pickedWord;
				
				// Remove the word just picked from all Options
				allOptionWords = allOptionWords.replace(pickedWord, "").trim();

			}
			
			// Random the options
			for(int i = 0; i<MAX_OPTION_WORDS;i++)
			{
				// If only one word left
				if(noRandomOptionWords.length()==1)
				{
					finalOptionWords.add(noRandomOptionWords);
				}
				else
				{
					int num = ran.nextInt(noRandomOptionWords.length() - 1);
					String pickedWord = noRandomOptionWords.substring(num, num+1);
					finalOptionWords.add(pickedWord);
					
					// Remove it from no random option list
					noRandomOptionWords = noRandomOptionWords.replace(pickedWord, "").trim();
				}
			}
			
			lineOptionWords.put(rIndex, finalOptionWords);
		}
	}
	
	public int GetPoemLineCount()
	{
		return lineCount;
	}
	
	public String GetPoemLineText(int lineIndex)
	{
		if(linesContent != null && linesContent.size() > 0 
			&& lineIndex >=0 && lineIndex < lineCount)
			return linesContent.get(lineIndex);
		
		return "";
	}
	
	public ArrayList<String> GetOptionWords(int lineIndex)
	{
		ArrayList<String> list = new ArrayList<String>(MAX_OPTION_WORDS);
		
		if(lineOptionWords.size() > 0 
				&& lineIndex >=0 && lineIndex < lineCount)
		{
			list = lineOptionWords.get(lineIndex);
		}
			
		return list;
	}
	
 	public String GetShortTitle()
	{
		return Title.length() > 12 ? Title.substring(0,11) + "..": Title;
	}
}
