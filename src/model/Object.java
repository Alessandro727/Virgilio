package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class Object { 
	
	public static Object weightedChoice(Map<Long, Object> map)	{
		List<Object> list = new ArrayList<>();
		for (Long lon : map.keySet())
			for(long i=0; i<lon;i++)		{
				list.add(map.get(lon));
			}
		Random random = new Random();
		Object objects = list.get(random.nextInt(list.size()));
		
		return objects;
	}

}
