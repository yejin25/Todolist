package kr.ac.jbnu.se.mobile.oneulro;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class inputCodePopup extends Activity {

    private static final String NOTIFICATION_CHANNEL_ID = "1002";
    private EditText input;
    private Button yes, no;
    private String invite_code;

    private String name = LoginData.firebaseAuth.getCurrentUser().getDisplayName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_inputpopup);

        input = (EditText) findViewById(R.id.inputText);
        yes = (Button) findViewById(R.id.input_yes);
        no = (Button) findViewById(R.id.input_no);


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                invite_code = input.getText().toString();
                if(invite_code.contains("#")) {
                    String[] splitText = invite_code.split("#");

                    intent.putExtra("uid", splitText[0]);
                    intent.putExtra("unixTime", splitText[1]);

                    Log.d("TAG", "--uid--" + splitText[0]);
                    Log.d("TAG", "--unixTime--" + splitText[1]);

                    setResult(RESULT_OK, intent);
                    notificationInvite();
                    finish();
                }
                else{
                    Toast.makeText(inputCodePopup.this, "잘못된 초대 코드입니다\n   다시 입력해주세요", Toast.LENGTH_SHORT).show();
                }

            }
        });

        no.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void notificationInvite() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground)) //BitMap 이미지 요구
                .setContentTitle("ToDoList  오늘로")
                .setContentText("공유 그룹에 " + name + "님이 초대되었습니다.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName  = "공유 그룹 초대";
            String description = "개인 그룹이 공유 그룹이 됨";

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);
            channel.setDescription(description);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        assert notificationManager != null;
        notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
}
