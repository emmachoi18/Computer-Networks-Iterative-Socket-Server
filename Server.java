import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

/*
 Server class is the iterative, single-threaded program that accepts requests
 from clients. It handles one client at a time and supports the following
 client requests: Date & Time, Uptime, Memory Use, Netstat, Current Users, and
 Running Processes.
 
 The Server class will listen for client requests on the specified network
 address and port. Once a request is received, the server determines which 
 command was requested, performs the operation (Linux commands) and collects the
 output, then replies to the request with the output from the operation performed.
 
 */
public class Server
{

	public static void main(String[] args)
	{
		int portNumber = getPortNumber(); // Get port number from user.
		
		try
		{
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(portNumber); // Create new socket with port number.
			System.out.println("Server is listening on port " + portNumber);
			
			while(true)
			{
				Socket clientSocket = serverSocket.accept(); // Accepts connection made to socket.
				System.out.println("New client connected");
				handleClient(clientSocket);
				clientSocket.close();
			}// End while loop.
		}// End try.
		catch(Exception e)
		{
			e.printStackTrace();
		}// End catch.
	}// End main method.
	
	public static void handleClient(Socket clientSocket)
	{
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			
			String command = in.readLine();
			if(command != null)
			{
				System.out.println("Received from client: " + command);
				String response = processCommand(command); // Call processCommand to run the Linux operation the user chose.
				out.write(response + "\n");
				out.flush();
				System.out.println("Sent reponse to client: " + response);
			}// End if.
			
		}// End try.
		catch(IOException e)
		{
			e.printStackTrace();
		}// End catch.
	}// End handleClient method.
	
	public static String processCommand(String command)
	{
		String response;
		
		switch(command)
		{
		case "1":
			response = LocalDateTime.now().toString(); // Date & Time (the date and time on the server)
			break;
		case "2":
			response = executeCommand("uptime"); // Uptime (how long the server has been running since last boot-up)
			break;
		case "3":
			response = executeCommand("free -h"); // Memory Use (current memory usage on server)
			break;
		case "4":
			response = executeCommand("netstat"); // Netstat (lists network connections on the server)
			break;
		case "5":
			response = executeCommand("who"); // Current Users (list of users currently connected to the server)
			break;
		case "6":
			response = executeCommand("ps aux"); // Running Processes (list of programs currently running on the server)
			break;
		default:
			response = "Unknown command";
		}// End switch.
		
		return response;
	}// End processCommand method.
	
	public static String executeCommand(String command)
	{
		StringBuilder output = new StringBuilder();
		
		try
		{
			Process process = Runtime.getRuntime().exec(command); // Execute command.
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while((line = reader.readLine()) != null)
			{
				output.append(line).append("\n"); // Append result to output.
			}// End while loop.
			process.waitFor(); // Waits for current process to complete before moving on to the next.
		}// End try.
		catch(IOException | InterruptedException e)
		{
			e.printStackTrace();
		}// End catch.
		
		return output.toString(); // Return the output of the command.
	}// End executeCommand method.
	
	public static int getPortNumber()
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int portNumber = 0;
		boolean validity = false;
		while(!validity)
		{
			try
			{
				System.out.println("Enter port number: ");
				String userInput = br.readLine();
				portNumber = Integer.parseInt(userInput);
				validity = true;
			}// End try.
			catch(NumberFormatException e)
			{
				System.out.println("Please enter a valid port number: ");
			}// End catch.
			catch(Exception e)
			{
				e.printStackTrace();
			}// End catch.
		}// End while loop.
		
		return portNumber;
	}// End getPortNumber method.
}// End Server class.
