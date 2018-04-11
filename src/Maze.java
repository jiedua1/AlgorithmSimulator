import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Maze extends JPanel implements ActionListener, MouseListener, MouseMotionListener, KeyListener{
	
	private static int cellSize = 15;
	private static int mazeHorizontal = 50;
	private static int mazeVertical = 50;
	private Square[][] maze = new Square[mazeHorizontal][mazeVertical];
	private boolean mouseDown = false;
	private int mouseMode = 1; // 1 = activating, 0 = deactivating, 2 = choosing start point, 3 = choosing end point, 
	private int mouseX = 0, mouseY = 0;
	private int tick = 0;
	private Queue<Square> queue = new ArrayDeque<Square>();
	//we need to keep track of the start and endpoints for djikstra's, also we need to limit these to 1 each
	private int[] startSquare = null;
	private int[] endSquare = null;
	private ArrayDeque djikstraQueue;
	private static JButton placeStart;
	private static JButton placeEnd;
	private static JButton drawPath;
	
	public static void main(String[] args) {
		JPanel menuPanel = new JPanel();
		menuPanel.setPreferredSize(new Dimension(mazeHorizontal*cellSize,80));
		menuPanel.setLayout(new GridLayout(3,2));
		placeStart = new JButton("Choose Startpoint");
		placeEnd = new JButton("Choose Endpoint");
		drawPath = new JButton("Find Path");
		
		menuPanel.add(placeStart);
		menuPanel.add(placeEnd);
		menuPanel.add(drawPath);
		
		Maze maze1 = new Maze();
		maze1.setPreferredSize(new Dimension(mazeHorizontal*cellSize,mazeVertical*cellSize));
		maze1.addMouseListener(maze1);
		maze1.addMouseMotionListener(maze1);
		maze1.setFocusable(true);
		JFrame window  = new JFrame("Maze Test");
		window.setLayout(new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		placeStart.addActionListener(maze1);
		placeEnd.addActionListener(maze1);
		drawPath.addActionListener(maze1);
		window.add(maze1);
		window.add(menuPanel);
		
		window.pack();
		window.setFocusable(true);
		maze1.addKeyListener(maze1);
		window.setVisible(true);
		
		
		maze1.init();		
		
	}
	
	public Maze() {
		super();
		tick = 0;
		for(int i = 0; i<mazeHorizontal; i++) {
			for(int j = 0; j < mazeVertical; j++) {
				maze[i][j] = new Square(i,j, 0, cellSize); //makes a new empty square
			}
		}
	}
	
	public void init() {
		Timer timer = new Timer(10, this);
		timer.start();
		tick = 0;
		//for a snake grid use code below
		for(int i = 0; i<mazeHorizontal; i++) {
			for(int j = 0; j < mazeVertical; j+=2) {
				maze[i][j].activate();
				if(i == 0 && j % 4 == 0) maze[i][j].deactivate();
				if(i == mazeHorizontal-1 && j%4 == 2) maze[i][j].deactivate();
			}
		}
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.BLACK);
		for(int i = 0; i<mazeHorizontal; i++) {
			for(int j = 0; j < mazeVertical; j++) {
				maze[i][j].draw(g);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == placeStart) {
			mouseMode = 2;
		}
		if(e.getSource() == placeEnd) {
			mouseMode = 3;		
		}
		if(e.getSource() == drawPath) {
			//fill in djikstras algorithm here for drawing the path...
		}
		if(tick % 3 == 0 && queue.size() > 0) queue.poll().activate();
		repaint();
		tick++;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.requestFocus();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(mouseMode == 0 || mouseMode == 1) {
				mouseDown = true;
				int xIndex = e.getX()/cellSize, yIndex = e.getY()/cellSize;
				if(xIndex > mazeHorizontal-1 || xIndex < 0) return;
				if(yIndex > mazeVertical-1 || yIndex < 0) return;
				if(maze[xIndex][yIndex].getState() == 0) {
					mouseMode = 1; //activation mode
				} else {
					mouseMode = 0;
				}
				if(mouseMode == 1) {
					maze[xIndex][yIndex].activate();
				}
				else if(mouseMode == 0) {
					maze[xIndex][yIndex].deactivate();
				}
				repaint();
			} else if (mouseMode == 2) {
				mouseMode = 0;
				int xIndex = e.getX()/cellSize, yIndex = e.getY()/cellSize;
				if(xIndex > mazeHorizontal-1 || xIndex < 0) return;
				if(yIndex > mazeVertical-1 || yIndex < 0) return;
				if(startSquare != null) {
					if(maze[startSquare[0]][startSquare[1]].getState() == 3) {
							maze[startSquare[0]][startSquare[1]].deactivate();
					}
				}
				startSquare = new int[] {xIndex, yIndex};
				maze[xIndex][yIndex].changeState(Square.STARTTYPE);
				repaint();
			} else if (mouseMode == 3) {
				mouseMode = 0;
				int xIndex = e.getX()/cellSize, yIndex = e.getY()/cellSize;
				if(xIndex > mazeHorizontal-1 || xIndex < 0) return;
				if(yIndex > mazeVertical-1 || yIndex < 0) return;
				if(endSquare != null) {
					if(maze[endSquare[0]][endSquare[1]].getState() == 4) {
						maze[endSquare[0]][endSquare[1]].deactivate();
				}
					
				}
				endSquare = new int[] {xIndex, yIndex};
				maze[xIndex][yIndex].changeState(Square.ENDTYPE);
				repaint();
			}
		} 
		return;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		mouseDown = false;
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		int xIndex = e.getX()/cellSize, yIndex = e.getY()/cellSize;
		if(xIndex > mazeHorizontal-1 || xIndex < 0) return;
		if(yIndex > mazeVertical-1 || yIndex < 0) return;
		if(mouseMode == 1) {
			maze[xIndex][yIndex].activate();
		}
		else if(mouseMode == 0) {
			maze[xIndex][yIndex].deactivate();
		}
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		mouseX = arg0.getX();
		mouseY = arg0.getY();
	}
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_R) {
			queue.clear();
			for(int i = 0; i<mazeHorizontal; i++) {
				for(int j = 0; j < mazeVertical; j++) {
					maze[i][j].setState(0);
				}
			}
		}
		if(arg0.getKeyCode() == KeyEvent.VK_SPACE) {
			int xIndex = mouseX/cellSize, yIndex = mouseY/cellSize;
			if(xIndex > mazeHorizontal-1 || xIndex < 0) return;
			if(yIndex > mazeVertical-1 || yIndex < 0) return;
			int curRow = xIndex;
			int curCol = yIndex;
			while(queue.size() > 0) {
				queue.poll().setTempType(0);
			}
			maze[curRow][curCol].activate();
			Random random = new Random();
			ArrayList<Square> availableMoves = new ArrayList<Square>();
			if(curRow + 1 < mazeHorizontal && maze[curRow+1][curCol].getTempType() == 0) {
				availableMoves.add(maze[curRow+1][curCol]);
			}
			if(curRow - 1 >= 0 && maze[curRow-1][curCol].getTempType() == 0) {
				availableMoves.add(maze[curRow-1][curCol]);
			}
			if(curCol + 1 < mazeVertical && maze[curRow][curCol+1].getTempType() == 0) {
				availableMoves.add(maze[curRow][curCol+1]);
			}
			if(curCol - 1 >= 0 && maze[curRow][curCol-1].getTempType() == 0) {
				availableMoves.add(maze[curRow][curCol-1]);
			}
			while(availableMoves.size() > 0) {
					availableMoves.clear();
					if(curRow + 1 < mazeHorizontal && maze[curRow+1][curCol].getTempType() == 0) {
						availableMoves.add(maze[curRow+1][curCol]);
					}
					if(curRow - 1 >= 0 && maze[curRow-1][curCol].getTempType() == 0) {
						availableMoves.add(maze[curRow-1][curCol]);
					}
					if(curCol + 1 < mazeVertical && maze[curRow][curCol+1].getTempType() == 0) {
						availableMoves.add(maze[curRow][curCol+1]);
					}
					if(curCol - 1 >= 0 && maze[curRow][curCol-1].getTempType() == 0) {
						availableMoves.add(maze[curRow][curCol-1]);
					} 
					/*
					for(int i = -1; i<=1; i++) {
						for(int j = -1; j<=1; j++) {
							if(i != j) {
								if(curRow + i < mazeHorizontal && curRow + i >= 0 && curCol + j < mazeVertical 
										&& curCol + j >= 0 && maze[curRow+i][curCol+j].getTempType() == 0) {
									availableMoves.add(maze[curRow+i][curCol+j]);
								}
							}
						}
					} */
					int randomInt = random.nextInt(Math.max(1, availableMoves.size()));
					if(availableMoves.size()==0) break;
					Square newCell = availableMoves.get(randomInt);
					newCell.activateTemp();
					queue.add(newCell);
					curRow = newCell.x; curCol = newCell.y;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	} 
	

	

}
