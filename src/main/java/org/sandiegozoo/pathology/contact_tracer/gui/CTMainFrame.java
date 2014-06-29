package org.sandiegozoo.pathology.contact_tracer.gui;

//Should not have to import
import org.sandiegozoo.pathology.contact_tracer.dataimport.*;
import org.sandiegozoo.pathology.contact_tracer.gui.*;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.awt.*;
import java.io.File;

import javax.swing.*;

import org.sandiegozoo.pathology.contact_tracer.App;
import org.sandiegozoo.pathology.database.PathDBUtil;

public class CTMainFrame extends JFrame {

	private NamedComponentPanel basicPanel;
	
	abstract class GoAction extends AbstractAction {
		public GoAction(String in){
			super(in);
			myname = in;
		}
		
		protected App myApp;
		protected String myname;
		
		protected void createApp(){
			myApp = new App();
		}
		
		protected void go(){
			setEnabled(false);
			this.putValue(Action.NAME,"Running, please wait...");
			
			new Thread(){
				public void run(){
					try{
						try{
							PathDBUtil clearUtil = new PathDBUtil(myApp.sessionFactory.openSession());
							clearUtil.truncateAll();
							clearUtil.close();
						}catch(Exception e){ throw new Exception("Could not clear the database.", e); }
						
						//Call the app.
						myApp.call();
						JOptionPane.showMessageDialog(null, "Finished", "Done", JOptionPane.INFORMATION_MESSAGE);
					}catch(Exception e){
						System.err.println(e);
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}finally{
						putValue(Action.NAME,myname);
						setEnabled(true);
					}
				}
			}.start();
		}
	}
	
	class BasicGoAction extends GoAction {
		
		NamedComponentPanel subject;
		
		public BasicGoAction(NamedComponentPanel in){
			super("<html><b>GO</b></html>");
			subject = in;
		}
		
		public void actionPerformed(ActionEvent arg0) {
			createApp();
			boolean fail = false; 
			
			myApp.exposure_output_file = ((FileSelectorPanel)subject.getNamed("output_file")).getSelectedFile();
			if(myApp.exposure_output_file == null){
				JOptionPane.showMessageDialog(null, "You must select an output file.", "Missing Output", JOptionPane.ERROR_MESSAGE);
				fail = true;
			}else if(myApp.exposure_output_file.exists()){
				String[] overwrite_options = {"Overwrite","Choose Another"};
				int overwrite_exposure = JOptionPane.showOptionDialog(null, 
						"<html>The selected <b>Contacts Output</b> File exist, overwrite?</html>",
						"File Exists!",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.WARNING_MESSAGE,
						null,
						overwrite_options,
						0);
				if(overwrite_exposure != 0){
					fail = true;
				}
			}
			
			
			
			myApp.contamination_output_file = ((FileSelectorPanel)subject.getNamed("contamination_output_file")).getSelectedFile();
			if(!fail && myApp.contamination_output_file != null && myApp.contamination_output_file.exists()){
				
				String[] overwrite_options = {"Overwrite","Choose Another"};
				int overwrite_contamination = JOptionPane.showOptionDialog(null, 
						"<html>The selected <b>Contaminated Enclosures Output</b> File exist, overwrite?",
						"File Exists!",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.WARNING_MESSAGE,
						null,
						overwrite_options,
						0);
				if(overwrite_contamination != 0){
					fail = true;
				}
			}
			
			
			
			
			File timeline_file = ((FileSelectorPanel)subject.getNamed("timeline_file")).getSelectedFile();
			if(timeline_file != null){
				myApp.input_handlers.add(new TimelineHandler(timeline_file));
			}else{
				JOptionPane.showMessageDialog(null, "You must select a housing timeline file.", "Missing Input", JOptionPane.ERROR_MESSAGE);
				fail = true;
			}
			
			
			
			File diagnosis_file = ((FileSelectorPanel)subject.getNamed("diagnosis_file")).getSelectedFile();
			if(diagnosis_file != null){
				//TODO: actually use the values from the spinners.
				BetaGammaSpinners spinners = ((BetaGammaSpinners)subject.getNamed("default_values"));
				int beta = ((Integer)spinners.beta_spin.getValue()).intValue();
				int gamma = ((Integer)spinners.gamma_spin.getValue()).intValue();
				myApp.input_handlers.add(new BasicDiagnosisHandler(diagnosis_file,beta,gamma));
			}
			
			File infections_file = ((FileSelectorPanel)subject.getNamed("infection_file")).getSelectedFile();
			if(infections_file != null){
				myApp.input_handlers.add(new InfectionHandler(infections_file));
			}
			
			File contaminations_in_file = ((FileSelectorPanel)subject.getNamed("contamination_file")).getSelectedFile();
			if(contaminations_in_file != null){
				myApp.input_handlers.add(new ContaminationHandler(contaminations_in_file));
			}
			
			if(diagnosis_file == null && infections_file == null && contaminations_in_file == null){
				JOptionPane.showMessageDialog(null, "You must select at least one of: Basic Diagnosis file, Infections file, Environments file.", "Missing Input", JOptionPane.ERROR_MESSAGE);
				fail = true;
			}
			
			if(!fail){
				go();
			}
			
		}
		
	}
	
	public CTMainFrame(){
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setLayout(new BorderLayout());
		
		this.createBasicPanel();
		this.add(basicPanel, BorderLayout.CENTER);
		
		this.pack();
		
		this.setSize(this.getPreferredSize());
		
		
	}
		
	private void createBasicPanel(){
		basicPanel = new NamedComponentPanel();
		basicPanel.setLayout(new BoxLayout(basicPanel, BoxLayout.Y_AXIS));
		
		basicPanel.setName("THIS IS A TEST");
		
		
		basicPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		
		basicPanel.add(new JustifiedLabel("<html><b>Inputs:</b></html>"));
		
		basicPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		
		basicPanel.addNamed("timeline_file", new FileSelectorPanel("<html>Population Housing history Input File <em>(Required)<em></html>"));
		
		basicPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		
		basicPanel.add(new JustifiedLabel("<html><h4>Choose one or more of the following three input files (one is required):</h4></html>"));

		basicPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		
		basicPanel.addNamed("diagnosis_file", new FileSelectorPanel("Infected Individuals with estimated infectious periods"));
		basicPanel.addNamed("default_values", new BetaGammaSpinners());
		
		
		basicPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		
		basicPanel.addNamed("infection_file", new FileSelectorPanel("Infected Individuals with defined infectious periods"));
		
		basicPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		
		basicPanel.addNamed("contamination_file", new FileSelectorPanel("Enclosure Contaminations with defined infectious periods"));
		
		basicPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		
		basicPanel.add(new JustifiedLabel("<html><b>Outputs:</b></html>"));
		
		basicPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		
		FileSelectorPanel output_file_panel = new FileSelectorPanel("<html>Contact Output File <em>(Required)</em></html>");
		output_file_panel.setMode(FileSelectorPanel.SAVE_FILE);
		basicPanel.addNamed("output_file", output_file_panel);
		
		basicPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		
		FileSelectorPanel output_contamination_panel = new FileSelectorPanel("<html>Enclosure Contaminations Output File <em>(Optional)</em></html>");
		output_contamination_panel.setMode(FileSelectorPanel.SAVE_FILE);
		basicPanel.addNamed("contamination_output_file",output_contamination_panel);
		
		basicPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		
		NamedComponentPanel basicGoPanel = new NamedComponentPanel();
		basicGoPanel.setLayout(new BorderLayout());
		
		JButton basicGo = new JButton(new BasicGoAction(basicPanel));
		basicGoPanel.addNamed("go_button", basicGo);
		basicPanel.addNamed("go_panel", basicGoPanel);
		
		basicPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		
	}
	
	class JustifiedLabel extends JPanel {
		public JLabel mylabel;
		
		public JustifiedLabel(String in){
			mylabel = new JLabel(in);
			
			SpringLayout mySpring = new SpringLayout();
			
			this.setLayout(mySpring);
			
			this.add(mylabel);
			
			mySpring.putConstraint(SpringLayout.NORTH, mylabel, 5, SpringLayout.NORTH, this);
			mySpring.putConstraint(SpringLayout.WEST, mylabel, 5, SpringLayout.WEST, this);
			mySpring.putConstraint(SpringLayout.EAST, this, 20, SpringLayout.EAST, mylabel);
			mySpring.putConstraint(SpringLayout.SOUTH, this, 5, SpringLayout.SOUTH, mylabel);
			
		}
	}
	
	class BetaGammaSpinners extends JPanel {
		
		public JSpinner beta_spin;
		public JSpinner gamma_spin;
		
		public BetaGammaSpinners(){
			
			SpringLayout bg_spin_layout = new SpringLayout();
			
			//this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
			this.setLayout(bg_spin_layout);
			
			JLabel beta_label = new JLabel("<html>Contagious <em>&beta;</em> days before diagnosis_date</html>");
			this.add(beta_label);
			
			beta_spin = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
			beta_label.setLabelFor(beta_spin);
			this.add(beta_spin);
			
			JLabel gamma_label = new JLabel("<html>Contagion remains <em>&gamma;</em> days in the enclosure after the animal moves out</html>");
			this.add(gamma_label);
			
			gamma_spin = new JSpinner(new SpinnerNumberModel(0,0, 1000000, 1));
			gamma_label.setLabelFor(gamma_spin);
			this.add(gamma_spin);
			
			
			bg_spin_layout.putConstraint(SpringLayout.NORTH, beta_label, 5, SpringLayout.NORTH, this);
			bg_spin_layout.putConstraint(SpringLayout.NORTH, beta_spin, 5, SpringLayout.NORTH, this);
			
			bg_spin_layout.putConstraint(SpringLayout.WEST, beta_label, 5, SpringLayout.WEST, this);
			
			bg_spin_layout.putConstraint(SpringLayout.WEST, beta_spin, 5, SpringLayout.EAST, beta_label);
			bg_spin_layout.putConstraint(SpringLayout.EAST, this, 5, SpringLayout.EAST, beta_spin);
			bg_spin_layout.putConstraint(SpringLayout.EAST, this, 5, SpringLayout.EAST, gamma_spin);
			
			

			bg_spin_layout.putConstraint(SpringLayout.NORTH, gamma_label, 5, SpringLayout.SOUTH, beta_label);
			bg_spin_layout.putConstraint(SpringLayout.NORTH, gamma_spin, 5, SpringLayout.SOUTH, beta_spin);
			

			bg_spin_layout.putConstraint(SpringLayout.EAST, gamma_label, 0, SpringLayout.EAST, beta_label);
			bg_spin_layout.putConstraint(SpringLayout.WEST, gamma_label, 0, SpringLayout.WEST, beta_label);
			
			bg_spin_layout.putConstraint(SpringLayout.EAST, gamma_spin, 0, SpringLayout.EAST, beta_spin);
			bg_spin_layout.putConstraint(SpringLayout.WEST, gamma_spin, 0, SpringLayout.WEST, beta_spin);
			
			
			bg_spin_layout.putConstraint(SpringLayout.WEST, gamma_spin, 5, SpringLayout.EAST, gamma_label);
			
			
			bg_spin_layout.putConstraint(SpringLayout.SOUTH, this, 5, SpringLayout.SOUTH, gamma_label);
			bg_spin_layout.putConstraint(SpringLayout.SOUTH, this, 5, SpringLayout.SOUTH, gamma_spin);
			
		}
		
		
	}
	
}
