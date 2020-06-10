package kr.ac.jbnu.se.mobile.oneulro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    public static final int REQUERST_CODE_NEW_GROUP = 1000;
    public static final int REQUERST_CODE_SHARE_GROUP = 1004;
    private List<Group> groups;
    public static ArrayList<String> key = new ArrayList<>();
    private ImageView add_btn;
    private ImageView menu;
    private TextView currentName;
    private TextView currentID;
    private ListView listView;
    private ImageView go_Group;
    private Switch toggle;
    private GroupAdapter adapter;
    private FirebaseDatabase database =FirebaseDatabase.getInstance();
    private DatabaseReference  databaseRef = database.getReference();

    private String uid = LoginData.firebaseAuth.getUid();
    private static String ID = LoginData.firebaseAuth.getCurrentUser().getEmail();
    private static String Name = LoginData.firebaseAuth.getCurrentUser().getDisplayName();

    public String index;
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

        Log.d ("TAG", "-----logndat-----" + ID);
        Log.d ("TAG", "-----logndat-----" + Name);

        loadGroup();

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
                    // do stuff
                } else {
                    turn = true;
                    sharedPref(turn);
                    Toast.makeText(getApplicationContext(), "알림을 켰습니다", Toast.LENGTH_SHORT).show();
                    // do other stuff
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

                        Toast.makeText(getApplicationContext(), "SelectedItem 2", Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);

        MenuItem toggleServie = menu.findItem(R.id.notify);

        Switch toggleSwitch = (Switch) MenuItemCompat.getActionView(toggleServie);

        toggleSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "SelectedItem 2", Toast.LENGTH_SHORT).show();
            }
        });

        // 검색정보를 설정하고, 이벤트 리스너를 추가하자...

        return super.onCreateOptionsMenu(menu);
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
