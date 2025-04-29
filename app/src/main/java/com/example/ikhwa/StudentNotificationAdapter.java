// StudentNotificationAdapter.java

package com.example.ikhwa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class StudentNotificationAdapter extends BaseAdapter {

    private Context context;
    private List<StudentNotificationItem> notificationList;
    private LayoutInflater inflater;

    public StudentNotificationAdapter(Context context, List<StudentNotificationItem> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_notification_design, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = convertView.findViewById(R.id.notification_title);
            //holder.dateTextView = convertView.findViewById(R.id.notification_date);
           // holder.timeTextView = convertView.findViewById(R.id.notification_time);
            holder.descriptionTextView = convertView.findViewById(R.id.notification_description);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        StudentNotificationItem notification = notificationList.get(position);
        holder.titleTextView.setText(notification.getTitle());
        holder.descriptionTextView.setText(notification.getDescription());
        holder.dateTextView.setText(notification.getDate());
        holder.timeTextView.setText(notification.getTime());

        return convertView;
    }

    static class ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        TextView dateTextView;
        TextView timeTextView;
    }
}
