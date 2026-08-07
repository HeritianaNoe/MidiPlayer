package com.midi.gs.lakabe;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FrameConfig extends JDialog{
	private static final long serialVersionUID = 4734445917595539395L;
	
	public FrameConfig(final Frame frame, final MidiSequencer midiSequencer) {
		 super(frame, "Config", Dialog.ModalityType.DOCUMENT_MODAL);
		 setSize(new Dimension(244, 170));
		 int xn = ((frame.getWidth() / 2) + frame.getX()) - (getWidth() / 2);
		 int yn = ((frame.getHeight() / 2) + frame.getY())  - (getHeight() / 2);
		 setBounds(xn, yn, getWidth(), getHeight());
		 setResizable(false);
		 Container dialogContainer = getContentPane();
		 dialogContainer.setLayout(new BorderLayout());

		 JLabel lbl = new JLabel("Midi Output Device:" , SwingConstants.LEFT);
		 lbl.setBorder(new EmptyBorder(5, 10, 0, 10));
	     
		 int i = 0;
		 final JComboBox<Object> combo = new JComboBox<Object>();
		 combo.setBorder(new EmptyBorder(0, 10, 0, 10));
		 for(String s: midiSequencer.getNameMidiDevice()){
			 combo.insertItemAt(s, i); i++; 
		 }
		 combo.setSelectedIndex(frame.default_device);
		 
		 JPanel panelContent = new JPanel(new FlowLayout(FlowLayout.LEFT));
		 
		 JCheckBox checkPlay = new JCheckBox("Sequence Manual");
		 checkPlay.setSelected(frame.isSequenceManual);
		 checkPlay.setBorder(new EmptyBorder(5, 10, 10, 10));
		 checkPlay.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				
				JCheckBox o = (JCheckBox)e.getSource(); 
				frame.isSequenceManual = o.isSelected();
			}
			 
		 });
		 
		 panelContent.add(lbl);
		 panelContent.add(combo);
		 panelContent.add(checkPlay);
		 
		 dialogContainer.add(panelContent, BorderLayout.CENTER);   
		 
		 JPanel panel1 = new JPanel();
		 panel1.setLayout(new GridLayout(1, 0, 5, 0));
		 panel1.setBorder(new EmptyBorder(5, 10, 10, 10));
		 JButton cancelButton = new JButton("Cancel");
		 cancelButton.addActionListener(new ActionListener() {
		     @Override
		     public void actionPerformed(ActionEvent e) {
		        setVisible(false); dispose();
		     }
		 });
		 
		 JButton okButton = new JButton("Ok");
		 okButton.addActionListener(new ActionListener() {
		     @Override
		     public void actionPerformed(ActionEvent e) {
		    	 frame.default_device = combo.getSelectedIndex();
		    	 midiSequencer.setChangeDevice(frame.default_device);
		    	 
		    	 setVisible(false); dispose(); frame.writeConfig();
		     }
		 });
		
		 panel1.add(cancelButton);
		 panel1.add(okButton);
		 dialogContainer.add(panel1, BorderLayout.SOUTH);
	}

}
