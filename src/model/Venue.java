package model;

import java.util.ArrayList;
import java.util.List;

public class Venue {
	
	private int id;
	private String latitude;
	private String longitude;
	//private int checkinsNumber;
	private String name_fq;
	private String category_fq;
	private String status;
	private MacroCategory macro_category;
	private String foursquare_id;
	private String link_fq;
	private String mediaUrl;
	private String creator;
	private String why;
	private String provider;
	private String source;
	private String externalLink;
	private String museumDescription;
	private List<Checkin> checkins;
	
	
	public Venue() {
		this.checkins = new ArrayList<Checkin>();
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
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

	public String getLink_fq() {
		return link_fq;
	}

	public void setLink_fq(String link_fq) {
		this.link_fq = link_fq;
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
		return museumDescription;
	}

	public void setMuseumDescription(String museumDescription) {
		this.museumDescription = museumDescription;
	}
	
	public String toString()	{
		return "Venue: "+getName_fq()+", "+getCategory_fq()+", "+getCheckinsNumber()+", "+getStatus()+", "+getMacro_category();
		
	}
	
}