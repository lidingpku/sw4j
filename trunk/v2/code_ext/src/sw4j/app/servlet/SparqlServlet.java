/**
 * Copyright 2007 Inference Web Group.  All Rights Reserved.
*/

package sw4j.app.servlet;


import sw4j.app.servlet.SparqlService;
import sw4j.app.servlet.common.AbstractService;
import sw4j.app.servlet.common.AbstractServlet;



/**
 * Web Interface for PmlValidator.
 *  
 * @author Li
 *
 */
public class SparqlServlet extends AbstractServlet {
	@Override
	public AbstractService getSvc() {
		return  new SparqlService();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -3392604472787612392L;

      
}
