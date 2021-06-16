package com.example.udezumou;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity{

    // 定数
    public static final String EXTRA_NICKNAME = "NICKNAME";

    // メンバー変数
    private String m_strNickname = "";

    private TextView wsStateTextView;
    private TextView wsMyMessageTextView;
    private TextView wsRecvMessageTextView;
    private EditText inputName;

    private boolean isFight = false;

    private static String ServerIP = "192.168.0.5"; //(仮)
    private static String ServerPORT = "8000";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 呼び出し元からパラメータ取得
        Bundle extras = getIntent().getExtras();
        if( null != extras )
        {
            m_strNickname = extras.getString( EXTRA_NICKNAME );
        }

        // ハンドルネームの表示
        TextView textviewMyName = (TextView)findViewById( R.id.textView_myName);
        textviewMyName.setText( m_strNickname );


        wsStateTextView = findViewById(R.id.textView_progress);
        wsMyMessageTextView = findViewById(R.id.textView_myMuscle);
        wsRecvMessageTextView = findViewById(R.id.textView_opponentMuscle);

    }

    //定期的にセンサーの値をGET
    final Handler getMuscleLoop = new Handler();
    final Runnable r = new Runnable() {
        int count = 0;
        @Override
        public void run() {
            // UIスレッド
            if (!isFight) { //戦い終わったら抜ける
                return;
            }
            //HTTPのGETメソッドで情報を取得
            //MuscleHttpGetTask task = new MuscleHttpGetTask(this);
            //task.execute();

            getMuscleLoop.postDelayed(this, 500); //0.5秒毎
        }
    };

}