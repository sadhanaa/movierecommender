package models;


public class RecommendedItemWrapper {
	private float itemValue;
	private Movie movie;
	
	public RecommendedItemWrapper(float itemValue, Movie movie) {
		this.itemValue = itemValue;
		this.movie = movie;
	}

	public float getItemValue() {
		return itemValue;
	}
	public Movie getMovie() {
		return movie;
	}
}
