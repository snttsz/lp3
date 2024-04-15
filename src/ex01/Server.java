package ex01;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class ClientHandler extends Thread
{
    public ClientHandler(Socket socket)
    {
        this.clientSocket = socket;
    }

    public void run()
    {
        try
        {
            ObjectOutputStream outputBroadcast = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream inputBroadcast = new ObjectInputStream(clientSocket.getInputStream());

            String firstMessage = "Hello, I'm waiting for a message from you!";

            outputBroadcast.writeObject(firstMessage);
            outputBroadcast.flush();

            String received = (String) inputBroadcast.readObject();
            System.out.println("Client " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " sent a message: " + received);
            
            outputBroadcast.writeObject("Hello Client! You said: " + received);
            outputBroadcast.flush();

            inputBroadcast.close();
            outputBroadcast.close();
            clientSocket.close();
        }
        catch(Exception e)
        {
            System.out.println("ClientHandler -> run() -> Exception occurred in client " + this.clientSocket.getPort() + " -> " + e.getMessage());
        }
    }

    private Socket clientSocket;
}

public class Server 
{
    public static void main(String[] args)
    {
        try
        {
            ServerSocket server = new ServerSocket(1234);
            System.out.println("Server listening on port 1234");

            while (true) 
            {
                Socket client = server.accept();
                System.out.println("Got new connection from client: " + client.getPort());

                ClientHandler clientHandler = new ClientHandler(client);
                clientHandler.start();
            }
        }
        catch(Exception e)
        {
            System.out.println("Server -> main -> Error ocurred -> " + e.getMessage());
        }
    }
}
