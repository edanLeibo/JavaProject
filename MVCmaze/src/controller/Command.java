package controller;
/**
* <h1>Command interface</h1>
* This interface represents a general command
* Each command has it's own different functionality  
*
* @author  Leibovitz Edan
* @version 1.0
* @since   2014-09-14
*/
public interface Command {
	/**
	 * this method uses to activate a command
	 * 
     * @param args - a String that contains all of the arguments
	 * @return Nothing
	 */
	void doCommand(String[] args);
}
