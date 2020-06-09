package kr.ac.jbnu.se.mobile.oneulro;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class codePopup extends Activity {

    private TextView code;
    private int position;
    private String uid = LoginData.firebaseAuth.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendpopup);

        code = (TextView)findViewById(R.id.codeText);

        position = MainActivity.code_index;

        Log.d("TAG","----"+position);
        code.setText(uid + "#" + MainActivity.key.get(position));

        code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("uid Text", uid+"#"+MainActivity.key.get(position));
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getApplicationContext(), "Copied!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
