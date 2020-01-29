package com.matthiaswk.tetris;

import java.awt.*;
import java.util.Random;

@SuppressWarnings("serial")
public class Block{
	private Point coordinates;
	private int dimension;
	private Image image;
	private BlockColor blockColor;

	public enum BlockColor{
		LIGHTBLUE, YELLOW, PINK, BLUE, ORANGE, GREEN, RED, EMPTY
	}

	Block(){
		this.blockColor = BlockColor.EMPTY;
	}

	Block(Point coordinates, int dimension, BlockColor blockColor){
		this.coordinates = coordinates;
		this.dimension = dimension;
		this.blockColor = blockColor;
		
		loadImage();
	}

	public Point getCoordinates(){
		return coordinates;
	}

	public void setCoordinates(Point newCoords){
		coordinates = newCoords;
	}

	public boolean isEmpty(){
		return blockColor.equals(BlockColor.EMPTY);
	}

	public void draw(Graphics g, GameField field){
		g.drawImage(image, coordinates.x*dimension, coordinates.y*dimension, field);
	}

	public void moveDown() {
		moveDown(1);
	}

	public void moveDown(int numMoves) {
		int x = coordinates.x;
		int newY = coordinates.y + numMoves;
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

	private void loadImage()
	{
		Toolkit t = Toolkit.getDefaultToolkit();
		Image temp;
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
}
