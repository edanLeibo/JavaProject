package view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import controller.Command;
/**
* <h1>CLI</h1>
* This class is a general purpose command line interface that can be used for any kind
* of bufferedReader and printWriter
*
* @author  Leibovitz Edan
* @version 1.0
* @since   2014-09-14
*/
public class CLI {
	private BufferedReader in;
	private PrintWriter out;
	private HashMap<String, Command> commands;
	
	/**
	 * this method is a constructor
	 * 
     * @param in - a bufferedReader
     * @param out - a PrintWriter
	 * @return Nothing
	 */
	public CLI(BufferedReader in, PrintWriter out) {
		this.in = in;
		this.out = out;		
	}
	/**
	 * this method uses to set a commands map
	 * 
     * @param commands - a hash map containing commands and their names
	 * @return Nothing
	 */
	public void setCommands(HashMap<String, Command> commands) {
		this.commands = commands;
	}
	/**
	 * this method prints the options for the user to choose from
	 * 
	 * @return Nothing
	 */
	private void printMenu() {
		out.println();
		out.print("Choose command: (|");
		for (String command : commands.keySet()) {
			out.print("| "+command + " |");
		}
		out.println("|)");
		out.flush();
	}
	/**
	 * this method activates a new thread of the CLI
	 * which makes a loop of interaction with the user
	 * 
	 * @return Nothing
	 */
	public void start() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
				
					printMenu();
					try {
						String commandLine = in.readLine();
						//-----Parsing the input----
						
						//First word is the command
						String arr[] = commandLine.split(" ");
						String command = arr[0];			
						

						//Checking the validity of the command 
						if(!commands.containsKey(command)) {
							out.println("Command doesn't exist");
						}
						
						//Taking out the arguments
						else {
							String[] args = null;
							
							//Check if there are arguments
							if (arr.length > 1) {
								String commandArgs = commandLine.substring(
										commandLine.indexOf(" ") + 1);
								args = commandArgs.split(" ");							
							}	
							//Now args is a String array of all the arguments 
							//Activating the right command 
							
							Command cmd = commands.get(command);
							cmd.doCommand(args);				
							
							if (command.equals("exit"))
								break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}			
		});
		thread.start();		
	}
	
	
}
