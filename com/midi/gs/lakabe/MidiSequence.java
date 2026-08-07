package com.midi.gs.lakabe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;
import javax.sound.midi.ShortMessage;

public class MidiSequence {
	private InputStream inStream;
	private Sequence sequence = null;
	private Track[] track;
	
	private int fileFormat;
	private float divisionType;
	private int resolution;
	private int numTracks;

	private int ticks;
    private int status;
    private int runningStatus;  
    
	private List<int[]> trackData = new ArrayList<int[]>();
	private int trackIndex;
	
	private List<MidiEvent> events = new ArrayList<MidiEvent>();
	
	public MidiSequence(File file) {
		try {
			inStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		int data = readInt32(); 
		if(data == 0x4D546864){ //MThd
			data = readInt32();
			if(data == 0x00000006){
				fileFormat = readInt16(); 	 
				numTracks = readInt16(); 	  
				
				int r1 = readInt8();
				int r2 = readInt8();
				
				if(Sequence.SMPTE_24 == (float)r1) {
					divisionType = Sequence.SMPTE_24;
					resolution = r2;
				}
				else if(Sequence.SMPTE_25 == (float)r1) {
					divisionType = Sequence.SMPTE_25;
					resolution = r2;
				}
				else if(Sequence.SMPTE_30DROP == (float)r1) {
					divisionType = Sequence.SMPTE_30DROP;
					resolution = r2;
				}
				else if(Sequence.SMPTE_30 == (float)r1) {
					divisionType = Sequence.SMPTE_30;
					resolution = r2;
				}
				else{
					divisionType = Sequence.PPQ;
					resolution = (r1 << 8) | r2;
				}
				 
				try {
					sequence = new Sequence(divisionType, resolution, numTracks);
					track = new Track[numTracks];
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
				}
				
				for (int i = 0; i < numTracks; i++){
					track[i] = sequence.createTrack();
					
					data = readInt32(); 
					if(data == 0x4D54726B){ //MTrk
						data = readInt32();
						if(data > 0){
							int[] bytes = new int[data];
							for (int j = 0; j < bytes.length; j++)
								bytes[j] = readInt8();
							
							trackData.add(bytes);
						}
					}
				}
				
				parseTrack();
			}
		}
		
		try {
			inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Sequence getSequence(){
		return sequence;
	}
	
	public int getMidiFileType(){
		return fileFormat;
	}
	
	private void parseTrack(){
		for (int i = 0; i < trackData.size(); i++){
			trackIndex = ticks = runningStatus = 0; events = new ArrayList<MidiEvent>();
			while(trackIndex < trackData.get(i).length){
				
				ticks += readVariableLength(i);
				
				if((trackData.get(i)[trackIndex] & 0x80) == 0x80){
					status = trackData.get(i)[trackIndex]; trackIndex++;
	            }
	            else{
	                status = runningStatus;
	            } 
				
				parseMessage(i);
			}
			
			analyseMessage(i);
		}
		
		System.out.println("Tapitra!"); 
	}
	
	private void parseMessage(int index){
		try {
			if(status >= 0x80 && status <= 0xEF){
				if(((status & 0xC0) == 0xC0) || ((status & 0xD0) == 0xD0)){
					int[] bytes = new int[1];
					bytes[0] = trackData.get(index)[trackIndex]; trackIndex++;
					events.add(new MidiEvent(new ShortMessage(status, bytes[0], 0), ticks));
				}
				else{
					int[] bytes = new int[2];
					bytes[0] = trackData.get(index)[trackIndex]; trackIndex++;
					bytes[1] = trackData.get(index)[trackIndex]; trackIndex++;
					events.add(new MidiEvent(new ShortMessage(status, bytes[0], bytes[1]), ticks));
				}
				runningStatus = status;
			}
			else if(status == 0xFF){
				int type = trackData.get(index)[trackIndex]; trackIndex++;
				
				if(type == 0x2F){
					trackIndex++;
					byte[] bytes = new byte[1]; bytes[0] = 0;
					events.add(new MidiEvent(new MetaMessage(type, bytes, bytes.length), ticks));
				}
				else{
					byte[] bytes = new byte[readVariableLength(index)];
					for (int i = 0; i < bytes.length; i++){
						bytes[i] = (byte)(trackData.get(index)[trackIndex] & 0xFF); trackIndex++;
					}	

					if(type == 0x51){
						int t = MidiHelpers.getUnsignedInt(bytes, 3);
						float bpm = 60000000.0f / t;
						float tick = ((float) (t / 1000) / (float) resolution);
						System.out.println(String.format("Tempo: %.2f ms/tick, %.0f bpm", tick, bpm));
					}
					
					events.add(new MidiEvent(new MetaMessage(type, bytes, bytes.length), ticks));
				}
			}
			else if(status == 0xF0 || status == 0xF7){
				byte[] bytes = new byte[readVariableLength(index)];
				for (int i = 0; i < bytes.length; i++){
					bytes[i] = (byte)(trackData.get(index)[trackIndex] & 0xFF); trackIndex++;
				}	
				events.add(new MidiEvent(new SysexMessage(status, bytes, bytes.length), ticks));
			}
			else if(status >= 0xF1 && status <= 0xF6){
				System.out.println("Tsy fantatra!");
			}
			else if(status >= 0xF8 && status <= 0xFE){
				System.out.println("Tsy fantatra!");
			}
			else{
				System.out.println("Warning: unknown event! " + status);
			}
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	private void analyseMessage(int index){
		for (int i = 0; i < events.size(); i++){
			MidiEvent e = events.get(i);
			track[index].add(e);
		}
	}
	
	private int readInt8(){
		int b = 0;
		try {
			b = (inStream.read() & 0xFF);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	private int readInt16(){
		int b = 0;
		try {
			b = (inStream.read() & 0xFF);
			b = b << 8 | (inStream.read() & 0xFF);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return b;
    }
	
	private int readInt32(){
		int b = 0;
		try {
			b = (inStream.read() & 0xFF);
			b = b << 8 | (inStream.read() & 0xFF);
			b = b << 8 | (inStream.read() & 0xFF);
			b = b << 8 | (inStream.read() & 0xFF);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return b;
    }
	
	private int readVariableLength(int index){
		int result = 0;

        result = trackData.get(index)[trackIndex]; trackIndex++;
        
        if((result & 0x80) == 0x80){
            result &= 0x7F;

            int temp;

            do {
                temp = trackData.get(index)[trackIndex]; trackIndex++;
                result <<= 7;
                result |= temp & 0x7F;
            }while((temp & 0x80) == 0x80);
        }

        return result;            
	}
}
