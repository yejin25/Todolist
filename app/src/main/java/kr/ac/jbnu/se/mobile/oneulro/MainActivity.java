package kr.ac.jbnu.se.mobile.oneulro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    public static final int REQUERST_CODE_NEW_GROUP = 1000;
    private FirebaseDatabase database =FirebaseDatabase.getInstance();
    private DatabaseReference  databaseRef = database.getReference();

    private List<Group> groups;
    public static ArrayList<String> key = new ArrayList<>();

    private ImageView add_btn;
    private ImageView menu;
    private TextView currentName;
    private TextView currentID;
    private ListView listView;
    private ImageView go_Group;

    private GroupAdapter adapter;

    private String uid = LoginData.firebaseAuth.getUid();
    public String index;
    private static String ID;
    private static String Name;
    public static int code_index;
    public static boolean turn;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add_btn = (ImageView) findViewById(R.id.add_btn);
        listView = (ListView) findViewById(R.id.listView);
        go_Group = (ImageView) findViewById(R.id.gogroup);
        menu = (ImageView) findViewById(R.id.menubar);

        groups = new ArrayList<>();

        adapter = new GroupAdapter(groups);

        listView.setAdapter(adapter);

        loadGroup();

        Name = LoginData.firebaseAuth.getCurrentUser().getDisplayName();
        ID = LoginData.firebaseAuth.getCurrentUser().getEmail();

        Log.d ("TAG", "-----logndat-----" + ID);
        Log.d ("TAG", "-----logndat-----" + Name);

        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        turn = sf.getBoolean("notify",false);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitializeLayout();
            }
        });

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
    public void sharedPref(Boolean check){
        SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notify",check);
        editor.commit();
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

    public void InitializeLayout()
    {
        DrawerLayout drawLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);

        View header = navigationView.getHeaderView(0);
        currentName = (TextView) header.findViewById(R.id.currentname);
        currentID = (TextView) header.findViewById(R.id.currentid);

        currentName.setText(Name);
        currentID.setText(ID);

        loadPhoto();

        final Switch drawerSwitch = (Switch) navigationView.getMenu().findItem(R.id.notify).getActionView();

        drawerSwitch.setChecked(!turn);

        drawerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    turn = false;
                    sharedPref(turn);
                    Toast.makeText(getApplicationContext(), "알림을 껐습니다", Toast.LENGTH_SHORT).show();
                } else {
                    turn = true;
                    sharedPref(turn);
                    Toast.makeText(getApplicationContext(), "알림을 켰습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        LoginData.mGoogleSignInClient.signOut();

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);

                        Toast.makeText(getApplicationContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    default:
                        break;
                }

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                return true;
            }
        });

        drawLayout.openDrawer(Gravity.LEFT);
    }

    public void loadPhoto(){
        CircleImageView user_profile = findViewById(R.id.googleImage);

        Thread mThread = new Thread() {

            @Override

            public void run() {

                try {

                    URL url = new URL(LoginData.firebaseAuth.getCurrentUser().getPhotoUrl().toString());

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setDoInput(true);

                    conn.connect();


                    InputStream is = conn.getInputStream();

                    bitmap = BitmapFactory.decodeStream(is);

                } catch (MalformedURLException ee) {

                    ee.printStackTrace();

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }

        };

        mThread.start();
        try {

            mThread.join();

            //변환한 bitmap적용

            user_profile.setImageBitmap(bitmap);

        } catch (InterruptedException e) {

            e.printStackTrace();

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

                saveGroup(group);
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
