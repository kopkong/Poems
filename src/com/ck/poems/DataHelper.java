package com.ck.poems;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class DataHelper {
	private Context ActivityContext;
	private static Dictionary<Integer,Poem> PoemDict;
	private static ArrayList<Poem> Poems;
	private static ArrayList<String> PoemGenre;
	final int RECENT_RANGE_MIN = 0;
	final int RECENT_RANGE_MAX = 5;


	public DataHelper(Context context) 
	{
		ActivityContext = context;
		
		PoemDict = new Hashtable<Integer,Poem>();
		Poems = new ArrayList<Poem>();
		PoemGenre = new ArrayList<String>();
		
		LoadStaticPoemsData();
	}
	
	public List<String> GetPoemsGenre()
	{
		return PoemGenre;
	}
	
 	public List<Poem> GetRecentPoem()
	{
		if(Poems == null)
			return null;
		
		// Sort P

		// Return sublist
		return Poems.subList(RECENT_RANGE_MIN, RECENT_RANGE_MAX );
	
	}
	
	public List<Poem> GetPoemsByGenre(String genre)
	{
		List<Poem> list = new ArrayList<Poem>();
		
		for(Iterator<Poem> it = Poems.iterator();it.hasNext();)
		{
			Poem p = it.next();
			if(p.Genre.equalsIgnoreCase(genre))
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
	

}
