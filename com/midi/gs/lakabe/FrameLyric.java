package com.midi.gs.lakabe;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

public class FrameLyric extends JFrame{
	private static final long serialVersionUID = 828605957411879516L;

	private JTextArea textArea = null;
	
	public FrameLyric() {
		super("Lyric");
		Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		setBounds(center.x - 200, center.y - 210, 400, 420);
		setResizable(false);
		setLayout(new BorderLayout());
		setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		JScrollPane panelScroll = new JScrollPane(textArea);
        panelScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
		JPanel content = new JPanel(new GridLayout(1, 0));
		content.setBorder(new EmptyBorder(5, 5, 5, 5));
		content.add(panelScroll);
		
		getContentPane().add(content, BorderLayout.CENTER);
	}

	public void setText(String txt){
		textArea.append(txt);
	}
	
	public void clear(){
		textArea.setText("");
	}
}
