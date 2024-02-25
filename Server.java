package projectOS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server 
{
	public static void main(String[] args) throws IOException
	{
        int cores = getNumberOfCores();
        Process process = Runtime.getRuntime().exec("uname");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String os = reader.readLine();
        System.out.println("The number of available cores : " + cores);
        System.out.println("Current operating system : " + os + "\n");

        
		System.out.println("Server Running ....... \n");
		
		ServerSocket ss=new ServerSocket(5002);
		while(true)
		{
			Socket s=ss.accept();
	        BufferedReader cin = new BufferedReader(new InputStreamReader(s.getInputStream()));
			int clientOption = Integer.parseInt(cin.readLine());
			String path = cin.readLine();
			String word = cin.readLine();
			
			switch (clientOption) {
			case 1:
				System.out.println("The client chose Equal Distribution"); new Service(cores, path, word, s, 0);
				break;
			case 2:
				System.out.println("The client chose Round Robin Distribution"); new Service(cores, path, word, s, 1);
				break;
			case 3:
				System.out.println("The client chose to Quit");
				break;
			
			}
			
			reader.close();
			cin.close();
		}
	}
	
	 private static int getNumberOfCores() {
	        try {
	            Process process = Runtime.getRuntime().exec("nproc");
	            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	            String line = reader.readLine();
	            return line != null ? Integer.parseInt(line.trim()) : 1; // Default to 1 if unable to determine
	        } catch (IOException e) {
	            e.printStackTrace();
	            return 1; // Default to 1 on exception
	        }
	 }
}
