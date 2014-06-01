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
	private NamedComponentPanel advancedPanel;
	
	//TODO: Add an icon?
	class AdvancedGoAction extends AbstractAction {
		
		NamedComponentPanel subject;
		
		public AdvancedGoAction(NamedComponentPanel in){
			super("GO");
			subject = in;
		}
		
		public void actionPerformed(ActionEvent arg0) {
			App myApp = new App();
			
			myApp.exposure_output_file = ((FileSelectorPanel)subject.getNamed("output_file")).getSelectedFile();
			
			
			File timeline_file = ((FileSelectorPanel)subject.getNamed("timeline_file")).getSelectedFile();
			if(timeline_file != null){
				myApp.input_handlers.add(new TimelineHandler(timeline_file));
			}
			
			File infection_file = ((FileSelectorPanel)subject.getNamed("infection_file")).getSelectedFile();
			if(infection_file != null){
				myApp.input_handlers.add(new InfectionHandler(infection_file));
			}
			
			//myApp.input_handlers.add(e)
			//myApp.limit_enclosures_file = ((FileSelectorPanel)subject.getNamed("enclosures_file")).getSelectedFile();
			
			File contamination_file = ((FileSelectorPanel)subject.getNamed("contamination_file")).getSelectedFile();
			if(contamination_file != null){
				myApp.input_handlers.add(new ContaminationHandler(contamination_file));
			}
			
			
			//TODO: Use a background thread for this.
			try{
				myApp.call();
			}catch(Exception e){
				System.err.println(e);
				e.printStackTrace();
			}
			
		}
		
	}
	
	class BasicGoAction extends AbstractAction {
		
		NamedComponentPanel subject;
		
		public BasicGoAction(NamedComponentPanel in){
			super("GO");
			subject = in;
		}
		
		public void actionPerformed(ActionEvent arg0) {
			App myApp = new App();
			
			myApp.exposure_output_file = ((FileSelectorPanel)subject.getNamed("output_file")).getSelectedFile();
			
			
			File timeline_file = ((FileSelectorPanel)subject.getNamed("timeline_file")).getSelectedFile();
			if(timeline_file != null){
				myApp.input_handlers.add(new TimelineHandler(timeline_file));
			}
			
			File diagnosis_file = ((FileSelectorPanel)subject.getNamed("diagnosis_file")).getSelectedFile();
			if(diagnosis_file != null){
				//TODO: actually use the values from the spinners.
				BetaGammaSpinners spinners = ((BetaGammaSpinners)subject.getNamed("default_values"));
				int beta = ((Integer)spinners.beta_spin.getValue()).intValue();
				int gamma = ((Integer)spinners.gamma_spin.getValue()).intValue();
				myApp.input_handlers.add(new BasicDiagnosisHandler(diagnosis_file,beta,gamma));
			}
			//See advanced version if you want other input files.
			
			//TODO: Use a background thread for this.
			try{
				myApp.call();
			}catch(Exception e){
				System.err.println(e);
				e.printStackTrace();
			}
			
		}
		
	}
	
	public static final String ADVANCED = "Advanced Usage";
	public static final String BASIC = "Basic Usage";
	
	private class SwitchListener implements ItemListener{

		Container with_cardlayout;
		
		public SwitchListener(Container cl){
			with_cardlayout = cl;
			
		}
		
		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			((CardLayout)(with_cardlayout.getLayout())).show(with_cardlayout, (String)e.getItem());
		}
		
	}
	
	public CTMainFrame(){
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setLayout(new BorderLayout());
		
		JPanel comboBoxPanel = new JPanel();
		String comboBoxItems[] = { ADVANCED, BASIC };
		JComboBox cb = new JComboBox(comboBoxItems);
		cb.setEditable(false);
		comboBoxPanel.add(cb);
		
		JPanel cards = new JPanel();
		cards.setLayout(new CardLayout());
		
		//setup the advancedPanel
		this.createAdvancedPanel();
		cards.add(advancedPanel, ADVANCED);
		
		this.createBasicPanel();
		cards.add(basicPanel, BASIC);
		
		cb.addItemListener(new SwitchListener(cards));
		
		this.add(comboBoxPanel, BorderLayout.NORTH);
		this.add(cards, BorderLayout.CENTER);
		
		this.setSize(this.getPreferredSize());
		
		
	}
	
	//Cant be static because the action needs a reference to an instance.
	private void createAdvancedPanel(){
		advancedPanel = new NamedComponentPanel();
		advancedPanel.setLayout(new BoxLayout(advancedPanel,BoxLayout.Y_AXIS));
		advancedPanel.add(new JLabel("Outputs:"));
		FileSelectorPanel output_file_panel = new FileSelectorPanel("Exposures Output File");
		output_file_panel.setMode(FileSelectorPanel.SAVE_FILE);
		advancedPanel.addNamed("output_file", output_file_panel);
		
		advancedPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		advancedPanel.add(new JLabel("Inputs:"));
		
		advancedPanel.addNamed("timeline_file", new FileSelectorPanel("Timeline Input File"));
		advancedPanel.addNamed("infection_file", new FileSelectorPanel("Infection Input File"));
		advancedPanel.addNamed("enclosures_file", new FileSelectorPanel("Limit Enclosures File (Optional)"));
		advancedPanel.addNamed("contamination_file", new FileSelectorPanel("Other Enclosure Contaminations (ex: Environmental) File (Optional)"));
		
		advancedPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		
		NamedComponentPanel advancedGoPanel = new NamedComponentPanel();
		advancedGoPanel.setLayout(new BorderLayout());
		
		JButton advancedGo = new JButton(new AdvancedGoAction(advancedPanel));//TODO: Swap out for an action.
		advancedGoPanel.addNamed("go_button", advancedGo);
		advancedPanel.addNamed("go_panel",advancedGoPanel);
		
		advancedPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		
	}
	

	
	private void createBasicPanel(){
		basicPanel = new NamedComponentPanel();
		basicPanel.setLayout(new BoxLayout(basicPanel, BoxLayout.Y_AXIS));
		basicPanel.add(new JLabel("Outputs:"));
		FileSelectorPanel output_file_panel = new FileSelectorPanel("Exposures Output File");
		output_file_panel.setMode(FileSelectorPanel.SAVE_FILE);
		basicPanel.addNamed("output_file", output_file_panel);
		
		basicPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		basicPanel.add(new JLabel("Inputs:"));
		
		basicPanel.addNamed("timeline_file", new FileSelectorPanel("Timeline Input File"));
		basicPanel.addNamed("diagnosis_file", new FileSelectorPanel("Diagnosis Input File (infection periods guessed.)"));
		basicPanel.addNamed("default_values", new BetaGammaSpinners());
		basicPanel.addNamed("enclosures_file", new FileSelectorPanel("Limit Enclosures File (Optional)"));
		
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
			JLabel beta_label = new JLabel("Contagious BETA days before DoDx");
			this.add(beta_label);
			
			beta_spin = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
			beta_label.setLabelFor(beta_spin);
			this.add(beta_spin);
			
			JLabel gamma_label = new JLabel("Contagion lingers GAMMA days in the environment");
			this.add(gamma_label);
			
			gamma_spin = new JSpinner(new SpinnerNumberModel(0,0, 1000000, 1));
			gamma_label.setLabelFor(gamma_spin);
			this.add(gamma_spin);
		}
		
		
	}
	
}
