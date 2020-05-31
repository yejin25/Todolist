package kr.ac.jbnu.se.mobile.oneulro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Group> groups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView)findViewById(R.id.listView);

        groups.add(new Group("기숙사","룸메와 할일들"));
        groups.add(new Group("기숙사","룸메와 할일들"));
        GroupAdapter adapter = new GroupAdapter(getApplicationContext(),groups);

        listView.setAdapter(adapter);

    }

}
