package com.example.tuchiyama.aidlsample;

interface IMyAidlInterface {

    /**
    * @param number
    * @return
    */
    int[] timer(int number);

    int getPid();

    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
