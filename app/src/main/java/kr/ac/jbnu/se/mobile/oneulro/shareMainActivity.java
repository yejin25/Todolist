package kr.ac.jbnu.se.mobile.oneulro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import java.util.List;

public class shareMainActivity extends AppCompatActivity {

    private ListView listView;
    private GroupAdapter adapter;
    private List<Group> shareGroup;
    private ImageView inputCode;
    private List<String> people;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseRef = database.getReference();

    public static ArrayList<String> key = new ArrayList<>();

    private String uid;
    private String input_uid ;
    private String input_unixTime;
    private String index;

    public static final int REQUERST_CODE_SHARE_GROUP = 1004;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharemain);

        listView = (ListView) findViewById(R.id.sharedlistView);
        inputCode = (ImageView) findViewById(R.id.addshared);

        uid = LoginData.firebaseAuth.getUid();

        shareGroup = new ArrayList<>();
        people = new ArrayList<>();

        adapter = new GroupAdapter(shareGroup);

        listView.setAdapter(adapter);

        loadSharedGroup();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), todoActivity.class);
                //int listPosition = listView.getPositionForView((View) view.getParent());
                index = key.get(position);
                intent.putExtra("sharedIndex", index);
                Log.d("TAG", "-----000----" + index);
                startActivity(intent);
            }
        });

        inputCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), inputCodePopup.class);
                startActivityForResult(intent, REQUERST_CODE_SHARE_GROUP);
            }
        });
    }

    public void deleteClick(View v){
        int count;
        count = adapter.getCount();
        if (count > 0) {
            final int position = listView.getPositionForView((View) v.getParent());
            Group item = shareGroup.get(position);

            if (position > -1 && position < count) {

                shareGroup.remove(item);
                listView.clearChoices();
                databaseRef.child("SharedGroup").child(uid).child(key.get(position)).removeValue();
                adapter.notifyDataSetChanged();

                Toast.makeText(shareMainActivity.this, "삭제되었습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUERST_CODE_SHARE_GROUP){
            if(resultCode==RESULT_OK) {
                input_uid = data.getStringExtra("uid");
                input_unixTime = data.getStringExtra("unixTime");

                Log.d("Tag","------------"+input_uid);
                addSharedGroup();

            }
        }
    }

    private void addSharedGroup() {

        databaseRef.child("Group").child(input_uid).child(input_unixTime).addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                shareGroup.clear();
                Group group = dataSnapshot.getValue(Group.class);

                shareGroup.add(group);

                databaseRef.child("SharedGroup").child(input_uid).child(input_unixTime).setValue(group);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));
    }

    private void loadSharedGroup(){
        databaseRef.child("SharedGroup").child(uid).addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                key.clear();
                shareGroup.clear();
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    if (childDataSnapshot.getKey() != null) {
                        Group data = childDataSnapshot.getValue(Group.class);

                        key.add(childDataSnapshot.getKey());
                        shareGroup.add(data);
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