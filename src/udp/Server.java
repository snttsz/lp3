package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class ClientHandler extends Thread
{
    public ClientHandler(DatagramSocket serverSocket, DatagramPacket receivedPacket)
    {
        this.serverSocket = serverSocket;
        this.receivedPacket = receivedPacket;
    }

    public void run()
    {
        try
        {
            String message = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
            InetAddress clientAddress = receivedPacket.getAddress();
            int clientPort = receivedPacket.getPort();

            System.out.println("Message received from client " + clientAddress + ":" + clientPort + " -> " + message);

            String answer = "Got your message! You said: " + message;
            byte[] sendData = answer.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
            this.serverSocket.send(sendPacket);
        }   
        catch (Exception e)
        {
            System.out.println("Error ocurred in Client -> main -> " + e.getMessage());
        }
    }

    private DatagramSocket serverSocket;
    private DatagramPacket receivedPacket;
}

public class Server 
{
    public static void main(String[] args)
    {
        final int PORT = 1234;

        try
        {
            DatagramSocket serverSocket = new DatagramSocket(PORT);

            System.out.println("UDP Server listening on port 1234...");

            while (true)
            {
                byte[] receivedData = new byte[1024];
                DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);

                serverSocket.receive(receivedPacket);

                new ClientHandler(serverSocket, receivedPacket).start();
            }
        }
        catch(Exception e)
        {
            System.out.println("Server -> main -> Error ocurred -> " + e.getMessage());
        }
    }
}
