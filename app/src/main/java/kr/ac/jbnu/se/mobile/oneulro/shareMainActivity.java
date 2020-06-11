package kr.ac.jbnu.se.mobile.oneulro;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import java.util.List;

public class shareMainActivity extends AppCompatActivity {


    private static final String NOTIFICATION_CHANNEL_ID = "1002";

    private GroupAdapter adapter;
    private ImageView inputCode;
    private ListView listView;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseRef = database.getReference();

    public static ArrayList<String> key = new ArrayList<>();
    public static List<String> uidKey = new ArrayList<>();
    private List<Group> shareGroup;
    private List<String> people;

    private String uid;
    public static String input_uid ;
    private String input_unixTime;
    private String index;
    private Boolean turn;

    private int listposition;

    public static final int REQUERST_CODE_SHARE_GROUP = 1004;

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharemain);

        listView = (ListView) findViewById(R.id.sharedlistView);
        inputCode = (ImageView) findViewById(R.id.addshared);

        uid = LoginData.firebaseAuth.getUid();

        turn = MainActivity.turn;

        shareGroup = new ArrayList<>();
        people = new ArrayList<>();

        adapter = new GroupAdapter(shareGroup);

        listView.setAdapter(adapter);

        loadSharedGroup();
        name = LoginData.firebaseAuth.getCurrentUser().getDisplayName();

        Intent data = getIntent();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), todoActivity.class);

                index = key.get(position);
                listposition = position;
                intent.putExtra("position",listposition);
                intent.putExtra("sharedIndex", index);

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
                databaseRef.child("SharedGroup").child(uid).child(uidKey.get(position)).child(key.get(position)).removeValue();
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
                databaseRef.child("SharedGroup").child(uid).child(input_uid).child(input_unixTime).setValue(group);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));
        notificationInvite(turn);
    }

    private void loadSharedGroup(){
        databaseRef.child("SharedGroup").child(uid).addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                key.clear();
                shareGroup.clear();
                uidKey.clear();

                for (final DataSnapshot childDataSnap : dataSnapshot.getChildren()) {
                    databaseRef.child("SharedGroup").child(uid).child(childDataSnap.getKey()).addListenerForSingleValueEvent((new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                Group data = childDataSnapshot.getValue(Group.class);

                                uidKey.add(dataSnapshot.getKey());
                                key.add(childDataSnapshot.getKey());
                                shareGroup.add(data);

                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));
    }

    private void notificationInvite(Boolean turn) {
        if(turn) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Intent notificationIntent = new Intent(this, MainActivity.class);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground))
                    .setContentTitle("ToDoList  오늘로")
                    .setContentText("공유 그룹에 " + name + "님이 초대되었습니다.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                builder.setSmallIcon(R.drawable.ic_launcher_foreground);
                CharSequence channelName = "공유 그룹 초대";
                String description = "개인 그룹이 공유 그룹이 됨";

                int importance = NotificationManager.IMPORTANCE_HIGH;

                NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
                channel.setDescription(description);

                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);

            } else
                builder.setSmallIcon(R.mipmap.ic_launcher);

            assert notificationManager != null;
            notificationManager.notify(5678, builder.build());
        }
    }

}