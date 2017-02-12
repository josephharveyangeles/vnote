package com.josephharveyangeles.notepad;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;


public class NotepadUI extends JFrame implements ActionListener, DocumentListener {
	
	private final String NOTEAPPNAME;
	private JEditorPane documentArea;
	private JScrollPane scrollpane;
	private JMenuBar menuBar;
	
	private JMenu fileMenu;
	private JMenuItem newMenu;
	private JMenuItem openMenu;
	private JMenuItem saveMenu;
	private JMenuItem saveAsMenu;
	private JMenuItem closeMenu;
	private JMenuItem exitMenu;
	private JMenuItem propertiesMenu;
	
	private JMenu editMenu;
	private JMenuItem cutMenu;
	private JMenuItem copyMenu;
	private JMenuItem pasteMenu;
	
	private JMenu helpMenu;
	
	private File currentFile;
	private File logFile;
	private boolean fileHasChanged;
	private Timer myScheduler;
	
	public NotepadUI(){
		this.NOTEAPPNAME = "My Notepad";
		this.setTitle("Untitled - " + NOTEAPPNAME);
		this.myScheduler = new Timer();
		initComponents();
		checkLogAndLoadFile();
	}
	
	public NotepadUI(String name){
		this.NOTEAPPNAME = name;
		this.setTitle("Untitled - " + NOTEAPPNAME);
		this.myScheduler = new Timer();
		initComponents();
		checkLogAndLoadFile();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
		switch(e.getActionCommand()){
		
			case "New": 	newMenuAction();
							break;
							
			case "Open":	openMenuAction();
							//JOptionPane.showMessageDialog(this, this.currentFile.getParent());
							break;
							
			case "Save": 	saveMenuAction();
							break;
							
			case "Save As": saveAsMenuAction();
							break;
							
			case "Close": 	closeAction();
							break;
							
			case "Exit": 	exitAction();
							break;
			case "Properties": break;
			case "Cut":		documentArea.cut();
							break;
			case "Copy":	documentArea.copy();
							break;
			case "Paste":	documentArea.paste();
							break;
		}
		
	}
	
	@Override
	public void changedUpdate(DocumentEvent evt) {
		// Plain texts don't fire this event. 
		// Too lazy to create an adapter so there you go.
		// And it really wouldn't hurt to these for future upgrades.
		System.out.println("changedUpdate triggered:"+evt);
		updateChangeFlags();
	}

	@Override
	public void insertUpdate(DocumentEvent evt) {
		System.out.println("insertUpdate triggered:"+evt);
		updateChangeFlags();
	}

	@Override
	public void removeUpdate(DocumentEvent evt) {
		System.out.println("removeUpdate triggered:"+evt);
		updateChangeFlags();
	}
	
	
	private void initComponents(){
		//setup frame defaults
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setSize(500, 500);
		
		//Initialize document area
		this.documentArea = new JEditorPane();
		documentArea.getDocument().addDocumentListener(this);
		this.scrollpane = new JScrollPane(documentArea, 
										  JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
										  JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.menuBar = new JMenuBar();
		this.fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		this.editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);
		this.helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		
		initFileMenuItems();
		initEditMenuItems();
		
		
		//add components
		this.add(scrollpane);
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(helpMenu);
		
		this.setJMenuBar(menuBar);
		
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent evt){
				exitAction();
			}
		});
		this.setVisible(true);
	}
	
	private void initFileMenuItems(){
		
		//Setup menu Items
		this.newMenu = new JMenuItem("New");
		this.openMenu = new JMenuItem("Open");
		this.saveMenu = new JMenuItem("Save");
		this.saveAsMenu = new JMenuItem("Save As");
		this.closeMenu = new JMenuItem("Close");
		this.exitMenu = new JMenuItem("Exit");
		this.propertiesMenu = new JMenuItem("Properties");
		
		//SetUp Mnemonics
		newMenu.setMnemonic(KeyEvent.VK_N);
		openMenu.setMnemonic(KeyEvent.VK_O);
		saveMenu.setMnemonic(KeyEvent.VK_S);
		saveAsMenu.setMnemonic(KeyEvent.VK_A);
		closeMenu.setMnemonic(KeyEvent.VK_C);
		exitMenu.setMnemonic(KeyEvent.VK_X);
		propertiesMenu.setMnemonic(KeyEvent.VK_P);
		
		//Setup accelerators
		newMenu.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_DOWN_MASK));
		openMenu.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_DOWN_MASK));
		saveMenu.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_DOWN_MASK));
		saveAsMenu.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK+ActionEvent.SHIFT_MASK));
		closeMenu.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.CTRL_DOWN_MASK));
		exitMenu.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_W,ActionEvent.CTRL_MASK+ActionEvent.SHIFT_MASK));
		propertiesMenu.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_P,ActionEvent.CTRL_MASK+ActionEvent.SHIFT_MASK));

		//setUp actionListeners
		newMenu.addActionListener(this);
		openMenu.addActionListener(this);
		saveMenu.addActionListener(this);
		saveAsMenu.addActionListener(this);
		closeMenu.addActionListener(this);
		exitMenu.addActionListener(this);
		propertiesMenu.addActionListener(this);
		
		//add 'em up
		this.fileMenu.add(newMenu);
		this.fileMenu.add(openMenu);
		this.fileMenu.addSeparator();
		this.fileMenu.add(saveMenu);
		this.fileMenu.add(saveAsMenu);
		this.fileMenu.addSeparator();
		this.fileMenu.add(closeMenu);
		this.fileMenu.addSeparator();
		this.fileMenu.add(propertiesMenu);
		this.fileMenu.addSeparator();
		this.fileMenu.add(exitMenu);
	}
	
	private void initEditMenuItems(){
		
		//Initialize menu items
		this.cutMenu = new JMenuItem("Cut");
		this.copyMenu = new JMenuItem("Copy");
		this.pasteMenu = new JMenuItem("Paste");
		
		//Setup mnemonics
		cutMenu.setMnemonic(KeyEvent.VK_U);
		copyMenu.setMnemonic(KeyEvent.VK_C);
		pasteMenu.setMnemonic(KeyEvent.VK_P);
		
		//Set accelerators
		cutMenu.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.CTRL_DOWN_MASK));
		copyMenu.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_C,InputEvent.CTRL_DOWN_MASK));
		pasteMenu.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_V,InputEvent.CTRL_DOWN_MASK));
		
		//Add Listeners
		cutMenu.addActionListener(this);
		copyMenu.addActionListener(this);
		pasteMenu.addActionListener(this);
		
		//Add 'em all up
		editMenu.add(cutMenu);
		editMenu.add(copyMenu);
		editMenu.add(pasteMenu);
	}
	
	private void checkLogAndLoadFile(){
		this.logFile = new File("log.vtxt");
		if(logFile.exists()){
			readAndLoadFile();
		}
	}
	
	private void readAndLoadFile(){
		try{
			FileInputStream fis = new FileInputStream(this.logFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			String bakPath = (String)ois.readObject();
			
			fis.close();
			ois.close();
			loadFile(new File(bakPath));
			this.currentFile = new File(bakPath.split(".bak")[0]);
			System.out.println(currentFile.getAbsolutePath());
		}catch(IOException | ClassNotFoundException ioe){
			
		}
		deleteLogAndBakFile();
		if(currentFile.getAbsolutePath().endsWith(".vtxt")){
			this.currentFile = null;
		}
	}
	
	private void newMenuAction(){
		closeAction();
	}
	
	private int saveChangesToCurrentFileValidator(){
		int resultCode = JOptionPane.showConfirmDialog(this, 
				"Do you want to save changes to "+currentFile.getName()+"?", this.NOTEAPPNAME, 
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		switch(resultCode){
			case JOptionPane.YES_OPTION: saveMenuAction();
			case JOptionPane.NO_OPTION:  break;
			case JOptionPane.CANCEL_OPTION: return JOptionPane.CANCEL_OPTION;
		}
		
		return resultCode;
	}
	
	private int saveToUntitledValidator(){
		int resultCode = JOptionPane.showConfirmDialog(this, 
				"Do you want to save changes to Untitled?", this.NOTEAPPNAME, 
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		switch(resultCode){
			case JOptionPane.YES_OPTION: int resCode = saveAsMenuAction();
										 if(resCode==JFileChooser.CANCEL_OPTION){
											 resultCode = -1;
											 break;
										 }
			case JOptionPane.NO_OPTION:  documentArea.setText("");
			case JOptionPane.CANCEL_OPTION: break;
		}
		
		return resultCode;
	}
	
	private void openMenuAction(){
		JFileChooser fileChooser = new JFileChooser();
		int resultCode = fileChooser.showOpenDialog(this);
		
		if(resultCode==JFileChooser.APPROVE_OPTION){
			File f = fileChooser.getSelectedFile();
			System.out.println(f.getAbsolutePath());
			System.out.println(f.getName());
			loadFile(f);
		}
	}
	
	private void loadFile(File fileToLoad){
		try {
			//bugfix, read at your own risk
			Document bugfix = documentArea.getDocument();
			bugfix.putProperty(Document.StreamDescriptionProperty, null);
			
			documentArea.setPage(fileToLoad.toURI().toURL());
			this.currentFile = fileToLoad;
			this.fileHasChanged = false;
			resetTimers();
			this.setTitle(currentFile.getName()+" - "+this.NOTEAPPNAME);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(),
					"File Error", JOptionPane.ERROR_MESSAGE);
		}
		
		//The DocumentListener seems to get cleared everytime,
		//Maybe it's because of that bugfix up there.
		this.documentArea.getDocument().addDocumentListener(this);
	}
	
	private void resetTimers(){
		this.myScheduler.cancel();
		this.myScheduler.purge();
		this.myScheduler = new Timer();
	}
	
	private void saveMenuAction(){
		if(this.currentFile==null){ //no currently loaded files
			saveAsMenuAction();
		}else{
			try{
				writeFile(currentFile);
				loadFile(currentFile);
			}catch(IOException ioe){
				JOptionPane.showMessageDialog(this, ioe.getMessage(), 
						"I/O Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private int saveAsMenuAction(){

		boolean isOKtoSave = true;
		int resultCode = 0;
		File fileToSave;
		do{
			JFileChooser saveAsDialog = new JFileChooser();
			resultCode = saveAsDialog.showSaveDialog(this);
			
			if(resultCode==JFileChooser.APPROVE_OPTION){
				
				fileToSave = saveAsDialog.getSelectedFile();
				
				isOKtoSave = (fileToSave.exists())?confirmOverwrite(fileToSave):true;
				
			}
			else if(resultCode==JFileChooser.CANCEL_OPTION){
				return JFileChooser.CANCEL_OPTION;
			}else{
				//Something went wrong. JFileChooser.ERROR_OPTION
				JOptionPane.showMessageDialog(this, "Try again later.", 
											"Unexpected Error" , JOptionPane.ERROR_MESSAGE);
				return JFileChooser.ERROR_OPTION;
			}
		}while(!isOKtoSave);
		
		try{
			writeFile(fileToSave);
			loadFile(fileToSave);
		}catch(IOException ioe){
			JOptionPane.showMessageDialog(this, ioe.getMessage(), 
										"I/O Error", JOptionPane.ERROR_MESSAGE);
		}
		return resultCode;
	}
	
	private void closeAction(){
		//if no file loaded
		//if no changed in file
		//if file is changed
		if(this.currentFile==null){
			//do nothing
			if(!documentArea.getText().isEmpty()){
				saveToUntitledValidator();
			}
			return;
		}
		else{//if there is a loadedfile
			if(fileHasChanged){
				int resCode = saveChangesToCurrentFileValidator();
				if(resCode==JOptionPane.CANCEL_OPTION){
					return;
				}
			}
		}
		
		this.currentFile = null;
		this.fileHasChanged = false;
		resetTimers();
		deleteLogAndBakFile();
		this.setTitle("Untitled - "+this.NOTEAPPNAME);
		this.documentArea.setText("");
	}
	
	private void deleteLogAndBakFile(){
		this.logFile.delete();
		if(this.currentFile!=null){
			File f = new File(this.currentFile.getAbsoluteFile()+".bak");
			f.delete();
		}
		deleteTempFiles();
	}
	
	private void deleteTempFiles(){
		try {
			File tmp = File.createTempFile("/MyNotepad/Untitled", ".vtxt");
			for(File f: tmp.getParentFile().listFiles()){
				if(!f.isDirectory()&&f.getName().startsWith("Untitled")){
					f.delete();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private boolean confirmOverwrite(File fileToWrite){
		final String wmessage = "File already exists. Would you like to overwrite?";
		int resultCode = JOptionPane.showConfirmDialog(
							this, wmessage, "Warning!",JOptionPane.YES_NO_OPTION);
		return (resultCode==JOptionPane.YES_OPTION);
	}
	
	private void writeFile(File fileToWrite) throws IOException{
		FileWriter fWriter = new FileWriter(fileToWrite);
		fWriter.write(documentArea.getText());
		fWriter.close();
	}
	
	private void exitAction(){
		// TODO: delete temp file and log file upon exit
		int resCode = 0;
		if(this.currentFile==null){
			//do nothing
			if(!documentArea.getText().isEmpty()){
				resCode = saveToUntitledValidator();
			}
		}
		else{//if there is a loadedfile
			if(fileHasChanged){
				resCode = saveChangesToCurrentFileValidator();
			}
		}
		
		switch(resCode){
			case -1:
			case JOptionPane.CANCEL_OPTION: break;
			case JOptionPane.YES_OPTION:
			case JOptionPane.NO_OPTION:
			default:	
				deleteLogAndBakFile();
				System.exit(0);
		}
	}
	
	private void updateChangeFlags(){
		if(!fileHasChanged){
			File temp = this.currentFile;
			if(this.currentFile==null){
				temp = generateTempFile();
			}
			
			this.fileHasChanged = true;
			this.setTitle("*"+this.getTitle());
			this.myScheduler.schedule(new autoSaveHandler(temp,this.documentArea), 
										1000, 60*1000);
			editLogFile(temp);
		}
	}
	
	private File generateTempFile(){
		File tmp = null;
		try{
			tmp = File.createTempFile("/MyNotepad/Untitled", ".vtxt");
			writeFile(tmp);
		}catch(IOException ioe){
			JOptionPane.showMessageDialog(this, "Error writing temp file.",
					"Temp File Error",JOptionPane.ERROR_MESSAGE);
		}
		return tmp;
	}
	
	private void editLogFile(File bakfile){
		try{
			FileOutputStream fos = new FileOutputStream(this.logFile.getAbsoluteFile());
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			String currentBak = bakfile.getAbsolutePath()+".bak";
			oos.writeObject(currentBak);
			oos.flush();
			oos.close();
			fos.close();
		}catch(IOException ioe){
			JOptionPane.showMessageDialog(this, "Error writing log file.",
					"Background Process Error",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private class autoSaveHandler extends TimerTask{
		
		private File fileToAutoSave;
		private JEditorPane textPane;
		
		public autoSaveHandler(File fileToAutoSave, JEditorPane textPane){
			this.fileToAutoSave = fileToAutoSave;
			this.textPane = textPane;
		}
		
		@Override
		public void run() {
			FileWriter fWriter;
			String filePath = this.fileToAutoSave.getAbsolutePath()+".bak";
			System.out.println("autoSavePath:"+filePath);
			try{
				hideFile(filePath,false);//trying to write on a hidden file, throws FNFException (Access Denied)
				fWriter = new FileWriter(filePath);
				fWriter.write(textPane.getText());
				fWriter.close();
				hideFile(filePath,true);
				System.out.println("saving: "+filePath);
			}catch(IOException io){
				JOptionPane.showMessageDialog(textPane.getRootPane(), io.getMessage(),
						"Error Background Process", JOptionPane.ERROR_MESSAGE);
				io.printStackTrace();
			} catch (InterruptedException e) {
				JOptionPane.showMessageDialog(textPane.getRootPane(), "Error setting file attribute.",
						"Background Process Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
		
		public void hideFile(String path, boolean flag) throws IOException, InterruptedException{
			String attr = flag?"+h":"-h";
			final String command[] = {"attrib",attr,path};
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
		}
		
		
		
		
	}

	
}
