package com.midi.gs.lakabe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

import javax.swing.JOptionPane;

public class MidiSequencer implements MetaEventListener{
	private ArrayList<Info> newInfos = new ArrayList<Info>();
	private MidiDevice midiOutput = null;
	private Sequencer sequencer;
	private Sequence sequence;
	private MidiReceiver midiReceiver;
	
	private MidiSequence midiSequence;
	private MidiChannel[] midiChannel;
	
	private List<String> nameMidiDevice = new ArrayList<String>();
    
	private PianoControl pianoControl = null;
	private FrameLyric frameLyric = null;
	private FramePitch framePitch = null;
	
	private long timePause = 0;
	private File file;
	
	private int newVolume = 0;
	private int[] newRangeVolume = new int[2]; 
	
	private float defaultTempo = 120.0f;
	private float newTempo = 120.0f;
	private int[] newRangeTempo = new int[2]; 
	
	public static ArrayList<Long> timings = new ArrayList<Long>();
	public static ArrayList<Float> bpms = new ArrayList<Float>();
	
	public MidiSequencer(PianoControl pianoControl, FrameLyric frameLyric, FramePitch framePitch) {
		this.pianoControl = pianoControl;
		this.frameLyric = frameLyric;
		this.framePitch = framePitch;
		
		Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (Info info : infos){
			try {
				MidiDevice midiDevice = MidiSystem.getMidiDevice(info);
				if(midiDevice.getMaxReceivers() != 0 && !midiDevice.isOpen()){
					if (!info.getName().equals("Real Time Sequencer") && !info.getName().equals("$NORECEIVER")){
						if (info.getName().equals("Gervill")){
							newInfos.add(info);
							nameMidiDevice.add(info.getDescription());
						}
						else{
							newInfos.add(info);
							nameMidiDevice.add(info.getName());
						}
					}
					
				}
			} 
			catch (MidiUnavailableException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void setOpenDevice(int index){
        if(midiOutput != null) 
        	midiOutput.close();
        
        if(sequencer != null) 
			sequencer.close();
        
        try {
        	midiOutput = MidiSystem.getMidiDevice(newInfos.get(index));
        	sequencer = MidiSystem.getSequencer();
        	
            if(!midiOutput.isOpen())
                midiOutput.open();

            if(!sequencer.isOpen())
                sequencer.open();
            
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();
			midiChannel = synthesizer.getChannels();

            Transmitter transmitter = sequencer.getTransmitter();
			midiReceiver = new MidiReceiver(midiOutput.getReceiver(), pianoControl, framePitch);
			transmitter.setReceiver(midiReceiver);
			
			setChangeCycleMethod();
        } 
        catch (MidiUnavailableException ex) {
            ex.printStackTrace();
        }
    }
	
	public void setChangeDevice(int index) {
		timePause = sequencer.getTickPosition();
		boolean isRunning = isPlaying();
		
		if(sequencer != null) 
			sequencer.close();
		
		if(midiOutput != null) 
			midiOutput.close();
		
		try {
			midiOutput = MidiSystem.getMidiDevice(newInfos.get(index));
        	sequencer = MidiSystem.getSequencer();
        	
            if(!midiOutput.isOpen())
                midiOutput.open();

            if(!sequencer.isOpen())
                sequencer.open();
            
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();
			midiChannel = synthesizer.getChannels();

            Transmitter transmitter = sequencer.getTransmitter();
			midiReceiver = new MidiReceiver(midiOutput.getReceiver(), pianoControl, framePitch);
			transmitter.setReceiver(midiReceiver);
			
			sequencer.setSequence(sequence);
			setChangeCycleMethod();
			
			if (isRunning) {
				sequencer.start();
			}
			sequencer.setTickPosition(timePause);
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public boolean midiStart(File file, boolean isSequenceManual) {
		this.file = file;

		if(isSequenceManual){
			try {
				midiStop(); 
				timePause = 0;
				frameLyric.clear();
				midiSequence = new MidiSequence(file);
				sequence = midiSequence.getSequence();
				sequencer.setSequence(sequence);
				sequencer.addMetaEventListener(this);
				setChangeCycleMethod();
				sequencer.start();  
				
				timings = new ArrayList<Long>();
				bpms = new ArrayList<Float>();
				MidiTrack.getDefaultTempos(sequencer);
				
				newVolume = 0;
				newRangeVolume[0] = -128; newRangeVolume[1] = 128;
				defaultTempo = 0;
				newTempo = 0;
				
				return true;
			} 
			catch (InvalidMidiDataException ex) {
				ex.printStackTrace(); 
				JOptionPane.showMessageDialog(null, String.format("Error: %s.", ex.getMessage()), "Midi by LakaBe", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		else{
			try {
				timePause = 0;
				frameLyric.clear();
				sequence = MidiSystem.getSequence(file);
				sequencer.setSequence(sequence);
				sequencer.addMetaEventListener(this);
				setChangeCycleMethod();
				sequencer.start();  
				
				timings = new ArrayList<Long>();
				bpms = new ArrayList<Float>();
				MidiTrack.getDefaultTempos(sequencer);
				
				newVolume = 0;
				newRangeVolume[0] = -128; newRangeVolume[1] = 128;
				defaultTempo = 0;
				newTempo = 0;
				
				return true;
			} 
			catch (InvalidMidiDataException | IOException ex) {
				ex.printStackTrace(); 
				JOptionPane.showMessageDialog(null, String.format("Error: %s.", ex.getMessage()), "Midi by LakaBe", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
	}
	
	public void setChangeCycleMethod(){
		if(Frame.modePlay == Frame.MODE_REPEAT){
			sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
			sequencer.setLoopStartPoint(0);
			sequencer.setLoopEndPoint(-1);
		}
		else{
			sequencer.setLoopCount(0);
			sequencer.setLoopStartPoint(0);
			sequencer.setLoopEndPoint(-1);
		}
		sequencer.setTickPosition(0);
	}
	
	public void saveSequence(File outputFile){
		if(isPlaying())
			MidiTrack.saveMidi(sequencer, outputFile);
		else
			JOptionPane.showMessageDialog(null, "Play sequence!", "Midi by LakaBe", JOptionPane.WARNING_MESSAGE); 
	}
	
	public void printSequence(){
		if(isPlaying())
			MidiTrack.printSequence(sequencer, file, midiSequence.getMidiFileType());
		else
			JOptionPane.showMessageDialog(null, "Play sequence!", "Midi by LakaBe", JOptionPane.WARNING_MESSAGE); 
	}
	
	public int[] getRangeVolume(){
		
		if(newRangeVolume[0] == -128 && newRangeVolume[1] == 128){
			List<Integer> trackID = MidiTrack.getTracks(sequencer);
			for(int id : trackID){
				int[] oldRange = MidiTrack.getTrackDynamicVolumeRange(sequencer, id);
				if(newRangeVolume[0] < oldRange[0]){
					newRangeVolume[0] = oldRange[0];  
				}
				if(newRangeVolume[1] > oldRange[1]){
					newRangeVolume[1] = oldRange[1];  
				}
			}
		}

		return newRangeVolume;
	}
	
	public int getVolume(){
		return newVolume;
	}
	
	public void setVolume(int value){
		if(isPlaying()){
			int tmpVolume = newVolume;
			newVolume = value;
			
			if(value != 0) {
				value = value - tmpVolume;
			}

			List<Integer> trackID = MidiTrack.getTracks(sequencer);
			for(int id : trackID)
				MidiTrack.setTrackVolume(sequencer, id, value);
		}
	}
	
	public void setMute(boolean value){
		if(isPlaying()){
			List<Integer> trackID = MidiTrack.getTracks(sequencer);
			for(int id : trackID){
				if(value){
					MidiTrack.muteTrack(sequencer, id);
				}
				else{
					MidiTrack.unmuteTrack(sequencer, id);
				}
			}
		}
	}
	
	public int[] getRangeTempo(){
		newRangeTempo[0] = -90; 
		newRangeTempo[1] =  90; 
		return newRangeTempo;
	}
	
	public int getTempo(){
		return (int)newTempo;
	}
	
	public int getDefaultTempo(){
		return (int)defaultTempo;
	}
	
	public void setTempo(int value){
		if(isPlaying()){
			int tmpTempo = (int)newTempo;
			newTempo = value;
			
			if(value != 0) {
				value = value - tmpTempo;
			}
			
			if(bpms.size() > 1){
				MidiTrack.setEmptyTempos(sequencer);
				MidiTrack.setTempos(sequencer, timings, bpms, value);
			}
			else{
				sequencer.setTempoInBPM(sequencer.getTempoInBPM() + value);
			}
		}
	}
	
	public void setTrackSolo(Sequencer sequencer, int trackID, boolean solo){
		if(solo){
			MidiTrack.setTrackSolo(sequencer, trackID);
		}
		else{
			MidiTrack.setTrackNotSolo(sequencer, trackID);
		}
	}
	
	public void midiStop() {
		if(isPlaying())
			sequencer.stop();
		timePause = 0;
		sequencer.setTickPosition(0);
	}
	
	public void midiPause() {
		if (isPlaying()) {
			timePause = sequencer.getTickPosition();
			sequencer.stop();
		} else {
			if(timePause > 0){
				sequencer.setTickPosition(timePause);
				sequencer.start();
			}
		}
	}
	
	public boolean isPlaying() {
		return sequencer.isRunning();
	}
	
	public long getPosition() {
		return sequencer.getMicrosecondPosition();
	}
	
	public void setPosition(long time) {
		sequencer.setMicrosecondPosition(time);
	}

	public long getLength() {
		return sequencer.getMicrosecondLength();
	}
	
    public List<String> getNameMidiDevice() {
        return nameMidiDevice;
    }
    
	public void setClose(){
        if (sequencer != null && sequencer.isOpen() )
            sequencer.close();
        
        if (midiOutput != null && midiOutput.isOpen() )
            midiOutput.close();
	}

	@Override
	public void meta(MetaMessage m) {
		
		String txt = new String();
		switch(m.getType()){
		case 0x01 : txt = new String(m.getData()); break;
		case 0x02 : txt = new String(m.getData()); break;
		case 0x03 : txt = new String(m.getData()); break;
		case 0x04 : txt = new String(m.getData()); break;
		case 0x05 : txt = new String(m.getData()); break;
		default: break;
		}
		
		frameLyric.setText(txt);
	}
	
	public void send(int note, int velocity){
		int[] bytes = new int[2];
		bytes[0] = note;
		bytes[1] = velocity;
		
		midiChannel[1].noteOn(note, velocity);
//		try {
//			midiReceiver.send(new ShortMessage(0x90, bytes[0], bytes[1]), -1);
//		} catch (InvalidMidiDataException e) {
//			e.printStackTrace();
//		}
	}
}
