package kr.ac.jbnu.se.mobile.oneulro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class inputCodePopup extends Activity {

    EditText input;
    Button yes;
    String invite_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputpopup);

        input = (EditText) findViewById(R.id.inputText);
        yes = (Button) findViewById(R.id.input_yes);


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                invite_code = input.getText().toString();
                String[] splitText = invite_code.split("#");

                intent.putExtra("uid",splitText[0]);
                intent.putExtra("unixTime",splitText[1]);

                Log.d("TAG","--uid--"+splitText[0]);
                Log.d("TAG","--unixTime--"+splitText[1]);

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
