package com.midi.gs.lakabe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class PianoControl extends JPanel {
	private static final long serialVersionUID = 6072470186521240410L;

	private enum KeyType{
        White,
        Black
    }
	
	private static KeyType[] KeyTypeTable = {
        KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White,
        KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White,
        KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White,
        KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White,
        KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White,
        KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White,
        KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White,
        KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White,
        KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White,
        KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White,
        KeyType.White, KeyType.Black, KeyType.White, KeyType.Black, KeyType.White, KeyType.White, KeyType.Black, KeyType.White
    };
	
    private int lowNoteID = 24;
    private int highNoteID = 111;

    private int whiteKeyCount = 0;
    private float BlackKeyScale = 0.666666666f;
    private int whiteKeyWidth;
    private int blackKeyWidth;
    private int whiteKeyHeight;
    private int blackKeyHeight;
    
    private Frame frame;
    private PianoKey[] keys = null;
	
	public PianoControl(Frame frame) {
		this.setLayout(new BorderLayout(1, 0));
		this.setFocusable(true);
		this.addMouseListener(new Listener());
		this.addMouseMotionListener(new Listener());
		
		this.frame = frame;
		
		keys = new PianoKey[highNoteID - lowNoteID];
		
		for(int i = 0; i < keys.length; i++){
			keys[i] = new PianoKey();
            keys[i].setNote(i + lowNoteID); 
            keys[i].setPressed(false);

            if(KeyTypeTable[i] == KeyType.White)
            	whiteKeyCount++;
		}
	}

	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        whiteKeyWidth = getWidth() / whiteKeyCount; //16
        blackKeyWidth = (int)(whiteKeyWidth * BlackKeyScale); //10
        whiteKeyHeight = getHeight(); //51
        blackKeyHeight = (int)(whiteKeyHeight * BlackKeyScale); //34
      
        int inc = 0;
        for(int i = 0; i < keys.length; i++){
			if(KeyTypeTable[i] == KeyType.White){
				keys[i].setKeyType(KeyType.White);
				keys[i].setX((i - inc) * whiteKeyWidth); keys[i].setY(0);
				keys[i].setW(whiteKeyWidth); keys[i].setH(whiteKeyHeight);
				keys[i].paint(g2d);
			}
			else{
				inc++;
			}
		}
        
        inc = 0; int d = 1;
        for(int i = 0; i < keys.length; i++){
			if(KeyTypeTable[i] == KeyType.Black){ 
				inc = 11 + (whiteKeyWidth * (i - d));
				keys[i].setKeyType(KeyType.Black);
				keys[i].setX(inc); keys[i].setY(0);
				keys[i].setW(blackKeyWidth); keys[i].setH(blackKeyHeight);
				keys[i].paint(g2d);
				
				d++; 
			}
		}
        
//        for(int i = 0; i < keys.length; i++){
//        	if(KeyTypeTable[i] == KeyType.White)
//        		System.out.println("White: " + String.valueOf(keys[i].x) + " ! " + i + " ! " + Integer.toHexString(keys[i].getNote()));
//        	else
//        		System.out.println("Black: " + String.valueOf(keys[i].x) + " ! " + i + " ! " + Integer.toHexString(keys[i].getNote()));
//        }
	}
	
	public void setNote(int status, int note, int velocity){
		if((status & 0xf0) == 0x90){
			for(int i = 0; i < keys.length; i++){
				if(note == keys[i].getNote() && velocity != 0){
					keys[i].setPressed(true);
				}
				else if(note == keys[i].getNote() && velocity == 0){
					keys[i].setPressed(false); 
				}
			}
		}
		else{
			for(int i = 0; i < keys.length; i++){
				if(note == keys[i].getNote()){
					keys[i].setPressed(false); 
				}
			}
		}
		
		repaint();
	}
	
	class PianoKey{
		private boolean pressed;
		private int note;
		private int x, y, w, h;
		private KeyType key;
		
		public PianoKey() {
			this.pressed = false;
			this.note = 60; //0x3C middle C (C4)
			this.x = 0; this.y = 0;
			this.w = 0; this.h = 0;
			this.key = KeyType.White;
		}

		public boolean isPressed() {
			return pressed;
		}

		public void setPressed(boolean pressed) {
			this.pressed = pressed;
		}
		
		public int getNote() {
			return note;
		}

		public void setNote(int note) {
			this.note = note;
		}
		
		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}
		
		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}
		
		public int getW() {
			return w;
		}

		public void setW(int w) {
			this.w = w;
		}
		
		public int getH() {
			return h;
		}

		public void setH(int h) {
			this.h = h;
		}
		
		public KeyType getKeyType() {
			return key;
		}

		public void setKeyType(KeyType key) {
			this.key = key;
		}
		
		public void paint(Graphics2D g2d){
	
	        if(key == KeyType.White){
	        	if(isPressed()){
					g2d.setColor(Color.BLUE);
				}
				else{
					g2d.setColor(Color.WHITE);
				}
				
				g2d.fillRoundRect(x, y, w, h, 3, 3);
				g2d.setColor(Color.GRAY);
				g2d.drawRoundRect(x, y, w, h, 3, 3);
	        }
	        else{
	        	if(isPressed()){
					g2d.setColor(Color.BLUE);
				}
				else{
					g2d.setColor(Color.BLACK);
				}
				
				g2d.fillRoundRect(x, y, w, h, 3, 3);
				g2d.setColor(Color.GRAY);
				g2d.drawRoundRect(x, y, w, h, 3, 3);
	        }
		}
	}

	private class Listener extends MouseAdapter{
        @Override
		public void mouseClicked(MouseEvent e){
        	final int x = e.getX();
        	final int y = e.getY();
        	
        	int note = 0;
        	boolean t = false;
        	for(int i = 0; i < keys.length; i++){
        		if(KeyTypeTable[i] == KeyType.Black){
        			if(keys[i].getKeyType() == KeyType.Black){
            			if(x >= keys[i].x && x <= (keys[i].x + keys[i].w) && keys[i].h == 34 && y <= 34){
                			keys[i].setPressed(true); 
                			note = keys[i].getNote();
                        	repaint();
                        	System.out.println("Note: " + Integer.toHexString(keys[i].getNote()) + " ! " + String.valueOf(keys[i].getNote()));
                        	t = true; break;
            			}
            		}
        		}
        	}
        	
        	if(!t){
        		for(int i = 0; i < keys.length; i++){
            		if(KeyTypeTable[i] == KeyType.White){
            			if(keys[i].getKeyType() == KeyType.White){
                			if(x >= keys[i].x && x <= (keys[i].x + keys[i].w) && keys[i].h == 51 && y <= 51){
                    			keys[i].setPressed(true);
                    			note = keys[i].getNote();
                            	repaint();
                            	System.out.println("Note: " + Integer.toHexString(keys[i].getNote()) + " ! " + String.valueOf(keys[i].getNote()));
                            	break;
                			}
                		}
            		}
            	}
        	}
        	
        	if(note > 0){
        		frame.send(note, 90);
        	}
        }

        @Override
		public void mouseMoved(MouseEvent e){
        	for(int i = 0; i < keys.length; i++){
        		keys[i].setPressed(false);
            	repaint();
        	}
        }

        @Override
		public void mouseDragged(MouseEvent e){
        	final int x = e.getX();
        	final int y = e.getY();
        	
        	int note = 0;
        	boolean t = false;
        	for(int i = 0; i < keys.length; i++){
        		if(KeyTypeTable[i] == KeyType.Black){
        			if(keys[i].getKeyType() == KeyType.Black){
            			if(x >= keys[i].x && x <= (keys[i].x + keys[i].w) && keys[i].h == 34 && y <= 34){
                			keys[i].setPressed(true);
                			note = keys[i].getNote();
                        	repaint();
                        	System.out.println("Note: " + Integer.toHexString(keys[i].getNote()));
                        	t = true; break;
            			}
            		}
        		}
        	}
        	
        	if(!t){
        		for(int i = 0; i < keys.length; i++){
            		if(KeyTypeTable[i] == KeyType.White){
            			if(keys[i].getKeyType() == KeyType.White){
                			if(x >= keys[i].x && x <= (keys[i].x + keys[i].w) && keys[i].h == 51 && y <= 51){
                    			keys[i].setPressed(true);
                    			note = keys[i].getNote();
                            	repaint();
                            	System.out.println("Note: " + Integer.toHexString(keys[i].getNote()));
                            	break;
                			}
                		}
            		}
            	}
        	}
        	
        	if(note > 0){
        		frame.send(note, 90);
        	}
        }

        @Override
		public void mouseReleased(MouseEvent e){
        	for(int i = 0; i < keys.length; i++){
        		keys[i].setPressed(false);
            	repaint();
        	}
        }
	}
}
