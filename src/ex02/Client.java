package ex02;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client 
{
    public static void main(String[] args)
    {
        try
        {
            Socket client = new Socket("localhost", 1234);

            ObjectOutputStream outputBroadcast = new ObjectOutputStream(client.getOutputStream());

            File file = new File("ex02/teste2.txt");
            long fileSize = file.length();
            String filename = file.getName();

            DataOutputStream dataOutputStream = new DataOutputStream(outputBroadcast);
            dataOutputStream.writeLong(fileSize);
            dataOutputStream.writeUTF(filename);

            FileInputStream fileBroadcast = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead = 0;

            while ((bytesRead = fileBroadcast.read(buffer)) != -1) 
            {
                outputBroadcast.write(buffer, 0, bytesRead);
            }

            fileBroadcast.close();
            outputBroadcast.close();
            client.close();
        }
        catch(Exception e)
        {
            System.out.println("Error ocurred in Client -> main -> " + e.getMessage());
        }
    }    
}
