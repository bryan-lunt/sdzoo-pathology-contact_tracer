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
			super("GO");
			subject = in;
		}
		
		public void actionPerformed(ActionEvent arg0) {
			createApp();
			boolean fail = false; 
			
			myApp.exposure_output_file = ((FileSelectorPanel)subject.getNamed("output_file")).getSelectedFile();
			if(myApp.exposure_output_file == null){
				JOptionPane.showMessageDialog(null, "You must select an output file.", "Missing Output", JOptionPane.ERROR_MESSAGE);
				fail = true;
			}
			
			myApp.contamination_output_file = ((FileSelectorPanel)subject.getNamed("contamination_output_file")).getSelectedFile();
			
			
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
		
		
		basicPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		basicPanel.add(new JLabel("Basic Inputs:"));
		
		basicPanel.addNamed("timeline_file", new FileSelectorPanel("Housing Timeline Input File (Required)"));
		basicPanel.addNamed("diagnosis_file", new FileSelectorPanel("Diagnosis Input File (infection periods guessed.) (One required *)"));
		basicPanel.addNamed("default_values", new BetaGammaSpinners());
		
		basicPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		basicPanel.add(new JLabel("Advanced Inputs:"));
		
		basicPanel.addNamed("infection_file", new FileSelectorPanel("Infection Input File (One required *)"));
		basicPanel.addNamed("contamination_file", new FileSelectorPanel("Other Enclosure Environments (ex: Contamiation) File (Optional)"));
		
		basicPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		
		basicPanel.add(new JLabel("Outputs:"));
		FileSelectorPanel output_file_panel = new FileSelectorPanel("Exposures Output File (Required)");
		output_file_panel.setMode(FileSelectorPanel.SAVE_FILE);
		basicPanel.addNamed("output_file", output_file_panel);
		
		FileSelectorPanel output_contamination_panel = new FileSelectorPanel("Environments Output File (will include inputed Environments.) (Optional)");
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
	
	class BetaGammaSpinners extends JPanel {
		
		public JSpinner beta_spin;
		public JSpinner gamma_spin;
		
		public BetaGammaSpinners(){
			
			SpringLayout bg_spin_layout = new SpringLayout();
			
			JLabel beta_label = new JLabel("Contagious B days before diagnosis_date.");
			this.add(beta_label);
			
			beta_spin = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
			beta_label.setLabelFor(beta_spin);
			this.add(beta_spin);
			
			JLabel gamma_label = new JLabel("Contagion lingers G days in the environment");
			this.add(gamma_label);
			
			gamma_spin = new JSpinner(new SpinnerNumberModel(0,0, 1000000, 1));
			gamma_label.setLabelFor(gamma_spin);
			this.add(gamma_spin);
			
			
			bg_spin_layout.putConstraint(SpringLayout.NORTH, beta_label, 5, SpringLayout.NORTH, this);
			bg_spin_layout.putConstraint(SpringLayout.NORTH, beta_spin, 5, SpringLayout.NORTH, this);
			bg_spin_layout.putConstraint(SpringLayout.NORTH, gamma_label, 5, SpringLayout.NORTH, this);
			bg_spin_layout.putConstraint(SpringLayout.NORTH, gamma_spin, 5, SpringLayout.NORTH, this);
			
			bg_spin_layout.putConstraint(SpringLayout.WEST, beta_label, 5, SpringLayout.WEST, this);
			bg_spin_layout.putConstraint(SpringLayout.WEST, beta_spin, 5, SpringLayout.EAST, beta_label);
			bg_spin_layout.putConstraint(SpringLayout.WEST, gamma_label, 5, SpringLayout.EAST, beta_spin);
			bg_spin_layout.putConstraint(SpringLayout.WEST, gamma_spin, 5, SpringLayout.EAST, gamma_label);
			bg_spin_layout.putConstraint(SpringLayout.WEST,this, 5, SpringLayout.EAST, gamma_spin);
			
			bg_spin_layout.putConstraint(SpringLayout.SOUTH, this, 5, SpringLayout.NORTH, beta_label);
			
			
			
		}
		
		
	}
	
}
