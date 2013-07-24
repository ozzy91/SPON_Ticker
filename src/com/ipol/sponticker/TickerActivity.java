package com.ipol.sponticker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class TickerActivity extends Activity {

	TickerFragment fragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticker);

		fragment = new TickerFragment();
		getFragmentManager().beginTransaction().add(R.id.ticker_fragment_container, fragment)
				.commit();

	}
}
