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
	
	// Init poem data for a quiz
	// Always call this method first if want to use this poem as a quiz.
	public void InitQuiz()
	{
		if(Context.length() > 0 )
		{
			
		}
		
	}
	
	public int getPoemLines()
	{
		return lineCount;
	}
}
