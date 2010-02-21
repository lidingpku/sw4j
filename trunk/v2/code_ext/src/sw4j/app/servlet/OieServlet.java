/**
 * Copyright 2007 Inference Web Group.  All Rights Reserved.
*/

package sw4j.app.servlet;


import sw4j.app.servlet.common.AbstractService;
import sw4j.app.servlet.common.AbstractServlet;



/**
 * Web Interface for Owl Instance Validation.
 *  
 * @author Li
 *
 */
public class OieServlet extends AbstractServlet {
	@Override
	public AbstractService getSvc() {
		return  new OieService();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -3392704472787612392L;

      
}
