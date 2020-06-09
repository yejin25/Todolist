package kr.ac.jbnu.se.mobile.oneulro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    public static final int REQUERST_CODE_NEW_GROUP = 1000;
    private List<Group> groups;
    public static ArrayList<String> key = new ArrayList<>();
    private ImageView add_btn;
    private TextView category;
    private ListView listView;
    private ImageView go_Group;
    private GroupAdapter adapter;
    private FirebaseDatabase database =FirebaseDatabase.getInstance();
    private DatabaseReference  databaseRef = database.getReference();
    private String uid = LoginData.firebaseAuth.getUid();

    public String index;
    public static int code_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        add_btn = (ImageView) findViewById(R.id.add_btn);
        listView = (ListView) findViewById(R.id.listView);
        category = (TextView) findViewById(R.id.category);
        go_Group = (ImageView) findViewById(R.id.gogroup);

        groups = new ArrayList<>();

        adapter = new GroupAdapter(groups);

        listView.setAdapter(adapter);

        loadGroup();

        add_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), addGroupPopup.class);
                startActivityForResult(intent, REQUERST_CODE_NEW_GROUP);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),todoActivity.class);
                //int listPosition = listView.getPositionForView((View) view.getParent());
                code_index = position;
                index = key.get(position);
                intent.putExtra("index", index);
                startActivity(intent);
            }
        });

        go_Group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),shareMainActivity.class);
                startActivity(intent);
            }
        });
    }

        public void deleteClick(View v){
            int count;
                count = adapter.getCount();
                if (count > 0) {
                    final int position = listView.getPositionForView((View) v.getParent());
                    Group item = groups.get(position);

                    if (position > -1 && position < count) {

                            groups.remove(item);
                            listView.clearChoices();
                            databaseRef.child("Group").child(uid).child(key.get(position)).removeValue();
                            adapter.notifyDataSetChanged();

                        Toast.makeText(MainActivity.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUERST_CODE_NEW_GROUP){
            if(resultCode==RESULT_OK) {
                String description = data.getStringExtra("description");
                String title = data.getStringExtra("title");
                int color = data.getIntExtra("color", 123456);
                Date currentTime = Calendar.getInstance().getTime();
                Group group = new Group(description,title,currentTime,color);

                Log.d("Tag","------------"+title);
                saveGroup(group);
                //String id = LoginData.firebaseAuth.getCurrentUser().getEmail();
              //  String result = id.substring(id.lastIndexOf("@"));
            }
        }
    }

    private void saveGroup(Group group){
        Integer intUnixTime = (int) System.currentTimeMillis();

        databaseRef.child("Group").child(uid).child(String.valueOf(intUnixTime)).setValue(group);
    }

    private void loadGroup() {

        databaseRef.child("Group").child(uid).addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                key.clear();
                groups.clear();
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    if (childDataSnapshot.getKey() != null) {
                        Group data = childDataSnapshot.getValue(Group.class);
                        key.add(childDataSnapshot.getKey());
                        groups.add(data);
//                        databaseRef.child("Group").child(uid).child(childDataSnapshot.getKey()).addListenerForSingleValueEvent((
//                                new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        Group data = dataSnapshot.getValue(Group.class);
//                                        groups.add(0,data);
//                                        adapter.notifyDataSetChanged();
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                }
//                        ));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));
    }
}
