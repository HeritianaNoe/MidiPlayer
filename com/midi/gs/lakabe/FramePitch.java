package com.midi.gs.lakabe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.RenderingHints;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class FramePitch extends JDialog{
	private static final long serialVersionUID = 4382659421914334107L;
	
	private PitchCanvas pitchCanvas = null;
	private int N = 16;
	private int[] velocitys = new int[N];
	
	public FramePitch(final Frame frame) {
		super(frame, "Pitch", Dialog.ModalityType.MODELESS);
		Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		setBounds(center.x - 102, center.y - 90, 203, 180);
		setResizable(false);
		setLayout(new BorderLayout());
		setFont(new Font(Font.DIALOG, Font.PLAIN, 12)); 
		
		pitchCanvas = new PitchCanvas();
		
		for(int i = 0 ; i < N ; i ++ ){
			velocitys[i] = 2;
		}
		
		JPanel content = new JPanel(new BorderLayout());
		content.setBorder(new EmptyBorder(5, 5, 5, 5));
		content.add(pitchCanvas);
		
		getContentPane().add(content, BorderLayout.CENTER);
	}

	public void initialize(){
		for(int i = 0 ; i < N ; i ++ ){
			velocitys[i] = 2;
		}
	}
	
	public void setPitchData(int note, int velocity){	
		for(int i = 0 ; i < N ; i ++ ){
			if(note == i)
				velocitys[i] = velocity + 2;
		}
		pitchCanvas.repaint();
	}
	
	private class PitchCanvas extends JPanel{
		private static final long serialVersionUID = -4452360467383264495L;
		
        public PitchCanvas(){
            super(true);
            setBackground(Color.BLACK);
        }
        
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D)g;

            
            RenderingHints hints = new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.addRenderingHints(hints);

             
            int w = getWidth()/N;
            MidiPitch.setColor(g2d, MidiPitch.Green);
            for(int i = 0 ; i < N ; i++ ) {
            	MidiPitch.fillRectangle(g2d, 1 + i * w, getHeight() - velocitys[i], w - 1, velocitys[i]);
            }
        }
	}
}
