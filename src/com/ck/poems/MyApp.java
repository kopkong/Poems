package com.ck.poems;

import android.app.Application;

public class MyApp extends Application {
	private Poem poem;
	
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
}
