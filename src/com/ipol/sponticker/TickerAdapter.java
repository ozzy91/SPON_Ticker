package com.ipol.sponticker;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipol.sponticker.model.TickerEvent;

public class TickerAdapter extends ArrayAdapter<TickerEvent> {

	private List<TickerEvent> mObjects;
	private Context mContext;
	private int layoutId;

	// views
	private TextView txtMinute;
	private TextView txtDiscreption;
	private TextView txtCommentary;
	private ImageView imgIcon;

	public TickerAdapter(Context context, int textViewResourceId,
			List<TickerEvent> objects) {
		super(context, textViewResourceId, objects);

		mContext = context;
		layoutId = textViewResourceId;
		mObjects = objects;
		
		for(TickerEvent ev : objects){
			System.out.println(ev.getMinute());
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (mObjects.get(position).getType() == null)
			return 1;
		else
			return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (mObjects.get(position).getType() == null) {

			View view = LayoutInflater.from(mContext).inflate(R.layout.item_ticker_test, null);
			((TextView) view.findViewById(R.id.item_test)).setText(mObjects.get(position).getCommentary());
			return view;
		} else {

			TickerEvent event = mObjects.get(position);
			View view = LayoutInflater.from(mContext).inflate(layoutId, null);

			txtMinute = (TextView) view.findViewById(R.id.txt_event_minute);
			txtDiscreption = (TextView) view.findViewById(R.id.txt_event_description);
			txtCommentary = (TextView) view.findViewById(R.id.txt_event_commentary);
			imgIcon = (ImageView) view.findViewById(R.id.img_event_icon);

			// check if event is in added time
			if (event.getAddedTime() == 0) {
				txtMinute.setText(event.getMinute() + ". Minute");
			} else {
				txtMinute.setText(event.getMinute() + "+" + event.getAddedTime()
						+ ". Minute");
			}

			// check if there is a commentary
			String commentary = event.getCommentary();
			if (commentary.equals("{}")) {
				txtCommentary.setVisibility(View.GONE);
			} else {
				txtCommentary.setText(event.getCommentary());
			}

			switch (event.getType()) {
			case NORMAL:
				txtDiscreption.setVisibility(View.GONE);
				imgIcon.setVisibility(View.GONE);
				break;
			case GOAL:
				imgIcon.setImageResource(R.drawable.timeline_ball_2x);
				txtDiscreption.setText("Tor: " + event.getScore() + ", "
						+ event.getPlayer() + " (" + event.getTeam() + ")");
				break;
			case OWNGOAL:
				imgIcon.setImageResource(R.drawable.timeline_ball_2x);
				txtDiscreption.setText("Eigentor: " + event.getScore() + ", "
						+ event.getPlayer() + " (" + event.getTeam() + ")");
				break;
			case YELLOW:
				imgIcon.setImageResource(R.drawable.timeline_card_yellow_2x);
				txtDiscreption.setText("Gelbe Karte: " + event.getPlayer() + " ("
						+ event.getTeam() + ")");
				break;
			case RED:
				imgIcon.setImageResource(R.drawable.timeline_card_red_2x);
				txtDiscreption.setText("Rote Karte: " + event.getPlayer() + " ("
						+ event.getTeam() + ")");
				break;
			case YELLOWRED:
				imgIcon.setImageResource(R.drawable.timeline_card_yellowred_2x);
				txtDiscreption.setText("Gelb-Rote Karte: " + event.getPlayer() + " ("
						+ event.getTeam() + ")");
				break;
			case SUBSTITUTE:
				imgIcon.setImageResource(R.drawable.timeline_substitution_2x);
				txtDiscreption.setText("Wechsel " + event.getTeam() + ": "
						+ event.getPlayer() + " für " + event.getOtherPlayer());
				break;
			case PENALTY:
				imgIcon.setVisibility(View.GONE);
				txtDiscreption.setText("Elfmeter gepfiffen");
			}

			return view;
		}
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

}
