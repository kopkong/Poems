package com.example.android.effectivenavigation;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Quiz extends Activity {
	private static Poem quizPoem;
	
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
        	if( i%2 == 0)
        	{
        		TextView t3 = new TextView(this);
        		t3.setText(str);
        		t3.setTextSize(20);
        		ll.addView(t3);
        	}
        	else
        	{
        		LinearLayout ll2 = new LinearLayout(this);
        		ll2.setOrientation(0);
        		ll.addView(ll2);
        		
        		int len = str.length();
        		
        		for(int j =0 ; j<len; j++)
        		{
        			TextView tCell = new TextView(this);
        			tCell.setBackgroundColor(Color.GRAY);
        			tCell.setTextSize(20);
        			if(j == len - 1)
        				tCell.setText(str.substring(j, j+1));
        			else
        				tCell.setText("��");
        			
        			ll2.addView(tCell);
        		}
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

}
