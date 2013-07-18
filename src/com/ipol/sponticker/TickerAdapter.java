package com.ipol.sponticker;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipol.sponticker.model.Player;
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
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TickerEvent event = mObjects.get(position);
		Player player = event.getPlayer();
		View view = LayoutInflater.from(mContext).inflate(layoutId, null);

		txtMinute = (TextView) view.findViewById(R.id.txt_event_minute);
		txtDiscreption = (TextView) view
				.findViewById(R.id.txt_event_description);
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
					+ player.getName() + " (" + player.getTeam() + ")");
			break;
		case YELLOW:
			imgIcon.setImageResource(R.drawable.timeline_card_yellow_2x);
			txtDiscreption.setText("Gelbe Karte: " + player.getName() + " ("
					+ player.getTeam() + ")");
			break;
		case RED:
			imgIcon.setImageResource(R.drawable.timeline_card_red_2x);
			txtDiscreption.setText("Rote Karte: " + player.getName() + " ("
					+ player.getTeam() + ")");
			break;
		case YELLOWRED:
			imgIcon.setImageResource(R.drawable.timeline_card_yellowred_2x);
			txtDiscreption.setText("Gelb-Rote Karte: " + player.getName()
					+ " (" + player.getTeam() + ")");
			break;
		case SUBSTITUTE:
			imgIcon.setImageResource(R.drawable.timeline_substitution_2x);
			txtDiscreption.setText("Wechsel " + player.getTeam() + ": "
					+ player.getName() + " für " + event.getOtherPlayer());
			break;
		}

		return view;
	}
}
