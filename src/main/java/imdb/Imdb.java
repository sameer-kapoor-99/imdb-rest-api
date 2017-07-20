package imdb;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
//Imdb class containing movie name, imdb id, cast list and imdb rating
//This class is stored as a document in the database
@Document
public class Imdb {
	@Id private String id;
	
	@Indexed(unique=true)
	private String imdbId;
	
	@Indexed
	private String movie;
	private double rating;
	private List<String> cast;
	public String getImdbId() {
		return imdbId;
	}
	public void setImdbId(String imdbId) {
		this.imdbId = imdbId;
	}
	public String getMovie() {
		return movie;
	}
	public void setMovie(String movie) {
		this.movie = movie;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public List<String> getCast() {
		return cast;
	}
	public void setCast(List<String> cast) {
		this.cast = cast;
	}
	@Override
	public String toString() {
		return "Imdb [id=" + id + ", imdbId=" + imdbId + ", movie=" + movie + ", rating=" + rating + ", cast=" + cast
				+ "]";
	}
	
	
	

}
