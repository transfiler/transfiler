package com.example.transfiler.transfiler;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;

import android.content.Intent;

import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity
{
    Button sendButton;
    Button receiveButton;

    EditText messageBox;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendButton = ( Button ) findViewById( R.id.sendButton );
        receiveButton = ( Button ) findViewById( R.id.receiveButton );

        messageBox = ( EditText ) findViewById( R.id.message );

        messageBox.clearFocus();
    }

    // Klikniecie przycisku Send
    public void onClickSend( View view )
    {
        Intent intent = new Intent( MainActivity.this, PlaySound.class );
        intent.putExtra( "message", messageBox.getText().toString() );

        MainActivity.this.startActivity( intent );
    }

    // Klikniecie przycisku Receive
    public void onClickReceive( View view )
    {
        Intent intent = new Intent( MainActivity.this, RecordSound.class );

        MainActivity.this.startActivity( intent );
    }
}
