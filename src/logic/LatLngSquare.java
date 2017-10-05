package logic;

import model.Venue;

import java.util.List;


public class LatLngSquare {
	
	private double maxLat;
	private double minLat;
	private double maxLng;
	private double minLng;
	
	public LatLngSquare(List<Venue> venues) {
		this.setSquareDimension(venues);
	}
	
		
	public double getMaxLat() {
		return this.maxLat;
	}
	
	public void setMaxLat(double lat) {
		this.maxLat = lat;
	}
	
	public double getMinLat() {
		return this.minLat;
	}
	
	public void setMinLat(double lat) {
		this.minLat = lat;
	}
	
	public double getMaxLng() {
		return this.maxLng;
	}
	
	public void setMaxLng(double lng) {
		this.maxLng = lng;
	}
	
	public double getMinLng() {
		return this.minLng;
	}
	
	public void setMinLng(double lng) {
		this.minLng = lng;
	}
	
	
	
	public void setSquareDimension(List<Venue> venues) {
		Venue v;
		v = venues.get(0);
		
		double maxLat = Double.valueOf(v.getLatitude());
		double minLat = maxLat;
		double maxLng = Double.valueOf(v.getLongitude());
		double minLng = maxLng;
		
		double lat, lng;
		for (int i=1; i<venues.size(); i++) {
			v = venues.get(i);
			lat = Double.valueOf(v.getLatitude());
			lng = Double.valueOf(v.getLongitude());
			if (lat > maxLat)
				maxLat = lat;
			if (lat < minLat)
				minLat = lat;
			if (lng > maxLng)
				maxLng = lng;
			if (lng < minLng)
				minLng = lng;
		}
		
		this.setMaxLat(maxLat+0.015);
		this.setMinLat(minLat-0.015);
		this.setMaxLng(maxLng+0.02);
		this.setMinLng(minLng-0.02);		
	}

}