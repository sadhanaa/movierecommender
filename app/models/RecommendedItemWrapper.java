package models;


public class RecommendedItemWrapper implements Comparable<RecommendedItemWrapper>{
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

	@Override
	public int compareTo(RecommendedItemWrapper o) {
		if(this.itemValue < o.getItemValue()) {
			return 1;
		} else if(this.itemValue < o.getItemValue()) {
			return -1;
		}
		return 0;
	}
}
