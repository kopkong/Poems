package com.example.android.effectivenavigation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class Quiz extends Activity {
	private static Poem quizPoem;
	private static int current_selected_row = 0;
	private static int current_selected_col = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz);
		// Show the Up button in the action bar.
		setupActionBar();
		
        // Init and load data
        MyApp appState = ((MyApp)getApplicationContext());
        quizPoem = appState.GetQuizPoem();
        
        // Show Title
        TextView t1 = (TextView)this.findViewById(R.id.label_quiz_title);
        t1.setText(quizPoem.Title);
        
        // Show Author
        TextView t2 = (TextView)this.findViewById(R.id.label_quiz_author);
        t2.setText(quizPoem.Author);
        
        // Show Contents
        LinearLayout ll = (LinearLayout)this.findViewById(R.id.ll_quiz);
        int lines =  quizPoem.GetPoemLineCount();
        for(int i=0;i<lines;i++)
        {
        	String str = quizPoem.GetPoemLineText(i);
    		LinearLayout ll2 = new LinearLayout(this);
    		ll2.setOrientation(0);
    		ll.addView(ll2);
    		int len = str.length();
    		
    		for(int j =0 ; j<len; j++)
    		{
    			TextView tCell = new TextView(this);
    			setQuizCellLayout(tCell);
    			
    			if(i%2 == 0 || j == len - 1)
    			{
    				setNormalQuizCellStyle(tCell);
    				tCell.setText(str.substring(j, j+1));
    			}
    			else
    			{
    				setBorderQuizCellStyle(tCell);
    				tCell.setOnClickListener(new OnClickListener(){
    					@Override
    					public void onClick(View v)
    					{
    						setSelectedQuizCellStyle((TextView)v);
    					}
    				});
    			}
    			ll2.addView(tCell);
    		}
        	
        }
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quiz, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setQuizCellLayout(TextView cell)
	{
		cell.setWidth(120);
		cell.setHeight(120);
		cell.setTextSize(25);
		cell.setGravity(Gravity.CENTER);
	}
	
	@SuppressWarnings("deprecation")
	private void setBorderQuizCellStyle(TextView cell)
	{
		Drawable borderBackground = this.getResources().getDrawable(R.drawable.quizcell_border);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			cell.setBackground(borderBackground);
		else
			cell.setBackgroundDrawable(borderBackground);
	}
	
	private void setNormalQuizCellStyle(TextView cell)
	{
		// do something here ?
	}
	
	private void setSelectedQuizCellStyle(TextView cell)
	{
		Drawable borderBackground = this.getResources().getDrawable(R.drawable.quizcell_selected);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			cell.setBackground(borderBackground);
		else
			cell.setBackgroundDrawable(borderBackground);
	}
	
	private void setCheckedWrongQuizCellStyle(TextView cell)
	{
		
	}
	
	private void unSelectedQuizCell(int row,int col)
	{
		
	}
}
