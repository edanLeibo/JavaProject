package model;

import io.MyCompressorOutputStream;
import io.MyDecompressorInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;





import algorithms.demo.MazeAdapter;
import algorithms.mazeGenerators.GrowingTreeGenerator;
import algorithms.mazeGenerators.LastInSelector;
import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Maze3dGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.mazeGenerators.RandomSelector;
import algorithms.mazeGenerators.SimpleMaze3dGenerator;
import algorithms.search.BFS;
import algorithms.search.DFS;
import algorithms.search.Searcher;
import algorithms.search.Solution;
import controller.Controller;
/**
* <h1>MyModel </h1>
* This class implements Model interface.
*   
* @author  Leibovitz Edan
* @version 1.0
* @since   2014-09-14
*/
public class MyModel implements Model {
	private Controller controller;	
	
	//The Model knows that threads are running
	private List<Thread> threads = new ArrayList<Thread>();
	
	//The Model keeps all the generated mazes
	private Map<String, Maze3d> mazes = new ConcurrentHashMap<String, Maze3d>();
	
	//The Model keeps all the calculated solutions
	private Map<String, Solution<Position>> solutions = new ConcurrentHashMap<String, Solution<Position>>();
	
//	private List<GenerateMazeRunnable> generateMazeTasks = new ArrayList<GenerateMazeRunnable>();
	
	private class GenerateMazeRunnable implements Runnable {
		//TODO: allow generating simple maze? allow to choose random/last?
		
		private int floors, rows, cols;
		private String name;
		private Maze3dGenerator generator;
		
		public GenerateMazeRunnable(String name,int floors,int rows, int cols,String type) {
			this.floors = floors;
			this.rows = rows;
			this.cols = cols;
			this.name = name;
			switch (type){
			case("simple"):{
				generator=new SimpleMaze3dGenerator();
				break;
			}
			case("growingLast"):{
				GrowingTreeGenerator g=new GrowingTreeGenerator();
				g.setSelector(new LastInSelector());
				generator=g;
				break;
			}
			case("growingRandom"):{
				GrowingTreeGenerator g=new GrowingTreeGenerator();
				g.setSelector(new LastInSelector());
				generator=g;
				break;
			}
				
			}
		}
		
		@Override
		public void run() {
			Maze3d maze = generator.generate(floors, rows, cols);
			mazes.put(name, maze);
			controller.notifyMazeIsReady(name);			
		}
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	@Override
	public void generateMaze(String name,int floors, int rows, int cols, String type) {
		if (mazes.containsKey(name)){
			controller.notifyError("Name already exists choose different one");
			return;
		}
		if (!type.equals("growingLast")&& !type.equals("growingRandom") && !type.equals("simple")){
			controller.notifyError("The generating algorithm "+type+ " doesn't exist");
			return;
		}
		GenerateMazeRunnable generateMaze = new GenerateMazeRunnable(name,floors,rows, cols, type);
		//generateMazeTasks.add(generateMaze);
		Thread thread = new Thread(generateMaze);
		thread.start();
		threads.add(thread);		
	}

	@Override
	public Maze3d getMaze(String name) {
		return mazes.get(name);
	}
	
	public void solveMaze(String name, String algorithm) {
		//First check if the maze is in the system
		if (!mazes.containsKey(name)){
			controller.notifyError("Maze doesn't exist");
			return;
		}
		
		//Second if we have a solution already return the solution immediately 
		if (solutions.containsKey(name)){
			controller.notifyError("Solution was already created for this maze, can't create another one.");
			return;
		}
		final String myName=name;
		final String algo=algorithm;
		Thread myThread = new Thread (new Runnable() {

			@Override
			public void run() {		
				Maze3d myMaze = mazes.get(myName);
				Searcher <Position> myAlgorithm;

				switch (algo){
					case "BFS":
						myAlgorithm = new BFS <Position>();					
						break;

					case "DFS":
						myAlgorithm = new DFS <Position>();
						break;

					default:
						controller.notifyError("Algorithm doesn't exist");
						return;
					}
					Solution<Position> sol=myAlgorithm.search(new MazeAdapter(myMaze));
					solutions.put(myName, sol);
					controller.notifySolutionIsReady(myName);
			}
		});
		myThread.start();	
		threads.add(myThread);

	}
		
	public void exit() {
		for (Thread t : threads) {
			t.interrupt();
		}
	}
	
	// The array will be empty if the directory is empty. Returns null if this abstract pathname does not denote a directory, or if an I/O error occurs.
	@Override
	public String[] getDirsAndFilesInPath(String path){
		String dirPath = path;
		File dir = new File(dirPath);
		String[] files = dir.list();
		return files;
	}
	
	@Override
	public int[][] getCrossSection(int index, String axis, String name) {
		Maze3d requestedMaze= mazes.get(name);
		
		// if maze doesn't exist
		if (requestedMaze==null) return null;
		
		int[][] ans=null;
		if (axis.equals("x") || axis.equals("X")){
			ans= requestedMaze.getCrossSectionByX(index);
		}
		else if (axis.equals("y") || axis.equals("Y")){
			ans= requestedMaze.getCrossSectionByY(index);
		}
		else if (axis.equals("z") || axis.equals("Z")){
			ans= requestedMaze.getCrossSectionByY(index);
		}
		return ans;
	}

	@Override
	public void saveMaze(String name, String filename) {
		
		Maze3d maze=mazes.get(name);
		
		//check if there is a maze under that name
		if (maze==null){
			controller.notifyError("No maze exists under that name");
			return;
		}
		
		OutputStream out = null;
		try {
			out = new MyCompressorOutputStream(
					new FileOutputStream(filename));
			byte[] arr = maze.toByteArray();
			out.write(arr);
		}
		catch(IOException e){
			controller.notifyError("File Error");
		}	
		finally{
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				controller.notifyError("File Error- can't close file");
			}
			
		}
	}

	@Override
	public boolean loadMaze(String filename, String name) {

		//check if there is a maze under that name
		if (mazes.containsKey(name)){
			controller.notifyError("The name "+name+ " already exists in the system, please choose a different name");
			return false;
		}
		InputStream in=null;
		try {			
			in = new MyDecompressorInputStream(
				new FileInputStream(filename));
		}catch (FileNotFoundException e){
			controller.notifyError("File Error- can't find the file "+filename);
			return false;
		}	
			int size = 0;
			try {
				int factor=1;
				for(int i=0; i<4; i++){
					int digit=in.read();
					size=size+digit*factor;
					factor=factor*255;
				}
			} catch (IOException e) {
				controller.notifyError("File Error- can't read file "+filename);
				return false;
			}
			finally{
				try {
					in.close();
				} catch (IOException e) {
					controller.notifyError("File Error- can't close the file "+filename);
					return false;
				}
			}
			
			byte b[] = new byte[size];
			
			try {
				in.read(b);
				
			} catch (IOException e) {
				controller.notifyError("File Error- can't read the file "+filename);
				return false;
			}
			finally{
				try {
					in.close();
				} catch (IOException e) {
					controller.notifyError("File Error- can't close the file "+filename);
					return false;
				}
			}
			mazes.put(name, new Maze3d(b));
			return true;
		}

	@Override
	public Solution<Position> getSolution(String name) {
		if (!solutions.containsKey(name)){
			controller.notifyError("There isn't a solution for maze "+name);
			return null;
		}
		return solutions.get(name);
	}
	
}
