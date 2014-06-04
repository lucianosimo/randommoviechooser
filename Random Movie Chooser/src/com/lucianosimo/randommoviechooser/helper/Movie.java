package com.lucianosimo.randommoviechooser.helper;

public class Movie {

	private Integer id;
	private String title;
	private Integer seen;
	
	public Movie (Integer id, String title, Integer seen) {
		this.id = id;
		this.title = title;
		this.seen = seen;
	}
	
	public Movie () {
		this.id = 0;
		this.title = null;
		this.seen = 0;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public Integer getSeen() {
		return this.seen;
	}
}
