package com.bd17kaka.lotteryui;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.bd17kaka.service.LotterySearcherUtil;

public class SSHActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener {

	private static String ITEM1_INPUT_RED_NUMS = "";

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ssh);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(getActionBarThemedContextCompat(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, new String[] {
								getString(R.string.title_ssh_section1),
								getString(R.string.title_ssh_section2),
								getString(R.string.title_ssh_section3), }), this);
		// 选择指定的item菜单
		Intent intent = getIntent();
		// 这里获取值的时候，在put的时候是什么类型，那么在这里调用get**Extra方法时。**就要是相应的类型
		int seletedItem = intent.getIntExtra(MainActivity.SELECTED_ITEM_INDEX, -1);
		actionBar.setSelectedNavigationItem(seletedItem);

		String redBalls = intent.getStringExtra(MainActivity.BALL_NUMS);
		ITEM1_INPUT_RED_NUMS = redBalls;
	}

	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ssh, menu);
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		Fragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
		return true;
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = null;
			
			// 根据不同的item，现在不同的xml文件
			int selectedItemIndex = (getArguments().getInt(ARG_SECTION_NUMBER));
			switch (selectedItemIndex) {
			case 1:
				rootView = inflater.inflate(R.layout.fragment_ssh_dummy,
						container, false);
				if (ITEM1_INPUT_RED_NUMS != null && ITEM1_INPUT_RED_NUMS != "") {
					// 如果输入的红球序列号不为空的话，查询其概率
					ConnectivityManager connMgr = 
							(ConnectivityManager) rootView.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
					EditText rsEditText = (EditText) rootView.findViewById(
							R.id.ssh_input_show_rs);
					rsEditText.setText("查询中...");
					if (networkInfo != null && networkInfo.isConnected()) {
						// 有网络连接就查询
						new LotterySearcherUtil().getSSHProbability(ITEM1_INPUT_RED_NUMS, rsEditText);
					} else {
						rsEditText.setText("没有可用的网络.");
					}
					
					// 将双色球号输入框设置为用户输入
					EditText inputEditText = (EditText) rootView.findViewById(
							R.id.ssh_input_test_probability);
					inputEditText.setText(ITEM1_INPUT_RED_NUMS);
				}
				break;
			case 2:
				rootView = inflater.inflate(R.layout.fragment_ssh_dummy_show_rs,
						container, false);
				break;
			case 3:
				rootView = inflater.inflate(R.layout.fragment_ssh_dummy_show_rs,
						container, false);
				break;
			}
			
			return rootView;
		}
	}

}
