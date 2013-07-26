package com.ipol.sponticker;

import java.io.IOException;
import java.io.InputStream;

import com.ipol.sponticker.MatchPickerDialog.Callback;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class TickerActivity extends Activity implements Callback{

	TickerFragment fragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticker);

		fragment = new TickerFragment();
		getFragmentManager().beginTransaction().add(R.id.ticker_fragment_container, fragment)
				.commit();

	}
	
	public void chooseMatch(View view){
		MatchPickerDialog dialog = new MatchPickerDialog();
		dialog.show(getFragmentManager(), "");
		
	}

	@Override
	public void matchClicked(String filename) {
		System.out.println(filename);
		
		InputStream is = null;
		try {
			is = getAssets().open("matches/"+filename);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		fragment = new TickerFragment();
		fragment.parseMatch(is);
		
		getFragmentManager().beginTransaction().replace(R.id.ticker_fragment_container, fragment)
		.commit();
		
	}
}
