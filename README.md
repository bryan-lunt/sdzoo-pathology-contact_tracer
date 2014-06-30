Infectious Disease Contact Tracer, version 1.0
==============================================

This document describes the purpose, how to run, and details on all input and output formats of the program Infectious Disease Contract Tracer.  

# Purpose

The Infectious Disease Contact Tracer program was created to identify individuals or environments that came into contact (i.e., were exposed) to another individual that was diagnosed with an infectious disease.  It uses a minimum of 2 input files to cross reference the dates and locations of population members with the dates and locations of diseased individuals or contaminated environments. The program outputs a list of individuals (and optionally environments) with the time periods during which they were exposed to the diseased individual.   This program was built to facilitate identification of individuals at high-risk for developing or spreading an infectious disease in a dynamic population of zoo animals.  It is being used to aid in epidemiologic research and support disease investigations in relation to San Diego Zoo Global’s animal health and conservation programs.   

# To run the program

Download program: Download the file “xxxxxx”.  

Navigate to Input files: Specify the location of all input files.  The population housing history is a required input file that will be cross-referenced with one or more additional input files.  Three types of additional input files (see program interface) are allowed and a minimum of one is required.  Uploading more than one additional file will result in an output with appended data (see section on Output Formats>Contact Output File). 

Identify output file(s): Specify the name and location of the output file(s) where output(s) will be written.  Contact output file is a required output file that will list all identified contact instances.  An optional second output file can be specified (Enclosure Contaminations Output) which will create a list of contaminated enclosures and dates of contamination.  

Run program:  Press Go. When program finishes running, navigate to the previously specified location of the output file and open the file to view the results. 

# Conventions

- All input and output formats are CSV. (Comma Separated Values)
- Column headers should be omitted from input files.
- All dates can be formatted as "YYYY-MM-DD" or "MM/DD/YYYY"; For optional date inputs, "0" or “” (blank) can be used to indicate missing dates, which will default to today’s date.
- Inputs in < brackets > are optional.

#INPUT formats

## Population housing history input file 

This housing history file contains information about locations of individuals in a defined population over time. 

	animal_local_id (Free text), enclosure_name (Free text), move_in (Date), move_out (Date; See below *)

- move_out : Input "" or "0" to indicate enclosures currently inhabited - these will default to today’s date.

Separate lines should be used to indicate housing changes for the same individual over time.  An example input to show animal 12345 inhabited 2 different enclosures for 1 month each would be:
12345, enclosure1, 1/1/2001, 2/1/2002
12345, enclosure2, 2/1/2002, 3/1/2002

There is no sanity checking to prevent duplicate or overlapping records.

## Infected individuals with estimated infectious periods input file


This input allows the program to estimate the infectious periods from a single date.  The user inputs a list of cases and their diagnosis dates, and subsequently specifies the number of days before the diagnosis date during which an animal is considered infectious.  If desired, an option is available to include a time period in days post-removal of the case where the environment remains contaminated (enter “0” where specified if there is no presumed environmental contamination).

	case_animal_local_id (free text), diagnosis_date (Date)

Note:  Use of this input file generates output that has the important characteristic of continued exposure accumulation throughout the duration of two individuals’ time in contact.  For example, suppose a user is interested in identifying all individuals exposed to an infectious animal 30 days prior to its diagnosis.  If diagnosis occurs on the date of death and the infected animal was removed then the search would encompass solely the 30 day incubation period.  However, if the infected animal remains in the environment for an additional 30 days after diagnosis, the search would include a 60 day period (30 days prior to diagnosis and the 30 days of continued exposure to co-inhabitants after diagnosis).  Thus, output generated from this input file may be most appropriate for studies of a chronic infectious disease.  Alternatively, the selection of an input file with user-defined infectious periods (described below) will search between two specific dates of interest.

## Infected individuals with defined infectious periods input file

This input file contains a list of all cases and specific dates corresponding to the beginning and end of an infectious period.
This format may be appropriate for diseases or individuals where infectious periods are specifically known or can be deduced from diagnostic data.  The program has been built to permit storage of additional information, however this version only uses the first four to identify contacts.  

	case_animal_local_id (free text), days_environmental_contamination (integer, 0, 1, 2…; See below.), begin_infectious_date (Date), <end_infectious_date> (Date; See below.), <diagnosis_date> (Date), <disease_name> (Free text), <notes> (Free text)

- case_animal_local_id : The local_id of an infected animal.
- days_environmental_contamination : Used to specify days of environmental contamination post-removal of a case; set to 0 if there is no presumed environmental contamination.
- end_infectious_date : Input "0" or “” to indicate enclosures currently inhabited - these will default to today’s date

Unused fields can be left blank and commas are not necessary to separate them when no later column is used.
Thus, 12345, 0, 2012-01-01 is an acceptable input line.  

## Enclosure Contaminations with defined infectious periods input file

This input file contains a list of all enclosures and specific dates corresponding to the beginning and end of a contamination period.  Using this input file will generate a list of individuals that came into contact with that environment during the specified time period.

	enclosure_local_id (Free text), start_date (Date), <end_date> (Date; defaults to today if left blank or specified as “0”) 

# OUTPUTS

## Contact Output File 

This is the primary output file for identifying which individuals came into contact with cases or contaminated environments during the estimated or specified periods provided in the input files.  Output data will be combined into a single file even if more than the two required input files are selected. 
	animal_local_ID (text), source_case_local _id (text; See below *), duration_in_days (integer; *), start_date (Date;*), end_date (Date; *), enclosure_name (text; *)

- source_case_local_id : This field will be blank if unavailable, for example, if the program was run with the enclosure contamination input file
- duration_in_days :  The duration of contact calculated by subtracting the start_date from the end_date
- start_date : The beginning of the overlapping period of contact between individuals in the population and cases or contaminated environments.
- end_date : The end of overlapping infectious period of interest.
- enclosure_name : The location where this contact occurred.

Note:  Individuals cannot have overlapping contact dates with themselves.


## Enclosure Contaminations Output file

This file contains all the enclosure contaminations identified by the program.  It is of compatible format to an Enclosure Contaminations Input File. (So that it may be reused as input later, if a user so desires.)  This output file was included for users who want to easily create a list of contaminated environments and their corresponding dates. 

	enclosure_local_id (text), start_date (date; See below *), end_date (date; *), source_animal_local_ID (text; *)

- start_date : The beginning of overlapping infectious period.
- end_date :  The end of overlapping infectious period.
- source_animal_local_ID : Indicates the case to which the contamination is attributed, if available.
