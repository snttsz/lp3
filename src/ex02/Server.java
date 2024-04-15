package ex02;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
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
            ObjectInputStream inputBroadcast = new ObjectInputStream(this.clientSocket.getInputStream());
            
            DataInputStream dataInputStream = new DataInputStream(inputBroadcast);
            long fileSize = dataInputStream.readLong();
            String fileName = dataInputStream.readUTF();

            FileOutputStream fileBroadcast = new FileOutputStream("ex02/file/" + fileName);
            byte[] buffer = new byte[1024];
            int bytesRead = 0;

            while (fileSize > 0 && (bytesRead = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) 
            {
                fileBroadcast.write(buffer, 0, bytesRead);
                fileSize -= bytesRead;
            }        

            System.out.println("Received file from " + this.clientSocket.getInetAddress() + ":" + this.clientSocket.getPort() + " -> " + fileName);

            inputBroadcast.close();
            fileBroadcast.close();
            this.clientSocket.close();
        } 
        catch (Exception e) 
        {
            System.out.println("ClientHandler -> run() -> Exception occurred with client " + this.clientSocket.getPort() + " -> " + e.getMessage());
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
