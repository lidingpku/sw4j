package sw4j.util;

import java.util.Collection;

public class ToolMath {
	public static Integer max(Collection<Integer> ary){
		Integer ret = null;
		for(Integer value: ary){
			if (null==ret)
				ret = value;
			else
				ret = Math.max(ret, value);
		}
		return ret;
	}
	
	public static Integer avg(Collection<Integer> ary){
		if (ary.size()==0)
			return 0;
		
		Integer sum = 0;
		for(Integer value: ary){
			sum += value;
		}
		return sum/ary.size();
	}

	public static Integer sum(Collection<Integer> ary){
		Integer sum = 0;
		for(Integer value: ary){
			sum += value;
		}
		return sum;
	}
	
}
