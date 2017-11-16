package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class Object { 
	
	private int popularity;
	
	public static Object weightedChoice(Map<Long, Object> map)	{
		List<Object> list = new ArrayList<>();
		for (Long lon : map.keySet())
			for(long i=0; i<map.get(lon).getPopularity(); i++)		{
				list.add(map.get(lon));
			}
		Random random = new Random();
		System.out.println("Dimensioni Lista: "+list.size());
		Object objects = list.get(random.nextInt(list.size()));
		
		return objects;
	}

	public Object(int pop)	{
		this.setPopularity(pop);
		
	}


	public int getPopularity() {
		return popularity;
	}

	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}

	
}
