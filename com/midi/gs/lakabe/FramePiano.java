package com.midi.gs.lakabe;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class FramePiano extends JFrame{
	private static final long serialVersionUID = 6139482647367078459L;

	private PianoControl pianoControl = null;
	
	public FramePiano(Frame frame) {
		super("Piano");
		Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		setBounds(center.x - 422, center.y - 50, 844, 100);
		setResizable(false);
		setLayout(new BorderLayout());
		setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
		
		pianoControl = new PianoControl(frame);
		
		JPanel content = new JPanel(new GridLayout(1, 0));
		content.setBorder(new EmptyBorder(5, 5, 5, 5));
		content.add(pianoControl);
		
		getContentPane().add(content, BorderLayout.CENTER);
	}

	public PianoControl getPianoControl(){
		return pianoControl;
	}
}
