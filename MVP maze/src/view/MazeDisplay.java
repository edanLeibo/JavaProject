package view;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Shell;

import algorithms.mazeGenerators.Maze3d;

public class MazeDisplay extends Canvas {
	
	private int[][] mazeData;				//The floor we currently working on 
	private int endRow;
	private int endCol;
	private int endFloor;
	private GameCharacter gameCharacter;
	Maze3d maze;							//The maze we currently working on 

	
	public MazeDisplay(Shell parent, int style) {
		super(parent, style);
		mazeData=null;
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
				
				//Drawing the ending point of the character is in right floor
				if (gameCharacter.getFloor()==endFloor){
					e.gc.setForeground(new Color(null,0,255,0));
					e.gc.setBackground(new Color(null,0,255,0));
					e.gc.fillOval(endRow, endCol, w/2, h/2);
				}
				//Drawing the gameCharacter
				gameCharacter.paint(e, w, h);
			}
		});
	}
	
	public void setMazeData(int[][] mazeData) {
		//When given a new maze 'look' it updates its inner data
		//and immediately draws it on the board
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
		this.redraw();
	}

	/**
	 * @param endCol the endCol to set
	 */
	public void setEndCol(int endCol) {
		this.endCol = endCol;
		this.redraw();
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

//	public void setStartingPoint(int z, int y, int x) {
//		gameCharacter.setFloor(z);	
//		gameCharacter.setRow(y);
//		gameCharacter.setCol(x);
//	}
	
}
