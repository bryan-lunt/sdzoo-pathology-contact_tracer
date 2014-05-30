package org.sandiegozoo.pathology.contact_tracer.dataexport;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import au.com.bytecode.opencsv.*;

public class CSVExporter {

	public interface ExportFormat<T>{
		public void setData(List<T> in);
		public List<String[]> formatContents();
	}
	
	private CSVWriter writer;
	
	public void setDestination(Writer dest){
		writer = new CSVWriter(dest);
	}
	
	public void writeCSV(ExportFormat theFormat) throws IOException{
		writer.writeAll(theFormat.formatContents());
		writer.flush();
	}
	
	
}
