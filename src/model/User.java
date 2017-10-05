package model;

import java.util.ArrayList;
import java.util.List;

public class User {
	
	private int id;
	private String username;
	private String password;
	private String gender;
	private int age;
	private String role;
	private List<Integer> friends;
	private List<Checkin> checkins;
	private double[] weigths;
	
	public User() {
		this.checkins = new ArrayList<Checkin>();
		this.setFriends(new ArrayList<Integer>());
		this.weigths = new double[11];	// 10 + 1 cos� che non devo cominciare da zero		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<Integer> getFriends() {
		return friends;
	}

	public void setFriends(List<Integer> friends) {
		this.friends = friends;
	}
	
	public void addFriend(int friend) {
		this.friends.add(friend);
	}

	public List<Checkin> getCheckins() {
		return checkins;
	}

	public void setCheckins(List<Checkin> checkins) {
		this.checkins = checkins;
	}
	
	public void addCheckin(Checkin c) {
		this.checkins.add(c);
	}

	public double[] getWeigths() {
		return weigths;
	}

	public void setWeigths(double[] weigths) {
		this.weigths = weigths;
	}
	
	/**
	 * @param i	l'indice del peso che si vuole ottenere
	 * @return	il peso della categoria con id pari ad i
	 */
	public double getWeigth(int i) {
		return this.weigths[i];
	}
	
	public void setWeight(int i, double d) {
		if (i < this.weigths.length)
			this.weigths[i] = d;
	}

}