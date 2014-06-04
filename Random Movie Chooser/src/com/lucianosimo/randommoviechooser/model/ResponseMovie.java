package com.lucianosimo.randommoviechooser.model;

import com.google.gson.annotations.SerializedName;

public class ResponseMovie {

	@SerializedName("Title")
	private String title;
	
	@SerializedName("Year")
	private String year;
	
	@SerializedName("Rated")
	private String rated;
	
	@SerializedName("Released")
	private String released;
	
	@SerializedName("Runtime")
	private String runtime;
	
	@SerializedName("Genre")
	private String genre;
	
	@SerializedName("Director")
	private String director;
	
	@SerializedName("Actors")
	private String actors;
	
	@SerializedName("Plot")
	private String plot;
	
	@SerializedName("Poster")
	private String poster;
	
	@SerializedName("imdbRating")
	private double imdbRating;
	
	@SerializedName("Type")
	private String type;
	
	@SerializedName("imdbID")
	private String imdbID;

	public String getTitle() {
		return title;
	}

	public String getYear() {
		return year;
	}

	public String getRated() {
		return rated;
	}

	public String getReleased() {
		return released;
	}

	public String getRuntime() {
		return runtime;
	}

	public String getGenre() {
		return genre;
	}

	public String getDirector() {
		return director;
	}

	public String getActors() {
		return actors;
	}

	public String getPlot() {
		return plot;
	}

	public double getImdbRating() {
		return imdbRating;
	}
	
	public String getPoster() {
		return poster;
	}
	
	public String getType() {
		return type;
	}	
	
	public String getImdbID() {
		return imdbID;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setRated(String rated) {
		this.rated = rated;
	}

	public void setReleased(String released) {
		this.released = released;
	}

	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public void setActors(String actors) {
		this.actors = actors;
	}

	public void setPlot(String plot) {
		this.plot = plot;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public void setImdbRating(double imdbRating) {
		this.imdbRating = imdbRating;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setImdbID(String imdbID) {
		this.imdbID = imdbID;
	}
	
	
}
