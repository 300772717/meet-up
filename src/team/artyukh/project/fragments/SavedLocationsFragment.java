package team.artyukh.project.fragments;

import java.lang.reflect.Field;

import team.artyukh.project.BindingActivity;
import team.artyukh.project.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


public class SavedLocationsFragment extends Fragment {
	private BindingActivity parent;
	private RelativeLayout rl;
	private View root;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		parent = (BindingActivity) getActivity();
		root = inflater.inflate(R.layout.fragment_saved_locations, container, false);
		rl = (RelativeLayout) root.findViewById(R.id.RelativeLayoutLocations);
		new Thread(){
			public void run(){
				int r = 32, g = 64, b = 0;
				int dr = 1, dg = 1, db = 1;
				while (true) {
					while (r <= 256 && r >= 0) {
						r += 20 * dr;
						while (g <= 256 && g >= 0) {
							g += 20 * dg;
							while (b <= 256 && b >= 0) {
								b += 20 * db;
								final int var = -256 * 256 * r - 256 * g - b;
								try {
									Thread.sleep(50);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								parent.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										rl.setBackgroundColor(var);
										rl.invalidate();
									}

								});
							}
							db *= -1;
							b += 20 * db;
						}
						dg *= -1;
						g += 20 * dg;
					}
					dr *= -1;
					r += 20 * dr;
				}
			}
			
		}.start();
		return root;
	}
	
	@Override
	public void onDetach() {
	    super.onDetach();

	    try {
	        Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
	        childFragmentManager.setAccessible(true);
	        childFragmentManager.set(this, null);

	    } catch (NoSuchFieldException e) {
	        throw new RuntimeException(e);
	    } catch (IllegalAccessException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	public SavedLocationsFragment() {
	}

}
