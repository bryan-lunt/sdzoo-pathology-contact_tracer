package org.sandiegozoo.pathology.contact_tracer.dataimport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import org.hibernate.SessionFactory;

public class DataImport {
	
	private SessionFactory factory;
	public DataImport(SessionFactory fact){
		factory = fact;
	}
	
	private void loadSomeInfile(File infile, CSVImporter.ImportHandler handler) throws Exception{
		Reader timeline_reader = new FileReader(infile);
		//Attempt loading the timeline file
		try {
			CSVImporter my_csv_reader = new CSVImporter();
			my_csv_reader.setSource(timeline_reader);
			my_csv_reader.readInto(handler);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public void loadTimelineFile(File infile) throws Exception{
		loadSomeInfile(infile, new TimelineHandler(factory));
	}
	
	public void loadInfectionFile(File infile) throws Exception{
		loadSomeInfile(infile, new InfectionHandler(factory));
	}
	
	public void loadContaminationFile(File infile) throws Exception{
		loadSomeInfile(infile, new ContaminationHandler(factory));
	}
	
}
