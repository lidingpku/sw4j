/**
 * Copyright 2007 Inference Web Group.  All Rights Reserved.
*/

package sw4j.app.servlet;



import sw4j.app.servlet.PingService;
import sw4j.app.servlet.common.AbstractService;
import sw4j.app.servlet.common.AbstractServlet;



/**
 * Web Interface for .
 *  
 * @author Li
 *
 */
public class PingServlet extends AbstractServlet {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public AbstractService getSvc() {
		return new PingService();
	}
	
	


   
}
