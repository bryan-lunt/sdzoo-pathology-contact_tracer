This document describes the input and output formats for the current, basic Contact Tracer program.

Conventions
-----------

All input and output formats are CSV.

All dates are of the format "YYYY-MM-DD", "MM/DD/YYYY", with "" or "0" for missing data.

Inputs in < brackets > are optional.

INPUT formats
=============

Housing History Timeline
--------

Housing files contain information about which patient was in which location at which time.

	animal_native_id (Free text), enclosure_name (Free text), move_in (Date), move_out (Date) (Use "" or "0" for ongoing.)


Infections
----------

This file contains a list of all infections, giving the dates of beginning/ending (optional) of contagiousness, and other notes if you desire.
The program only actually uses the first four fields at the moment, but the database stores all of this.

	patient_native_id (free text), linger_days (integer, 0, 1, 2...), onset_date (Date), <cure_date> (Date, ongoing if blank), <diagnosis_date> (Date), <disease_name> (Free text), <notes> (Free text)


It is acceptable to not have commas for later unused fields.
So:
	12345, 0, 2012-01-01

Is an acceptable input line.


Contaminations
--------------

	enclosure_native_id (Free text), start_date (Date), <end_date> (Date, defaults to today if not given.), <direct_or_environmental "0" or "1"> (defaults to 0 if not given, the proceeding comma is optional.)


Basic Diagnosis
---------------

This convinience file allows the program to guess the onset and linger dates based on the diagnosis date and default parameters. (Default parameters are provided elsewhere.)

	animal_native_ID (free text), diagnosis_date (Date), <disease_name> (Free text)


OUTPUTS
=======



Exposure
--------

	animal_native_ID (text), source_animal_native_id (text, blank if unavailable, for example exposed to an environmental contamination.), duration_in_days (integer), start_date (Date), end_date (Date), enclosure_name

Contamination
-------------

This file contains all the enclosure contaminations identified by the program. It is compatible with the input Contaminations, which will ignore the extra field.

	enclosure_native_id (text), start_date (date), end_date (date), direct_or_environmental (integer), source_animal_native_ID (text)
