package kr.ac.jbnu.se.mobile.oneulro;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

    private ArrayList<Item> todo;
    private ListView mlistView;
    private ItemAdapter mAdapter;
    private ImageView add_todo;
    private EditText text_todo;
    private int position;
    private String index;
    private String sharedIndex;
    private Button sendCode;
    private CheckBox check;
    private View v;

    private String uid = LoginData.firebaseAuth.getUid();
    private ArrayList<String> item_key = new ArrayList<>();
    private FirebaseDatabase mDatabase;
    private DatabaseReference databaseReference;

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

        if(sharedIndex != null){
        databaseReference = mDatabase.getReference("Group").child(uid).child(sharedIndex);
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
        sendCode = (Button) findViewById(R.id.code);
        check = (CheckBox) findViewById(R.id.check);

        mAdapter = new ItemAdapter(todo);

        mlistView.setAdapter(mAdapter);

        loadItem();

        add_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item todoItem = new Item(LoginData.firebaseAuth.getCurrentUser().getDisplayName(), text_todo.getText().toString(), MainActivity.key.get(position), "false");
                mAdapter.notifyDataSetChanged();
                saveItem(todoItem);
                text_todo.setText("");
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

                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int count;
                        count = mAdapter.getCount();
                        if (count > 0) {
                            final int position = mlistView.getPositionForView((View) v.getParent());
                            Log.d("TAG", "--this----" + position);
                            Item item = todo.get(position);

                            if (position > -1 && position < count) {

                                todo.remove(item);
                                mlistView.clearChoices();
                                databaseReference.child("TODO").child(item_key.get(position)).removeValue();
                                mAdapter.notifyDataSetChanged();

                                Toast.makeText(todoActivity.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
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

    /*Toast.makeText(todoActivity.this, "클.", Toast.LENGTH_SHORT).show();
                final int position = mlistView.getPositionForView((View) v.getParent());
                todo.get(position).setStatus(Item.Status.DONE);
                databaseReference.child("TODO").child(item_key.get(position)).child("status").setValue(todo.get(position).getStatus());*/


        private void saveItem(Item todo){
        Integer intUnixTime = (int) System.currentTimeMillis();

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
}