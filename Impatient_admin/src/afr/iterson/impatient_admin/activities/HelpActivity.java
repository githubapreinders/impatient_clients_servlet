package afr.iterson.impatient_admin.activities;

import java.util.ArrayList;
import java.util.List;

import afr.iterson.impatient_admin.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class HelpActivity extends FragmentActivity
{
	MyPageAdapter pageAdapter;

	public static Intent makeIntent(Context context)
	{
		return new Intent(context, HelpActivity.class);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_helpscreens);
		List<Fragment> fragments = getFragments();
		pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		pager.setPageTransformer(true, new ZoomOutPageTransformer());
		pager.setAdapter(pageAdapter);
	}

	class ZoomOutPageTransformer implements ViewPager.PageTransformer
	{

		private static final float MIN_SCALE = 0.85f;
		private static final float MIN_ALPHA = 0.5f;

		@Override
		public void transformPage(View view, float position)
		{
			int pageWidth = view.getWidth();
			int pageHeight = view.getHeight();

			if (position < -1)
			{ // [-Infinity,-1)
				// This page is way off-screen to the left.
				view.setAlpha(0);

			} else if (position <= 1)
			{ // [-1,1]
				// Modify the default slide transition to shrink the page as
				// well
				float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
				float vertMargin = pageHeight * (1 - scaleFactor) / 2;
				float horzMargin = pageWidth * (1 - scaleFactor) / 2;
				if (position < 0)
				{
					view.setTranslationX(horzMargin - vertMargin / 2);
				} else
				{
					view.setTranslationX(-horzMargin + vertMargin / 2);
				}

				// Scale the page down (between MIN_SCALE and 1)
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);

				// Fade the page relative to its size.
				view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

			} else
			{ // (1,+Infinity]
				// This page is way off-screen to the right.
				view.setAlpha(0);
			}
		}

	}

	
	public class DepthPageTransformer implements ViewPager.PageTransformer {
	    private static final float MIN_SCALE = 0.75f;

	    public void transformPage(View view, float position) {
	        int pageWidth = view.getWidth();

	        if (position < -1) { // [-Infinity,-1)
	            // This page is way off-screen to the left.
	            view.setAlpha(0);

	        } else if (position <= 0) { // [-1,0]
	            // Use the default slide transition when moving to the left page
	            view.setAlpha(1);
	            view.setTranslationX(0);
	            view.setScaleX(1);
	            view.setScaleY(1);

	        } else if (position <= 1) { // (0,1]
	            // Fade the page out.
	            view.setAlpha(1 - position);

	            // Counteract the default slide transition
	            view.setTranslationX(pageWidth * -position);

	            // Scale the page down (between MIN_SCALE and 1)
	            float scaleFactor = MIN_SCALE
	                    + (1 - MIN_SCALE) * (1 - Math.abs(position));
	            view.setScaleX(scaleFactor);
	            view.setScaleY(scaleFactor);

	        } else { // (1,+Infinity]
	            // This page is way off-screen to the right.
	            view.setAlpha(0);
	        }
	    }
	}
	
	class MyPageAdapter extends FragmentPagerAdapter
	{

		private List<Fragment> fragments;

		public MyPageAdapter(FragmentManager fm, List<Fragment> fragments)
		{
			super(fm);
			this.fragments = fragments;
		}

		@Override
		public Fragment getItem(int position)
		{
			return this.fragments.get(position);
		}

		@Override
		public int getCount()
		{
			return this.fragments.size();
		}
	}

	private List<Fragment> getFragments()
	{
		List<Fragment> fList = new ArrayList<Fragment>();
		fList.add(HelpFragment.newInstance("file:///android_asset/helpscreens/1.html"));
		fList.add(HelpFragment.newInstance("file:///android_asset/helpscreens/2.html"));
		fList.add(HelpFragment.newInstance("file:///android_asset/helpscreens/3.html"));
		fList.add(HelpFragment.newInstance("file:///android_asset/helpscreens/4.html"));
		fList.add(HelpFragment.newInstance("file:///android_asset/helpscreens/5.html"));
		fList.add(HelpFragment.newInstance("file:///android_asset/helpscreens/6.html"));
		fList.add(HelpFragment.newInstance("file:///android_asset/helpscreens/7.html"));
		fList.add(HelpFragment.newInstance("file:///android_asset/helpscreens/8.html"));

		return fList;
	}
	
	@Override
	public void onPause()
	{
		overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
		super.onPause();
	}
	
}
