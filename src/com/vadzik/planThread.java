package com.vadzik;

public class planThread implements Runnable{

    String state;
    String name;
    threadContext tContext;
    int priority;
    boolean paused = false;
    final static Object lock = Planner.getLock();
    boolean exit = false;

    planThread(String name, int priority){
        this.name = name;
        state = "Ready";
        tContext = new threadContext(name);
        this.priority = priority;
    }

    public void run() {}

    public int getPriority(){
        return priority;
    }
}
