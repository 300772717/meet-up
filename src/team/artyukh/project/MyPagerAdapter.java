package team.artyukh.project;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MyPagerAdapter extends FragmentStatePagerAdapter{

	private ArrayList<Fragment> fragList = new ArrayList<Fragment>();
	private ArrayList<String> fragTitles = new ArrayList<String>();
	
	public MyPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}
	
	public void addFragment(Fragment fragment, String title){
		fragList.add(fragment);
		fragTitles.add(title);
	}
	
	@Override
	public Fragment getItem(int pos) {
		return fragList.get(pos);
	}

	@Override
	public int getCount() {
		return fragList.size();
	}
	
	@Override
	public String getPageTitle(int pos){
		return fragTitles.get(pos);
	}

}
