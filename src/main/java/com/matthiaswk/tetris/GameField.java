package com.matthiaswk.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@SuppressWarnings("serial")
public class GameField extends JPanel implements Runnable, KeyListener{
	private final int WIDTH = 300;
	private final int HEIGHT = WIDTH * 2;
	private final int DIMENSION = WIDTH / 10;
	private final int BORDER_DOWN=19;
	private final int BORDER_LEFT=0;
	private final int BORDER_RIGHT=9;
	private final int REGULAR_DELAY = 500;
	private final int FAST_DELAY = 100;

    private int delay = REGULAR_DELAY;
    
    private boolean isPlaying;
	private boolean isPaused;
	private boolean isPressed;
	private final Object pauseLock = new Object();
    private Thread animator;
	private Block currentBlock;
	private Tetromino currentTetromino;
	private Block[][] lockedBlocks;
	
	GameField(){
		InitField();
		spawnTetromino();
	}
	
	private void InitField() {
		setBackground(Color.LIGHT_GRAY);
		//setOpaque(false);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		lockedBlocks = new Block[10][20];
		isPlaying = true;
		isPaused = false;
	}

	private boolean canPlay() {
		for(int x=0; x<10; x++) {
			if (lockedBlocks[x][0]!=null)
				return false;
		}
		return true;
	}

	private void spawnTetromino() {
		Tetromino t = new Tetromino(DIMENSION);
		currentTetromino = t;
	}

	private void addBlock() {
		Block b = new Block(new Point(4, 0), DIMENSION, Block.BlockColor.getRandomColor());
		currentBlock = b;
	}	
	
	private boolean canMoveDown() {
		int x = currentBlock.coordinates.x;
		int y = currentBlock.coordinates.y;
		if(y < BORDER_DOWN && lockedBlocks[x][y+1] == null)
			return true;
		else
			return false;
	}

	private boolean tetrominoCanMoveDown() {
		for(Block block : currentTetromino.blocks){
			if(!canMoveDown(block))
				return false;
		}
		return true;
	}

	private boolean tetrominoCanMoveRight() {
		for(Block block : currentTetromino.blocks){
			if(!canMoveRight(block))
				return false;
		}
		return true;
	}

	private boolean tetrominoCanMoveLeft() {
		for(Block block : currentTetromino.blocks){
			if(!canMoveLeft(block))
				return false;
		}
		return true;
	}

	private boolean canMoveDown(Block block) {
		int x = block.coordinates.x;
		int y = block.coordinates.y;
		if(y < BORDER_DOWN && lockedBlocks[x][y+1] == null)
			return true;
		else
			return false;
	}
	
	private boolean canMoveRight(Block block) {
		int x = block.coordinates.x;
		int y = block.coordinates.y;
		if(x < BORDER_LEFT || (x < BORDER_RIGHT && lockedBlocks[x+1][y] == null))
			return true;
		else
			return false;
	}
	
	private boolean canMoveLeft(Block block) {
		int x = block.coordinates.x;
		int y = block.coordinates.y;
		if( x > BORDER_RIGHT || (x > BORDER_LEFT && lockedBlocks[x-1][y] == null))
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
		for(Block block : currentTetromino.blocks){
			int y = block.coordinates.y;
			if(y > BORDER_DOWN)
				return true;
		}
		return false;
	}
	private boolean tetrominoOutOfBoundsRightLeftOrOverlapping(){
		boolean result = false;
		for(Block block : currentTetromino.blocks){
			int x = block.coordinates.x;
			int y = block.coordinates.y;
			if(x < BORDER_LEFT || x > BORDER_RIGHT)
				result = true;
			else if(lockedBlocks[x][y]!=null)
				result = true;
		}
		return result;
	}

	private boolean tetrominoCanRotateRight() {
		for (int blockIndex = 0; blockIndex < 4; blockIndex++){
			Point rotatedCoords = currentTetromino.rotatedRightCoordinates(blockIndex);
			if(lockedBlocks[rotatedCoords.x][rotatedCoords.y]!=null)
				return false;
		}
		return true;
	}

	private boolean tetrominoCanRotateLeft() {
		for (int blockIndex = 0; blockIndex < 4; blockIndex++){
			Point rotatedCoords = currentTetromino.rotatedLeftCoordinates(blockIndex);
			if(lockedBlocks[rotatedCoords.x][rotatedCoords.y]!=null)
				return false;
		}
		return true;
	}

	private void moveTetrominoAllTheWayDown() {
		while(tetrominoCanMoveDown()) {
			currentTetromino.moveDown();
		}
	}

	private void correctIfOutOfBounds() {
		for(Block block : currentTetromino.blocks){
			int x = block.coordinates.x;
			if(x < BORDER_LEFT)
				currentTetromino.moveRight();
			else if(x > BORDER_RIGHT)
				currentTetromino.moveLeft();
		}
	}

	private void lockInBlock(Block b) {
		int x = b.coordinates.x;
		int y = b.coordinates.y;
		lockedBlocks[x][y] = b;
	}

	private void lockInTetrominoBlocks(){
		for(Block block : currentTetromino.blocks){
			lockInBlock(block);
		}
	}
	
	private void checkAllRowsAndDeleteFull() {
		for(int row = BORDER_DOWN; row >0; row--) {
			while(rowIsFull(row)){
				deleteRow(row);
				collapseRowsAbove(row);
			}
		}
	}
	
	private boolean rowIsFull(int row) {
		for(int x=0; x<10; x++) {
			if (lockedBlocks[x][row]==null)
				return false;
		}
		return true;
	}
	
	private void deleteRow(int row) {
		for(int x=0; x<10; x++) {
			lockedBlocks[x][row] = null;
		}
	}
	
//	// For debugging
//	private void printArray() {
//		String s = "[";
//		for(int x=0; x<10; x++) {
//			s+="[";
//			for(int y=0; y<20; y++) {
//				if (lockedBlocks[x][y]==null)
//					s+="0,";
//				else
//					s+="1,";
//			}
//			s+="]";
//		}
//		s+="]";
//		System.out.println(s);
//	}

	private void collapseRowsAbove(int row) {
		for(int y=row-1; y>=0; y--){
			for(int x=0; x <= BORDER_RIGHT; x++) {
				Block block = lockedBlocks[x][y];
				if(block!=null) {
					lockedBlocks[x][y]=null;
					block.moveDown();
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
		for(Block block : currentTetromino.blocks){
			g.drawImage(block.image, block.coordinates.x*DIMENSION, block.coordinates.y*DIMENSION, this);
		}
	    
	    for(int x = 0; x < 10; x++) {
	    	for(int y = 0; y < 20; y++) {
	    		Block b = lockedBlocks[x][y];
	    		if(b != null)
	    			g.drawImage(b.image, b.coordinates.x*DIMENSION, b.coordinates.y*DIMENSION, this);
	    	}
	    }
	}

	
	@Override
	public void run() {
		
		long beforeTime, timeDiff, sleep;

        beforeTime = System.currentTimeMillis();

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
					} catch (InterruptedException ex) {
						break;
					}
					if (!isPlaying) {
						break;
					}
				}
			}

            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = delay - timeDiff;

            if (sleep < 0) {
                sleep = 2;
            }

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                
                String msg = String.format("Thread interrupted: %s", e.getMessage());
                
                JOptionPane.showMessageDialog(this, msg, "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }

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
            
            beforeTime = System.currentTimeMillis();
        }
	}

	public void pause() {
		// you may want to throw an IllegalStateException if !running
		isPaused = true;
	}

	public void resume() {
		synchronized (pauseLock) {
			isPaused = false;
			pauseLock.notifyAll(); // Unblocks thread
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if(isPlaying) {
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
			}
			else if( keyCode == KeyEvent.VK_DOWN) {
				delay=FAST_DELAY;
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
