package com.ipol.sponticker;

import android.app.Activity;
import android.os.Bundle;

public class TickerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticker);

		TickerFragment fragment = new TickerFragment();
		getFragmentManager().beginTransaction().add(R.id.ticker_fragment_container, fragment)
				.commit();

	}

}
