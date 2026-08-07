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

public class FrameTempo extends JDialog{
	private static final long serialVersionUID = 7620968420874578724L;
	
	private MidiSequencer midiSequencer;
	private JSlider sliderTempo;
	private JLabel lblTempo, lblValueTempo;
	private JCheckBox checkDefault;
	
	public FrameTempo(Frame frame, final MidiSequencer midiSequencer) {
		super(frame, "Tempo", Dialog.ModalityType.DOCUMENT_MODAL);
		setSize(new Dimension(260, 160));
		int xn = ((frame.getWidth() / 2) + frame.getX()) - (getWidth() / 2);
		int yn = ((frame.getHeight() / 2) + frame.getY())  - (getHeight() / 2);
		setBounds(xn, yn, getWidth(), getHeight());
		setResizable(false);
		setLayout(new BorderLayout());
		setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
		
		this.midiSequencer = midiSequencer;
		
		lblTempo = new JLabel("Tempo", SwingConstants.CENTER);
		
		sliderTempo = new JSlider(JSlider.HORIZONTAL);
		sliderTempo.setPaintLabels(true);
		if(midiSequencer.isPlaying())
			sliderTempo.setValue(midiSequencer.getTempo());
		else
			sliderTempo.setValue(0);
		sliderTempo.addChangeListener(new javax.swing.event.ChangeListener() {
			@Override
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				JSlider slider = (JSlider) evt.getSource();
				if (slider.getValueIsAdjusting()){
					String sign = "+";
                    if (slider.getValue() < 0){
                        sign = "";
                    }
                    lblValueTempo.setText(sign + slider.getValue());
				}
			}
		});
		
		if(midiSequencer.isPlaying()){
			sliderTempo.setMaximum(midiSequencer.getRangeTempo()[1]);
			sliderTempo.setMinimum(midiSequencer.getRangeTempo()[0]);
		}
		else{
			sliderTempo.setMaximum(200);
			sliderTempo.setMinimum(0);
		}
		
		lblValueTempo = new JLabel(String.valueOf(sliderTempo.getValue()), SwingConstants.CENTER);
		checkDefault = new JCheckBox("Default");
		checkDefault.setBorderPaintedFlat(true);
		
		JPanel contentDefault = new JPanel(new BorderLayout());
		contentDefault.setBorder(new EmptyBorder(0, 3, 0, 3));
		contentDefault.add(checkDefault, BorderLayout.WEST);
		contentDefault.add(lblValueTempo, BorderLayout.EAST);
		
		JPanel contentTempo = new JPanel(new BorderLayout());
		contentTempo.add(lblTempo, BorderLayout.NORTH);
		contentTempo.add(sliderTempo, BorderLayout.CENTER);
		contentTempo.add(contentDefault, BorderLayout.SOUTH);
		
		JPanel content = new JPanel(new GridLayout(1, 0));
		content.setBorder(new EmptyBorder(5, 5, 10, 5));
		content.add(contentTempo);
		
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
				if(checkDefault.isSelected()){
					midiSequencer.setTempo(midiSequencer.getDefaultTempo());
					sliderTempo.setValue(midiSequencer.getDefaultTempo());
				}
				else {
					midiSequencer.setTempo(sliderTempo.getValue());
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
	
	public void setRangeTempo(int[] values){
		if(midiSequencer.isPlaying()){
			sliderTempo.setMaximum(values[1]);
			sliderTempo.setMinimum(values[0]);
			sliderTempo.setValue(midiSequencer.getTempo());
		}	
		invalidate();  
	}
}
