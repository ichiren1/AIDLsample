package com.example.tuchiyama.aidlsample;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    Button startButton;
    Button stopButton;
    IMyAidlInterface mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MyService.class);
        bindService(intent,mServiceConnection, BIND_AUTO_CREATE);
        setContentView(R.layout.activity_main);

        startButton = (Button)findViewById(R.id.start_button);
//        stopButton = (Button)findViewById(R.id.stop_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(getBaseContext(), MyService.class));
                int hour = Integer.parseInt(((EditText)findViewById(R.id.hour)).getText().toString());
                int minute = Integer.parseInt(((EditText)findViewById(R.id.minute)).getText().toString());
                try {
                    mService.timer(hour, minute);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

//        stopButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                stopService(new Intent(getBaseContext(), MyService.class));
//            }
//        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mService != null){
            unbindService(mServiceConnection);
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = IMyAidlInterface.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };
}
