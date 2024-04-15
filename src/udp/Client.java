package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client 
{
    public static void main(String[] args)
    {
        final String SERVER_ADDRESS  = "localhost";
        final int SERVER_PORT = 1234;

        try
        {
            DatagramSocket clientSocket = new DatagramSocket();

            String message = "Hello Server!";
            byte[] sendData = message.getBytes();

            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
            clientSocket.send(sendPacket);

            /* 
             *      RECEIVING PACKET FROM SERVER
             */
            byte[] receivedData = new byte[1024];
            DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
            clientSocket.receive(receivedPacket);

            String serverAnswer = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

            System.out.println("Message from server: " + serverAnswer);

            clientSocket.close();
        }
        catch(Exception e)
        {
            System.out.println("Error ocurred in Client -> main -> " + e.getMessage());
        }
    }    
}
