package com.example.android.effectivenavigation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Map;
import java.util.HashMap;

@SuppressLint("NewApi")
public class Quiz extends Activity {
	private static Poem quizPoem;
	private static int current_selected_cellid = -1;
	private static int cellid_count = 0;
	private static Map<Integer,Integer> cellid_toRowIndex;
	private static Map<Integer,Integer> cellid_toColumnIndex;
	
	enum CellState
	{
		Normal,
		Selected,
		MarkedWrong
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz);
		// Show the Up button in the action bar.
		setupActionBar();
		
        // Init and load data
        MyApp appState = ((MyApp)getApplicationContext());
        quizPoem = appState.GetQuizPoem();
        cellid_toRowIndex = new HashMap<Integer,Integer>();
        cellid_toColumnIndex = new HashMap<Integer,Integer>();
        
        // Show Title
        TextView t1 = (TextView)this.findViewById(R.id.label_quiz_title);
        t1.setText(quizPoem.Title);
        
        // Show Author
        TextView t2 = (TextView)this.findViewById(R.id.label_quiz_author);
        t2.setText(quizPoem.Author);
        
        // Show Contents
        showQuizContent();
        
        // Show Options
        showOptionWords();
	}
	
	/**
	 * Show Quiz content = the content of the poem
	 */
	private void showQuizContent()
	{
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
    			tCell.setId(getQuizCellId(i,j));
    			setQuizCellLayout(tCell);
    			
    			if(i%2 == 0 || j == len - 1)
    			{
    				//setQuizCellStyle(tCell,CellState.Normal);
    				tCell.setText(str.substring(j, j+1));
    			}
    			else
    			{
    				setQuizCellStyle(tCell,CellState.Normal);
    				tCell.setOnClickListener(new OnClickListener(){
    					@Override
    					public void onClick(View v)
    					{
    						selectCell((TextView)v);
    					}
    				});
    			}
    			ll2.addView(tCell);
    		}
        }
	}
	
	/**
	 * Show the option words to complete the poem
	 */
	private void showOptionWords()
	{
		// need to know which line/row is selected
		// Option words won't change for one row
	}
	
	/**
	 * Return identity for quiz cell
	 */
	private int getQuizCellId(int rowIndex,int colIndex)
	{
		int cellid = cellid_count;
		cellid_toRowIndex.put(cellid, rowIndex);
		cellid_toColumnIndex.put(cellid, colIndex);
		cellid_count++;
		return cellid;
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}
	
	/** 
	 * Initial quiz cell's layout 
	 */
	private void setQuizCellLayout(TextView cell)
	{
		cell.setWidth(120);
		cell.setHeight(120);
		cell.setTextSize(25);
		cell.setGravity(Gravity.CENTER);
	}
	
	/** 
	 * Change cell style
	 */
	private void setQuizCellStyle(TextView cell,CellState state)
	{
		Drawable borderBackground;
		switch(state)
		{
		case Normal:
			borderBackground = this.getResources().getDrawable(R.drawable.quizcell_border);
			break;
		case Selected:
			borderBackground = this.getResources().getDrawable(R.drawable.quizcell_selected);
			break;
		case MarkedWrong:
			borderBackground = null;
			break;
		default:
			borderBackground = null;
			break;
		}
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			cell.setBackground(borderBackground);
		else
			cell.setBackgroundDrawable(borderBackground);
		
	}
	
	/**
	 * Action when one cell is taped
	 */
	private void selectCell(TextView cell)
	{
		// Parse rowIndex and colIndex from ID first
		int id = cell.getId();
		//int rowIndex = cellid_toRowIndex.get(id);
		//int colIndex = cellid_toColumnIndex.get(id);
		
		// do nothing if tap on cell repeatedly
		if(id == current_selected_cellid)
			return;
		
		// unselect current cell, if there has.
		if(current_selected_cellid > 0)
		{
			TextView tv = (TextView)this.findViewById(current_selected_cellid);
			setQuizCellStyle(tv,CellState.Normal);
		}
		
		// set current cell
		current_selected_cellid = id;
		
		// change style
		setQuizCellStyle(cell, CellState.Selected);
		
	}
	
}
