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
        Point[] oldCoords = tetromino.getBlockCoordinates();
        ArrayList<Point> l1 = new ArrayList<>();
        Collections.addAll(l1, oldCoords);

        printCoords();
        tetromino.rotateRight();
        printCoords();
        tetromino.rotateLeft();
        printCoords();

        Point[] newCoords = tetromino.getBlockCoordinates();
        ArrayList<Point> l2 = new ArrayList<>();
        Collections.addAll(l2, newCoords);

        for(Point p : l1)
            assertTrue(l2.contains(p));
    }

    private void printCoords(){
        System.out.print("(");
        for(int i = 0; i<4 ;i++){
            Point p = tetromino.getBlockCoordinates()[i];
            System.out.print(p +", ");
        }

        System.out.println(")");
    }

    @Test
    void rotateLR() {
        Point[] oldCoords = tetromino.getBlockCoordinates();
        ArrayList<Point> l1 = new ArrayList<>();
        Collections.addAll(l1, oldCoords);

        printCoords();
        tetromino.rotateLeft();
        printCoords();
        tetromino.rotateRight();
        printCoords();

        Point[] newCoords = tetromino.getBlockCoordinates();
        ArrayList<Point> l2 = new ArrayList<>();
        Collections.addAll(l2, newCoords);

        for(Point p : l1)
            assertTrue(l2.contains(p));
    }

    @Test
    void rotateRRRR() {
        Point[] oldCoords = tetromino.getBlockCoordinates();
        ArrayList<Point> l1 = new ArrayList<>();
        Collections.addAll(l1, oldCoords);

        printCoords();
        tetromino.rotateRight();
        printCoords();
        tetromino.rotateRight();
        printCoords();
        tetromino.rotateRight();
        printCoords();
        tetromino.rotateRight();
        printCoords();

        Point[] newCoords = tetromino.getBlockCoordinates();
        ArrayList<Point> l2 = new ArrayList<>();
        Collections.addAll(l2, newCoords);

        for(Point p : l1)
            assertTrue(l2.contains(p));
    }

    @Test
    void rotateLLLL() {
        Point[] oldCoords = tetromino.getBlockCoordinates();
        ArrayList<Point> l1 = new ArrayList<>();
        Collections.addAll(l1, oldCoords);

        printCoords();
        tetromino.rotateLeft();
        printCoords();
        tetromino.rotateLeft();
        printCoords();
        tetromino.rotateLeft();
        printCoords();
        tetromino.rotateLeft();
        printCoords();

        Point[] newCoords = tetromino.getBlockCoordinates();
        ArrayList<Point> l2 = new ArrayList<>();
        Collections.addAll(l2, newCoords);

        for(Point p : l1)
            assertTrue(l2.contains(p));
    }
}