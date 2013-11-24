package com.example.android.effectivenavigation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class DataHelper {
	private Context ActivityContext;
	private static ArrayList<Poem> Poems;
	final int RECENT_RANGE_MIN = 0;
	final int RECENT_RANGE_MAX = 4;


	public DataHelper(Context context) 
	{
		ActivityContext = context;
		
		Poems = new ArrayList<Poem>();
		LoadStaticPoemsData();
		
	}
	
	// Get recent poems,
	// Position from 0 - 4
	public Poem GetRecentPoem(int position)
	{
		if(Poems == null)
			return null;
		
		// Sort P
		
		if(position >= RECENT_RANGE_MIN && position <= RECENT_RANGE_MAX){
			return Poems.get(position);
		}
		
		return null;
	
	}
	
	public int GetTotalPoemCount()
	{
		if(Poems != null)
			return Poems.size();
		else
			return 0;
	}
	
	private void LoadStaticPoemsData()
	{
		try
		{
			JSONObject jObject = new JSONObject(loadJSONFromAsset());
			
			JSONArray poemsArray = jObject.getJSONArray("300poems");
			
			for(int i = 0 ; i< poemsArray.length(); i++)
			{
				JSONObject obj = poemsArray.getJSONObject(i);
				
				Poem p = new Poem();
				String title = obj.getString("title");
				title = title.length() > 12 ? title.substring(0,11) + "…": title;
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
				
				p.Title = title;
				p.Author = obj.getString("author");
				p.Context = context;
				p.Snapshot = snapshot;
				
				Poems.add(p);
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
