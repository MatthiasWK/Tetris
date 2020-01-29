package com.matthiaswk.tetris;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TetrominoTest {
    Tetromino tetromino;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        tetromino = new Tetromino(30, Tetromino.TetrominoShape.Z);
    }

    @Test
    void rotateRL() {
        ArrayList<Point> oldCoords = coordsAsList();

        printCoords();
        tetromino.rotateRight();
        printCoords();
        tetromino.rotateLeft();
        printCoords();

        ArrayList<Point> newCoords = coordsAsList();

        assertTrue(newCoords.containsAll(oldCoords));
    }

    @Test
    void rotateLR() {
        ArrayList<Point> oldCoords = coordsAsList();

        printCoords();
        tetromino.rotateLeft();
        printCoords();
        tetromino.rotateRight();
        printCoords();

        ArrayList<Point> newCoords = coordsAsList();

        assertTrue(newCoords.containsAll(oldCoords));
    }

    @Test
    void rotateRRRR() {
        ArrayList<Point> oldCoords = coordsAsList();

        printCoords();
        tetromino.rotateRight();
        printCoords();
        tetromino.rotateRight();
        printCoords();
        tetromino.rotateRight();
        printCoords();
        tetromino.rotateRight();
        printCoords();

        ArrayList<Point> newCoords = coordsAsList();

        assertTrue(newCoords.containsAll(oldCoords));
    }

    @Test
    void rotateLLLL() {
        ArrayList<Point> oldCoords = coordsAsList();

        printCoords();
        tetromino.rotateLeft();
        printCoords();
        tetromino.rotateLeft();
        printCoords();
        tetromino.rotateLeft();
        printCoords();
        tetromino.rotateLeft();
        printCoords();

        ArrayList<Point> newCoords = coordsAsList();

        assertTrue(newCoords.containsAll(oldCoords));
    }

    private ArrayList<Point> coordsAsList(){
        Point[] coords = tetromino.getBlockCoordinates();
        ArrayList<Point> list = new ArrayList<>();
        Collections.addAll(list, coords);
        return list;
    }

    private void printCoords(){
        System.out.print("(");
        for(int i = 0; i<4 ;i++){
            Point p = tetromino.getBlockCoordinates()[i];
            System.out.print(p +", ");
        }

        System.out.println(")");
    }
}