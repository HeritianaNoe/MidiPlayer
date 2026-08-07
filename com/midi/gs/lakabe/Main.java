package com.midi.gs.lakabe;

import java.awt.EventQueue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import com.midi.gs.lakabe.*;

public class Main {
	public static final String APP_NAME      = "LakaBe";
    public static final String APP_TITLE     = "MidiPlayer";
    public static final String APP_VERSION   = "Version 1.0";
    public static final String COPYRIGHT     = "Copyright 2016 - RASOLOMANANA Heritiana Noe";
    
	public static void onAuthor(){
		try {
			String author = APP_NAME + 
					"\r\n" + 
					APP_TITLE + 
					"\r\n" + 
					APP_VERSION +
					"\r\n" + 
					"===============================================" + 
					"\r\n" + 
					"Copyright (c) 2016 - RASOLOMANANA Heritiana Noe" +
					"\r\n" + 
					"rasolomananaheritiana@gmail.com" +
					"\r\n" + 
					"(261) 034 10 402 34 - (261) 032 59 755 76" +
					"\r\n" +
					"ANTANANARIVO - MADAGASIKARA";
				
			String home = System.getProperty("user.home"); 
			Path path = Paths.get(home + "/lakabe/midi/author.txt");
			
			if(!Files.exists(path)) {
				Files.createDirectories(path);
			}
			
			if(Files.deleteIfExists(path)) {
				Files.createFile(path);
	            Files.write(path, author.getBytes(), StandardOpenOption.WRITE);
	            System.out.println(path.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					onAuthor();
					new Frame();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
	}

}
