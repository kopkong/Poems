package com.ck.poems;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;

public class MyApp extends Application {
	private Poem poem;
	private Map<Integer,Integer> poemTotalCount;
	private Map<Integer,Integer> poemWrongCount;
	
	MyApp()
	{
		poem = null;
		InitRecorder();
	}
	
	public void SetQuizPoem(Poem p)
	{
		poem = p;
	}
	
	public Poem GetQuizPoem()
	{
		// Remember to init quiz data first
		poem.InitQuiz();
		return poem;
	}
	
	public void InitRecorder()
	{
		poemTotalCount = new HashMap<Integer,Integer>();
		poemWrongCount = new HashMap<Integer,Integer>();
	}
	
	public void UpdateRecorder(int pid,boolean result)
	{
		if(poemTotalCount.containsKey(pid))
		{
			int val = poemTotalCount.get(pid);
			poemTotalCount.put(pid, val + 1);
		}
		else
		{
			poemTotalCount.put(pid, 1);
		}
		
		if(poemWrongCount.containsKey(pid))
		{
			// Wrong
			if(!result){
				int val = poemWrongCount.get(pid);
				poemWrongCount.put(pid, val + 1);
			}
		}
		else
		{
			// Correct
			if(result)
				poemWrongCount.put(pid, 0);
			else // Wrong
				poemWrongCount.put(pid, 1);
		}
	}
}
