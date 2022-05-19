package com.vadzik;

public class threadContext {

    String threadName;
    long value;
    long i;
    String curString;

    threadContext(String name){
        threadName = name;
        i = 1;
        value = 1;
    }
}
