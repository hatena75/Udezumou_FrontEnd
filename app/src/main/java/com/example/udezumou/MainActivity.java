package com.example.udezumou;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity{

    // 定数
    public static final String EXTRA_NICKNAME = "NICKNAME";

    // メンバー変数
    private String m_strNickname = "";
    private Socket m_socket;
    private String roomID;

    private TextView StateTextView;
    private TextView MyPowerTextView;
    private TextView OpponentPowerTextView;
    private TextView MyNameTextView;
    private TextView OpponentNameTextView;

    private boolean isFight = false;

    private static final String URI_SERVER = "http://192.168.10.11:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StateTextView = findViewById(R.id.textView_progress);
        MyPowerTextView = findViewById(R.id.textView_myMuscle);
        OpponentPowerTextView = findViewById(R.id.textView_opponentMuscle);
        MyNameTextView = findViewById(R.id.textView_myName);
        OpponentNameTextView = findViewById(R.id.textView_opponentName);

        // 呼び出し元からパラメータ取得
        Bundle extras = getIntent().getExtras();
        if( null != extras )
        {
            m_strNickname = extras.getString( EXTRA_NICKNAME );
        }

        // ハンドルネームの表示
        MyNameTextView.setText(m_strNickname);

        // サーバーとの接続
        try
        {
            m_socket = IO.socket( URI_SERVER );
        }
        catch( URISyntaxException e )
        {    // IO.socket失敗
            e.printStackTrace();
            Toast.makeText( this, "URI is invalid.", Toast.LENGTH_SHORT ).show();
            finish();    // アクティビティ終了
            return;
        }
        m_socket.connect(); // 接続

        // 接続完了時の処理
        m_socket.on( Socket.EVENT_CONNECT, new Emitter.Listener()
        {
            @Override
            public void call( final Object... args )
            {
                runOnUiThread( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.d("Connected", "success");
                        // サーバーに、loginイベントを送信
                        m_socket.emit( "login", m_strNickname );
                        Toast.makeText( MainActivity.this, "Connected.", Toast.LENGTH_SHORT ).show();
                    }
                } );

            }
        } );

        // 接続エラー時の処理
        m_socket.on( Socket.EVENT_CONNECT_ERROR, new Emitter.Listener()
        {
            @Override
            public void call( final Object... args )
            {
                runOnUiThread( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.d("Connected", "fail");
                        Toast.makeText( MainActivity.this, "Connection error.", Toast.LENGTH_SHORT ).show();
                        finish();    // アクティビティ終了
                        return;
                    }
                } );

            }
        } );

        // 切断時の処理
        m_socket.on( Socket.EVENT_DISCONNECT, new Emitter.Listener()
        {
            @Override
            public void call( final Object... args )
            {
                runOnUiThread( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        isFight = false;
                        Toast.makeText( MainActivity.this, "Disconnected.", Toast.LENGTH_SHORT ).show();
                    }
                } );
            }
        } );

        //マッチ時
        m_socket.on( "mached", new Emitter.Listener()
        {
            @Override
            public void call( final Object... args )
            {
                runOnUiThread( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText( MainActivity.this, "Match!", Toast.LENGTH_SHORT ).show();
                        isFight = true;
                        JSONObject objMessage = (JSONObject)args[0];
                        try
                        {
                            roomID = objMessage.getString("roomId");
                            if(objMessage.getString("name1").equals(m_strNickname)){
                                OpponentNameTextView.setText(objMessage.getString("name2"));
                            }
                            else
                            {
                                OpponentNameTextView.setText(objMessage.getString("name1"));
                            }
                        }
                        catch( JSONException e )
                        {
                            e.printStackTrace();
                        }

                    }
                } );
            }
        } );

        //対戦中の更新処理
        m_socket.on( "battle", new Emitter.Listener()
        {
            @Override
            public void call( final Object... args )
            {
                runOnUiThread( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        JSONObject objMessage = (JSONObject)args[0];
                        try
                        {
                            //各々のパワー表示を更新
                            MyPowerTextView.setText(objMessage.getString("userName"));
                            OpponentPowerTextView.setText(objMessage.getString("opponent"));
                        }
                        catch( JSONException e )
                        {
                            e.printStackTrace();
                        }

                    }
                } );
            }
        } );

        //対戦終了処理
        m_socket.on( "fin", new Emitter.Listener()
        {
            @Override
            public void call( final Object... args )
            {
                runOnUiThread( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText( MainActivity.this, "Finish!", Toast.LENGTH_SHORT ).show();
                        JSONObject objMessage = (JSONObject)args[0];
                        try
                        {
                            //各々のパワー表示を更新
                            String userGage = objMessage.getString("userName");
                            String opponentGage = objMessage.getString("opponent");
                            MyPowerTextView.setText(userGage);
                            OpponentPowerTextView.setText(objMessage.getString(opponentGage));
                            if(Integer.parseInt(userGage) > Integer.parseInt(opponentGage)){
                                StateTextView.setText("You Win!");
                            }
                            else{
                                StateTextView.setText("You Lose!");
                            }
                            isFight = false;
                            //通信切断
                            m_socket.disconnect();
                        }
                        catch( JSONException e )
                        {
                            e.printStackTrace();
                        }

                    }
                } );
            }
        } );

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        m_socket.disconnect();        // 切断
    }

    //定期的にセンサーの値をGETしてサーバ側に送信
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

            // 「Send」ボタンを押したときの処理
            Random rand = new Random();
            int myPower = rand.nextInt(1023); //筋力センサーの値

            // サーバーに情報を送信する。
            //msg.value,mgs.roomId,msg.userName,
            String jsonStr = "{\"roomId\" : " + roomID + ", \"userName\" : " + m_strNickname + ", \"value\" : " + myPower + " }";
            Log.d("JSON", jsonStr);
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                m_socket.emit( "battle", jsonObj );
            } catch (JSONException e) {
                e.printStackTrace();
            }

            getMuscleLoop.postDelayed(this, 500); //0.5秒毎
        }
    };

}