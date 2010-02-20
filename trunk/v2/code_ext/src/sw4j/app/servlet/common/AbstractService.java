package sw4j.app.servlet.common;


import sw4j.rdf.load.RDFSYNTAX;
import sw4j.util.DataSmartMap;
import sw4j.util.Sw4jException;
import sw4j.util.ToolSafe;
import sw4j.util.ToolString;
import sw4j.util.ToolURI;

abstract public class AbstractService  {
	
	public static final String HTTP_REQUEST_URI = "HTTP_REQUEST_URI".toLowerCase();
	public static final String HTTP_QUERY_STRING = "HTTP_QUERY_STRING".toLowerCase();
	public static final String HTTP_REMOTE_ADDR = "HTTP_REMOTE_ADDR".toLowerCase();
	public static final String HTTP_REQUESTED_SESSION_ID = "HTTP_REQUESTED_SESSION_ID".toLowerCase();

	
	public static final String PARAM_URI = "uri";
	public static final String PARAM_URL = "url";
	public static final String PARAM_OUTPUT = "output";
	public static final String PARAM_VIEW = "view";
	public static final String PARAM_OPTION = "option";
		
	abstract public String getName();
	abstract public DataServletResponse run();
	
	public DataSmartMap params = new DataSmartMap();
	protected DataSmartMap context = new DataSmartMap();

	
	public String getSyntaxRdf(){
		return RDFSYNTAX.parseSyntaxRdf(this.params.getAsString(PARAM_OUTPUT));
	}
	
	public boolean getBoolParam(String key){
		String value = this.params.getAsString(key);
		if (ToolSafe.isEmpty(value))
			return false;
		if (value.equalsIgnoreCase("y"))
			return true;
		if (value.equalsIgnoreCase("yes"))
			return true;
		if (value.equalsIgnoreCase("t"))
			return true;
		if (value.equalsIgnoreCase("true"))
			return true;
		return false;
	}
	
	public String getUriParam(String key) {
		String value = this.params.getAsString(key);
		if (ToolSafe.isEmpty(value))
			return null;
		else
			try {
				value = value.trim();
				return ToolURI.decodeURIString(value);
			} catch (Sw4jException e) {
				e.printStackTrace();
				return value;
			}
	}
	
	public String getNormalizedParam(String key) {
		String value = this.params.getAsString(key);
		if (ToolSafe.isEmpty(value))
			return null;
		else
			return ToolString.normalize_param(value);
	}
}
