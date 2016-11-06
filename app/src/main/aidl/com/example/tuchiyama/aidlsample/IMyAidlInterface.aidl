package com.example.tuchiyama.aidlsample;

interface IMyAidlInterface {

    /**
    * @param hour
    * @param minute
    * @return
    */
    void timer(int hour, int minute);

    int getPid();

    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
