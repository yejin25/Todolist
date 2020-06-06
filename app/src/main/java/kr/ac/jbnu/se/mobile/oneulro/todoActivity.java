package kr.ac.jbnu.se.mobile.oneulro;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class todoActivity extends AppCompatActivity {

    private List<Item> todo;
    private ListView mlistView;
    private ItemAdapter mAdapter;
    private ImageView add_todo;
    private EditText text_todo;
    private int position;

    private FirebaseDatabase mdatabase =FirebaseDatabase.getInstance();
    private DatabaseReference  databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);

        todo = new ArrayList<>();

        mlistView = (ListView)findViewById(R.id.todoListView);
        add_todo = (ImageView)findViewById(R.id.add_todo);
        text_todo = (EditText)findViewById(R.id.text_todo);

        mAdapter = new ItemAdapter(todo);

        mlistView.setAdapter(mAdapter);

        Intent intent = new Intent();
        position = intent.getIntExtra("position",1);

        databaseReference = mdatabase.getReference("Group");

        add_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Item todo = new Item(LoginData.firebaseAuth.getCurrentUser().getDisplayName(),text_todo.getText().toString());
                todo.add(new Item(LoginData.firebaseAuth.getCurrentUser().getDisplayName(),text_todo.getText().toString()));
                mAdapter.notifyDataSetChanged();
               // saveItem(todo);
                text_todo.setText("");
            }
        });

    }

    private void saveItem(Item todo){
        databaseReference.child("TODO").setValue(todo);
    }
}
