package logic;

import java.util.List;

import model.Scenario;
import model.Venue;
import postgres.CheckinPostgres;
import postgres.PersistenceException;
import postgres.VenuePostgres;
import util.GeoLocation;
import util.Utilities;

public class VenueSearcher {
	
	private LatLngSquare llSquare;
	private Scenario scenario;
	
	public VenueSearcher(LatLngSquare llSquare, Scenario scenario) {
		this.llSquare = llSquare;
		this.scenario = scenario;
	}
	
	
	/**
	 * 
	 * @return	tutti i venue nel quadrato, esclusi quelli della categoria Food.
	 * @throws PersistenceException
	 */
	public List<Venue> getAllVenuesInTheSquare() {
		List<Venue> venues = null;
		try {
			venues = VenuePostgres.retrieveVenuesBySquareLimits(this.llSquare);
		} catch (PersistenceException e) {
			e.printStackTrace();
		}		
		return venues;		
	}
	
	
	/**
	 * 
	 * @return	tutti i venue nel quadrato, esclusi quelli della categoria Food.
	 * @throws PersistenceException
	 */
	public List<Venue> getAllVenuesInTheSquareWithoutFood() {
		List<Venue> venues = null;
		try {
			venues = VenuePostgres.retrieveVenuesBySquareLimitsAndCategories(this.llSquare, " mc.id != 5 and c.id != 354 and c.id != 282 and c.id != 283 and c.id != 505 ");
		} catch (PersistenceException e) {
			e.printStackTrace();
		}		
		return venues;		
	}
	
	
	
	/**
	 * 
	 * @return	tutti i venue nel quadrato, esclusi quelli della categoria Food e NightLife.
	 * @throws PersistenceException
	 */
	public List<Venue> getAllVenuesInTheSquareWithoutFoodAndNightLife() {
		List<Venue> venues = null;
		try {
			venues = VenuePostgres.retrieveVenuesBySquareLimitsAndCategories(this.llSquare, " mc.id != 5 and mc.id != 6 ");
		} catch (PersistenceException e) {
			e.printStackTrace();
		}		
		return venues;		
	}
	
	
	
	/**
	 * 
	 * @return	il venue appartenente alla categoria Food che si trova pi� vicino al centro del quadrato.
	 * @throws PersistenceException
	 */
	public Venue getOneFoodInTheCenterWithoutContext() {
		List<Venue> venues = null;
		Venue venue = null;
		try {
			venues = VenuePostgres.retrieveVenuesBySquareLimitsAndCategories(this.llSquare, " mc.id = 5 ");
			if (venues == null)
				return null;
			// else
			String[] center = GeoLocation.getHalfwayPointBetweenTwoPoints(llSquare.getMinLat(), llSquare.getMinLng(), llSquare.getMaxLat(), llSquare.getMaxLng()).split(",");
			venue = GeoLocation.getMostNearAndPopularVenueWithoutContext(center[0], center[1], venues);
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
		
		return venue;
	}
	
	
	
	public Venue getOneFoodInTheCenterWithContext() {
		List<Venue> venues = null;
		Venue venue = null;
		try {
			venues = VenuePostgres.retrieveVenuesBySquareLimitsAndCategories(this.llSquare, " mc.id = 5 ");
			if (venues == null)
				return null;
			// else
			
			venues = Utilities.getFirst_N_ElementFromMap(CheckinPostgres.getCheckinsNumbersByVenueId(venues), venues.size());
			String[] center = GeoLocation.getHalfwayPointBetweenTwoPoints(llSquare.getMinLat(), llSquare.getMinLng(), llSquare.getMaxLat(), llSquare.getMaxLng()).split(",");
			venue = GeoLocation.getMostNearAndPopularVenueWithContext(center[0], center[1], venues);
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
		
		return venue;
	}
	
	
	
	/**
	 * 
	 * @param n 
	 * @return i primi n venue comprese tutte le categorie (di Food c'� al massimo solo un venue) 
	 */
	public List<Venue> getVenuesWithoutContext(int n) {
		List<Venue> venues = null;
		
		try {
			venues = getAllVenuesInTheSquareWithoutFood();
			
			if (venues == null)
				return null;
			
			venues = Utilities.getFirst_N_ElementFromMap(CheckinPostgres.getCheckinsNumbersByVenueId(venues), n);
			
			/*if (this.scenario.getFood()) {
				Venue foodVenue = getOneFoodInTheCenterWithoutContext();
				if (foodVenue != null)
					venues.add(foodVenue);
			}*/
			Venue foodVenue = getOneFoodInTheCenterWithoutContext();
			if (foodVenue != null)
				venues.add(foodVenue);
			
		} catch (PersistenceException e) {
			e.printStackTrace();
		}	
		
		return venues;
	}
	
	
	
	/**
	 * 
	 * @param n 
	 * @return i primi n venue. A seconda del contesto vengono esclude le categorie NightLife (tutta o niente) e Food (uno o niente) 
	 */
	public List<Venue> getVenuesWithContext(int n) {
		List<Venue> venues = null;
		
		try {
			int ore = Integer.parseInt(this.scenario.getHour().substring(0, 2));
			if (ore > 05 && ore < 20)
				venues = getAllVenuesInTheSquareWithoutFoodAndNightLife();
			else 
				venues = getAllVenuesInTheSquareWithoutFood();
			
			if (venues == null)
				return null;
			
			Filter filter = new Filter(this.scenario.getContext());
			venues = filter.filterVenues(venues);
			
			venues = Utilities.getFirst_N_ElementFromMap(CheckinPostgres.getCheckinsNumbersByVenueId(venues), n);
			
			if (this.scenario.getFood()) {
				Venue foodVenue = getOneFoodInTheCenterWithContext();
				if (foodVenue != null)
					venues.add(foodVenue);
			}
			
			
		} catch (PersistenceException e) {
			e.printStackTrace();
		}	
		
		return venues;
	}
	
	
	
	/**
	 * 
	 * @param n 
	 * @return i primi n venue, con le categorie indicate
	 */
	public List<Venue> getVenuesWithContextAndCategories(int n, String[] categories) {
		if (categories == null)
			return getVenuesWithContext(n);
		
		

		String cats = " (mc.id = " + categories[0];
		for(int i=1; i<categories.length; i++)
			cats += " or mc.id = " + categories[i];
		cats += ") ";
		if (!Utilities.arrayContainsString(categories, "5"))
			cats += " and mc.id != 5 ";	//remove food
		
		List<Venue> venues = null;
		
		try {
			venues = VenuePostgres.retrieveVenuesBySquareLimitsAndCategories(this.llSquare, cats);
			
			if (venues == null)
				return null;
			
			venues = Utilities.getFirst_N_ElementFromMap(CheckinPostgres.getCheckinsNumbersByVenueId(venues), n);
			
									
			Filter filter = new Filter(this.scenario.getContext());
			venues = filter.filterVenues(venues);
			
			
		} catch (PersistenceException e) {
			e.printStackTrace();
		}	
		
		return venues;
	}

}
