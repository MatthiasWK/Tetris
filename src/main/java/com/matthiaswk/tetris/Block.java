package com.matthiaswk.tetris;

import java.awt.*;
import java.util.Random;

@SuppressWarnings("serial")
public class Block{
	public Point coordinates;
	public int dimension;
	public Image image;
	public BlockColor blockColor;

	public enum BlockColor{
		LIGHTBLUE, YELLOW, PINK, BLUE, ORANGE, GREEN, RED;
		
		public static BlockColor getRandomColor() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
	}
	
	Block(Point coordinates, int dimension, BlockColor blockColor){
		this.coordinates = coordinates;
		this.dimension = dimension;
		this.blockColor = blockColor;
		
		loadImage();
	}
	
	public void loadImage()
	{		
		Toolkit t = Toolkit.getDefaultToolkit();
		Image temp = null;
		switch(blockColor) {
			case LIGHTBLUE:
				temp = t.getImage(getClass().getResource("/block_lightblue.png"));
				break;
			case YELLOW:
				temp = t.getImage(getClass().getResource("/block_yellow.png"));
				break;
			case PINK:
				temp = t.getImage(getClass().getResource("/block_pink.png"));
				break;
			case BLUE:
		    	temp = t.getImage(getClass().getResource("/block_blue.png"));
		    	break;
			case ORANGE:
				temp = t.getImage(getClass().getResource("/block_orange.png"));
				break;
			case GREEN:
		    	temp = t.getImage(getClass().getResource("/block_green.png"));
		    	break;
			case RED:
				temp = t.getImage(getClass().getResource("/block_red.png"));
				break;
			default:
				throw new IllegalStateException("Unexpected value: " + blockColor);
		}
	    image = temp.getScaledInstance(dimension, dimension, Image.SCALE_DEFAULT);
	}
	
	public void moveDown() {
		int x = coordinates.x;
		int newY = coordinates.y + 1;
		coordinates = new Point(x, newY);
	}
	
	public void moveRight() {
		int newX = coordinates.x + 1;
		int y = coordinates.y;
		coordinates = new Point(newX, y);
	}	
	
	public void moveLeft() {
		int newX = coordinates.x - 1;
		int y = coordinates.y;
		coordinates = new Point(newX, y);
	}
}
