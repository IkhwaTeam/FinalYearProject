package com.example.ikhwa;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class PendingRequestActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<TeacherModelRe> list;
    ArrayList<String> keys;
    TeacherAdapterRe adapter;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_request);

        listView = findViewById(R.id.list_view_requests);
        list = new ArrayList<>();
        keys = new ArrayList<>();

        adapter = new TeacherAdapterRe(this, list, keys);
        listView.setAdapter(adapter);

        reference = FirebaseDatabase.getInstance().getReference("PendingTeacherRequests");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                keys.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    TeacherModelRe model = ds.getValue(TeacherModelRe.class);
                    list.add(model);
                    keys.add(ds.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PendingRequestActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
