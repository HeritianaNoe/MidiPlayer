package com.midi.gs.lakabe;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;


public class MidiReceiver implements Receiver{
	private Receiver receiver;
	private PianoControl pianoControl = null;
	private FramePitch framePitch = null;
	
	public MidiReceiver(Receiver receiver, PianoControl pianoControl, FramePitch framePitch) {
        this.receiver = receiver;
        this.pianoControl = pianoControl;
        this.framePitch = framePitch;
	}

	@Override
	public void close() {
		receiver.close();
	}

	@Override
	public void send(MidiMessage message, long timeStamp) {
		byte[] b = message.getMessage();
		if (b.length == 3){
			int status = message.getStatus(); 
			int channel = status & 0x0f;
			int note = b[1] & 0xff;
			int velocity = b[2] & 0xff;
			pianoControl.setNote(status, note, velocity);
			framePitch.setPitchData(channel, note);
		}
        receiver.send(message, timeStamp);
	}
}
