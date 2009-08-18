package sw4j.app.servlet.common;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sw4j.util.DataSmartMap;

abstract public class AbstractServlet extends HttpServlet{
	
	
	   @Override
	   final public void doPost(HttpServletRequest request, HttpServletResponse response)
	    throws IOException, ServletException
		{
		   doProcessQuery(request,response);
		}
		
		@Override
		final public void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws IOException, ServletException
		{
	    	doProcessQuery(request,response);
		}

		abstract public AbstractService getSvc();
		
		@SuppressWarnings("unchecked")
		 protected void initParams(HttpServletRequest request, AbstractService svc ) {
	    	if (null!=svc && null!=request){
	    		Map<String,String> params = (Map<String,String>)  request.getParameterMap();
	    		Iterator<Map.Entry<String,String>> iter = params.entrySet().iterator();
	    		while (iter.hasNext()){
	    			Map.Entry<String,String> entry = iter.next();
	    			String key =  entry.getKey();
	    			String value = entry.getValue();
	    			put_key_value(svc.params, key, value);
	    		}
	    		put_key_value(svc.context,AbstractService.HTTP_REQUEST_URI, request.getRequestURI());
	    		put_key_value(svc.context,AbstractService.HTTP_QUERY_STRING, request.getQueryString());
	    		put_key_value(svc.context,AbstractService.HTTP_REMOTE_ADDR, request.getRemoteAddr());
	    		put_key_value(svc.context,AbstractService.HTTP_REQUESTED_SESSION_ID, request.getRequestedSessionId());
	    	}
	    	
	    }
	    
	    private void put_key_value(DataSmartMap data, String key, String value){
	    	data.addStringProperty(key);
	    	data.put(normalize_param(key),normalize_param( value));
	    }
	    
	    private String normalize_param(String param){
	    	return param.toLowerCase().trim();
	    }

	    
	    
	    final public void doProcessQuery(HttpServletRequest request, HttpServletResponse response)
	    throws IOException, ServletException{
	    	AbstractService svc = getSvc();
	    	initParams(request, svc);
	    	DataServletResponse ret = svc.run();
	       	ret.output(response);     	    	
	    }
}
