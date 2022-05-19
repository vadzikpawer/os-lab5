package com.vadzik;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;

public class Planner implements Runnable {

    static final ArrayList<planThread> planning = new ArrayList<>();
    private final static Object lock = new Object();
    static private final Object canPause = new Object();
    static int quantum = 2;
    static Thread[] planningThreads = new Thread[3];
    static boolean isPause = false;
    static String currentId = "Planner";
    static ArrayList<String> plan = new ArrayList<>();
    boolean exit = false;

    Planner() {
        planning.add(new threadHash("74e1bb62f8dabb8125a58852b63bdf6eaef667cb56ac7f7cdba6d7305c50a22f", "Hash-1", 5, quantum));
        planning.add(new threadHash("3a7bd3e2360a3d29eea436fcfb7e44c735d117c42d1c1835420b6b9942dd4f1b", "Hash-2", 2, quantum));
        planning.add(new threadFactorial("Factorial", 1, quantum));
        planningThreads[0] = new Thread(planning.get(0), planning.get(0).name);
        planningThreads[1] = new Thread(planning.get(1), planning.get(1).name);
        planningThreads[2] = new Thread(planning.get(2), planning.get(2).name);
    }

    public static Object getLock() {
        return lock;
    }

    public static void pauseTask(int num) {
        synchronized (lock) {
            try {
                isPause = true;
                lock.notifyAll();
                //System.out.println("num приостановлен");
                planning.get(num).paused = !(planning.get(num).paused);
                if (planning.get(num).state.equals("Wait"))
                    planning.get(num).state = "Ready";
                else if (!planning.get(num).state.equals("Wait"))
                    planning.get(num).state = "Wait";
            } catch (IndexOutOfBoundsException ignored) {
            } finally {
                isPause = false;
                lock.notifyAll();
            }
        }
    }

    public static void changePriority(int num, int newPriority) {
        synchronized (lock) {
            try {
                isPause = !isPause;
                lock.notifyAll();
                planning.get(num).priority = newPriority;
            } catch (IndexOutOfBoundsException ignored) {
            } finally {
                isPause = false;
                lock.notifyAll();
            }
        }

    }

    public static void showThreads() {
        for (int i = 0; i < 65; i++) System.out.print("_");
        System.out.print("\n");
        System.out.printf("|%20s|%10s|%10s|%20s|\n", "Имя потока", "Приоритет", "Состояние", "Значение");
        for (int i = 0; i < planning.size(); i++) {
            if (!planning.get(i).name.contains("Hash")) {
                System.out.printf("| %18s | %8d | %8s | %19d|\n",
                        planning.get(i).name, planning.get(i).priority, planning.get(i).state, planning.get(i).tContext.value);
            } else {
                System.out.printf("| %18s | %8d | %8s | %19s|\n",
                        planning.get(i).name, planning.get(i).priority, planning.get(i).state, planning.get(i).tContext.curString);
            }
        }
        for (int i = 0; i < 65; i++) System.out.print("_");
        System.out.print("\n");
    }

    public void exit() {
        synchronized (lock) {
            exit = true;
            for (planThread t : planning) {
                t.exit = true;
            }
            lock.notifyAll();
        }
    }

    public void run() {
        for (Thread i : planningThreads) {
            i.start();
        }
        while (!isDone()) {
            if (exit) break;
            //showThreads();
            try {
                if (isPause) continue;
                perfomTasks();
            } catch (InterruptedException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        if (isDone()) {
            System.out.print("Все задачи выполнены\n");
            showThreads();
        } else {
            synchronized (lock) {
                System.out.print("Планировщик завершил свое выполнение\n");
                showThreads();
                lock.notifyAll();
                /*for (Thread t : planningThreads) {
                    System.out.println(t.getState());
                }*/
            }
        }
    }

    void perfomTasks() throws InterruptedException, NoSuchAlgorithmException {
        synchronized (lock) {
            planning.sort(Comparator.comparing(planThread::getPriority).reversed());
            for (int i = 0; i < planning.size(); i++) {
                if (!planning.get(i).state.equals("Done")) {
                    if (planning.get(i).state.equals("Ready")) {
                        plan.add(planning.get(i).name);
                    }
                }
            }
            if (plan.size() != 0) {
                currentId = plan.get(0);
                plan.remove(0);
            }
            lock.notifyAll();
            while (!currentId.equals("Planner")) {
                lock.wait();
            }
        }
    }

    boolean isDone() {
        for (int i = 0; i < planning.size(); i++) {
            if (!planning.get(i).state.equals("Done"))
                return false;
        }
        return true;
    }

   public static void getThState(String name){
       for (Thread t: planningThreads) {
           if (t.getName().equals(name)) System.out.println(t.getState());
       }
    }

}
