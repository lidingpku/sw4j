/**
 * Copyright 2007 Inference Web Group.  All Rights Reserved.
*/

package sw4j.app.servlet;


import sw4j.app.servlet.ConvertService;
import sw4j.app.servlet.common.AbstractService;
import sw4j.app.servlet.common.AbstractServlet;



/**
 * Web Interface for PmlValidator.
 *  
 * @author Li
 *
 */
public class ConvertServlet extends AbstractServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3392604472787612392L;


	@Override
	public AbstractService getSvc() {
		return  new ConvertService();
	}

    
   
}
