package com.example.udezumou;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpGetTask extends AsyncTask<Void, Void, String> {
    private TextView mTextView;
    //private Activity mParentActivity;
    //private ProgressDialog mDialog = null;

    private String mUri="https://www.yamagiwalab.jp/~yama/KPK/Hello.html";
    public HttpGetTask(TextView textView){
        //this.mParentActivity = (Activity) parentActivity;
        this.mTextView = textView;
    }

    //タスク開始時
    /*
    @Override
    protected void onPreExecute(){
        mDialog = new ProgressDialog(mParentActivity);
        mDialog.setMessage("");
        mDialog.show();
    }
    */

    //メイン処理
    @Override
    protected String doInBackground(Void... voids){
        return exec_get();
    }

    //タスク終了時
    @Override
    protected void onPostExecute(String string){
        //mDialog.dismiss();
        this.mTextView.setText(string);
    }

    private String exec_get(){
        HttpURLConnection http = null;
        InputStream in = null;
        String src = "";
        try {
            URL url = new URL(mUri);
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.connect();
            in = http.getInputStream();
            byte[] line = new byte[1024];
            int size;
            while (true) {
                size = in.read(line);
                if (size <= 0) {
                    break;
                }
                Log.d("GET", new String(line));

                String regex = "(?<=<h1>).*(?=</h1>)";
                Pattern p = Pattern.compile(regex);

                Matcher m = p.matcher(new String(line));
                /*
                if (m.find()){
                    System.out.println("Start : " + m.start());
                    System.out.println("End   : " + m.end());
                    System.out.println("Match : " + m.group());
                }
                */
                if(m.find()){
                    src += m.group();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                if(http != null){
                    http.disconnect();
                }
                if(in != null){
                    in.close();
                }
            } catch (Exception ignored){

            }
        }
        return src;
    }
}
