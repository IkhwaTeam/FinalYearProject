package com.example.ikhwa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomTimingAdapter extends ArrayAdapter<String> {
    public CustomTimingAdapter(Context context, ArrayList<String> timings) {
        super(context, 0, timings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_timing, parent, false);
        }

        TextView timingTitle = convertView.findViewById(R.id.timingTitle);
        TextView timingMessage = convertView.findViewById(R.id.timingMessage);

        String timing = getItem(position);

        if (timing != null) {
            String[] parts = timing.split(" - ");
            timingTitle.setText(parts[0]);
            timingMessage.setText("New Updated time is: " + parts[1]);
        }

        return convertView;
    }
}
