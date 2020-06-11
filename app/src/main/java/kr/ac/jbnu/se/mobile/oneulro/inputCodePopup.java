package kr.ac.jbnu.se.mobile.oneulro;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class inputCodePopup extends Activity {

    private EditText input;
    private Button yes, no;
    private String invite_code;

    private shareMainActivity group_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_inputpopup);

        input = (EditText) findViewById(R.id.inputText);
        yes = (Button) findViewById(R.id.input_yes);
        no = (Button) findViewById(R.id.input_no);

        group_name = new shareMainActivity();

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

                    Toast.makeText(inputCodePopup.this, "새로운 ToDoList 그룹이 추가되었습니다", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onTouchEvent(MotionEvent event){

        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
}
