package com.example.android.effectivenavigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class Quiz extends Activity {
	private static Poem quizPoem;
	private static int current_selected_cellid = -1;
	private static int cellid_count = 0;
	private static Map<Integer,Integer> cellid_toRowIndex;
	private static Map<Integer,Integer> cellid_toColumnIndex;
	
	// Save all textView control for option words
	// For frequent usage 
	private static ArrayList<TextView> textView_OptionWords;
	
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
		// Generate textview for every option
		RelativeLayout rl = (RelativeLayout)this.findViewById(R.id.rl_options);
		
		// 14 options word 
		textView_OptionWords = new ArrayList<TextView>(Poem.MAX_OPTION_WORDS);
		
		for(int i=0;i< Poem.MAX_OPTION_WORDS; i ++)
		{
			TextView optionView = new TextView(this);
			
			optionView.setId(getOptionCellId(i));
			optionView.setWidth(80);
			optionView.setHeight(80);
			optionView.setTextSize(18);
			optionView.setGravity(Gravity.CENTER);
			
			textView_OptionWords.add(optionView);
			
			rl.addView(optionView);
		}
		
		// Display Initial option word
		setOptionsText();
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
	 * Return identity for option cell
	 * @return
	 */
	private int getOptionCellId(int optionIndex)
	{
		int optionCellID = 100 + cellid_count + optionIndex;
		
		return optionCellID ;
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
	
	/**
	 * Set/Reset Option words
	 */
	private void setOptionsText()
	{
		// Need to know which line/row is selected
		// Option words won't change for one row
		int rowIndex = 0;
		if(current_selected_cellid > 0 )
			rowIndex = cellid_toRowIndex.get(current_selected_cellid);
		
		// Get all option words
		ArrayList<String> options = quizPoem.GetOptionWords(rowIndex);
		
		for(int i =0 ; i< options.size() ; i++)
		{
			textView_OptionWords.get(i).setText(options.get(i));
			//TextView  optView = (TextView)this.findViewById();
		}
	}
}
