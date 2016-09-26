/**
 * 
 */
package searchingAlgorithmTests;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import algorithms.demo.MazeAdapter;
import algorithms.mazeGenerators.GrowingTreeGenerator;
import algorithms.mazeGenerators.LastInSelector;
import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Maze3dGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.mazeGenerators.RandomSelector;
import algorithms.mazeGenerators.SimpleMaze3dGenerator;
import algorithms.search.BFS;
import algorithms.search.Solution;
import algorithms.search.State;

/**
 * @author Edan
 *
 */
public class BFSTest {
	MazeAdapter adapter;
	Maze3d maze;
	private int amountOfMazes;
	private int amountOfBigMazes;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Maze3dGenerator generator = new SimpleMaze3dGenerator();
		maze = generator.generate(5, 6, 7);
		adapter = new MazeAdapter(maze);
		amountOfMazes = 1000;
		amountOfBigMazes = 5;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBFSConstructor() {
		BFS<Position> bfs = new BFS<Position>();
		assertNotNull(bfs);
	}

	@Test
	public void testBFSSearch() {
		BFS<Position> bfs = new BFS<Position>();
		Solution<Position> solution = bfs.search(adapter);
		List<State<Position>> states = solution.getStates();
		for (State<Position> s : states) {
			Position p = s.getValue();
			// Check that there is a path of zeros all along the way
			assertEquals((maze.getValue(p.z, p.y, p.x)), 0);
		}
	}

	@Test
	public void testIntensiveDifferentMazes() {
		BFS<Position> bfs = new BFS<Position>();
		for (int i = 0; i < amountOfMazes; i++) {
			Maze3dGenerator generator = new GrowingTreeGenerator(new RandomSelector());
			maze = generator.generate(7, 8, 7);
			adapter = new MazeAdapter(maze);
			Solution<Position> solution = bfs.search(adapter);
			List<State<Position>> states = solution.getStates();
			for (State<Position> s : states) {
				Position p = s.getValue();
				assertEquals((maze.getValue(p.z, p.y, p.x)), 0);
			}
		}
	}
	
	@Test
	public void testSolveingBigMazes() {
		BFS<Position> bfs = new BFS<Position>();
		for (int i = 0; i < amountOfBigMazes; i++) {
			Maze3dGenerator generator = new GrowingTreeGenerator(new LastInSelector());
			maze = generator.generate(13,13,13);
			adapter = new MazeAdapter(maze);
			Solution<Position> solution = bfs.search(adapter);
			List<State<Position>> states = solution.getStates();
			for (State<Position> s : states) {
				Position p = s.getValue();
				assertEquals((maze.getValue(p.z, p.y, p.x)), 0);
			}
		}
	}

}
