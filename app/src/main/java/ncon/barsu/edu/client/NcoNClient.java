package ncon.barsu.edu.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NcoNClient {
    private Socket CSock;
    private ObjectInputStream InStream;
    private ObjectOutputStream OutStream;

    public NcoNClient(int PORT) {
        try {
            InetSocketAddress IOEx = new InetSocketAddress(MainActivity.Host, PORT);
            CSock = new Socket();
            CSock.connect(IOEx, 0);
            if(CSock.isConnected()) {
                throw new IOException();
            }
        } catch (IOException var4) {
            System.out.println(var4);
        }

    }

    public String receive() throws IOException, ClassNotFoundException {
        return InStream.readObject().toString();
    }

    public boolean isConnected() {
        return this.CSock.isConnected();
    }

    public void send(String RecMessage) throws IOException {
        OutStream.writeObject(RecMessage);
        OutStream.flush();
    }

    public void OpenStream() {
        try {
            InStream = new ObjectInputStream(this.CSock.getInputStream());
            OutStream = new ObjectOutputStream(this.CSock.getOutputStream());
            System.out.println("P" + this.CSock.getPort() + ". Streams opened!");
        } catch (IOException var2) {
            System.out.println("P" + this.CSock.getPort() + ". Streams opened error!" + var2);
        }

    }

    public void CloseStream() {
        try {
            OutStream.close();
            InStream.close();
            CSock.close();
            System.out.println(CSock.getPort() + ". Streams closed!");
        } catch (IOException var2) {
            System.out.println(CSock.getPort() + ". Streams closed error!" + var2);
        }

    }
}
