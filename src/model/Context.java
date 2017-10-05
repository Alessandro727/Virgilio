package model;

public class Context {
	
	private int id;
	private String meteo;
	private String mode;
	private String city;
	private String start;
	private String end;
	private int time;
	private boolean sunny;
	
	public Context() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMeteo() {
		return meteo;
	}

	public void setMeteo(String meteo) {
		this.meteo = meteo;
		
		switch (meteo) {
			case "sereno": this.sunny = true; break;
			case "sereno o poco nuovloso": this.sunny = true; break;
			case "nuovloso": this.sunny = true; break;
			case "piovoso": this.sunny = false; break;
			case "brutto, piove": this.sunny = false; break;
			default: this.sunny = true; break;
		}
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
	
	public boolean getSunny() {
		return this.sunny;
	}
	
	public void setSunny(boolean sunny) {
		this.sunny = sunny;
	}

}
