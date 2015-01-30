package com.example.metaactivity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.metafragment.FeedsListFragment;
import com.example.metautils.UiUtils;

public class FeedsListActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        UiUtils.setPreferenceTheme(this);
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) 
        {
            FeedsListFragment fragment = new FeedsListFragment();
            getFragmentManager().beginTransaction().add(android.R.id.content, fragment, FeedsListFragment.class.getName()).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }

        return false;
    }
}
