package com.vadzik;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class threadHash extends planThread {

    String content;
    int quantum;

    public threadHash(String c, String name, int priority, int quantum) {
        super(name, priority);
        this.content = c;
        this.quantum = quantum;
        StringBuilder selected = new StringBuilder();
        char[] trial = new char[5];
        for (int z = 0; z < 5; z++) trial[z] = 'a';

        selected = new StringBuilder("");
        for (int z = 0; z < 5; z++) {
            selected.append(trial[z]);
        }
        tContext.curString = selected.toString();
    }

    public static String Hash(String s) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashInBytes = md.digest(s.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();

    }

    public void run() {
        while (!state.equals("Done")) {
            synchronized (lock) {
                if (exit) {
                    lock.notifyAll();
                    return;
                }
                while (!Planner.currentId.equals(name) || paused) {
                    try {
                        lock.wait();
                        if (exit) {
                            lock.notifyAll();
                            return;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (state.equals("Ready") && !exit) {
                    state = "Running";
                    //System.out.println("Hash-1 work");
                    for (int i = 0; i < quantum; i++) {
                        if (Planner.isPause) {
                            if (exit) break;
                            Planner.currentId = "Planner";
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        tContext.curString = produce();
                        try {
                            if (tContext.curString != null && Hash(tContext.curString).equals(this.content)) {
                                state = "Done";
                                //Planner.getThState(name);
                                break;
                            }
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!state.equals("Done")) {
                        state = "Ready";
                    }
                    if (Planner.plan.size() != 0) {
                        Planner.currentId = Planner.plan.get(0);
                        Planner.plan.remove(0);
                    } else Planner.currentId = "Planner";
                    lock.notifyAll();
                    //System.out.println("Hash-1 done");
                }
            }
        }
    }


    public String produce() {
        char[] trial = new char[5];
        for (int z = 0; z < 5; z++) {
            trial[z] = tContext.curString.charAt(z);
        }


        trial[4] = (char) (trial[4] + 1);
        for (int k = 4; k > 0; k--) {
            if ((trial[k] - 'a') > 25) {
                trial[k - 1] = (char) (trial[k - 1] + 1);
                trial[k] = 'a';
            }
        }
        StringBuilder selected = new StringBuilder("");
        for (int z = 0; z < 5; z++) {
            selected.append(trial[z]);
        }

        return selected.toString();
    }
}
