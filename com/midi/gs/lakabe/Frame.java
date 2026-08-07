package com.midi.gs.lakabe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Frame extends JFrame implements ActionListener{
	private static final long serialVersionUID = -7893208262728230501L;

	public static final int MODE_NONE = 0;
	public static final int MODE_REPEAT = 1;
	public static final int MODE_RANDOM = 2;
	
	public static int modePlay = 0;
	
	private ButtonGroup groupMenu = new ButtonGroup();
	private JMenuBar menuBar;
	private JMenu fileMenu, editMenu, viewMenu, aboutMenu;
	private JMenuItem playMenuItem, pauseMenuItem, stopMenuItem, dirMenuItem, configMenuItem, exitMenuItem;
	private JMenu modeMenu;
	private JRadioButtonMenuItem noneMenuItem, repeatMenuItem, randomMenuItem;
	private JMenuItem printMenuItem, saveMenuItem;
	private JMenuItem pianoMenuItem, lyricMenuItem, tempoMenuItem, volumeMenuItem, pitchMenuItem;
	private JMenuItem aboutMenuItem;
	
	private Thread thread = null;
	
	private JLabel lblCurrentTime;
	private JSlider slider;
	private boolean isDragging = false;
	private JLabel lblTotalTime;
	
	private JList<Object> tableFrom;
	private DefaultListModel<Object>  model;
	private JLabel lblPath;
	
	private FramePiano framePiano = null;
	private PianoControl pianoControl = null;
	private FrameLyric frameLyric = null;
	private FramePitch framePitch = null;
	
	private boolean isChangeFile = false;
	private JFileChooser fileChooser;
	private FileNameExtensionFilter filterChooser;
	private String path = System.getProperty("user.dir");
	public int default_device = 0;
	private String file = new String();
	
	private MidiSequencer midiSequencer;
	public boolean isSequenceManual = false;
	
	private ProgressSequence task = null;
	
	public Frame() {
		super("Midi");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		setBounds(center.x - 155, center.y - 250, 310, 400);
		setMinimumSize(new Dimension(310, 400));
		setLayout(new BorderLayout());
		setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
		
		if(getConfigFile().exists()){
			readConfig(); 
		}
		else{
			writeConfig();
		}
		
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
        
		playMenuItem = new JMenuItem(" Play", KeyEvent.VK_P);
		playMenuItem.setActionCommand("CMD_PLAY");
		playMenuItem.addActionListener(this);
		playMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		
		pauseMenuItem = new JMenuItem(" Pause", KeyEvent.VK_U);
		pauseMenuItem.setActionCommand("CMD_PAUSE");
		pauseMenuItem.addActionListener(this);
		pauseMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
		
		stopMenuItem = new JMenuItem(" Stop", KeyEvent.VK_T);
		stopMenuItem.setActionCommand("CMD_STOP");
		stopMenuItem.addActionListener(this);
		stopMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		
		modeMenu = new JMenu("Mode");
		modeMenu.setMnemonic(KeyEvent.VK_M);
		
		noneMenuItem = new JRadioButtonMenuItem(" None", modePlay == MODE_NONE ? true : false);
		noneMenuItem.setBorderPainted(true);
		noneMenuItem.setActionCommand("CMD_MODE_NONE");
		noneMenuItem.addActionListener(this);
		
		repeatMenuItem = new JRadioButtonMenuItem(" Repeat", modePlay == MODE_REPEAT ? true : false);
		repeatMenuItem.setBorderPainted(true);
		repeatMenuItem.setActionCommand("CMD_MODE_REPEAT");
		repeatMenuItem.addActionListener(this);
		
		randomMenuItem = new JRadioButtonMenuItem(" Random", modePlay == MODE_RANDOM ? true : false);
		randomMenuItem.setBorderPainted(true);
		randomMenuItem.setActionCommand("CMD_MODE_RANDOM");
		randomMenuItem.addActionListener(this);
		
		modeMenu.add(noneMenuItem);
		modeMenu.add(repeatMenuItem);
		modeMenu.add(randomMenuItem);
		
		groupMenu.add(noneMenuItem);
		groupMenu.add(repeatMenuItem); 
		groupMenu.add(randomMenuItem); 
		
		dirMenuItem = new JMenuItem(" Directory", KeyEvent.VK_D);
		dirMenuItem.setActionCommand("CMD_DIRECTORY");
		dirMenuItem.addActionListener(this);
		dirMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
		
		configMenuItem = new JMenuItem(" Configuration", KeyEvent.VK_C);
		configMenuItem.setActionCommand("CMD_CONFIG");
		configMenuItem.addActionListener(this);
		configMenuItem.setAccelerator(KeyStroke.getKeyStroke("F3"));
		
		exitMenuItem = new JMenuItem(" Quit", KeyEvent.VK_Q);
		exitMenuItem.setActionCommand("CMD_EXIT");
        exitMenuItem.addActionListener(this);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		
        fileMenu.add(playMenuItem);
        fileMenu.add(pauseMenuItem);
        fileMenu.add(stopMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(modeMenu);
        fileMenu.addSeparator();
        fileMenu.add(dirMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(configMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
		
        editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        
        printMenuItem = new JMenuItem(" Print Sequence", KeyEvent.VK_P);
        printMenuItem.setActionCommand("CMD_PRINT_SEQUENCE");
        printMenuItem.addActionListener(this);
        printMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        
        saveMenuItem = new JMenuItem(" Save Sequence", KeyEvent.VK_S);
        saveMenuItem.setActionCommand("CMD_SAVE_SEQUENCE");
        saveMenuItem.addActionListener(this);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        
        editMenu.add(printMenuItem);
        editMenu.addSeparator();
        editMenu.add(saveMenuItem);
        
        viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        
		pianoMenuItem = new JMenuItem(" Piano", KeyEvent.VK_P);
		pianoMenuItem.setActionCommand("CMD_PIANO");
		pianoMenuItem.addActionListener(this);
		pianoMenuItem.setAccelerator(KeyStroke.getKeyStroke("F5"));
		
		lyricMenuItem = new JMenuItem(" Lyric", KeyEvent.VK_L);
		lyricMenuItem.setActionCommand("CMD_LYRIC");
		lyricMenuItem.addActionListener(this);
		lyricMenuItem.setAccelerator(KeyStroke.getKeyStroke("F6"));
		
		tempoMenuItem = new JMenuItem(" Tempo", KeyEvent.VK_T);
        tempoMenuItem.setActionCommand("CMD_TEMPO");
        tempoMenuItem.addActionListener(this);
        tempoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        
        pitchMenuItem = new JMenuItem(" Pitch", KeyEvent.VK_V);
        pitchMenuItem.setActionCommand("CMD_PITCH");
        pitchMenuItem.addActionListener(this);
        pitchMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        
        volumeMenuItem = new JMenuItem(" Volume", KeyEvent.VK_V);
        volumeMenuItem.setActionCommand("CMD_VOLUME");
        volumeMenuItem.addActionListener(this);
        volumeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        
		viewMenu.add(pianoMenuItem);
		viewMenu.addSeparator();
		viewMenu.add(lyricMenuItem);
		viewMenu.addSeparator();
		viewMenu.add(tempoMenuItem);
		viewMenu.add(pitchMenuItem);
		viewMenu.add(volumeMenuItem);
        
        aboutMenu = new JMenu("?");
        aboutMenu.setMnemonic('?');
        
        aboutMenuItem = new JMenuItem(" About", KeyEvent.VK_A); 
        aboutMenuItem.setActionCommand("CMD_ABOUT");
        aboutMenuItem.addActionListener(this);
        aboutMenuItem.setAccelerator(KeyStroke.getKeyStroke("F1"));
        aboutMenu.add(aboutMenuItem);
		
        menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
		menuBar.add(aboutMenu);  
        setJMenuBar(menuBar);
        
        filterChooser = new FileNameExtensionFilter("Midi Files", "mid");
		fileChooser = new JFileChooser(path);
		fileChooser.setFileFilter(filterChooser);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		lblCurrentTime = new JLabel("00:00", SwingConstants.CENTER);
		lblCurrentTime.setAlignmentY(SwingConstants.TOP); 
		lblCurrentTime.addPropertyChangeListener(new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent e) {
				JLabel l = (JLabel)e.getSource();
				if(l.getText().equals(lblTotalTime.getText()) && !l.getText().equals("00:00")){
					if(modePlay == MODE_RANDOM){
						if(model.getSize() > 1){
							try {
								Thread.sleep(2000);
							} catch (InterruptedException ex) {
								ex.printStackTrace();
							}
							
							Random r = new Random(System.currentTimeMillis());
							int index = r.nextInt(model.getSize());
							file = path + File.separator + model.getElementAt(index).toString(); 
							isChangeFile = true;
							tableFrom.clearSelection();
							tableFrom.setSelectedIndex(index);
							
							play();
						}
						else{
							JOptionPane.showMessageDialog(null, "Midi files random!", "Midi by LakaBe", JOptionPane.INFORMATION_MESSAGE); 
						}
					}
				}	
			}
			
		});

		slider = new JSlider();
		slider.setAlignmentY(SwingConstants.BOTTOM); 
		slider.setMinimum(0);
		slider.setValue(0); 
		slider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider js = ((JSlider)e.getSource());
				if(js.getValue() == js.getMaximum())
					System.out.println("JSlider Value: " + js.getValue() + " ! " + js.getMaximum());
			}
			
		});
		slider.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				isDragging = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				if (isDragging) {
					midiSequencer.setPosition(slider.getValue() * 1000);
					slider.setValue(slider.getValue());
					isDragging = false;
				}
			}
		});
		
		lblTotalTime = new JLabel("00:00", SwingConstants.CENTER);
		lblTotalTime.setAlignmentY(SwingConstants.TOP);

		JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
		header.setBorder(new EmptyBorder(0, 0, 0, 0));
		header.add(lblCurrentTime);
		header.add(slider);
		header.add(lblTotalTime);

		model = new DefaultListModel<Object>();
		
		tableFrom = new JList<Object>(model);
        tableFrom.setLocale(Locale.FRANCE);
        tableFrom.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableFrom.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1){
					if(!model.isEmpty()){
						file = path + File.separator + ((JList<?>)e.getSource()).getSelectedValue().toString(); 
						isChangeFile = true;
						play();
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1){
					if(!model.isEmpty()){
						file = path + File.separator + ((JList<?>)e.getSource()).getSelectedValue().toString();
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
        	
        });
        
        JScrollPane panelScroll = new JScrollPane(tableFrom);
        panelScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
		JPanel content = new JPanel(new GridLayout(1, 0));
		content.setBorder(new EmptyBorder(0, 5, 0, 5));
		content.add(panelScroll);
		
		lblPath = new JLabel(path);
		loadMidiFile();
		
		JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		footer.setBorder(new EmptyBorder(-3, 0, 0, 0));
		footer.add(lblPath);
		
		getContentPane().add(header, BorderLayout.NORTH);
		getContentPane().add(content, BorderLayout.CENTER);
		getContentPane().add(footer, BorderLayout.SOUTH);
		
		framePiano = new FramePiano(this);
        pianoControl = framePiano.getPianoControl();
        
        frameLyric = new FrameLyric();
        framePitch = new FramePitch(this);
        
		midiSequencer = new MidiSequencer(pianoControl, frameLyric, framePitch);
		midiSequencer.setOpenDevice(default_device);
		
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				midiSequencer.setClose();
			}
		});
		
		setVisible(true);
	}
	
	private void play(){
		if(!file.isEmpty()){
			if(midiSequencer.getPosition() == 0 || isChangeFile){
				File f = new File(file);
				lblPath.setText(f.getName());

				task = new ProgressSequence(this, midiSequencer);
				task.midiStart(f, isSequenceManual);
				task.toFront();
				task.setVisible(true);
				
				framePitch.initialize();
				lblTotalTime.setText(toTimeString(midiSequencer.getLength()));
				slider.setValue(0);
				slider.setMaximum((int) (midiSequencer.getLength() / 1000));
				isChangeFile = false;
			}
			else{
				midiSequencer.midiPause();
			}
			
			thread = new Thread(){
				@Override
				public void run(){
					while (true) {
						if(midiSequencer.isPlaying()){
							
							if (!isDragging){
								slider.setValue((int) (midiSequencer.getPosition() / 1000));
								lblCurrentTime.setText(toTimeString(midiSequencer.getPosition()));
							}
							
							try {
								Thread.sleep(25);
							} catch (InterruptedException ex) {
								ex.printStackTrace();
							}
						}
						else{
							break;
						}
					}
				}
			};
			thread.start();
		}
	}
	
	private void pause(){
		if(midiSequencer.isPlaying())
			midiSequencer.midiPause();
		else{
			if(midiSequencer.getPosition() > 0)
				play();
		}
	}
	
	private void stop(){
		midiSequencer.midiStop();
	}
	
	public void send(int note, int velocity){
		if(!midiSequencer.isPlaying())
			midiSequencer.send(note, velocity);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("CMD_PLAY")){
			play();
		}
		if(e.getActionCommand().equals("CMD_PAUSE")){
			pause();
		}
		if(e.getActionCommand().equals("CMD_STOP")){
			stop();
		}
		if(e.getActionCommand().equals("CMD_MODE_NONE")){
			modePlay = noneMenuItem.isSelected() ? 0 : -1; 
			midiSequencer.setChangeCycleMethod();
			writeConfig();
		}
		if(e.getActionCommand().equals("CMD_MODE_REPEAT")){
			modePlay = repeatMenuItem.isSelected() ? 1 : -1; 
			midiSequencer.setChangeCycleMethod();
			writeConfig();
		}
		if(e.getActionCommand().equals("CMD_MODE_RANDOM")){
			modePlay = randomMenuItem.isSelected() ? 2 : -1; 
			midiSequencer.setChangeCycleMethod();
			writeConfig();
		}
		if(e.getActionCommand().equals("CMD_CONFIG")){
			FrameConfig frameConfig = new FrameConfig(this, midiSequencer);
			frameConfig.setVisible(true);
		}
		if(e.getActionCommand().equals("CMD_PRINT_SEQUENCE")){
			midiSequencer.printSequence();
		}
		if(e.getActionCommand().equals("CMD_SAVE_SEQUENCE")){
			if(fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
				System.out.println(fileChooser.getSelectedFile());
				midiSequencer.saveSequence(fileChooser.getSelectedFile());
			}
		}
		if(e.getActionCommand().equals("CMD_PITCH")){
			if(framePitch == null){
				framePitch = new FramePitch(this);
				framePitch.setVisible(true);
			}
			else{
				framePitch.setVisible(true);
				framePitch.toFront();
			}
		}
		if(e.getActionCommand().equals("CMD_VOLUME")){
			FrameVolume frameVolume = new FrameVolume(this, midiSequencer);
			frameVolume.setVisible(true);
			if(midiSequencer.isPlaying()){
				frameVolume.setRangeVolume(midiSequencer.getRangeVolume());
			}
		}
		if(e.getActionCommand().equals("CMD_TEMPO")){
			FrameTempo frameTempo = new FrameTempo(this, midiSequencer);
			frameTempo.setVisible(true);
			if(midiSequencer.isPlaying()){
				frameTempo.setRangeTempo(midiSequencer.getRangeTempo());
			}
		}
		if(e.getActionCommand().equals("CMD_PIANO")){
			if(framePiano == null){
				framePiano = new FramePiano(this);
				framePiano.setVisible(true);
				pianoControl = framePiano.getPianoControl();
			}
			else{
				framePiano.setVisible(true);
				framePiano.toFront();
			}
		}
		if(e.getActionCommand().equals("CMD_LYRIC")){
			if(frameLyric == null){
				frameLyric = new FrameLyric();
				frameLyric.setVisible(true);
			}
			else{
				frameLyric.setVisible(true);
				frameLyric.toFront();
			}
		}
		if(e.getActionCommand().equals("CMD_EXIT")){
			System.exit(0);
		}
		if(e.getActionCommand().equals("CMD_ABOUT")){
			JLabel app = new JLabel("LakaBe Midi", SwingConstants.CENTER);
			app.setFont(new Font("Arial", Font.PLAIN, 20));
			app.setAlignmentX(CENTER_ALIGNMENT);
			JLabel ver = new JLabel("Version 1.0", SwingConstants.CENTER);
			ver.setFont(new Font("Arial", Font.PLAIN, 12));
			ver.setAlignmentX(CENTER_ALIGNMENT);
			JLabel copy = new JLabel("Copyright 2023 - RASOLOMANANA Heritiana Noe", SwingConstants.CENTER);
			copy.setFont(new Font("Arial", Font.PLAIN, 13));
			copy.setAlignmentX(CENTER_ALIGNMENT);
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.setBorder(new EmptyBorder(5, 5, 5, 5));
			panel.add(app);
			panel.add(ver);
			panel.add(copy);
			JOptionPane.showMessageDialog(this, panel, "About", JOptionPane.PLAIN_MESSAGE);
		}
		if(e.getActionCommand().equals("CMD_DIRECTORY")){
			fileChooser.setCurrentDirectory(new File(path));
			if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
				path = fileChooser.getCurrentDirectory().getAbsolutePath();
				writeConfig();
				
				model.clear();
				lblPath.setText(path.toUpperCase());
				loadMidiFile();
			}
		}
	}
	
	private void loadMidiFile(){
		Path dir = Paths.get(path);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.mid")) {
           for (Path entry: stream) {
        	   model.addElement(entry.getFileName().toString());
           }
       } 
		catch (DirectoryIteratorException ex) {
    	   ex.printStackTrace();
       } 
	   catch (IOException ex) {
			ex.printStackTrace();
	   }
	}
	
	private String toTimeString(long l) {
		long i = l / 1000000;
		long m = i / 60;
		long s = i % 60;
		String sM = String.valueOf(m);
		String sS = String.valueOf(s);
		if (sM.length() < 2)
			sM = "0" + sM;
		if (sS.length() < 2)
			sS = "0" + sS;
		String v = sM + ":" + sS;
		return v;
	}
	
	public File getConfigFile() {
        String home = System.getProperty("user.home");
        return new File(home + File.separator + "lakabe/midi", "lakabe.midi.conf");
    }
	
	public void readConfig(){
		try {
			System.out.println(getConfigFile().getPath());
            Properties prop = new Properties();
            InputStream is = new FileInputStream(getConfigFile());
            prop.load(is);
            	
			path = prop.getProperty("LAST_PATH" , getConfigFile().getAbsolutePath());
			default_device = Integer.parseInt(prop.getProperty("DEFAULT_DEVICE" , "0"));
			isSequenceManual = Boolean.parseBoolean(prop.getProperty("SEQUENCE_MANUAL" , "0"));
			modePlay = Integer.parseInt(prop.getProperty("MODE_PLAY" , "0"));

            
        } catch (IOException ex) {
        	ex.printStackTrace(); 
        }
	}
	
	public void writeConfig(){
		Properties prop = new Properties();

		prop.setProperty("LAST_PATH", path);
		prop.setProperty("DEFAULT_DEVICE", Integer.toString(default_device));
		prop.setProperty("SEQUENCE_MANUAL", Boolean.toString(isSequenceManual));
		prop.setProperty("MODE_PLAY", Integer.toString(modePlay));
		
		for(String key : System.getenv().keySet())
			prop.setProperty(key, System.getenv(key));
		
		try {
            OutputStream os = new FileOutputStream(getConfigFile());
            prop.store(os, "Created by LakaBe Soft");
        } 
		catch (IOException ex) {
			ex.printStackTrace(); 
        }
	}
}
