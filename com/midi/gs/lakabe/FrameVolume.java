package com.midi.gs.lakabe;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class FrameVolume extends JDialog{
	private static final long serialVersionUID = -1170213211081615218L;
	
	private MidiSequencer midiSequencer;
	private JSlider sliderVolume;
	private JLabel lblVolume, lblValueVolume;
	private JCheckBox checkMute;
	
	public FrameVolume(Frame frame, final MidiSequencer midiSequencer) {
		super(frame, "Volume", Dialog.ModalityType.DOCUMENT_MODAL);
		setSize(new Dimension(260, 160));
		int xn = ((frame.getWidth() / 2) + frame.getX()) - (getWidth() / 2);
		int yn = ((frame.getHeight() / 2) + frame.getY())  - (getHeight() / 2);
		setBounds(xn, yn, getWidth(), getHeight());
		setResizable(false);
		setLayout(new BorderLayout());
		setFont(new Font(Font.DIALOG, Font.PLAIN, 12));

		this.midiSequencer = midiSequencer;
		
		lblVolume = new JLabel("Volume", SwingConstants.CENTER);
		
		sliderVolume = new JSlider(JSlider.HORIZONTAL);
		sliderVolume.setPaintLabels(true);
		if(midiSequencer.isPlaying())
			sliderVolume.setValue(midiSequencer.getVolume());
		else
			sliderVolume.setValue(0);
		sliderVolume.addChangeListener(new javax.swing.event.ChangeListener() {
			@Override
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				JSlider slider = (JSlider) evt.getSource();
				if (slider.getValueIsAdjusting()){
					String sign = "+";
                    if (slider.getValue() < 0){
                        sign = "";
                    }
                    lblValueVolume.setText(sign + slider.getValue());
				}
			}
		});
		
		if(midiSequencer.isPlaying()){
			sliderVolume.setMaximum(midiSequencer.getRangeVolume()[1]);
			sliderVolume.setMinimum(midiSequencer.getRangeVolume()[0]);
		}
		else{
			sliderVolume.setMaximum(100);
			sliderVolume.setMinimum(0);
		}
		
		lblValueVolume = new JLabel(String.valueOf(sliderVolume.getValue()), SwingConstants.CENTER);
		checkMute = new JCheckBox("Mute");
		checkMute.setBorderPaintedFlat(true);
		
		JPanel contentMute = new JPanel(new BorderLayout());
		contentMute.setBorder(new EmptyBorder(0, 3, 0, 3));
		contentMute.add(checkMute, BorderLayout.WEST);
		contentMute.add(lblValueVolume, BorderLayout.EAST);
		
		
		JPanel contentVolume = new JPanel(new BorderLayout());
		contentVolume.add(lblVolume, BorderLayout.NORTH);
		contentVolume.add(sliderVolume, BorderLayout.CENTER);
		contentVolume.add(contentMute, BorderLayout.SOUTH);
		
		JPanel content = new JPanel(new GridLayout(1, 0));
		content.setBorder(new EmptyBorder(5, 5, 10, 5));
		content.add(contentVolume);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false); dispose();
			}
		});
		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(checkMute.isSelected()){
					midiSequencer.setMute(checkMute.isSelected());
				}
				else {
					midiSequencer.setMute(checkMute.isSelected());
					midiSequencer.setVolume(sliderVolume.getValue());
				}
				setVisible(false); dispose();
			}
		});
		btnOk.setPreferredSize(btnCancel.getPreferredSize()); 
		
		JPanel foot = new JPanel(new FlowLayout(FlowLayout.CENTER));
		foot.add(btnCancel);
		foot.add(btnOk);
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(new EmptyBorder(7, 7, 7, 7));
		contentPane.add(content, BorderLayout.CENTER);
		contentPane.add(foot, BorderLayout.SOUTH);
		
		
		getContentPane().add(contentPane, BorderLayout.CENTER);
	}

	public void setRangeVolume(int[] values){
		if(midiSequencer.isPlaying()){
			sliderVolume.setMaximum(values[1]);
			sliderVolume.setMinimum(values[0]);
			sliderVolume.setValue(midiSequencer.getVolume());
		}	
		invalidate();  
	}
}
