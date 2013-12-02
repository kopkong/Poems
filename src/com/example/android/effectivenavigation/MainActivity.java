/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.effectivenavigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    // Global state and variables
    static MyApp appState;
    
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;
    
    private static DataHelper helper ;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        //actionBar.setHomeButtonEnabled(false); 

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // Add first tab, recent poems
        actionBar.addTab(actionBar.newTab()
        					.setText(getResources().getString(R.string.page1_title))
        					.setTabListener(this));
        
        // Add second tab, all poems
        actionBar.addTab(actionBar.newTab()
				.setText(getResources().getString(R.string.page2_title))
				.setTabListener(this));
        
        // Init and load data
        appState = ((MyApp)getApplicationContext());
        helper = new DataHelper(this);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                	return new RecentPoemSectionFragment();
                case 1:
                	return new AllPoemsSectionFragment();
                default:
                	return new BaseFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
        	return "Section " + (position + 1);
        }
    }
 
    public static class BaseFragment extends ListFragment{
    	
    	public final class ViewHolder
	    {
    		public LinearLayout item;
	    	public TextView title;
	    	public TextView author;
	    	public TextView snapshot;
	    }

        public class PoemsAdapter extends BaseAdapter
         {
         	private LayoutInflater mInflater;
         	private List<Map<String,Object>> listData;
         	private Context mContext;
         	
         	public PoemsAdapter(Context context, List<Map<String,Object>> list)
         	{
         		this.mInflater = LayoutInflater.from(context);
         		this.listData = list;
         		this.mContext = context;
         	}
         	
         	@Override
         	public int getCount()
         	{
         		return listData.size();
         	}
         	
         	@Override
         	public Object getItem(int position)
         	{
         		return null;
         	}
         	
         	@Override
         	public long getItemId(int position)
         	{
         		return position;
         	}
         	
         	@Override
         	public View getView(final int position, View convertView,ViewGroup parent)
         	{
         		ViewHolder holder = null;
         		if(convertView == null)
         		{
         			holder = new ViewHolder();
         			convertView = mInflater.inflate(R.layout.listview_poem,null);
         			holder.item = (LinearLayout)convertView.findViewById(R.id.ll_poem);
         			holder.title = (TextView)convertView.findViewById(R.id.label_poem_title);
         			holder.author = (TextView)convertView.findViewById(R.id.label_poem_author);
         			holder.snapshot = (TextView)convertView.findViewById(R.id.label_poem_snapshot);
         			convertView.setTag(holder);
         		}
         		else
         		{
         			holder = (ViewHolder)convertView.getTag();
         		}
         		
         		holder.title.setText((String)listData.get(position).get("title"));
         		holder.author.setText((String)listData.get(position).get("author"));
         		holder.snapshot.setText((String)listData.get(position).get("snapshot"));
         		
         		holder.item.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext,Quiz.class);
						appState.SetQuizPoem(helper.GetPoemByID((Integer)listData.get(position).get("id")));
						startActivity(intent);
//						AlertDialog.Builder builder = new AlertDialog.Builder(mContext);  
//						builder.setTitle((String)listData.get(position).get("title"));
//						builder.setMessage((String)listData.get(position).get("snapshot"));
//						
//						builder.show();
					}
				});
         		return convertView;
         	}
         }
    }
    
    /**
     * A fragment that launches other parts of the demo application.
     */
    public static class RecentPoemSectionFragment extends BaseFragment {
    	private static List<Map<String,Object>> listData = new ArrayList<Map<String,Object>>();
    	
    	private void FillListViewData()
	    {
    		List<Poem> pList = helper.GetRecentPoem();
    		
    		if(pList!=null && !pList.isEmpty())
    		{
    			for(Poem p : pList)
    			{
    				Map<String,Object> map = new HashMap<String,Object>();
    				if(p != null)
					{
    					map.put("id", p.ID);
		    			map.put("title",p.GetShortTitle());
		    			map.put("author", p.Author);
		    			map.put("snapshot", p.Snapshot);
					}
		    		else
		    		{
		    			map.put("id", 0);
		    			map.put("title",getResources().getString(R.string.empty_poem_title));
		    			map.put("author",getResources().getString(R.string.empty_poem_author));
		    			map.put("snapshot",getResources().getString(R.string.empty_poem_snapshot));
		    		}
    				
    				listData.add(map);
    			}
			}
	    }
    	
    	@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	             Bundle savedInstanceState) {
	     	super.onCreateView(inflater, container, savedInstanceState);
	        View rootView = inflater.inflate(R.layout.fragment_list_recentpoems, container, false);  
	        FillListViewData();
	        PoemsAdapter adapter = new PoemsAdapter(getActivity(),listData);
	        setListAdapter(adapter);
	        return rootView;
	     }
       
    }

    public static class AllPoemsSectionFragment extends BaseFragment{
    	private static List<Map<String,Object>> listData = new ArrayList<Map<String,Object>>();
    	
    	private void FillListViewData()
	    {
	    	List<String> genres = helper.GetPoemsGenre();
	    	
	    	if(genres != null && !genres.isEmpty())
	    	{
	    		for(String str : genres)
	    		{
	    			List<Poem> pList = helper.GetPoemsByGenre(str);
	    			
	        		if(pList!=null && !pList.isEmpty())
	        		{
	        			for(Poem p : pList)
	        			{
	        				Map<String,Object> map = new HashMap<String,Object>();
	        				if(p != null)
	    					{
	        					map.put("id", p.ID);
	    		    			map.put("title",p.GetShortTitle());
	    		    			map.put("author", p.Author);
	    		    			map.put("snapshot", p.Snapshot);
	    					}
	    		    		else
	    		    		{
	    		    			map.put("id", 0);
	    		    			map.put("title",getResources().getString(R.string.empty_poem_title));
	    		    			map.put("author",getResources().getString(R.string.empty_poem_author));
	    		    			map.put("snapshot",getResources().getString(R.string.empty_poem_snapshot));
	    		    		}
	        				
	        				listData.add(map);
	        			}	
	    			}
	    		}
	    	}
	    }
    	
    	@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	             Bundle savedInstanceState) {
	     	super.onCreateView(inflater, container, savedInstanceState);
	        View rootView = inflater.inflate(R.layout.fragment_list_allpoems, container, false);  
	        FillListViewData();
	        PoemsAdapter adapter = new PoemsAdapter(getActivity(),listData);
	        setListAdapter(adapter);
	        return rootView;
	     } 
    	
    }

}
