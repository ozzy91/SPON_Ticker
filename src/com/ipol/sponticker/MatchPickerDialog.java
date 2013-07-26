package com.ipol.sponticker;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MatchPickerDialog extends DialogFragment {
	
	interface Callback{
		public void matchClicked(String filename);
	}
	
	public Callback callback;
	String[] assets = null;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		ListView list = new ListView(getActivity());
		
		try {
			
			assets = getActivity().getAssets().list("matches");
			for(String str : assets){
				System.out.println(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, assets);
		list.setAdapter(adapter);
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				callback.matchClicked(assets[arg2]);
				dismiss();
			}
		
		});
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(list);
		builder.setNegativeButton("Abbrechen", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		
		return builder.create();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		this.callback = (Callback) activity;
	
	}
	
	
	
	

}
