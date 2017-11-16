package model;

import java.util.ArrayList;
import java.util.List;

public class Book extends Object{
	
	private long id;
	private String latitude;
	private String longitude;
	private String name;
	private List<String> genres;
	private String creator;
	private String externalLink;
	private int linkCount;
	private String image;
	private int popularity;
	private String ISBN;
	
	
	public Book(int popularity)	{
		super(popularity);
		this.genres = new ArrayList<>();
		
	}
	
//	public Book(long id, String latitude, String longitude, String name)	{
//		this.id = id;
//		this.latitude = latitude;
//		this.longitude = longitude;
//		this.name = name;
//		
//	}


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


	public List<String> getGenres() {
		return this.genres;
	}

	public void setGenres(List<String> genres) {
		this.genres = genres;
	}


	public String getCreator() {
		return creator;
	}


	public void setCreator(String creator) {
		this.creator = creator;
	}


	public String getExternalLink() {
		return externalLink;
	}


	public void setExternalLink(String externalLink) {
		this.externalLink = externalLink;
	}

	public int getLinkCount() {
		return linkCount;
	}

	public void setLinkCount(int linkCount) {
		this.linkCount = linkCount;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getPopularity() {
		return popularity;
	}

	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}

	public String getISBN() {
		return ISBN;
	}

	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}

}
