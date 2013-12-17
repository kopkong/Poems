package com.ck.poems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class Quiz extends Activity {
	private static Poem quizPoem;
	private static int first_quiz_cellid = -1;
	private static int current_selected_cellid = -1;
	private static int current_selected_optionid = -1;
	private static int cellid_count = 0;
	private static Map<Integer,Integer> cellid_toRowIndex;
	private static Map<Integer,Integer> cellid_toColumnIndex;
	private static String current_selectedWord;
	private static ArrayList<Integer> quizcellid_list;
	
	// Save all textView control for option words
	// For multiply references
	private static ArrayList<TextView> textView_OptionWords;
	
	enum CellState
	{
		Normal,
		Selected,
		MarkedWrong,
		OptionNormal,
		OptionSelected,
		OptionDisabled
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
        
        // Make sure first cell is selected initially
        jumptoNextCell();
	}
	
	/**
	 * Show Quiz content = the content of the poem
	 */
	private void showQuizContent()
	{
		LinearLayout ll = (LinearLayout)this.findViewById(R.id.ll_quiz);
        int lines =  quizPoem.GetPoemLineCount();
        quizcellid_list = new ArrayList<Integer>();
        for(int i=0;i<lines;i++)
        {
        	String str = quizPoem.GetPoemLineText(i);
    		LinearLayout ll2 = new LinearLayout(this);
    		ll2.setOrientation(0);
    		ll2.setGravity(Gravity.CENTER);
    		ll.addView(ll2);
    		int len = str.length();
    		
    		for(int j =0 ; j<len; j++)
    		{
    			TextView tCell = new TextView(this);
    			
    			setQuizCellLayout(tCell);
    			
    			// 
    			if(i%2 == 0 || j == len - 1)
    			{
    				tCell.setText(str.substring(j, j+1));
    			}
    			else
    			{
    				int id = getQuizCellId(i,j);
    				tCell.setId(id);
    				setQuizCellStyle(tCell,CellState.Normal);
    				tCell.setOnClickListener(new OnClickListener(){
    					@Override
    					public void onClick(View v)
    					{
    						selectQuizCell((TextView)v);
    					}
    				});
    				
    				// Save cell id
    				quizcellid_list.add(id);
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
		// Linear container to put text view in it
		LinearLayout optLine1 = (LinearLayout)this.findViewById(R.id.ll_options1);
		LinearLayout optLine2 = (LinearLayout)this.findViewById(R.id.ll_options2);
		
		// 14 options word 
		textView_OptionWords = new ArrayList<TextView>(Poem.MAX_OPTION_WORDS);
		LayoutInflater inflater  = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		// Generate textview for every option
		for(int i=0;i< Poem.MAX_OPTION_WORDS; i ++)
		{
			TextView optionView;
			
			// Option 1- 7 put in line 1
			if(i < 7) 
			{
				optionView = (TextView)inflater.inflate(R.layout.textview_quizoptioncell,optLine1,false);
				optLine1.addView(optionView);
			}
			else
			{
				optionView = (TextView)inflater.inflate(R.layout.textview_quizoptioncell,optLine2,false);
				optLine2.addView(optionView);
			}
			
			optionView.setId(getOptionCellId(i));
			
			// Add click event
	        optionView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v)
				{
					selectOptionCell((TextView)v);
				}
			});
				
			// Save text view
			textView_OptionWords.add(optionView);
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
	 * Change option cell style
	 * @param cell
	 * @param state
	 */
	private void setOptionCellStyle(TextView cell,CellState state)
	{
		switch(state)
		{
		case OptionNormal:
			cell.setBackgroundColor(this.getResources().getColor(R.color.optioncellbackground));
			break;
		case OptionSelected:
			cell.setBackgroundColor(this.getResources().getColor(R.color.optioncellbackground_selected));
			break;
		default:
			break;
		}
	
	}
	
	/**
	 * Action when one cell is taped
	 */
	private void selectQuizCell(TextView cell)
	{
		// Parse rowIndex and colIndex from ID first
		int id = cell.getId();
		
		// do nothing if tap on cell repeatedly
		if(id == current_selected_cellid)
			return;
		
		// unselect current cell, if there has.
		if(current_selected_cellid > 0)
		{
			TextView tv = (TextView)this.findViewById(current_selected_cellid);
			setQuizCellStyle(tv,CellState.Normal);
		}
		
		//System.out.println("id = "+id);
		//System.out.println("current_selected_cellid =" + current_selected_cellid);
		
		// Row index has changed?
		boolean rowIndexChanged = current_selected_cellid == -1 || 
				(cellid_toRowIndex.get(id) != cellid_toRowIndex.get(current_selected_cellid));
		
		//System.out.println("row(id) = "+cellid_toRowIndex.get(id));
		//System.out.println("row(current_selected_cellid) =" + cellid_toRowIndex.get(current_selected_cellid));
		
		// set current cell
		current_selected_cellid = id;
		
		// change style
		setQuizCellStyle(cell, CellState.Selected);
		
		// If current row index has changed, reset option words
		if(rowIndexChanged)
			setOptionsText();
	}
	
	/**
	 * Action when one cell is taped
	 */
	private void selectOptionCell(TextView cell)
	{
		// Parse rowIndex and colIndex from ID first
		int id = cell.getId();
		
		// do nothing if tap on cell repeatedly
		if(id == current_selected_optionid)
			return;
		
		// unselect current cell, if there has.
		if(current_selected_optionid > 0)
		{
			TextView previous = (TextView)this.findViewById(current_selected_optionid);
			setOptionCellStyle(previous,CellState.OptionNormal);
		}
		
		// Set current cell
		current_selected_optionid = id;
		
		// Save current selected word
		current_selectedWord = cell.getText().toString();
		
		// Fill text into Quiz cell
		if(current_selected_cellid  >= 0)
		{
			fillQuizText();
			jumptoNextCell();
		}
		
		// Change style
		setOptionCellStyle(cell, CellState.OptionSelected);
		
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
		}
		
		// Clear current selected option
		current_selectedWord = "";
		current_selected_optionid = -1;
	}
	
	/**
	 * Fill text into quiz cell
	 */
	private void fillQuizText()
	{
		if(current_selectedWord != "" && current_selected_cellid >= 0)
		{
			TextView t1 = (TextView)this.findViewById(current_selected_cellid);
			t1.setText(current_selectedWord);
		}
	}
	
	/**
	 * Jump to next quiz cell
	 */
	private void jumptoNextCell()
	{
		int jumptoId = -1;
		int len = quizcellid_list.size();
		
		// No cell selected or current cell is the last cell
		if(current_selected_cellid == -1 || current_selected_cellid == len -1)
			jumptoId = quizcellid_list.get(0);
		else
		{
			int idx = quizcellid_list.indexOf(current_selected_cellid);
			jumptoId = quizcellid_list.get(idx + 1);
		}
		
		// TextView which we want jump to it
		TextView v = (TextView)this.findViewById(jumptoId);
		selectQuizCell(v);
	}
	
	
	
}
