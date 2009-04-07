/**
MIT License

Copyright (c) 2009 

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
 */

package sw4j.util;


/**
 * Common exception message 
 *   -  a string error summary
 *   -  a string error details  
 *   -  a string error state  (warning 1, error 2, fatal 3)
 *  
 * @author Li Ding
 * 
 */
public class Sw4jException extends Exception {
	////////////////////////////////////////////////
	// hidden data
	////////////////////////////////////////////////
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////
	// constant
	////////////////////////////////////////////////
	public final static int STATE_OK = 0;
	public final static int STATE_WARNING = 1;
	public final static int STATE_ERROR = 2;
	public final static int STATE_FATAL = 3;

	public final static int CONST_MIN_STATE = 0;
	public final static int CONST_MAX_STATE = 3;

	public final static String[] STATE_STRING = new String[] { "OK", "WARNING",
			"ERROR", "FATAL ERROR", };
	
	public final static String FIELD_STATE ="state";
	public final static String FIELD_SUMMARY ="summary";
	public final static String FIELD_DETAILS ="details";
	private final static String[] FIELDS= new String[]{
		FIELD_STATE,
		FIELD_SUMMARY,
		FIELD_DETAILS,
	};
	
	
	public final static String SUMMARY_IOException = "FATAL IO exception.";

	
	////////////////////////////////////////////////
	// internal data
	////////////////////////////////////////////////
	private int m_state;
	private DataSmartMap m_data = new DataSmartMap();
	
	////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////

	public Sw4jException(int state, Exception e) {
		this (state, e.getClass().getSimpleName(), e.getMessage());
	}

	public Sw4jException(int state, Exception e, String details) {
		this (state, e.getClass().getSimpleName(), details +"\n"+ e.getMessage());
	}

	public Sw4jException(int state, String summary) {
		this(state, summary, "");
	}
	
	public Sw4jException(int state, String summary, String details) {
		super(String.format(
				"State: %s\n" +
				"Summary: %s\n" +
				"Details: %s\n",
				STATE_STRING[Math.max(CONST_MIN_STATE, Math.min(CONST_MAX_STATE, state))] ,
				(null==summary)?"":summary,
				(null==details)?"":details));
		m_state= Math.max(CONST_MIN_STATE, Math.min(CONST_MAX_STATE, state));
		m_data.put(FIELD_STATE, STATE_STRING[m_state]);
		m_data.put(FIELD_SUMMARY, summary);
		m_data.put(FIELD_DETAILS, details);
		m_data.addStringProperties(FIELDS);
	}
	
	////////////////////////////////////////////////
	// functions
	////////////////////////////////////////////////

	public int getErrorStateID() {
		return this.m_state;
	}

	public String getErrorState() {
		return this.m_data.getEntryAsString(FIELD_STATE);
	}
	
	public String getErrorSummary(){
		return this.m_data.getEntryAsString(FIELD_SUMMARY);
	}
	
	public String getErrorDetails(){
		return this.m_data.getEntryAsString(FIELD_DETAILS);
	}

}
