package com.midi.gs.lakabe;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;


public class ProgressSequence extends JDialog{
	private static final long serialVersionUID = -6549952286843832461L;
	private MidiSequencer midiSequencer;
	private Image spinner;
	
	public ProgressSequence(Frame frame, MidiSequencer midiSequencer) {
		super(frame, Dialog.ModalityType.DOCUMENT_MODAL);
		setSize(new Dimension(40, 40));
		int xn = ((frame.getWidth() / 2) + frame.getX()) - (getWidth() / 2);
		int yn = ((frame.getHeight() / 2) + frame.getY())  - (getHeight() / 2);
		setBounds(xn, yn, getWidth(), getHeight());
		setResizable(false);
		setUndecorated(true);
		getContentPane().setLayout(new BorderLayout());
		
		spinner = getToolkit().getImage(getClass().getResource("/assets/images/spinner.gif"));
		JLabel img = new JLabel(new ImageIcon(spinner), SwingConstants.CENTER);
		img.setAlignmentX(SwingConstants.CENTER);
		getContentPane().add(img, BorderLayout.CENTER);
		
		this.midiSequencer = midiSequencer;
	}
	
	public void midiStart(final File file, final boolean isSequenceManual){
		
		new Thread(){
			@Override
			public void run(){
				boolean t = midiSequencer.midiStart(file, isSequenceManual);
				if(t){
					while(midiSequencer.isPlaying()){
						if(!isSequenceManual){
							try {
								Thread.sleep(1000);
							} catch (InterruptedException ex) {
								ex.printStackTrace();
							}
						}
						
						setVisible(false); dispose();
						
						break;
					}
				}
				else{
					setVisible(false); dispose();
				}	
				
			}
		}.start();
		
	}
}
