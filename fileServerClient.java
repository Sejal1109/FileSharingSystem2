import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.awt.event.*;
import java.awt.*;

public class fileServerClient extends Frame {
	private Socket socket = null;
	private BufferedReader in = null;
	private PrintWriter networkOut = null;
	private BufferedReader networkIn = null;
	public static String ClientFolder;
	public static String CompName;

	//we can read this from the user too
	public  static String SERVER_ADDRESS = "localhost";
	public  static int    SERVER_PORT = 16789;

	public fileServerClient() {


		try {
			socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
			in = new BufferedReader(new InputStreamReader(System.in));
		} catch (UnknownHostException e) {
			System.err.println("Unknown host: "+SERVER_ADDRESS);
		} catch (IOException e) {
			System.err.println("IOEXception while connecting to server: "+SERVER_ADDRESS);
		}
		if (socket == null) {
			System.err.println("socket is null");
		}
		try {
			networkOut = new PrintWriter(socket.getOutputStream(), true);
			networkIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			System.err.println("IOEXception while opening a read/write connection");
		}

		boolean ok;
		ok = processUserInput();

		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Display Menu  of actions
	// Alternatively you can always be in "reading mode" whatever is typed gets send to the server/other clients without they having to "List all messages"
	// -- This would work 100x better and easier if you make at least the client a JavaFX application, the user can type in a textbox, when pressed <enter> you send the message
	// --- Every time the server gets a message they send to all the other clients who get their UI refreshed with the most recent messages, etc.
	protected boolean processUserInput() {
		String input = null;

		// print the menu
		System.out.println("Commands: ");
		System.out.println("DIR -  To display contents of Server Directory");
		System.out.println("UPLOAD - to upload a file to the server");
		System.out.println("DOWNLOAD - to download a file from server");
		System.out.println("QUIT");
		System.out.print("Command> ");

		try {
			input = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (input.equals("DIR")) {
			listAllFiles();
		} else if (input.equals("UPLOAD")) {
			uploadNewFile();
		} else if(input.equals("DOWNLOAD")){
			downloadFileFromServer();
		}
		else if (input.equals("QUIT")) {
			return false;
		} else{
			System.out.println("Invalid Command. Please Try Again! ");
		}
		return true;
	}

	//menu option 3
	public void downloadFileFromServer() {
		String fileName = null;
		String data = null;

		System.out.print("Filename: ");
		try {
			fileName = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		networkOut.println("DOWNLOAD " + fileName);
		try{
			data = networkIn.readLine();
			String data2w[] = data.split("/");

			File output = new File(ClientFolder + fileName);
			FileWriter fin = new FileWriter(output);
			for(int i=0; i<data2w.length; i++){
				fin.append(data2w[i] + "\n");
			}
			fin.flush();
			fin.close();

		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

	// menu option 2
	public void uploadNewFile() {
		String fileName = null;
		String message = null;

		System.out.print("Filename: ");
		try {
			fileName = in.readLine();
			File myFile = new File(ClientFolder + fileName);
			FileReader fr = new FileReader(myFile);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine())!= null){
				System.out.println(line);
			}
		} catch (IOException e) {
			System.err.println("Error reading from socket");
		}
		String filePath = ClientFolder + fileName;
		networkOut.println("UPLOAD " + filePath);
		try{
			message = networkIn.readLine();
		}
		catch (IOException e){
			System.err.println("Error reading from socket.");
		}
		System.out.println(message);
	}

	// menu option 1
	public void listAllFiles(){
		String File = null;
		networkOut.println("DIR");
		try {
			File = networkIn.readLine();
		} catch (IOException e) {
			System.err.println("Error reading from socket");
		}
		String files[] = File.split(",");

		for (int i=0; i<files.length; i++) {
			System.out.println(files[i]);
		}
	}

	public static void main(String[] args) {
		if (args.length <= 1) {
			System.out.println("Usage: java fileServerClient <Computer Name> <Client Folder Path> [<port>=80]");
			System.exit(0);
		}
			CompName = args[0];
			ClientFolder = args[1];

			fileServerClient client = new fileServerClient();
	}
}