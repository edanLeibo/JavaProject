package view;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Observer;

import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
/**
* <h1>MyView </h1>
* This class implements View interface.
* It knows only the controller and uses a CLI
* for interacting with the user.
*   
* @author  Leibovitz Edan
* @version 1.0
* @since   2014-09-14
*/

//MyView observes the CLI and been observed by the Presenter
public class MyView extends Observable implements View, Observer {
	
	private BufferedReader in;
	private PrintWriter out;
	private CLI cli;

	public MyView(BufferedReader in, PrintWriter out) {
		this.in = in;
		this.out = out;				
		cli = new CLI(in, out);
		cli.addObserver(this);	
	}

	@Override
	public void displayFilesInPath(String path, String[] files){
		if (files==null){
			out.println("No such Path");
		}
		else if (files.length == 0) {
		    	out.println("The directory is empty");
		} else {
			out.println("Files in "+path);
		    for (String aFile : files) {
		        out.println(aFile);
		    }
		}
        out.flush();
	}
		
	@Override
	public void notifyMazeIsReady(String name) {
		out.println("maze " + name + " is ready");
		out.flush();
	}
	
	@Override
	public void displayMaze(Maze3d maze) {
		if (maze==null){
			out.println("Maze doesn't exist");
			out.flush();
			return;
		}
		out.println(maze);
		out.flush();
	}

	@Override
	public void start() {
		cli.start();
	}

	@Override
	public void displayBadArguments(){
		out.println("Bad arguments");
		out.flush();
	}

	@Override
	public void displayCrossSectionMaze(int index, String axis, String name, int[][] maze2d) {
		out.println("The maze "+name+ " axis "+axis+" index "+index);
		for(int i=0; i< maze2d.length; i++){
			for(int j=0; j< maze2d[i].length; j++){
				out.print(maze2d[i][j]+ " ");
			}
			out.println("\n");
		}
		out.flush();
	}
	
	@Override
	public void displayErrorMessage(String error) {
		out.println(error);
		out.flush();

	}
	
	@Override
	public void notifyMazeSaved(String name) {
		out.println("Maze "+ name+ " was saved successfully");
		out.flush();
	}

	@Override
	public void notifyMazeLoaded(String name) {
		out.println("Maze "+ name+ " was loaded successfully");		
		out.flush();
	}

	@Override
	public void notifySolutionIsReady(String name) {
		out.println("Solution for maze "+ name+ " is ready");		
		out.flush();
	}

	@Override
	public void displaySolution(String name, Solution<Position> sol){
	if (sol==null) return;
	out.println("Solution for maze "+ name+ ": ");
	out.println(sol);
	out.flush();
	}
	
	@Override
	public void update(Observable o, Object arg) {
		//MyView observes the cli and when an input command is coming 
		//it just passes a notification to the presenter
		if (o == cli) {
			setChanged();
			notifyObservers(arg);
		}
	}

	@Override
	public void displayMessage(String msg) {
		out.println(msg);
		out.flush();		
	}
}
