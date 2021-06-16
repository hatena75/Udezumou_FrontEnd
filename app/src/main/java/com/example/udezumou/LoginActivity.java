package com.example.udezumou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button buttonGoToChat = (Button)findViewById( R.id.button_start );
        buttonGoToChat.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                String strNickname = ( (EditText)findViewById( R.id.editText_name ) ).getText().toString();
                if( strNickname.isEmpty() )
                {
                    Toast.makeText( LoginActivity.this, "Enter your nickname", Toast.LENGTH_SHORT ).show();
                }
                else
                {
                    Intent intent = new Intent( LoginActivity.this, MainActivity.class );
                    intent.putExtra( MainActivity.EXTRA_NICKNAME, strNickname );

                    startActivity( intent );
                }
            }
        } );
    }
}