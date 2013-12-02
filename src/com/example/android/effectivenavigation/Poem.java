package com.example.android.effectivenavigation;

import java.util.ArrayList;

public class Poem {
	public int ID;
	public String Title;
	public String Author;
	public String Snapshot;
	public String Context;
	public String Genre;
	
	private ArrayList<String> linesContent;
	private int lineCount;
	private String noPuncutationContext;
	
	// Init poem data for a quiz
	// Always call this method first if want to use this poem as a quiz.
	public void InitQuiz()
	{
		if(Context.length() > 0 )
		{
			linesContent = new ArrayList<String>();
			noPuncutationContext = "";
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
					line = "";
				}
				else
				{
					noPuncutationContext += ch;
				}
			}
		}
		lineCount = linesContent.size();
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
	
	public String GetShortTitle()
	{
		return Title.length() > 12 ? Title.substring(0,11) + "…": Title;
	}
}
