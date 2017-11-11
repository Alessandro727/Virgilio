package logic.router;

import java.util.Map;

import model.Object;

public interface JenaManager {
	
	Map<Long, Object> retriveNodes(double lat, double lon, double radius);

}
