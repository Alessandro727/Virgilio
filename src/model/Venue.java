package model;

import java.util.ArrayList;
import java.util.List;

public class Venue {
	
	private long id;
	private String latitude;
	private String longitude;
	//private int checkinsNumber;
	private String name_fq;
	private String category_fq;
	private String openHours;
	private String status;
	private MacroCategory macro_category;
	private String foursquare_id;
	private String mediaUrl;
	private String creator;
	private String why;
	private String provider;
	private String source;
	private String externalLink;
	private String description;
	private List<Checkin> checkins;
	
	
	public Venue() {
		this.checkins = new ArrayList<Checkin>();
	}
	
	public Long getId() {
		return this.id;
	}
	
	public void setId(long i) {
		this.id = i;
	}
	
	public String getLatitude() {
		return this.latitude;
	}
	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public String getLongitude() {
		return this.longitude;
	}
	
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public String getName_fq() {
		return this.name_fq;
	}
	
	public void setName_fq(String name_fq) {
		this.name_fq = name_fq;
	}
	
	public String getCategory_fq() {
		return this.category_fq;
	}
	
	public void setCategory_fq(String category_fq) {
		this.category_fq = category_fq;
	}
	
	public String getStatus() {
		return this.status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

	public int getCheckinsNumber() {
		return this.checkins.size();
	}	

	public MacroCategory getMacro_category() {
		return macro_category;
	}

	public void setMacro_category(MacroCategory macro_category) {
		this.macro_category = macro_category;
	}

	public String getFoursquare_id() {
		return foursquare_id;
	}

	public void setFoursquare_id(String foursquare_id) {
		this.foursquare_id = foursquare_id;
	}

	public List<Checkin> getCheckins() {
		return this.checkins;
	}

	public void setCheckins(List<Checkin> checkins) {
		this.checkins = checkins;
	}
	
	public void addCheckin(Checkin c) {
		this.checkins.add(c);
	}

	public String getMediaUrl() {
		return mediaUrl;
	}

	public void setMediaUrl(String mediaUrl) {
		this.mediaUrl = mediaUrl;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getWhy() {
		return why;
	}

	public void setWhy(String why) {
		this.why = why;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getExternalLink() {
		return externalLink;
	}

	public void setExternalLink(String externalLink) {
		this.externalLink = externalLink;
	}

	public String getMuseumDescription() {
		return description;
	}

	public void setMuseumDescription(String museumDescription) {
		this.description = museumDescription;
	}
	
	public String toString()	{
		return "Venue: "+getName_fq()+", "+getCategory_fq()+", "+getCheckinsNumber()+", "+getStatus()+", "+getMacro_category();
		
	}

	public String getOpenHours() {
		return openHours;
	}

	public void setOpenHours(String openHours) {
		this.openHours = openHours;
	}
	
}