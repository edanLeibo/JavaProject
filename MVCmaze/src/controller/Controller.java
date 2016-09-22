package controller;
/**
* <h1>Controller interface</h1>
* This interface is the Facade of the controller layer.  
* It has the functionality that a controller must have.
* 
* @author  Leibovitz Edan
* @version 1.0
* @since   2014-09-14
*/

public interface Controller {
	/**
	 * this method uses to notify the view that the maze 'name' was created 
	 * 
     * @param name - the maze name
	 * @return Nothing
	 */
	void notifyMazeIsReady(String name);
	/**
	 * this method uses to notify that the solution for the maze 'name' is ready
	 * 
     * @param name - the maze name
	 * @return Nothing
	 */
	void notifySolutionIsReady(String name);
	/**
	 * this method uses to notify the controller about an error that occurred
	 * 
     * @param error - a description of the error
	 * @return Nothing
	 */
	void notifyError(String error);
}
