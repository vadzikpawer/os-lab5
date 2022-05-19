package com.vadzik;

public class threadFactorial extends planThread {

    String name;
    int priority;
    int quantum;
    long MAX_FACTORIAL = 1_000_000_000;

    threadFactorial(String name, int priority, int quantum) {
        super(name, priority);
        this.name = name;
        this.priority = priority;
        this.quantum = quantum;
    }

    @Override
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
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (state.equals("Ready")) {
                    state = "Running";
                    //System.out.println("Factorial work");
                    //Planner.showThreads();
                    for (int j = 0; j < quantum; j++) {
                        if (Planner.isPause) {
                            Planner.currentId = "Planner";
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (tContext.value < MAX_FACTORIAL) {
                            tContext.i++;
                            tContext.value = tContext.value * tContext.i;
                            if (!state.equals("Wait")) {
                                state = "Ready";
                            }
                            try {
                                lock.wait(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            state = "Done";
                        }
                    }
                }
                if (Planner.plan.size() != 0) {
                    Planner.currentId = Planner.plan.get(0);
                    Planner.plan.remove(0);
                } else Planner.currentId = "Planner";
                lock.notifyAll();
                //System.out.println("Factorial done");
            }
        }
    }
}
