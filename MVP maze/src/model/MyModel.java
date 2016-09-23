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
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import presenter.Presenter;
import algorithms.demo.MazeAdapter;
import algorithms.mazeGenerators.GrowingTreeGenerator;
import algorithms.mazeGenerators.LastInSelector;
import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Maze3dGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.mazeGenerators.SimpleMaze3dGenerator;
import algorithms.search.BFS;
import algorithms.search.DFS;
import algorithms.search.Searcher;
import algorithms.search.Solution;
/**
* <h1>MyModel </h1>
* This class implements Model interface.
*   
* @author  Leibovitz Edan
* @version 1.0
* @since   2014-09-14
*/
public class MyModel extends Observable implements Model {
	
	//The Model knows that threads are running
	private ExecutorService executor;
	
	//The Model keeps all the generated mazes
	private Map<String, Maze3d> mazes = new ConcurrentHashMap<String, Maze3d>();
	
	//The Model keeps all the calculated solutions
	private Map<String, Solution<Position>> solutions = new ConcurrentHashMap<String, Solution<Position>>();
	
	public MyModel() {
		//properties = PropertiesLoader.getInstance().getProperties();
		executor = Executors.newFixedThreadPool(3);//properties.getNumOfThreads());
		//loadSolutions();
	}
	
	private class GenerateMazeCallable implements Callable<Maze3d> {
		
		private int floors, rows, cols;
		private String name;
		private Maze3dGenerator generator;
		
		private GenerateMazeCallable(String name,int floors,int rows, int cols,String type) {
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
		public Maze3d call() throws Exception {
			Maze3d maze = generator.generate(floors, rows, cols);
			mazes.put(name, maze);
			
			//When the maze is ready notify the presenter about it 
			setChanged();
			notifyObservers("maze_ready " + name);		
			return maze;
		}
	}
	
	private class SolveMazeCallable implements Callable<Solution<Position>>{
		String name; 
		Searcher <Position> myAlgorithm;
		
		public SolveMazeCallable(String name, String algorithm) {
			this.name=name;
			switch (algorithm){
			case "BFS":
				myAlgorithm = new BFS <Position>();					
				break;
				
			case "DFS":
				myAlgorithm = new DFS <Position>();
				break;
			}
		}
		
		@Override
		public Solution<Position> call() throws Exception {
			Maze3d myMaze = mazes.get(name);
			Solution<Position> sol=myAlgorithm.search(new MazeAdapter(myMaze));
			solutions.put(name, sol);
			setChanged();
			notifyObservers("solution_ready "+name);
			return sol;
		}
	}
	
	@Override
	public void generateMaze(String name,int floors, int rows, int cols, String type) {
		if (mazes.containsKey(name)){
			setChanged();
			notifyObservers("display_msg The maze "+name+ " already exists choose different one");
			return;
		}
		if (!type.equals("growingLast")&& !type.equals("growingRandom") && !type.equals("simple")){
			setChanged();
			notifyObservers("display_msg The generating algorithm "+type+ " doesn't exist");
			return;
		}
		GenerateMazeCallable generateMaze = new GenerateMazeCallable(name,floors,rows, cols, type);
		executor.submit(generateMaze);
	}
	
	public void solveMaze(String name, String algorithm) {
		//First check if the maze is not in the system
		if (!mazes.containsKey(name)){
			setChanged();
			notifyObservers("display_msg "+name +" Maze doesn't exist in the system");
			return;
		}
		
		//Second if we have a solution already return the solution immediately 
		if (solutions.containsKey(name)){
			setChanged();
			notifyObservers("solution_ready " +name);			
			return;
		}
		
		if (!(algorithm.equals("BFS") || algorithm.equals("DFS"))){
			setChanged();
			notifyObservers("display_msg "+algorithm +" can't be recognized as an algorithm");
			return;
		}
		
		executor.submit(new SolveMazeCallable(name, algorithm));

	}
		
	@Override
	public Maze3d getMaze(String name) {
		return mazes.get(name);
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
			setChanged();
			notifyObservers("display_msg No maze exists under that name");
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
			setChanged();
			notifyObservers("display_msg File Error");
		}	
		finally{
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				setChanged();
				notifyObservers("display_msg File Error- can't close file");
			}
			
		}
	}

	@Override
	public boolean loadMaze(String filename, String name) {

		//check if there is a maze under that name
		if (mazes.containsKey(name)){
			setChanged();
			notifyObservers("display_msg The name "+name+ " already exists in the system, please choose a different name");
			return false;
		}
		InputStream in=null;
		try {			
			in = new MyDecompressorInputStream(
				new FileInputStream(filename));
		}catch (FileNotFoundException e){
			setChanged();
			notifyObservers("display_msg File Error- can't find the file "+filename);
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
				setChanged();
				notifyObservers("display_msg File Error- can't read file "+filename);
				return false;
			}
			finally{
				try {
					in.close();
				} catch (IOException e) {
					setChanged();
					notifyObservers("display_msg File Error- can't close the file "+filename);
					return false;
				}
			}
			
			byte b[] = new byte[size];
			
			try {
				in.read(b);
				
			} catch (IOException e) {
				setChanged();
				notifyObservers("display_msg File Error- can't read the file "+filename);
				return false;
			}
			finally{
				try {
					in.close();
				} catch (IOException e) {
					setChanged();
					notifyObservers("display_msg File Error- can't close the file "+filename);
					return false;
				}
			}
			mazes.put(name, new Maze3d(b));
			return true;
		}

	@Override
	public Solution<Position> getSolution(String name) {
		if (!solutions.containsKey(name)){
			setChanged();
			notifyObservers("display_msg There isn't a solution for maze "+name);
			return null;
		}
		return solutions.get(name);
	}
	
	public void exit() {
		executor.shutdownNow();
	}
}
