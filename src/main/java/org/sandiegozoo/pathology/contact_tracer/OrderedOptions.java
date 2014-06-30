package org.sandiegozoo.pathology.contact_tracer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class OrderedOptions extends Options {
	
	private Map<String,Integer> myOrdering = new HashMap<String,Integer>();
	private int current_opt_number = 0;
	
	private Options enclosed = new Options();
	
	public Options addOption(String sname, boolean has_args, String about){
		Options theRet_in = super.addOption(sname, has_args, about);
		
		myOrdering.put(sname, current_opt_number);
		current_opt_number++;
		
		return this;
	}
	
	public Options addOption(String sname, String lname, boolean has_args, String about){
		Options theRet_in = super.addOption(sname, lname, has_args, about);
		
		myOrdering.put(sname, current_opt_number);
		current_opt_number++;
		
		return this;
	}
	
	
	public HelpFormatter getOrderedHelp(){
		HelpFormatter the_ret = new HelpFormatter();
		
		the_ret.setOptionComparator(new MyOptComparator());
		
		return the_ret;
	}
	
	public class MyOptComparator implements Comparator<Option>{
    	
		public int compare(Option arg0, Option arg1) {
			int val0 = myOrdering.get(arg0.getOpt());
			int val1 = myOrdering.get(arg1.getOpt());
			
			if(val0 < val1)
				return -1;
			else if (val0 == val1)
				return 0;
			else if(val0 > val1)
				return 1;
			
			return 0;
		}
    	
    }
}
