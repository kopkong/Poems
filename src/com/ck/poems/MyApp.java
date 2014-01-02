package com.ck.poems;

import android.app.Application;

public class MyApp extends Application {
	private Poem poem;
	private DataHelper appDataHelper;
	
	public MyApp()
	{
		poem = null;
	}
	
	public void SetHelper(DataHelper helper)
	{
		appDataHelper = helper;
	}
	
	public DataHelper GetAppDataHelper()
	{
		return appDataHelper;
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
	

	

}
