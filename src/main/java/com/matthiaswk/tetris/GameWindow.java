package com.matthiaswk.tetris;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class GameWindow extends JFrame{
	
	public GameWindow() {
		initUI();
	}
	
	private void initUI() {
		GameField gf = new GameField();
		add(gf);

		addKeyListener(gf);
        setResizable(false);
        pack();
        
        setTitle("Tetris");    
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
        setVisible(true);
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
            new GameWindow();
        });
	}
}
