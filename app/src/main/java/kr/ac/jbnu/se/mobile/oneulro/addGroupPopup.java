package kr.ac.jbnu.se.mobile.oneulro;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.util.ArrayList;

import petrov.kristiyan.colorpicker.ColorPicker;

public class addGroupPopup extends Activity {
    private ImageView color_btn;
    private Button confirm;
    private EditText description;
    private EditText title;
    private int pickColor;


    protected void onCreate(Bundle saveInstanceStatus) {
        super.onCreate(saveInstanceStatus);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_addgouppopup);

        color_btn = (ImageView) findViewById(R.id.color_btn);
        confirm = (Button) findViewById(R.id.confirm);
        description = (EditText) findViewById(R.id.description);
        title = (EditText) findViewById(R.id.title);

        color_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("description", description.getText().toString());
                intent.putExtra("title", title.getText().toString());
                intent.putExtra("color", pickColor);
                if(!description.getText().toString().isEmpty()&& !title.getText().toString().isEmpty() && pickColor != 000000) {
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else{
                    Toast.makeText(addGroupPopup.this, "모든 항목을 입력하거나 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void openColorPicker(){
        ColorPicker colorPicker = new ColorPicker(this);
        ArrayList<String> colors = new ArrayList<>();

        colors.add("#ffab91");
        colors.add("#F48FB1");
        colors.add("#ce93d8");
        colors.add("#b39ddb");
        colors.add("#9fa8da");
        colors.add("#90caf9");
        colors.add("#81d4fa");
        colors.add("#80deea");
        colors.add("#80cbc4");
        colors.add("#c5e1a5");
        colors.add("#e6ee9c");
        colors.add("#fff59d");
        colors.add("#ffe082");
        colors.add("#ffcc80");
        colors.add("#bcaaa4");

        colorPicker.setColors(colors)  // 만들어둔 list 적용
                .setColumns(5)  // 5열로 설정
                .setRoundColorButton(true)  // 원형 버튼으로 설정

                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        color_btn.setBackgroundColor(color);// OK 버튼 클릭 시 이벤
                        pickColor = color;
                    }

                    @Override
                    public void onCancel() {

                    }
                }).show();  // dialog 생성
    }
}
