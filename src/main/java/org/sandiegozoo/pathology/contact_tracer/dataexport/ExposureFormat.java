package org.sandiegozoo.pathology.contact_tracer.dataexport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.sandiegozoo.pathology.database.domain.Exposure;

public class ExposureFormat implements CSVExporter.ExportFormat<Exposure>{

	static DateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
	
	private List<Exposure> thedata;
	
	public ExposureFormat(List<Exposure> in){
		this.setData(in);
	}
	
	public void setData(List<Exposure> in){
		thedata = in;
	}
	
	public List<String[]> formatContents(){
		List<String[]> retval = new ArrayList<String[]>();
		for(Exposure one_exp : thedata){
			
			String exposed_native_id = one_exp.animal_id.native_ID;
			String source_animal_native_id = (one_exp.source.source_inf_id == null ? "" : one_exp.source.source_inf_id.animal_id.native_ID);
			String duration = "" + one_exp.getDurationDays();
			String start_date = date_format.format(one_exp.start_date.getTime());
			String end_date = date_format.format(one_exp.end_date.getTime());
			String enclosure_name = one_exp.source.enc_id.name;
			
			String[] one_array = new String[6];
			one_array[0] = exposed_native_id;
			one_array[1] = source_animal_native_id;
			one_array[2] = duration;
			one_array[3] = start_date;
			one_array[4] = end_date;
			one_array[5] = enclosure_name;
			
			retval.add(one_array);
		}
		
		return retval;
	}
	
}
