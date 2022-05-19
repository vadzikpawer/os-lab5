package com.vadzik;

import java.util.Scanner;

public class Main {

    static Planner planner = new Planner();
    static String[] Menu = {"Приостановвить\\продолжить исполнение процесса\n","Изменить приоритет процесса\n", "Показать состояние процессов\n","Закончить работу\n"};
    static boolean isExit = false;
    static Scanner in = new Scanner(System.in);


    public static void main(String[] args) throws InterruptedException {
        Thread plan = new Thread(planner, "Planner");
        plan.start();
        //plan.join();
        while(!isExit){
            printMenu();
            System.out.print("Введите пункт меню: ");
            int option = in.nextInt();
            switch (option){
                case 1:
                    Planner.showThreads();
                    System.out.print("Введите номер процесса для приостановки\\возобновления: ");
                    int num = in.nextInt();
                    Planner.pauseTask(num-1);
                    break;
                case 2:
                    Planner.showThreads();
                    System.out.print("Введите номер процесса для изменения приоритета: ");
                    int numTh = in.nextInt();
                    System.out.print("Введите новое значение приоритета процесса: ");
                    int prTh = in.nextInt();
                    Planner.changePriority(numTh-1, prTh);
                    break;
                case 3:
                    Planner.showThreads();
                    break;
                case 4:
                    isExit = true;
                    planner.exit();
                    break;
                default:
                    System.out.print("Введен неверный вариант!!!\n\n");
                    break;
            }
        }

    }

    static void printMenu(){
        for (int i =0; i <Menu.length;i++){
            System.out.printf("%d. %s", i+1, Menu[i]);
        }
    }
}
