package projectOS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client 
{
	public static void main(String[] args) throws UnknownHostException, IOException 
	{
		Scanner kb = new Scanner(System.in);
		Socket s = new Socket("localhost",5002);
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		PrintStream p = new PrintStream(s.getOutputStream());
		
		System.out.println("Connected to "+s.getInetAddress()+":"+s.getPort());
		
		System.out.println("Welcome to Parallel File Search Server Load Distributor!\r\n");
		int userOption;
		do {
			System.out.println("Choose one of the following options:");
			System.out.println("1. Equal Distribution");
			System.out.println("2. Round Robin Distribution");
			System.out.println("3. Quit");

			userOption = kb.nextInt();
		} while (userOption > 3 || userOption < 1);
		System.out.println();
		p.println(userOption);
		if (userOption != 3) {
			kb.nextLine();
			System.out.print("Enter the path you'd like to search through: ");
			String path = kb.nextLine();
			
			System.out.print("Enter the word you'd like to search for: ");
			String word = kb.nextLine();
			p.println(path);
			p.println(word);
			
			System.out.println();
			
			String output;
			while ((output = in.readLine()) != null)
				System.out.println(output);
		}
		
		in.close();
		p.close();
		kb.close();
		s.close();
	}
}
