package sw4j.util;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;


public abstract class DataSettings extends Properties{
	
	protected  Logger getLogger(){
		return Logger.getLogger(this.getClass().getSimpleName());
	}
	/**
	 * check if the settings is valid
	 * @return
	 */
	protected boolean isValid(String [] fields_required){
		for (int i=0; i< fields_required.length; i++){
			String field = fields_required[i];
			String location = getProperty(field);
			if (ToolSafe.isEmpty(location)){
				getLogger().warning("value of a property not specified: "+field);
				return false;
			}
			if (field.startsWith("dir_")){
				File f  = new File(location);
				if (!f.exists()){
					getLogger().warning("the directory does not exist:"+ f.getAbsolutePath());
					return false;
				}
			}
		}
		return true;
	}
	
}
