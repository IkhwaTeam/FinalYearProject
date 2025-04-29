package com.example.ikhwa;
import android.content.Context;
import android.widget.ArrayAdapter;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


public class TeacherNotificationAdapter extends ArrayAdapter<TeacherNotificationModel> {

    private Context context;
    private List<TeacherNotificationModel> notificationList;

    public TeacherNotificationAdapter(Context context, List<TeacherNotificationModel> notificationList) {
        super(context, 0, notificationList);
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_notification_design, parent, false);
        }

        TeacherNotificationModel notification = notificationList.get(position);

        TextView title = convertView.findViewById(R.id.notification_title);
        TextView description = convertView.findViewById(R.id.notification_description);
       // TextView time = convertView.findViewById(R.id.notification_time);
        ImageView icon = convertView.findViewById(R.id.notification_logo);

        title.setText(notification.getTitle());
        description.setText(notification.getDescription());
       // time.setText(notification.getTime());
        //icon.setImageResource(R.drawable.notification_logo); // your icon here

        return convertView;
    }
}
