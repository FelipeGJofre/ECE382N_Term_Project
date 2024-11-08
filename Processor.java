import java.util.ArrayList;
import java.lang.Integer;

import java.io.*;
import java.util.Scanner;
import java.net.Socket;

package ECE382N_Term_Project;

public class Processor extends Thread{
    Thread receiveThread;
    Thread program_thread;

    
    public int send(String message, int message_type, ArrayList<Integer> vector_clock){
        return 0;
    }

    public String receive(int message_type){
        return "Hello World";
    }

    public void run(){
        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("Hello\n");
                }
            }
        });
    }
}