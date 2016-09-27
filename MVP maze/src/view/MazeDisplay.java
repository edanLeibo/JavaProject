package view;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;

public class MazeDisplay extends Canvas {
	private int[][] mazeData;				//The floor we currently working on 
	private int endRow;
	private int endCol;
	private int endFloor;
	private GameCharacter gameCharacter;
	private Maze3d maze;							//The maze we currently working on 
	private String mazeName;
	private boolean withHint;
	private ArrayList<Position> path;
	
	
	public MazeDisplay(Shell parent, int style) {
		super(parent, style);
		path=null;
		mazeData=null;
		withHint=false;
		//Adding the event handler which will paint the maze every time needed
		this.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				if (mazeData == null)
					return;

				e.gc.setForeground(new Color(null,0,0,0));
				e.gc.setBackground(new Color(null,0,0,0));
				
				int width=getSize().x;
				int height=getSize().y;
				
				int w=width/mazeData[0].length;
				int h=height/mazeData.length;
				
				for(int i=0;i<mazeData.length;i++)
					for(int j=0;j<mazeData[i].length;j++){
						int x=j*w;
						int y=i*h;
						if(mazeData[i][j]!=0)
							e.gc.fillRectangle(x,y,w,h);
					}
				//If user asked to draw the maze with hint on it
				if (withHint){
					for (Position p: path){
						if (p.z==gameCharacter.getFloor()){
							e.gc.setForeground(new Color(null,150,120,111));
							e.gc.setBackground(new Color(null,230,214,111));
							e.gc.fillOval(p.x*w,p.y*h, w, h);
						}
					}
				}
				//Drawing the ending point of the character is in right floor
				if (gameCharacter.getFloor()==endFloor){
					e.gc.setForeground(new Color(null,0,120,111));
					e.gc.setBackground(new Color(null,0,214,111));
					e.gc.fillOval(endCol*w,endRow*h, w, h);
				}
				//Drawing the gameCharacter
				gameCharacter.paint(e, w, h);
			}
		});
		this.addKeyListener(new KeyListener() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.keyCode) {
				case SWT.ARROW_UP:
						if (mazeData[gameCharacter.getRow()-1][gameCharacter.getCol()]==0){
							gameCharacter.setRow(gameCharacter.getRow()-1);
						}
					break;
				case SWT.ARROW_LEFT:
					if (mazeData[gameCharacter.getRow()][gameCharacter.getCol()-1]==0){
						gameCharacter.setCol(gameCharacter.getCol()-1);
					}						
					break;
				case SWT.ARROW_RIGHT:
					if (mazeData[gameCharacter.getRow()][gameCharacter.getCol()+1]==0){
						gameCharacter.setCol(gameCharacter.getCol()+1);
					}

					break;
				case SWT.ARROW_DOWN:
					if (mazeData[gameCharacter.getRow()+1][gameCharacter.getCol()]==0){
						gameCharacter.setRow(gameCharacter.getRow()+1);
					}
					break;
				case SWT.PAGE_UP:
					if (maze.getMaze3d()[gameCharacter.getFloor()+1][gameCharacter.getRow()][gameCharacter.getCol()]==0){
						gameCharacter.setFloor(gameCharacter.getFloor()+1);
						mazeData=maze.getCrossSectionByZ(gameCharacter.getFloor());
					}
					break;
				case SWT.PAGE_DOWN:
					if (maze.getMaze3d()[gameCharacter.getFloor()-1][gameCharacter.getRow()][gameCharacter.getCol()]==0){
						gameCharacter.setFloor(gameCharacter.getFloor()-1);
						mazeData=maze.getCrossSectionByZ(gameCharacter.getFloor());
					}
					break;
				default:
					break;
				}
				redraw();
			}
			@Override
			public void keyReleased(KeyEvent e) {				
			}
			
		});
	}
	
	public void setMazeData(int[][] mazeData) {
		//When given a new maze 'look' it updates its inner data
		this.mazeData = mazeData;
	}

	/**
	 * @param gameCharacter the gameCharacter to set
	 */
	public void setGameCharacter(GameCharacter gameCharacter) {
		this.gameCharacter = gameCharacter;
	}
	
	/**
	 * @param endFloor the endFloor to set
	 */
	public void setEndFloor(int endFloor) {
		this.endFloor = endFloor;
	}	

	/**
	 * @param endRow the endRow to set
	 */
	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	/**
	 * @param endCol the endCol to set
	 */
	public void setEndCol(int endCol) {
		this.endCol = endCol;
	}

	public int getCharacterFloor() {
		return gameCharacter.getFloor();
	}

	/**
	 * @param maze the maze to set
	 */
	public void setMaze(Maze3d maze) {
		this.maze = maze;
	}

	/**
	 * @return the name
	 */
	public String getMazeName() {
		return mazeName;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.mazeName = name;
	}

	public void playWalk(ArrayList<Position> path) {
		Timer myTimer = new Timer();
		MovingAlongPath myTask= new MovingAlongPath(this,path, myTimer);
		myTimer.scheduleAtFixedRate(myTask, 0, 500);
	}
	
	private class MovingAlongPath extends TimerTask{

			ArrayList<Position> path;
			MazeDisplay mazeDisplay;
			int i;
			 Timer timer;
			
			public MovingAlongPath(MazeDisplay mazeDisplay, ArrayList<Position> path, Timer timer){
				this.mazeDisplay=mazeDisplay;
				this.path=path;
				this.i=0;
				this.timer=timer;
			}
			
			@Override
			public void run() {
				getDisplay().syncExec(new Runnable() {
					
					@Override
					public void run() {	
						gameCharacter.setFloor(path.get(i).z);
						gameCharacter.setRow(path.get(i).y);
						gameCharacter.setCol(path.get(i).x);
						mazeData=maze.getCrossSectionByZ(path.get(i).z);
						mazeDisplay.redraw();
						i=i+1;
						if (i==path.size()) {
							cancel();
							timer.cancel();
						}
					}
				});
			}
	}

	public void showHint(ArrayList<Position> path) {
		this.path=path;
		withHint=true;
		redraw();
	}
	

	
}