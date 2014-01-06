package com.ck.poems;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

public class DataHelper {
	private Context ActivityContext;
	private static Dictionary<Integer,Poem> PoemDict;
	private static ArrayList<Poem> Poems;
	private static ArrayList<String> PoemGenre;
	
	
	final int RECENT_RANGE_MIN = 0;
	final int RECENT_RANGE_MAX = 5;
	static final int TOTAL_POEMS_COUNT = 313;
	
	private Map<Integer,Integer> poemTotalCount;
	private Map<Integer,Integer> poemWrongCount;
	private ArrayList<Integer> recentPoems;

	public DataHelper(Context context) 
	{
		ActivityContext = context;
		
		PoemDict = new Hashtable<Integer,Poem>();
		Poems = new ArrayList<Poem>();
		PoemGenre = new ArrayList<String>();
		
		LoadStaticPoemsData();
		InitRecorder();
		InitRecent();
	}
	
	public List<String> GetPoemsGenre()
	{
		return PoemGenre;
	}
	
 	public List<Poem> GetRecentPoem()
	{
		if(Poems == null)
			return null;

		// Return sublist
		ArrayList<Poem> retList = new ArrayList<Poem>();
		for(int x: recentPoems)
		{
			retList.add(PoemDict.get(x));
		}
		
		return retList;
	
	}
	
	public List<Poem> GetPoemsByGenre(String genre,String searchString)
	{
		List<Poem> list = new ArrayList<Poem>();
		
		for(Iterator<Poem> it = Poems.iterator();it.hasNext();)
		{
			Poem p = it.next();
			if(p.Genre.equalsIgnoreCase(genre))
			{
				if(searchString.equals(""))
				{
					list.add(p);
				}
				else
				{
					if(p.Author.contains(searchString)|| p.Title.contains(searchString))
						list.add(p);
				}
			}
		
		}
		return list;
	}
	
	public List<Poem> GetPoemsBySearchString(String inputString)
	{
		List<Poem> list = new ArrayList<Poem>();
		for(Iterator<Poem> it = Poems.iterator(); it.hasNext();)
		{
			Poem p = it.next();
			if(p.Author.contains(inputString) || p.Title.contains(inputString))
			{
				list.add(p);
			}
		}
		
		return list;
	}
	
	public int GetTotalPoemCount()
	{
		if(Poems != null)
			return Poems.size();
		else
			return 0;
	}
	
	public Poem GetPoemByID(int poemID)
	{
		if(PoemDict != null)
			return PoemDict.get(poemID);
		
		return null;
	}
	
	private void LoadStaticPoemsData()
	{
		try
		{
			JSONObject jObject = new JSONObject(loadJSONFromAsset());
			JSONArray poemsArray = jObject.getJSONArray("300poems");
			JSONArray poemsGenre = jObject.getJSONArray("genre");
			
			// Load Poems
			for(int i = 0 ; i< poemsArray.length(); i++)
			{
				JSONObject obj = poemsArray.getJSONObject(i);
				
				Poem p = new Poem();
				String context = obj.getString("content");
				String snapshot = "";
				
				// Find snapshot
				Pattern snapPattern = Pattern.compile("([^，。！？]+[，。！？])([^，。！？]+[，。！？])(.+)");
				Matcher m = snapPattern.matcher(context);
				if(m.find() && m.groupCount()> 2)
				{
					String s1 = m.group(1);
					String s2 = m.group(2);
					
					snapshot = s1+ s2;
				}
				else
				{
					snapshot = context.length()>=16 ? context.substring(0,16):context;
				}
				
				p.ID = obj.getInt("id");
				p.Title = obj.getString("title"); ;
				p.Author = obj.getString("author");
				p.Context = context;
				p.Snapshot = snapshot;
				p.Genre = obj.getString("genre");
				
				Poems.add(p);
				PoemDict.put(p.ID, p);
			}
			
			// Load Genre
			for(int i = 0 ; i< poemsGenre.length(); i++)
			{
				PoemGenre.add(poemsGenre.getString(i));
			}
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	private String loadJSONFromAsset()
	{
		String json = null;
        try {

            InputStream is = ActivityContext.getAssets().open("300poems.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
	}
	
	public void InitRecorder()
	{
		poemTotalCount = new HashMap<Integer,Integer>(TOTAL_POEMS_COUNT);
		poemWrongCount = new HashMap<Integer,Integer>(TOTAL_POEMS_COUNT);
		
		LoadRecordFromFile();
	}
	
	public void InitRecent()
	{
		recentPoems = new ArrayList<Integer>(RECENT_RANGE_MAX);
		
		LoadRecentFromFile();
	}
	
	public void UpdateRecorder(int pid,boolean result)
	{
		if(poemTotalCount.containsKey(pid))
		{
			int val = poemTotalCount.get(pid);
			poemTotalCount.put(pid, val + 1);
		}
		
		if(poemWrongCount.containsKey(pid))
		{
			// Wrong
			if(!result){
				int val = poemWrongCount.get(pid);
				poemWrongCount.put(pid, val + 1);
			}
		}
		
		UpdateRecent(pid);
	}
	
	private void UpdateRecent(int pid)
	{
		// Replace every one in recent list with previous one
		// and kick out the last one
		int newID = pid;
		int oldID = 0;
		for(int i = RECENT_RANGE_MIN; i < RECENT_RANGE_MAX ; i++)
		{
			oldID = recentPoems.get(i);
			recentPoems.set(i,newID);
			newID = oldID;
		}
	}
	
	public void SaveRecordToFile()
	{
		String totalCountValue = "";
		String wrongCountValue = "";
		
		for(int pid = 1; pid < TOTAL_POEMS_COUNT; pid ++)
		{
			int total = 0;
			if (poemTotalCount.containsKey(pid))
				total = poemTotalCount.get(pid);
			
			int wrong = 0;
			if(poemWrongCount.containsKey(pid))
				wrong = poemWrongCount.get(pid);
			
			totalCountValue += String.valueOf(total) + ",";
			wrongCountValue += String.valueOf(wrong) + ",";
		}
		
		// Trim last comma
		totalCountValue = totalCountValue.substring(0,totalCountValue.length() - 1);
		wrongCountValue = wrongCountValue.substring(0,wrongCountValue.length() - 1);
		
		// Save
		WriteRecords(ActivityContext.getString(R.string.sharedPerfkey_totalCount),totalCountValue);
		WriteRecords(ActivityContext.getString(R.string.sharedPerfkey_wrongCount),wrongCountValue);
	}
	
	private void LoadRecordFromFile()
	{	
		String totalCountValue = ReadRecords(ActivityContext.getString(R.string.sharedPerfkey_totalCount));
		String wrongCountValue = ReadRecords(ActivityContext.getString(R.string.sharedPerfkey_wrongCount));
	
		String array_total[] = totalCountValue.split(",");
		String array_wrong[] = wrongCountValue.split(",");
		
		for(int i = 0 ; i < array_total.length;i++)
		{
			// Poem ID starts from 1, but array list index starts from 0
			int pid = i + 1;
			
			poemTotalCount.put(pid,Integer.parseInt(array_total[i])); 
			poemWrongCount.put(pid,Integer.parseInt(array_wrong[i]));
		}
	}

	private void SaveRecentToFile()
	{
		String recentPoemIDs = "";
		for(int i = RECENT_RANGE_MIN ; i < RECENT_RANGE_MAX ; i ++)
		{
			recentPoemIDs += recentPoems.get(i).toString() + ",";
		}
		
		// Trim last comma
		recentPoemIDs = recentPoemIDs.substring(0,recentPoemIDs.length() - 1);
		
		// Save
		WriteRecords(ActivityContext.getString(R.string.sharedPerfkey_recentPoems), recentPoemIDs);
	}
	
	private void LoadRecentFromFile()
	{
		String recentPoemIDs = ReadRecords(ActivityContext.getString(R.string.sharedPerfkey_recentPoems));
		String[] array_recentPoems = recentPoemIDs.split(",");
		
		if(array_recentPoems.length > RECENT_RANGE_MAX) // default value is loaded
		{
			recentPoems.add(1);
			recentPoems.add(2);
			recentPoems.add(3);
			recentPoems.add(4);
			recentPoems.add(5);
		}
		else
		{
			for(int i = 0 ; i < RECENT_RANGE_MAX ; i++)
			{
				recentPoems.add(Integer.parseInt(array_recentPoems[i]));
			}
		}
			
	}
	
	private void WriteRecords(String key,String value)
	{
		SharedPreferences sharedPref = ActivityContext.getSharedPreferences(ActivityContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	private String ReadRecords(String key)
	{
		SharedPreferences sharedPref = ActivityContext.getSharedPreferences(ActivityContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		String defaultValue = ActivityContext.getResources().getString(R.string.sharedPerfkey_defaultValue);
		String val = sharedPref.getString(key, defaultValue);
		
		return val;
	}
}