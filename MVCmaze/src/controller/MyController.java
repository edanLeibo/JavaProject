package controller;

import model.Model;
import view.View;
/**
* <h1>MyController </h1>
* This class implements Controller interface,
* and connects the view and the model.
*   
*
* @author  Leibovitz Edan
* @version 1.0
* @since   2014-09-14
*/
public class MyController implements Controller {

	private View view;
	private Model model;
	private CommandsManager commandsManager;
	
	/**
	 * Controller c'tor
	 * @param view
	 * @param model
	 */
	public MyController(View view, Model model) {
		this.view = view;
		this.model = model;
		
		commandsManager = new CommandsManager(model, view);
		view.setCommands(commandsManager.getCommandsMap());
	}
		
	@Override
	public void notifyMazeIsReady(String name) {
		view.notifyMazeIsReady(name);
	}

	@Override
	public void notifyError(String error) {
		view.displayErrorMessage(error);
	}

	@Override
	public void notifySolutionIsReady(String name) {
		view.notifySolutionIsReady(name);		
	}

}
