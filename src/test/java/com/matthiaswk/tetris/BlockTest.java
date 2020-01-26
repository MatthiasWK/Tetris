package com.matthiaswk.tetris;

import jdk.nashorn.internal.ir.annotations.Ignore;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
class BlockTest {
    Block block;


    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        block = new Block(new Point(0,0),30, Block.BlockColor.BLUE);
    }

    @org.junit.jupiter.api.Test
    void moveDown() {
        block.moveDown();
        assertEquals(new Point(0,1), block.coordinates);
    }

    @org.junit.jupiter.api.Test
    void moveRight() {
        block.moveRight();
        assertEquals(new Point(1,0), block.coordinates);
    }

    @org.junit.jupiter.api.Test
    void moveLeft() {
        block.moveLeft();
        assertEquals(new Point(-1,0), block.coordinates);
    }
}