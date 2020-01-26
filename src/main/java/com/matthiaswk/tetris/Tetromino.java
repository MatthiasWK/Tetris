package com.matthiaswk.tetris;

import java.awt.*;
import java.util.Random;

public class Tetromino {
    public Point coordinates;
    public int dimension;
    public TetrominoShape shape;
    public Block[] blocks;
    private Point[] blockCoordinates;
    private Block.BlockColor blockColor;
    final private Point SpawnPoint = new Point(4,1);

    final private Point[][] IShapeCoordinates = new Point[][]{
            {new Point(-1, 0), new Point(0, 0), new Point(1, 0), new Point(2, 0)},
            {new Point(1, -1), new Point(1, 0), new Point(1, 1), new Point(1, 2)},
            {new Point(-1, 1), new Point(0, 1), new Point(1, 1), new Point(2, 1)},
            {new Point(0, -1), new Point(0, 0), new Point(0, 1), new Point(0, 2)}
    };
    private int IShapeRotationIndex = 0;

    public enum TetrominoShape{
        I, O, T, J, L, S, Z;

        public static TetrominoShape getRandomShape() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    Tetromino(int dimension){
        coordinates = SpawnPoint;
        this.dimension = dimension;
        blocks = new Block[4];
        shape = TetrominoShape.getRandomShape();
        buildTetromino();
    }

    Tetromino(int dimension, Point coordinates){
        this.coordinates = coordinates;
        this.dimension = dimension;
        blocks = new Block[4];
        shape = TetrominoShape.getRandomShape();
        buildTetromino();
    }

    private void buildTetromino(){
        initBlocks();
        buildBlocks();
    }

    private void initBlocks(){
        switch (shape){
            case I:
                blockColor = Block.BlockColor.LIGHTBLUE;
                blockCoordinates = new Point[]{new Point(0, 0), new Point(-1, 0), new Point(1, 0), new Point(2, 0)};
                break;
            case O:
                blockColor = Block.BlockColor.YELLOW;
                blockCoordinates = new Point[]{new Point(0, 0), new Point(0, -1), new Point(1, 0), new Point(1, -1)};
                break;
            case T:
                blockColor = Block.BlockColor.PINK;
                blockCoordinates = new Point[]{new Point(0, 0), new Point(0, -1), new Point(-1, 0), new Point(1, 0)};
                break;
            case J:
                blockColor = Block.BlockColor.BLUE;
                blockCoordinates = new Point[]{new Point(0, 0), new Point(-1, 0), new Point(1, 0), new Point(-1, -1)};
                break;
            case L:
                blockColor = Block.BlockColor.ORANGE;
                blockCoordinates = new Point[]{new Point(0, 0), new Point(-1, 0), new Point(1, 0), new Point(1, -1)};
                break;
            case S:
                blockColor = Block.BlockColor.GREEN;
                blockCoordinates = new Point[]{new Point(0, 0), new Point(0, -1), new Point(1, -1), new Point(-1, 0)};
                break;
            case Z:
                blockColor = Block.BlockColor.RED;
                blockCoordinates = new Point[]{new Point(0, 0), new Point(0, -1), new Point(-1, -1), new Point(1, 0)};
                break;
        }
    }

    private void buildBlocks(){
        for(int i = 0; i < blocks.length; i++){
            Point currentBlockCoordinates = blockCoordinates[i];
            Point newBlockCoordinates = new Point(coordinates.x + currentBlockCoordinates.x, coordinates.y + currentBlockCoordinates.y);
            blocks[i] = new Block(newBlockCoordinates, dimension, blockColor);
        }
    }

    public void moveDown() {
        int x = coordinates.x;
        int newY = coordinates.y + 1;
        coordinates = new Point(x, newY);

        for(Block block: blocks) {
            block.moveDown();
        }
    }

    public void moveRight() {
        int newX = coordinates.x + 1;
        int y = coordinates.y;
        coordinates = new Point(newX, y);

        for(Block block: blocks) {
            block.moveRight();
        }
    }

    public void moveLeft() {
        int newX = coordinates.x - 1;
        int y = coordinates.y;
        coordinates = new Point(newX, y);

        for(Block block: blocks) {
            block.moveLeft();
        }
    }

    public boolean isOShape(){
        return shape.equals(TetrominoShape.O);
    }

    public boolean isIShape(){
        return shape.equals(TetrominoShape.I);
    }

    public void rotateRight(){
        if(isIShape()){
            IShapeRotationIndex++;
            if(IShapeRotationIndex > 3)
                IShapeRotationIndex = 0;
            blockCoordinates = IShapeCoordinates[IShapeRotationIndex];
            buildBlocks();
        }
        else {
            for(int i = 0; i < blocks.length; i++){
                Block currentBlock = blocks[i];
                currentBlock.coordinates = rotatedRightCoordinates(i);
            }
        }
    }

    public Point rotatedRightCoordinates(int blockIndex){
        Point currentBlockCoordinates = blockCoordinates[blockIndex];
        int newX = -currentBlockCoordinates.y;
        int newY = currentBlockCoordinates.x;
        blockCoordinates[blockIndex] = new Point(newX,newY);
        Point newBlockCoordinates = new Point(coordinates.x + newX, coordinates.y + newY);
        return newBlockCoordinates;
    }

    public void rotateLeft(){
        if(isIShape()){
            IShapeRotationIndex--;
            if(IShapeRotationIndex < 0)
                IShapeRotationIndex = 3;
            blockCoordinates = IShapeCoordinates[IShapeRotationIndex];
            buildBlocks();
        }
        else {
            for(int i = 0; i < blocks.length; i++){
                Block currentBlock = blocks[i];
                currentBlock.coordinates = rotatedLeftCoordinates(i);
            }
        }
    }

    public Point rotatedLeftCoordinates(int blockIndex){
        Point currentBlockCoordinates = blockCoordinates[blockIndex];
        int newX = currentBlockCoordinates.y;
        int newY = -currentBlockCoordinates.x;
        blockCoordinates[blockIndex] = new Point(newX,newY);
        Point newBlockCoordinates = new Point(coordinates.x + newX, coordinates.y + newY);
        return newBlockCoordinates;
    }
}