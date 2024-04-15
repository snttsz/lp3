package ex01;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client 
{
    public static void main(String[] args)
    {
        try
        {
            Socket client = new Socket("localhost", 1234);

            ObjectInputStream inputBroadcast = new ObjectInputStream(client.getInputStream());
            ObjectOutputStream outputBroadcast = new ObjectOutputStream(client.getOutputStream());

            String firstMessage = (String) inputBroadcast.readObject();

            System.out.println("Message received from Server: " + firstMessage);

            outputBroadcast.writeObject("Hello Server! I'm here!");
            
            String echo = (String) inputBroadcast.readObject();

            System.out.println("Message received from Server: " + echo);

            inputBroadcast.close();
            outputBroadcast.close();

            System.out.println("Connection finished!");

            client.close();
        }
        catch(Exception e)
        {
            System.out.println("Error ocurred in Client -> main -> " + e.getMessage());
        }
    }
}
