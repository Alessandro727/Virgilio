package model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Scenario {
	
	private int id;
	private int user_id;
	private Context context;
	private String day;
	private String hour;
	private boolean food;
	
	public Scenario(int user_id, Context context) {
		this.user_id = user_id;
		this.context = context;
		DateFormat hour = new SimpleDateFormat("HH:mm");
		DateFormat date = new SimpleDateFormat("dd/MM/yy");
		DateFormat dayOfWeek = new SimpleDateFormat("E");
		Date currentDate = new Date();
		if (this.context.getCity().equals("New York"))
			currentDate = new Date(currentDate.getTime() - (1000 * 60 * 60 * 6));
		if (this.context.getCity().equals("London"))
			currentDate = new Date(currentDate.getTime() - (1000 * 60 * 60 * 1));
		this.hour = hour.format(currentDate);
		switch (dayOfWeek.format(currentDate)) {
			case "lun": this.day = "Luned� "; break;
			case "mar": this.day = "Marted� "; break;
			case "mer": this.day = "Mercoled� "; break;
			case "gio": this.day = "Gioved� "; break;
			case "ven": this.day = "Venerd� "; break;
			case "sab": this.day = "Sabato "; break;
			default: this.day = "Domenica "; break;
		}
		this.day += date.format(currentDate);
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

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public boolean getFood() {
		return food;
	}

	public void setFood(boolean food) {
		this.food = food;
	}

}
