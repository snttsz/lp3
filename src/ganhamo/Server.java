package ganhamo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Chat implements Runnable
{
    public Chat()
    {
        this.clients = new ArrayList<>();
    }

    public void addClient(ClientHandler client)
    {
        this.clients.add(client);
        System.out.println("Clients size -> " + this.clients.size());
        client.sendMessage("Você foi conectado no chat com o nome " + client.getClientName() + ". Você pode enviar mensagem para outros clientes e receber mensagens. As mensagens que são recebidas no chat estarão acompanhadas pelas barras '==='.");
    }

    public void run()
    {
        while (true)
        {
            try 
            {
                Thread.sleep(1);
            } catch (InterruptedException e) 
            {
                e.printStackTrace();
            }

            for (Iterator<ClientHandler> iterator = this.clients.iterator(); iterator.hasNext();) 
            {
                ClientHandler client = iterator.next();
                String message = client.getClientBuffer();
                
                if (client.getIsClientDisconnected()) 
                {
                    iterator.remove();
                    message = "Client " + client.getClientName() + " foi desconectada(o).";
                    this.sendToAllClients(message, "Servidor");
                } 
                else if (!message.isEmpty() && !message.equals(this.syncMessage)) 
                {
                    System.out.println("Client " + client.getClientName() + " sent -> " + message);
                    this.sendToAllClients(message, client.getClientName());
                    client.clearClientBuffer();
                } 
                else 
                {
                    this.sendToAllClients(syncMessage, "none");
                    client.clearClientBuffer();
                }
            }
        }
    }

    private void sendToAllClients(String message, String clientName)
    {
        String messageFromClient;

        if (clientName.equals("none"))
        {
            messageFromClient = this.syncMessage;
        }
        else
        {
            messageFromClient = clientName + " disse: " + message;
        }

        for (ClientHandler client : this.clients)
        {   
            client.sendMessage(messageFromClient);
        }
    }
    
    private List<ClientHandler> clients;
    private String syncMessage = "91929394000abcs3lk2oijekwjednnn";
}

class ClientHandler implements Runnable
{
    public ClientHandler(Socket socket)
    {
        this.clientSocket = socket;
    }

    public boolean checarAutenticacao(String senhaParaEntrar)
    {
        boolean result = false;

        try
        {
            this.clientOutputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
            this.clientInputStream = new ObjectInputStream(this.clientSocket.getInputStream());

            clientOutputStream.writeObject("Envie a senha na próxima mensagem.");

            String messageReceived = (String) clientInputStream.readObject();

            if (messageReceived.equals(senhaParaEntrar))
            {
                result = true;
                
                clientOutputStream.writeObject("Senha correta. Digite seu nome na próxima mensagem para se conectar no chat.");
                
                this.clientName = (String) clientInputStream.readObject();
            }
            else
            {
                clientOutputStream.writeObject("vaza vagner");

                clientInputStream.close();
                clientOutputStream.close();
                this.clientSocket.close();
            }
        }
        catch (Exception e)
        {
            // System.out.println("ClientHandler -> checarAutenticacao() -> Exception occurred with client " + this.clientName + " -> " + this.clientSocket.getPort() + " -> " + e.getMessage());
        }

        return result;
    }

    public void run()
    {
        while (true)
        {
            try
            {
                this.clientBuffer = (String) clientInputStream.readObject();
            }
            catch (Exception e)
            {
                this.isClientDisconnected = true;
                // System.out.println("ClientHandler -> getClientBuffer() -> Exception occurred with client " + this.clientSocket.getPort() + " -> " + e.getMessage());
            }
        }
    }

    public String getClientBuffer()
    {
        return this.clientBuffer;
    }

    public String getClientName()
    {
        return this.clientName;
    }

    public void clearClientBuffer()
    {
        this.clientBuffer = "";
    }

    public boolean getIsClientDisconnected()
    {
        return this.isClientDisconnected;
    }

    public void sendMessage(String message)
    {
        try
        {
            this.clientOutputStream.writeObject(message);
        }
        catch (Exception e)
        {
            this.isClientDisconnected = true;
            // System.out.println("ClientHandler -> sendMessage() -> Exception occurred with client " + this.clientSocket.getPort() + " -> " + e.getMessage());
        }
    }

    public ObjectInputStream getClObjectInputStream()
    {
        return this.clientInputStream;
    }

    private Socket clientSocket;
    private String clientName = "Intruso";
    private String clientBuffer = "";
    private boolean isClientDisconnected = false;
    private ObjectOutputStream clientOutputStream;
    private ObjectInputStream clientInputStream;
}

class ClientListener implements Runnable
{

    public ClientListener(ServerSocket serverSocket, Chat chat, String senhaParaEntrar)
    {
        this.serverSocket = serverSocket;
        this.chat = chat;
        this.senhaParaEntrar = senhaParaEntrar;
    }

    public void run()
    {
        try
        {
            while (true)
            {   
                Socket client = this.serverSocket.accept();
                System.out.println("Got new connection from client: " + client.getPort());

                ClientHandler newClient = new ClientHandler(client);
                
                if (newClient.checarAutenticacao(this.senhaParaEntrar)) 
                {
                    this.chat.addClient(newClient);
                    Thread clientThread = new Thread(newClient);
                    clientThread.start();
                }
            }
        }
        catch (Exception e)
        {   
            System.out.println("ClientListener -> run -> Error ocurred -> " + e.getMessage());
        }
    }

    private ServerSocket serverSocket;
    private String senhaParaEntrar;
    private Chat chat;
}

public class Server 
{
    public static void main(String[] args)
    {
        int port = Integer.valueOf(args[0]);

        try
        {       
            String senhaParaEntrar = "viado";

            ServerSocket server = new ServerSocket(port);
            System.out.println("Initialized server on port " + port);

            Chat chat = new Chat();
            ClientListener clientListener = new ClientListener(server, chat, senhaParaEntrar);

            Thread newThread = new Thread(clientListener);
            newThread.start();
            
            chat.run();
        }
        catch (Exception e)
        {   
            System.out.println("Server -> main -> Error ocurred -> " + e.getMessage());
        }
    }
}
