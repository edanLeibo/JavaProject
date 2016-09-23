package view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Observable;
/**
* <h1>CLI</h1>
* This class is a general purpose command line interface that can be used for any kind
* of bufferedReader and printWriter
*
* @author  Leibovitz Edan
* @version 1.0
* @since   2014-09-14
*/
public class CLI extends Observable{
	private BufferedReader in;
	private PrintWriter out;
	
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
	 * this method prints the options for the user to choose from
	 * 
	 * @return Nothing
	 */
	private void printMenu() {
		out.print("Choose command: ");
		out.flush();
	}

	/**
	 * this method activates a new thread of the CLI
	 * which makes a loop of interaction with the user
	 * 
	 * @return Nothing
	 */
	public void start() {
		
		//Creating the main loop of interaction
		//Exit command will stop the loop and the thread
		
		//When a new input is coming notify the view about it
		
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
				
					printMenu();
					try {
						String commandLine = in.readLine();
						setChanged();
						notifyObservers(commandLine);
						
						if (commandLine.equals("exit"))
							break;
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}			
		});
		thread.start();		
	}
	
	
}
