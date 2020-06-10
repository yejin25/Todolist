package kr.ac.jbnu.se.mobile.oneulro;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

public class todoActivity extends Activity {
    private static final String TAG = "todoActivity";
    private static final String NOTIFICATION_CHANNEL_ID = "1006" ;

    private ArrayList<Item> todo;
    private ListView mlistView;
    private ItemAdapter mAdapter;
    private ImageView add_todo;
    private ImageView sendCode;
    private EditText text_todo;
    private TextView group_title;

    private int position;
    private String index;
    private String sharedIndex;
    private View v;
    private int delete_position;

    private String title;
    private String uid = LoginData.firebaseAuth.getUid();
    private String name = LoginData.firebaseAuth.getCurrentUser().getDisplayName();
    private ArrayList<String> item_key = new ArrayList<>();
    private FirebaseDatabase mDatabase;
    private DatabaseReference databaseReference;

    private Boolean isShared = false;
    private Boolean turn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        index = intent.getStringExtra("index");
        sharedIndex = intent.getStringExtra("sharedIndex");

        Log.d("TAG","---^^----"+sharedIndex);
        Log.d("TAG","---^^!----"+index);
//        sharedPosition = intent.getIntExtra("sharedPosition",0);

        mDatabase = FirebaseDatabase.getInstance();

        turn = MainActivity.turn;

        if(sharedIndex != null){
        databaseReference = mDatabase.getReference("Group").child(uid).child(sharedIndex);
        isShared = true;
        }
        else{
            databaseReference = mDatabase.getReference("Group").child(uid).child(index);
        }

        //.child(MainActivity.key.get(position))

//        databaseRef = mDatabase.getReference("SharedGroup").child(uid).child(shareMainActivity.key.get(sharedPosition));

        todo = new ArrayList<>();

        mlistView = (ListView) findViewById(R.id.todoListView);
        add_todo = (ImageView) findViewById(R.id.add_todo);
        text_todo = (EditText) findViewById(R.id.text_todo);
        sendCode = (ImageView) findViewById(R.id.code);
        group_title = (TextView) findViewById(R.id.group_title);

        mAdapter = new ItemAdapter(todo);

        mlistView.setAdapter(mAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Group titleData = dataSnapshot.getValue(Group.class);
                    title = titleData.getText();
                    Log.d("TAG","---title---"+title);
                    group_title.setText(title);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        loadItem();

        add_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item todoItem = new Item(LoginData.firebaseAuth.getCurrentUser().getDisplayName(), text_todo.getText().toString(), MainActivity.key.get(position), "false");
                mAdapter.notifyDataSetChanged();
                saveItem(todoItem);
                text_todo.setText("");
                notificationADD(turn, isShared);
            }
        });

        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), codePopup.class);
                startActivity(intent);
            }
        });


        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TAG","----clicked-----");
                if(!Boolean.valueOf(todo.get(position).getStatus())) {
                    todo.get(position).setStatus("true");
                    databaseReference.child("TODO").child(item_key.get(position)).child("status").setValue(todo.get(position).getStatus());
                    mAdapter.notifyDataSetChanged();
                }
                else{
                    todo.get(position).setStatus("false");
                    databaseReference.child("TODO").child(item_key.get(position)).child("status").setValue(todo.get(position).getStatus());
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        mlistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TAG", "--Long Clicked---" + position);

                final AlertDialog.Builder builder = new AlertDialog.Builder(todoActivity.this);
                builder.setTitle("ToDo 삭제하기");
                builder.setMessage("todo list를 삭제하시겠습니까?");
                v = view;
                delete_position = position;

                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int count;
                        count = mAdapter.getCount();
                        if (count > 0) {
                            Log.d("TAG", "--this----" + delete_position);
                            Item item = todo.get(delete_position);

                            if (delete_position > -1 && delete_position < count) {

                                todo.remove(item);
                                mlistView.clearChoices();
                                databaseReference.child("TODO").child(item_key.get(delete_position)).removeValue();
                                mAdapter.notifyDataSetChanged();

                                Toast.makeText(todoActivity.this, "삭제되었습니다", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();
                return true;

            }
        });
    }


        private void saveItem(Item todo){

        databaseReference.child("TODO").push().setValue(todo);
    }

    private void loadItem(){
        databaseReference.child("TODO").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                item_key.clear();
                todo.clear();
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    if (childDataSnapshot.getKey() != null) {
                        Item data = childDataSnapshot.getValue(Item.class);
                        item_key.add(childDataSnapshot.getKey());
                        Log.d("TAG","-------"+childDataSnapshot.getKey());
                        todo.add(data);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void notificationADD(Boolean turn, Boolean isShared) {

        if (turn && isShared) {

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Intent notificationIntent = new Intent(this, MainActivity.class);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground))
                    .setContentTitle("ToDoList  오늘로")
                    .setContentText(name + "님이 " + title + " 리스트에 " + "ToDo 를 추가했습니다")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                builder.setSmallIcon(R.drawable.ic_launcher_foreground);
                CharSequence channelName = "todolist 추가";
                String description = "새로운 todo 가 리스트에 추가됨";

                int importance = NotificationManager.IMPORTANCE_HIGH;

                NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
                channel.setDescription(description);

                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);

            } else builder.setSmallIcon(R.mipmap.ic_launcher);

            assert notificationManager != null;
            notificationManager.notify(1234, builder.build());
        }
    }


}