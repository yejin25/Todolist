package kr.ac.jbnu.se.mobile.oneulro;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    public static final int REQUERST_CODE_NEW_GROUP = 1000;
    private List<Group> groups;
    private ImageView add_btn;
    private TextView category;
    private ListView listView;
    private GroupAdapter adapter;
    private FirebaseDatabase database =FirebaseDatabase.getInstance();
    private DatabaseReference  databaseRef = database.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        add_btn = (ImageView) findViewById(R.id.add_btn);
        listView = (ListView) findViewById(R.id.listView);
        category = (TextView) findViewById(R.id.category);

        groups = new ArrayList<>();

        adapter = new GroupAdapter(groups);

        listView.setAdapter(adapter);

        add_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), addGroupPopup.class);
                startActivityForResult(intent, REQUERST_CODE_NEW_GROUP);
            }
        });

    }
//        delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int count, checked;
//                count = adapter.getCount();
//
//                if (count > 0) {
//                    checked = listView.getCheckedItemPosition();
//
//                    if (checked > -1 && checked < count) {
//                        groups.remove(checked);
//                        listView.clearChoices();
//
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//            }
//        });

//        swipeLayout.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Star", Toast.LENGTH_SHORT).show();
//            }
//        });
        public void deleteClick(View v){
            int count;
                count = adapter.getCount();

                if (count > 0) {
                    final int position = listView.getPositionForView((View) v.getParent());
                    Group item = groups.get(position);

                    if (position > -1 && position < count) {
                        groups.remove(item);
                        listView.clearChoices();

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
                groups.add(new Group(title, description, currentTime, color));
                adapter.notifyDataSetChanged();
                Group group = new Group(title, description,currentTime,color);

                saveGroup(group);
                //String id = LoginData.firebaseAuth.getCurrentUser().getEmail();
              //  String result = id.substring(id.lastIndexOf("@"));

            }
        }
    }

    private void saveGroup(Group group){
        Integer intUnixTime = (int) System.currentTimeMillis();
        String uid = LoginData.firebaseAuth.getUid();

        databaseRef.child("Group").child(uid).child(String.valueOf(intUnixTime)).setValue(group);
    }


}
