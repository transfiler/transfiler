package programowanie.zespolowe.tfbeta;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    ImageButton pickButton, backListen, listenButton, sendButton, backSend;
    ImageView nuta, listening;
    TextView fileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeGUI();

    }

    private void initializeGUI(){
        pickButton = (ImageButton)findViewById(R.id.pickButton);
        backListen = (ImageButton)findViewById(R.id.backListen);
        listenButton = (ImageButton)findViewById(R.id.listenButton);
        nuta = (ImageView)findViewById(R.id.nuta);
        listening = (ImageView)findViewById(R.id.listening);
        sendButton = (ImageButton)findViewById(R.id.sendButton);
        backSend = (ImageButton)findViewById(R.id.backSend);
        fileName = (TextView)findViewById(R.id.fileName);
    }

    private void hideMenuShowListening(){
        pickButton.setVisibility(View.GONE);
        listenButton.setVisibility(View.GONE);
        backListen.setVisibility(View.VISIBLE);
        nuta.setVisibility(View.VISIBLE);
        listening.setVisibility(View.VISIBLE);
    }

    public void startListening(View v) {
        hideMenuShowListening();
    }

    private void showMenuHideListening(){
        pickButton.setVisibility(View.VISIBLE);
        listenButton.setVisibility(View.VISIBLE);
        backListen.setVisibility(View.GONE);
        nuta.setVisibility(View.GONE);
        listening.setVisibility(View.GONE);
    }


    public void backListening(View v){
        showMenuHideListening();
    }

    private void hideMenuShowSending(){
        Intent intent = new Intent( MainActivity.this, PlaySound.class );
        intent.putExtra( "message", FileBrowserActivity.binary );

        MainActivity.this.startActivity(intent);
    }



    public void pickFile(View v){

        Intent fileExploreIntent = new Intent(
                programowanie.zespolowe.tfbeta.FileBrowserActivity.INTENT_ACTION_SELECT_DIR,
                null, this,
                programowanie.zespolowe.tfbeta.FileBrowserActivity.class
        );
//  fileExploreIntent.putExtra(
//      ua.com.vassiliev.androidfilebrowser.FileBrowserActivity.startDirectoryParameter,
//      "/sdcard"
//  );//Here you can add optional start directory parameter, and file browser will start from that directory.
        startActivityForResult(
                fileExploreIntent,
                1
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == 1) {
            if(resultCode == this.RESULT_OK) {
                String path = data.getStringExtra(programowanie.zespolowe.tfbeta.FileBrowserActivity.returnFileParameter);
                String fileNameString = path.substring(path.lastIndexOf("/") + 1, path.length());
                fileName.setText(fileNameString);
                hideMenuShowSending();

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Klikniecie przycisku Receive
    public void onClickReceive( View view )
    {
        Intent intent = new Intent( MainActivity.this, RecordSound.class );

        MainActivity.this.startActivity(intent);
    }

    // Klikniecie przycisku Send
    public void onClickSend( View view )
    {
        Intent intent = new Intent( MainActivity.this, PlaySound.class );
        intent.putExtra( "message", FileBrowserActivity.binary + "E");

        MainActivity.this.startActivity( intent );
    }
}
