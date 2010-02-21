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
package sw4j.rdf.load;
/**
 * load jena model
 * 
 * @author Li Ding
 */

import com.hp.hpl.jena.rdf.model.Model;

import sw4j.task.common.AbstractTaskDesc;
import sw4j.task.common.DataTaskResult;
import sw4j.task.load.TaskLoad;

public class AgentModelLoader {
	
	private TaskLoad m_task_load=null;
	
	private TaskParseRdf m_task_rdf=null;
	private boolean b_parse_rdf =false;

	

	public AgentModelLoader(String szFileOrURI){
		m_task_load = TaskLoad.load(szFileOrURI);
	}

	public AgentModelLoader(String szFileOrURI,String szXmlBase, String szRdfSyntax){
		m_task_load = TaskLoad.load(szFileOrURI, szXmlBase, szRdfSyntax);
	}
	
	public AgentModelLoader(TaskLoad load){
		m_task_load = load;
	}

	final public TaskLoad getLoad(){
		return m_task_load;
	}
	
	
	final public TaskParseRdf getParseRdf(){
		if (!b_parse_rdf && null==m_task_rdf){
			if (null!=getLoad() && getLoad().isLoadSucceed())
				m_task_rdf = TaskParseRdf.parse(getLoad());
			b_parse_rdf =true;
		}
		
		return m_task_rdf;
	}



	final public Model getModelData(){
		if (null!= getParseRdf())
			return getParseRdf().getModel();
		return null;
	}

	final public String getModelXmlBase(){
		if (null!= getLoad())
			return getLoad().getXmlBase();
		return null;
	}


	final public DataTaskResult load(){
		DataTaskResult der = new DataTaskResult();
		AbstractTaskDesc task;
		
		task= this.getLoad();
		if (null!= task){
			der.reports.add(task.getReport());
		}
		
		task = this.getParseRdf();
		if (null!= task){
			der.reports.add(task.getReport());
		}
		
		der.setSuccessful((null!= getParseRdf()) && getParseRdf().hasModel()&& !getParseRdf().getReport().hasFatal());

		return der;
	}
}
