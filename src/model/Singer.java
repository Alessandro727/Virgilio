package model;

import java.util.ArrayList;
import java.util.List;

public class Singer {

	private long id;
	private String latitude;
	private String longitude;
	private String name;
	private List<String> song;
	private String externalLink;
	private int playCount;
	private String image;
	private List<String> genres;
	
	public Singer()	{
		this.song = new ArrayList<>();
		this.setGenres(new ArrayList<>());
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getSong() {
		return song;
	}

	public void setSong(List<String> song) {
		this.song = song;
	}

	public String getExternalLink() {
		return externalLink;
	}

	public void setExternalLink(String externalLink) {
		this.externalLink = externalLink;
	}

	public int getPlayCount() {
		return playCount;
	}

	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<String> getGenres() {
		return genres;
	}

	public void setGenres(List<String> genres) {
		this.genres = genres;
	}


	
}
