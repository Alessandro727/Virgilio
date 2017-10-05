package model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Checkin {
	
	private int id;
	private int user_id;
	private int location_id;
	private Date date;
	private String dateAsString;
	private int venue_id;
	private int checkin_id;
	private DateFormat df;
	private Venue venue;
	
	public Checkin() {
		this.df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getLocation_id() {
		return location_id;
	}

	public void setLocation_id(int location_id) {
		this.location_id = location_id;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(String date) {
		try {
			this.date = df.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getVenue_id() {
		return venue_id;
	}

	public void setVenue_id(int venue_id) {
		this.venue_id = venue_id;
	}
	
	public int getCheckin_id() {
		return checkin_id;
	}

	public void setCheckin_id(int checkin_id) {
		this.checkin_id = checkin_id;
	}

	public String getDateAsString() {
		return dateAsString;
	}

	public void setDateAsString(String dateAsString) {
		this.dateAsString = dateAsString;
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}
	
	
}