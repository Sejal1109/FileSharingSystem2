import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.*;

public class fileServerThread extends Thread {
	protected Socket socket       = null;
	protected PrintWriter out     = null;
	protected BufferedReader in   = null;

	protected String strUserID    = null;

	
	protected Vector messages     = null;

	public fileServerThread(Socket socket, Vector messages) {
		super();
		this.socket = socket;
		this.messages = messages;
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			System.err.println("IOEXception while opening a read/write connection");
		}
	}

	public void run() {
		// initialize interaction

		boolean endOfSession = false;
		while(!endOfSession) {
			endOfSession = processCommand();
		}
		try {
			socket.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	protected boolean processCommand() {
		String message = null;
		try {
			message = in.readLine();
		} catch (IOException e) {
			System.err.println("Error reading command from socket.");
			return true;
		}
		if (message == null) {
			return true;
		}
		StringTokenizer st = new StringTokenizer(message);
		String command = st.nextToken();
		String args = null;
		if (st.hasMoreTokens()) {
			args = message.substring(command.length()+1, message.length());
		}
		return processCommand(command, args);
	}

	protected boolean processCommand(String command, String arguments) {

		// these are the other possible commands
		if (command.equalsIgnoreCase("DIR")) {
			String filenames=" ";
			File allFiles = new File(".\\Server_data");
			if (allFiles.isDirectory()) {
				File[] content = allFiles.listFiles();
				for (File current: content) {
					filenames+=current.getName() + ",";
				}
				out.println(filenames);
			}
			return true;

		} else if (command.equalsIgnoreCase("UPLOAD")) {
			try{
				File myFile = new File(arguments);

				String v = myFile.getName();
				File dest = new File(".\\Server_data\\" + v);
				InputStream is = new FileInputStream(myFile);
				OutputStream os = new FileOutputStream(dest);

				byte[] buffer = new byte[1024];
				int length;
				while ((length = is.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}
				is.close();
				os.close();
				//Files.copy(myFile.toPath(), dest.toPath());
				out.println("File Transferred to the server");
			}
			catch (Exception e){
				e.printStackTrace();
			}
			return true;
		} else if (command.equalsIgnoreCase("DOWNLOAD")) {
			try{
				File currFile = new File(".\\Server_data\\" + arguments);
				String data = " ";
				BufferedReader br = new BufferedReader(new FileReader(currFile));
				String line;
				while ((line = br.readLine())!= null){
					data += line + "/";
				}
				out.println(data);
				}
			catch (IOException e){
				e.printStackTrace();
			}
			return true;
		}
		return true;
	}
	
}