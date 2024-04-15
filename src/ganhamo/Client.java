package ganhamo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

class GetKeyboardBuffer implements Runnable
{
    public GetKeyboardBuffer(Scanner scan)
    {
        this.scan = scan;
    }

    public void run()
    {
        try
        {
            while (true)
            {
                this.keyboardInput = this.scan.nextLine();
            }
        }
        catch (Exception e)
        {
            this.scan.close();
        }
    }

    public String getKeyboardInput()
    {
        return this.keyboardInput;
    }

    public void clearKeyboardInput()
    {
        this.keyboardInput = "";
    }
    
    private String keyboardInput = "";
    private Scanner scan;
}

public class Client 
{
    public static void main(String args[])
    {
        String host = args[0];
        int port = Integer.valueOf(args[1]);
        String syncMessage = "91929394000abcs3lk2oijekwjednnn";

        Scanner prompt = new Scanner(System.in);
        GetKeyboardBuffer keybBuffer = new GetKeyboardBuffer(prompt);

        Thread newThread = new Thread(keybBuffer);

        try
        {
            Socket client = new Socket(host, port);

            ObjectOutputStream outputBroadcast = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream inputBroadcast = new ObjectInputStream(client.getInputStream());

            String firstMessage = (String) inputBroadcast.readObject();

            System.out.println("Mensagem do servidor: " + firstMessage);
            System.out.print("Digite sua mensagem: ");

            // INÍCIO AUTENTICAÇÃO
            String senha = prompt.nextLine();

            outputBroadcast.writeObject(senha);

            String outputSenha = (String) inputBroadcast.readObject();

            System.out.println("Mensagem do servidor: " + outputSenha);

            if (outputSenha.equals("vaza vagner"))
            {
                System.out.println("Você inseriu a senha incorreta e foi desconectada(o) do servidor :( ...");
                outputBroadcast.close();
                inputBroadcast.close();
                client.close();
                System.exit(0);
            }

            System.out.print("Digite sua mensagem: ");

            String seuNome = prompt.nextLine();

            outputBroadcast.writeObject(seuNome);

            String outputNome = (String) inputBroadcast.readObject();

            if (!outputNome.equals(syncMessage))
            {
                System.out.println("=================");
                System.out.println("Mensagem do servidor: " + outputNome);
                System.out.println("=================");
            }
            // FIM AUTENTICAÇÃO

            newThread.start();

            try 
            {
                Thread.sleep(1);
            } catch (InterruptedException e) 
            {
                e.printStackTrace();
            }

            while (true)
            {

                if (keybBuffer.getKeyboardInput().isEmpty())
                {
                    outputBroadcast.writeObject(syncMessage);
                }
                else
                {
                    outputBroadcast.writeObject(keybBuffer.getKeyboardInput());
                    keybBuffer.clearKeyboardInput();
                }
                
                String outputMessage = (String) inputBroadcast.readObject();

                if (!outputMessage.equals("91929394000abcs3lk2oijekwjednnn"))
                {
                    System.out.println("\n=================");
                    System.out.println(outputMessage);
                    System.out.println("=================");
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("Error ocurred in Client -> main -> " + e.getMessage());
            System.exit(0);
        }
    }
}
