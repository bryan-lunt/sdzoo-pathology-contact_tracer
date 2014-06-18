package org.sandiegozoo.pathology.contact_tracer.dataexport;

import java.io.File;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.sandiegozoo.pathology.database.domain.*;

public class ContaminationWriter extends CSVOutput{

	static DateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
	public static final String QUERY = "from Contamination";
	
	public ContaminationWriter(File outputFile) {
		super(outputFile);
		this.setQuery(QUERY);
	}
	
	public ContaminationWriter(Writer outWriter){
		super(outWriter);
		this.setQuery(QUERY);
	}


	@Override
	public String[] handle_object(Object in) throws Exception {
		// TODO Auto-generated method stub
		
		//FORMAT: Enclosure_id, start_date, end_date, direct_or_environmental (environmental if blank), source_animal_native_id;
		
		Contamination one_con = (Contamination)in;
		
		String enclosure_id = one_con.enc_id.name;
		String start_date = date_format.format(one_con.start_date.getTime());
		String end_date = date_format.format(one_con.end_date.getTime());
		String direct_or_environmental = "0";
		String source_animal_native_id = "";
		
		Animal source_animal = (one_con.source_inf_id == null ? null : one_con.source_inf_id.animal_id);
		if(source_animal != null){
			direct_or_environmental = "1";
			source_animal_native_id = Long.toString(source_animal.native_ID);
			
		}
		
		
		String[] one_array = new String[5];
		one_array[0] = enclosure_id;
		one_array[1] = start_date;
		one_array[2] = end_date;
		one_array[3] = direct_or_environmental;
		one_array[4] = source_animal_native_id;
		
		return one_array;
	}
	
}
