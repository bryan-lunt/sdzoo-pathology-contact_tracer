package org.sandiegozoo.pathology.contact_tracer.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

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
	public void setSelectedFile(File in){
		selectedFile = in;
		filename_display.setText(null == in ? "" : in.getName());
	}
	public File getSelectedFile(){
		return selectedFile;
	}
	public void clear(){
		this.setSelectedFile(null);
	}
	
	
	private JLabel title_label;
	private JPanel subpanel;
	private JTextField filename_display;
	private JButton choose_button;
	private JButton clear_button;
	
	class FileSelectorPanelAction extends AbstractAction{
		public FileSelectorPanelAction(){
			super("Choose");
		}

		public void actionPerformed(ActionEvent arg0) {
			// TODO Open up the file dialog
			JFileChooser myChooser = new JFileChooser(lastDirectory);
			myChooser.setMultiSelectionEnabled(false);
			myChooser.setFileFilter(new FileNameExtensionFilter("Comma Separated Values","csv"));
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
				
				if(mode == SAVE_FILE){
					if(! selected.getName().endsWith(".csv")){
						selected = new File(selected.getParentFile(), selected.getName() + ".csv");
					}
				}
				
				
				setSelectedFile(selected);
			}
			
		}
	}
	
	class FileSelectorClearAction extends AbstractAction{
		public FileSelectorClearAction(){
			super("Clear");
		}
		
		public void actionPerformed(ActionEvent e){
			clear();
		}
	}
	
	private static final int MARGIN = 5;
	private static final int SEPARATION = 10;
	
	public FileSelectorPanel(String inTitle){
		
		
		SpringLayout my_spring = new SpringLayout();
		this.setLayout(my_spring);
		
		title_label = new JLabel(inTitle);

		filename_display = new JTextField();
		filename_display.setEditable(false);
		filename_display.setMinimumSize(new Dimension());
		
		choose_button = new JButton(new FileSelectorPanelAction());
		clear_button = new JButton(new FileSelectorClearAction());
		

		this.add(title_label);
		this.add(filename_display);
		this.add(choose_button);
		this.add(clear_button);
		
		this.setTitle(inTitle);
		
		my_spring.putConstraint(SpringLayout.NORTH, title_label, MARGIN , SpringLayout.NORTH, this);
		my_spring.putConstraint(SpringLayout.WEST, title_label, MARGIN , SpringLayout.WEST, this);
		my_spring.putConstraint(SpringLayout.WEST, filename_display, MARGIN, SpringLayout.WEST, this);
		
		
		my_spring.putConstraint(SpringLayout.NORTH, filename_display, SEPARATION, SpringLayout.SOUTH, title_label);
		my_spring.putConstraint(SpringLayout.NORTH, choose_button, SEPARATION, SpringLayout.SOUTH, title_label);
		my_spring.putConstraint(SpringLayout.NORTH, clear_button, SEPARATION, SpringLayout.SOUTH, title_label);
		
		my_spring.putConstraint(SpringLayout.WEST, choose_button, SEPARATION, SpringLayout.EAST, filename_display);
		my_spring.putConstraint(SpringLayout.WEST, clear_button, SEPARATION, SpringLayout.EAST, choose_button);
		
		
		my_spring.putConstraint(SpringLayout.EAST, this, MARGIN, SpringLayout.EAST, clear_button);
		my_spring.putConstraint(SpringLayout.SOUTH, this,  MARGIN, SpringLayout.SOUTH, filename_display);
		my_spring.putConstraint(SpringLayout.SOUTH, this, MARGIN, SpringLayout.SOUTH, choose_button);
		my_spring.putConstraint(SpringLayout.SOUTH, this, MARGIN, SpringLayout.SOUTH, clear_button);
		
		
	}
	
}
