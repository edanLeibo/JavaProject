package view;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Shell;

public class MazeDisplay extends Canvas {
	
	private int[][] mazeData;
	private int beginRow;
	private int beginCol;
	private int endRow;
	private int endCol;
	
	
	
	
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
				
				//Drawing beginning and ending point
				e.gc.setForeground(new Color(null,255,0,0));
				e.gc.setBackground(new Color(null,255,0,0));
				e.gc.fillOval(beginRow, beginCol, w/2, h/2);
				
				e.gc.setForeground(new Color(null,0,255,0));
				e.gc.setBackground(new Color(null,0,255,0));
				e.gc.fillOval(endRow, endCol, w/2, h/2);
			}
		});
	}
	
	public void setMazeData(int[][] mazeData) {
		//When given a new maze 'look' it updates its inner data
		//and immediately draws it on the board
		this.mazeData = mazeData;
		this.redraw();
	}

	/**
	 * @param beginRow the beginRow to set
	 */
	public void setBeginRow(int beginRow) {
		this.beginRow = beginRow;
		this.redraw();
	}

	/**
	 * @param beginCol the beginCol to set
	 */
	public void setBeginCol(int beginCol) {
		this.beginCol = beginCol;
		this.redraw();
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
	
}
