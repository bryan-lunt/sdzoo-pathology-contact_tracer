package org.sandiegozoo.pathology.contact_tracer.gui;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.*;

public class FileSelectorPanel extends JPanel {
	
	private static File lastDirectory = null;

	public static final int OPEN_FILE = 0;
	public static final int SAVE_FILE = 1;
	
	private String title;
	public void setTitle(String in){ title = in; title_label.setText(in);}
	public String getTitle(){return title;}
	
	private int mode = OPEN_FILE;
	public void setMode(int in){ mode = in;}
	public int getMode(){ return mode;}
	
	private File selectedFile = null;
	public void setSelectedFile(File in){selectedFile = in; filename_display.setText(in.getName());}
	public File getSelectedFile(){return selectedFile;}
	
	
	private JLabel title_label;
	private JPanel subpanel;
	private JTextField filename_display;
	private JButton choose_button;
	
	class FileSelectorPanelAction extends AbstractAction{
		public FileSelectorPanelAction(){
			super("Choose");
		}

		public void actionPerformed(ActionEvent arg0) {
			// TODO Open up the file dialog
			JFileChooser myChooser = new JFileChooser(lastDirectory);
			myChooser.setMultiSelectionEnabled(false);
			int dialog_result = JFileChooser.ERROR_OPTION;
			
			if(mode == OPEN_FILE){
				//open dialogue
				dialog_result = myChooser.showOpenDialog(null);
			}else if(mode == SAVE_FILE){
				//save dialogue
				dialog_result = myChooser.showSaveDialog(null);
			}else{/*Something wrong.*/}
			
			if(dialog_result == JFileChooser.APPROVE_OPTION){
				File selected = myChooser.getSelectedFile();
				lastDirectory = selected.getParentFile();
				setSelectedFile(selected);
			}
			
		}
	}
	
	public FileSelectorPanel(String inTitle){
		
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
		title_label = new JLabel();
		this.setTitle(inTitle);
		
		this.add(title_label);
		
		subpanel = new JPanel();
		subpanel.setLayout(new BoxLayout(subpanel,BoxLayout.X_AXIS));
		
		filename_display = new JTextField();
		filename_display.setEditable(false);
		
		subpanel.add(filename_display);
		
		choose_button = new JButton(new FileSelectorPanelAction());
		
		subpanel.add(choose_button);
		
		this.add(subpanel);
	}
	
}
