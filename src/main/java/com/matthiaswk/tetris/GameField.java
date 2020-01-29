package com.matthiaswk.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("serial")
public class GameField extends JPanel implements Runnable, KeyListener{
	private final int WIDTH = 300;
	private final int HEIGHT = WIDTH * 2;
	private final int DIMENSION = WIDTH / 10;
	private final int BORDER_DOWN=19;
	private final int BORDER_LEFT=0;
	private final int BORDER_RIGHT=9;
	private final int REGULAR_DELAY = 500;
	private final int SHORT_DELAY = 100;
	private final Object pauseLock = new Object();
	private final Object animateLock = new Object();
	private long beforeTime = System.currentTimeMillis();
    private int delay = REGULAR_DELAY;
	private boolean isPaused = false;
	private boolean isPressed = false;
    private boolean isPlaying = true;
	private boolean isInteractive = true;
    private Thread animator;
	private Tetromino currentTetromino;
	private Block[][] lockedBlocks;
	
	GameField(){
		initField();
		initBlocks();
		spawnTetromino();
	}

	private void initField() {
		setBackground(Color.LIGHT_GRAY);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}

	private void initBlocks(){
		lockedBlocks = new Block[10][20];
		for(Block[] row : lockedBlocks)
			Arrays.fill(row, new Block());
	}

	private boolean canPlay() {//TODO: adjust loose-condition
		for(int x=0; x<10; x++) {
			if (!lockedBlocks[x][0].isEmpty())
				return false;
		}
		return true;
	}

	private void spawnTetromino() {//TODO: adjust I-Tetromino spawn
		Tetromino t = new Tetromino(DIMENSION, Tetromino.TetrominoShape.getRandomShape());
		currentTetromino = t;
	}

	private boolean tetrominoCanMoveDown() {
		for(Block block : currentTetromino.getBlocks()){
			if(!canMoveDown(block))
				return false;
		}
		return true;
	}

	private boolean tetrominoCanMoveRight() {
		for(Block block : currentTetromino.getBlocks()){
			if(!canMoveRight(block))
				return false;
		}
		return true;
	}

	private boolean tetrominoCanMoveLeft() {
		for(Block block : currentTetromino.getBlocks()){
			if(!canMoveLeft(block))
				return false;
		}
		return true;
	}

	private boolean canMoveDown(Block block) {
		int x = block.getCoordinates().x;
		int y = block.getCoordinates().y;
		if(y < BORDER_DOWN && lockedBlocks[x][y+1].isEmpty())
			return true;
		else
			return false;
	}
	
	private boolean canMoveRight(Block block) {
		int x = block.getCoordinates().x;
		int y = block.getCoordinates().y;
		if(x < BORDER_LEFT || (x < BORDER_RIGHT && lockedBlocks[x+1][y].isEmpty()))
			return true;
		else
			return false;
	}
	
	private boolean canMoveLeft(Block block) {
		int x = block.getCoordinates().x;
		int y = block.getCoordinates().y;
		if( x > BORDER_RIGHT || (x > BORDER_LEFT && lockedBlocks[x-1][y].isEmpty()))
			return true;
		else
			return false;
	}

	private void tryRotatingTetrominoRight(){
		currentTetromino.rotateRight();

		if(tetrominoOutOfBoundsDown())
			currentTetromino.rotateLeft();
		else if(tetrominoOutOfBoundsRightLeftOrOverlapping()){
			if(tetrominoCanMoveRight()){
				currentTetromino.moveRight();
				if(tetrominoOutOfBoundsRightLeftOrOverlapping()){
					if(tetrominoCanMoveRight())
						currentTetromino.moveRight();
					else{
						currentTetromino.moveLeft();
						currentTetromino.rotateLeft();
					}
				}
			}
			else if(tetrominoCanMoveLeft()){
				currentTetromino.moveLeft();
				if(tetrominoOutOfBoundsRightLeftOrOverlapping()){
					if(tetrominoCanMoveLeft())
						currentTetromino.moveLeft();
					else{
						currentTetromino.moveRight();
						currentTetromino.rotateLeft();
					}
				}
			}
			else
				currentTetromino.rotateLeft();
		}
	}

	private void tryRotatingTetrominoLeft(){
		currentTetromino.rotateLeft();

		if(tetrominoOutOfBoundsDown())
			currentTetromino.rotateRight();
		else if(tetrominoOutOfBoundsRightLeftOrOverlapping()){
			if(tetrominoCanMoveRight()){
				currentTetromino.moveRight();
				if(tetrominoOutOfBoundsRightLeftOrOverlapping()){
					if(tetrominoCanMoveRight())
						currentTetromino.moveRight();
					else{
						currentTetromino.moveLeft();
						currentTetromino.rotateRight();
					}
				}
			}
			else if(tetrominoCanMoveLeft()){
				currentTetromino.moveLeft();
				if(tetrominoOutOfBoundsRightLeftOrOverlapping()){
					if(tetrominoCanMoveLeft())
						currentTetromino.moveLeft();
					else{
						currentTetromino.moveRight();
						currentTetromino.rotateRight();
					}
				}
			}
			else
				currentTetromino.rotateRight();
		}
	}

	private boolean tetrominoOutOfBoundsDown(){
		for(Block block : currentTetromino.getBlocks()){
			int y = block.getCoordinates().y;
			if(y > BORDER_DOWN)
				return true;
		}
		return false;
	}

	private boolean tetrominoOutOfBoundsRightLeftOrOverlapping(){
		for(Block block : currentTetromino.getBlocks()){
			int x = block.getCoordinates().x;
			int y = block.getCoordinates().y;
			if(x < BORDER_LEFT || x > BORDER_RIGHT)
				return true;
			else if(!lockedBlocks[x][y].isEmpty())
				return true;
		}
		return false;
	}

	private void moveTetrominoAllTheWayDown() {
		while(tetrominoCanMoveDown()) {
			currentTetromino.moveDown();
		}
	}

	private void lockInBlock(Block b) {
		int x = b.getCoordinates().x;
		int y = b.getCoordinates().y;
		if(lockedBlocks[x][y].isEmpty())
			lockedBlocks[x][y] = b;
		else
			System.out.println("Blocks are overlapping!");
	}

	private void lockInTetrominoBlocks(){
		for(Block block : currentTetromino.getBlocks()){
			lockInBlock(block);
		}
	}

	private void checkAllRowsAndDeleteFull() {
		ArrayList<Integer> fullRows = new ArrayList<Integer>();
		int topRow = BORDER_DOWN;
		int bottomRow = 0;
		for(int row = BORDER_DOWN; row > 0; row--) {
			if(rowIsFull(row)){
				fullRows.add(row);
				topRow = row;
				bottomRow = Math.max(bottomRow, row);
			}
		}
		if(!fullRows.isEmpty()){
			waitBeforeAnimating(); //short pause to be replaced by an animation in the future
			deleteRows(fullRows);
			collapseRowsAbove(topRow, bottomRow);
		}
	}

	private boolean rowIsFull(int row) {
		for(int x=0; x<10; x++) {
			if (lockedBlocks[x][row].isEmpty())
				return false;
		}
		return true;
	}

	private void deleteRows(ArrayList<Integer> fullRows) {
		for(int row : fullRows)
			deleteRow(row);
	}
	
	private void deleteRow(int row) {
		for(int x=0; x<10; x++) {
			lockedBlocks[x][row] = new Block();
		}
	}
	
//	// For debugging
//	private void printArray() {
//		String s = "[";
//		for(int x=0; x<10; x++) {
//			s+="[";
//			for(int y=0; y<20; y++) {
//				if (lockedBlocks[x][y].isEmpty())
//					s+="0,";
//				else
//					s+="1,";
//			}
//			s+="]";
//		}
//		s+="]";
//		System.out.println(s);
//	}
	private void collapseRowsAbove(int topRow, int bottomRow) {
		int numMoves = bottomRow - topRow + 1;

		for(int y=topRow-1; y>0; y--){
			for(int x=0; x <= BORDER_RIGHT; x++) {
				Block block = lockedBlocks[x][y];
				if(!block.isEmpty()) {
					lockedBlocks[x][y] = new Block();
					block.moveDown(numMoves);
					lockInBlock(block);
				}
			}
		}
	}

	@Override
    public void addNotify() {
        super.addNotify();

        animator = new Thread(this);
        animator.start();
    }
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); 
		drawBlocks(g);		
	}

	private void drawBlocks(Graphics g) {
		for(Block block : currentTetromino.getBlocks())
			block.draw(g, this);
	    
	    for(int x = 0; x < 10; x++) {
	    	for(int y = 0; y < 20; y++) {
	    		Block block = lockedBlocks[x][y];
	    		if(!block.isEmpty())
					block.draw(g, this);
	    	}
	    }
	}

	@Override
	public void run() {
		while (isPlaying) {

			synchronized (pauseLock) {// Handle pausing
				if (!isPlaying) {
					break;
				}
				if (isPaused) {
					try {
						synchronized (pauseLock) {
							pauseLock.wait();
						}
					} catch (InterruptedException e) {
						System.out.println(String.format("Thread interrupted: %s", e.getMessage()));
						break;
					}
					if (!isPlaying) {
						break;
					}
				}
			}

			waitBeforeAnimating();
            animate();
        }
	}

	private void waitBeforeAnimating(){
		waitBeforeAnimating(this.delay);
	}

	private void waitBeforeAnimating(int delay){
		long timeDiff = System.currentTimeMillis() - beforeTime;
		long sleep = delay - timeDiff;

		if (sleep < 0) {
			sleep = 2;
		}
		synchronized (animateLock){
			try {
				animateLock.wait(sleep);
			} catch (InterruptedException e) {
				System.out.println(String.format("Thread interrupted: %s", e.getMessage()));
			}
		}

		beforeTime = System.currentTimeMillis();
	}

	private void animate(){
		isInteractive = false;

		if(tetrominoCanMoveDown())
			currentTetromino.moveDown();
		else {
			lockInTetrominoBlocks();
			checkAllRowsAndDeleteFull();
			if (canPlay())
				spawnTetromino();
			else
				isPlaying = false;
		}

		repaint();
		isInteractive = true;
	}

	private void pause() {
		isPaused = true;
		isInteractive = false;
	}

	private void resume() {
		synchronized (pauseLock) {
			isPaused = false;
			pauseLock.notifyAll(); // Unblocks thread
			isInteractive = true;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if(isPlaying && isInteractive) {
			if( keyCode == KeyEvent.VK_RIGHT && tetrominoCanMoveRight()) {
	            currentTetromino.moveRight();
	    		repaint();
			}
			else if( keyCode == KeyEvent.VK_LEFT && tetrominoCanMoveLeft()) {
				currentTetromino.moveLeft();
	    		repaint();
			}
			else if( keyCode == KeyEvent.VK_UP && !isPressed) {
				isPressed = true;
				moveTetrominoAllTheWayDown();
				repaint();
				synchronized (animateLock){
					animateLock.notifyAll();
				}
			}
			else if( keyCode == KeyEvent.VK_DOWN) {
				delay= SHORT_DELAY;
			}
			else if( keyCode == KeyEvent.VK_D && !currentTetromino.isOShape() && !isPressed) {
				isPressed = true;
				tryRotatingTetrominoRight();
				repaint();
			}
			else if( keyCode == KeyEvent.VK_A && !currentTetromino.isOShape() && !isPressed) {
				isPressed = true;
				tryRotatingTetrominoLeft();
				repaint();
			}
			else if( keyCode == KeyEvent.VK_P) {
				if(!isPaused){
					pause();
					setBackground(Color.GRAY);
				}
				else{
					resume();
					setBackground(Color.LIGHT_GRAY);
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if( keyCode == KeyEvent.VK_DOWN)
			delay = REGULAR_DELAY;
		else if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_UP)
			isPressed = false;
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

}
