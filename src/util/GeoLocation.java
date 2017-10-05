package util;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import model.Venue;



public class GeoLocation {
	
	
	private static final double R = 6371; // Earth's radius (mean radius = 6.371 km)
	private static final double RAD = (3.14159265358979323846)/180;	// conversion factor for degrees into radians
	private static final double DEG = 180/(3.14159265358979323846);	// conversion factor for radians into degrees
	
	
	public static double getDistanceBetweenTwoPoints(String latitude1, String longitude1, String latitude2, String longitude2) {
		double lat1 = Double.parseDouble(latitude1);
		double lng1 = Double.parseDouble(longitude1);
		double lat2 = Double.parseDouble(latitude2);
		double lng2 = Double.parseDouble(longitude2);
		
		double dlat = (lat2 - lat1) * RAD;
		double dlng = (lng2 - lng1) * RAD;
		lat1 = lat1 * RAD;
		lat2 = lat2 * RAD;
		
		/*
		 * haversine formule:
		 * a = sin²(Δφ/2) + cos(φ1) * cos(φ2) * sin²(Δλ/2)
		 * c = 2 * atan2(√a, √(1−a))
		 * d = R * c
		 * 
		 *  where:
		 *  φ = latitude
		 *  λ = longitude
		 */
		
		
		// a = square of half the chord length between the points
		double a = Math.pow(Math.sin(dlat/2), 2) +
				Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlng/2), 2);
				
		// c = angular distance in radinats
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
				
		// distance
		double d = R * c;
		
		return d;
	}
	
	
	// less accurate but more performant version. It can be used for small distances
	public static double getDistanceBetweenTwoPointsMorePerformant(String latitude1, String longitude1, String latitude2, String longitude2) {
		double lat1 = Double.parseDouble(latitude1) * RAD;
		double lng1 = Double.parseDouble(longitude1) * RAD;
		double lat2 = Double.parseDouble(latitude2) * RAD;
		double lng2 = Double.parseDouble(longitude2) * RAD;
		
		
		/*
		 * formule (the Pythagoras’ theorem is used on an equirectangular projection):
		 * x = Δλ.cos(φ)
		 * y = Δφ
		 * d = R.√x² + y²
		 * 
		 */
		
		double x = (lng2 - lng1) * Math.cos((lat1+lat2)/2);
		
		double y = lat2 - lat1;
		
		double d = Math.sqrt((x*x) + (y*y)) * R;
		
		return d;
	}
	
	
	// half-way point along a great circle path between two points
	public static String getHalfwayPointBetweenTwoPoints(double lat1, double lng1, double lat2, double lng2) {
		/*double lat1 = Double.parseDouble(latitude1);
		double lng1 = Double.parseDouble(longitude1);
		double lat2 = Double.parseDouble(latitude2);
		double lng2 = Double.parseDouble(longitude2);*/
		
		double dlng = (lng2 - lng1) * RAD;
		lat1 = lat1 * RAD;
		lng1 = lng1 * RAD;
		lat2 = lat2 * RAD;		
		
		
		/*
		 * formule :
		 * Bx = cos(φ2).cos(Δλ)
		 * By = cos(φ2).sin(Δλ)
		 * φm = atan2( sin(φ1) + sin(φ2), √((cos(φ1)+Bx)² + By²) )
		 * λm = λ1 + atan2(By, cos(φ1)+Bx)
		 * 
		 */
		
		double Bx = Math.cos(lat2) * Math.cos(dlng);
		
		double By = Math.cos(lat2) * Math.sin(dlng);
		
		double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2),
								 Math.sqrt( (Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + (By * By) ) );
		
		double lng3 = lng1 + Math.atan2(By, Math.cos(lat1) + Bx);
		//lng3 = (lng3 + 3 * Math.PI) % (2 * Math.PI) - Math.PI;  // normalise to -180..+180°
		
		System.out.println(lat3*DEG + ", " + lng3*DEG);
		return (String.valueOf(lat3*DEG) + "," + String.valueOf(lng3*DEG));
	}
	
	
	
	
	
	public static Map<Double, Venue> orderVenuesByDistance(String lat, String lng, List<Venue> venues, int limit) {
		if (venues == null)
			return null;
		
		Map<Double, Venue> venuesMapTemp = new TreeMap<Double, Venue>();
		double distance;
		for(Venue v: venues) {
			distance = getDistanceBetweenTwoPointsMorePerformant(lat, lng, v.getLatitude(), v.getLongitude());
			venuesMapTemp.put(distance, v);
		}
		
		Map<Double, Venue> venuesMap = new TreeMap<Double, Venue>();
		int i = 0;
		for(double d: venuesMapTemp.keySet()) {
			if(i>=limit)
				break;
			venuesMap.put(d, venuesMapTemp.get(d));
			i++;
		}
		
		return venuesMap;
	}
	
	
	
	
	public static Venue getMostNearAndPopularVenueWithoutContext(String lat, String lng, List<Venue> venues) {
		if (venues == null || venues.size() < 1)
			return null;
		
		Venue venue = null;	// venue da ritornare
		double distanceAndPopularity = Integer.MAX_VALUE;
		
		Venue venueTemp;		
		double distanceAndPopularityTemp;
		
		for(int i = 0; i<venues.size(); i++) {
			venueTemp = venues.get(i);
			if (venueTemp.getCheckinsNumber() < 1)
				continue;
			distanceAndPopularityTemp = getDistanceBetweenTwoPointsMorePerformant(lat, lng, venueTemp.getLatitude(), venueTemp.getLongitude()) - venueTemp.getCheckinsNumber();
			if (distanceAndPopularityTemp < distanceAndPopularity) {
				distanceAndPopularity = distanceAndPopularityTemp;
				venue = venues.get(i);
			}
		}
		
		return venue;
	}
	
	
	
	public static Venue getMostNearAndPopularVenueWithContext(String lat, String lng, List<Venue> venues) {
		if (venues == null || venues.size() < 1)
			return null;
		
		Venue venue = null;	// venue da ritornare
		double distanceAndPopularity = Integer.MAX_VALUE;
		
		Venue venueTemp;		
		double distanceAndPopularityTemp;
		
		for(int i = 0; i<venues.size(); i++) {
			venueTemp = venues.get(i);
			if (venueTemp.getCheckinsNumber() < 1)
				continue;
			distanceAndPopularityTemp = getDistanceBetweenTwoPointsMorePerformant(lat, lng, venueTemp.getLatitude(), venueTemp.getLongitude()) - venueTemp.getCheckinsNumber();
			if (distanceAndPopularityTemp < distanceAndPopularity && Utilities.isOpen(venueTemp)) {
				distanceAndPopularity = distanceAndPopularityTemp;
				venue = venues.get(i);
			}
		}
		
		return venue;
	}
	
	
	


}