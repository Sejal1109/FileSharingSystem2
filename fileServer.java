import java.io.*;
import java.net.*;
import java.util.Vector;

public class fileServer {
	protected Socket clientSocket           = null;
	protected ServerSocket serverSocket     = null;
	protected fileServerThread[] threads    = null;
	protected int numClients                = 0;
	protected Vector messages               = new Vector();

	public static int SERVER_PORT = 16789;
	public static int MAX_CLIENTS = 25;

	public fileServer() {
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			System.out.println("---------------------------");
			System.out.println("File Server  is running");
			System.out.println("---------------------------");
			System.out.println("Listening to port: "+SERVER_PORT);
			threads = new fileServerThread[MAX_CLIENTS];
			while(true) {
				clientSocket = serverSocket.accept();
				System.out.println("Client #"+(numClients+1)+" connected.");
				threads[numClients] = new fileServerThread(clientSocket, messages);
				threads[numClients].start();
				numClients++;
			}
		} catch (IOException e) {
			System.err.println("IOException while creating server connection");
		}
	}

	public static void main(String[] args) {
		fileServer app = new fileServer();
	}
}