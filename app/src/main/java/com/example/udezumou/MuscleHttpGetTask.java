package com.example.udezumou;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MuscleHttpGetTask extends AsyncTask<Integer, Void, Void> {
    private Activity parentActivity;
    private ProgressDialog dialog = null;
    //実行するphpのURL
    private final String DEFAULTURL = "http://192.168.10.11/~pi/getmuscle.php";
    private String uri = null;

    public MuscleHttpGetTask(Activity parentActivity){
        this.parentActivity = parentActivity;
    }
    //タスク開始時
    @Override
    protected void onPreExecute(){
        //ダイアログを表示
        dialog = new ProgressDialog(parentActivity);
        dialog.setMessage("通信中・・・");
        dialog.show();
    }

    //メイン処理
    @Override
    protected Void doInBackground(Integer... arg0){
        uri = DEFAULTURL + "num=" + arg0[0].toString() + "&stat=" + arg0[1].toString();
        Log.d("RasPiLED", uri);
        exec_get();
        return null;
    }

    private  String exec_get(){
        HttpURLConnection http = null;
        InputStream in = null;
        String src = new String();
        try{
            //URLにHTTP接続
            URL url = new URL(uri);
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.connect();
            //データを取得
            in = http.getInputStream();
            //HTMLソースを読み出す
            byte[] line = new byte[1024];
            int size;
            while (true){
                size = in.read(line);
                if(size <= 0) break;
                src += new String(line);
            }
            //HTMLソースを表示
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                if(http != null) http.disconnect();
                if(in != null) in.close();
            }catch (Exception e){
            }
        }
        return src;
    }

    //タスク終了時
    @Override
    protected void onPostExecute(Void result){
        dialog.dismiss();
    }
}
