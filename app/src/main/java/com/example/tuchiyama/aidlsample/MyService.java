package com.example.tuchiyama.aidlsample;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.os.Process;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service{
    private Timer mTimer = null;
    Handler handler = new Handler();
    Boolean isStart;
    int[] members;

    public MyService() {
    }

    final static String TAG = "MyService";

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    public void callJanken(){
        if(isStart){
            int[] winner = janken(members);
            members = winner;
        }else{
            mTimer.cancel();
            mTimer = null;
        }
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }

    public synchronized void sleep(long msec) {
        try {
            wait(msec);
        } catch (InterruptedException e) {
        }
    }

    public void registerHandler(Handler UpdateHandler) {
        handler = UpdateHandler;
    }

    protected void sendBroadCast(String message) {

        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra("message", message);
        broadcastIntent.setAction("UPDATE_ACTION");
        getBaseContext().sendBroadcast(broadcastIntent);

    }

    public int[] janken(int[] members){
        if(members.length == 1){
            sendBroadCast("勝者: "+printArray(members)+"\n");
            endJanken();
            return new int[0];
        }
        ArrayList<Integer> gu = new ArrayList<>();
        ArrayList<Integer> choki = new ArrayList<>();
        ArrayList<Integer> pa = new ArrayList<>();
        Random rnd = new Random();

        int aiko = 0;
        do {
            gu.clear();
            choki.clear();
            pa.clear();
            for (int member : members) {
                int r = rnd.nextInt(3);
                switch (r) {
                    case 0:
                        gu.add(member);
                        break;
                    case 1:
                        choki.add(member);
                        break;
                    case 2:
                        pa.add(member);
                        break;
                }
            }
            aiko = 0;
            if (gu.size() > 0) aiko++;
            if (choki.size() > 0) aiko++;
            if (pa.size() > 0) aiko++;
        }while(aiko%2 != 0);
        int[] winners = {};
        if(gu.size() > 0){
            if(choki.size() > 0){
                winners = arrayListToArray(gu);
            }else{ //pa
                winners = arrayListToArray(pa);
            }
        }
        if(choki.size() > 0){
            if(pa.size() > 0){ //pa
                winners = arrayListToArray(choki);
            }else{ //gu
                winners = arrayListToArray(gu);
            }
        }
        sendBroadCast("敗者: "+printArray(difference(members, winners))+"\n");
        return  winners;
    }

    public String printArray(int[] array){
        String result = "";
        for(int a : array){
            result += " "+a;
        }
        return result;
    }

    public void endJanken(){
        isStart = false;
    }

    /**
     *
     * @param array1 > b
     * @param array2 < a
     * @return difference
     */
    public int[] difference(int[]array1, int[] array2){
        if(array2.length == 0){
            return array1;
        }
        Arrays.sort(array1);
        Arrays.sort(array2);
        int[] result = new int[array1.length];
        int ri = 0;
        int a2i=0;
        for(int a1i=0; a1i<array1.length; a1i++){
            for(; a2i<array2.length; a2i++){
                if(array1[a1i] == array2[a2i]){
                    a2i++;
                    break;
                }else if(array1[a1i] < array2[a2i]){
                    result[ri] = array1[a1i];
                    ri ++;
                    break;
                }
            }
            if(array2[array2.length-1] < array1[a1i]){
                result[ri] = array1[a1i];
                ri++;
            }
        }
        return Arrays.copyOfRange(result, 0, ri);
    }

    public int[] arrayListToArray(ArrayList<Integer> arraylist){
        int[] array = new int[arraylist.size()];
        for(int i=0; i<array.length; i++){
            array[i] = arraylist.get(i);
        }
        return array;
    }

    private final JankenInterface.Stub mBinder = new JankenInterface.Stub(){
        @Override
        public void startJanken(int number){
            isStart = true;
            members = new int[number];
            for(int i=0; i<number; i++){
                members[i] = i;
            }
            mTimer = new Timer(true);
            mTimer.schedule( new TimerTask(){
                @Override
                public void run(){
                    handler.post( new Runnable(){
                        public void run(){
                            callJanken();
                        }
                    });
                }
            }, 1000, 1000);
        }

        @Override
            public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
        }
    };



}
