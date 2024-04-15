package thread_contagem;

import java.util.ArrayList;
import java.util.List;

public class Main 
{
    public static void main(String[] args)
    {
        Thread thread1 = new Thread(new Printer(0));
        Thread thread2 = new Thread(new Printer(4));
        Thread thread3 = new Thread(new Printer(8));
        Thread thread4 = new Thread(new Printer(12));

        thread1.run();
        thread2.run();
        thread3.run();
        thread4.run();

        // for (int i = 0; i < 10; i++)
        // {
        //     Thread thread = new Thread(new Printer(i));
        // }
    }
}

class Printer implements Runnable
{
    public Printer(int number)
    {
        this.number = number;
    }

    public void run()
    {
        // synchronized (lock) 
        // {
        //     while (counter != number) 
        //     {
        //         try 
        //         {
        //             lock.wait();
        //         } 
        //         catch (InterruptedException e) 
        //         {
        //             e.printStackTrace();
        //         }
        //     }

        //     System.out.println(Thread.currentThread().getName() + ": " + number);
        //     counter++;
        //     lock.notifyAll();
        // }
        for (int i = this.number; i < this.number + 4; i++)
        {
            System.out.println(Thread.currentThread().getName() + ": " + i);
        }
    }

    // private static Object lock = new Object();
    // private static int counter = 1;
    private int number;
}
