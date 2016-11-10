package com.example.tuchiyama.aidlsample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {

    Button startButton;
    Button stopButton;
    JankenInterface mService;
    private UpdateReceiver upReceiver;
    private IntentFilter intentFilter;
    private TextView result;
    int gamesIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = this;
        Intent update_service = new Intent(context , MyService.class);
        startService(update_service);

        upReceiver = new UpdateReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("UPDATE_ACTION");
        registerReceiver(upReceiver, intentFilter);

        upReceiver.registerHandler(updateHandler);

        Intent intent = new Intent(this, MyService.class);
        bindService(intent,mServiceConnection, BIND_AUTO_CREATE);
        setContentView(R.layout.activity_main);

        startButton = (Button)findViewById(R.id.start_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gamesIndex = 1;
                TextView result = (TextView)findViewById(R.id.result);
                startService(new Intent(getBaseContext(), MyService.class));
                try {
                    int number = Integer.parseInt(((EditText) findViewById(R.id.number)).getText().toString());
                    try {
                        result.setText("参加者: "+number+"人\n");
                        mService.startJanken(number);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }catch(NumberFormatException e) {
                    result.setText("数値を入力してください");
                }

            }
        });
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
            mService = JankenInterface.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };

    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("test", "handleMessage");
            result = (TextView)findViewById(R.id.result);
            Bundle bundle = msg.getData();
            String message = bundle.getString("message");
            String prefix = "第"+gamesIndex+"戦 ";
            gamesIndex ++;
            result.append(prefix+message);
        }
    };
}
