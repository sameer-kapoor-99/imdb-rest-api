package imdb;

import org.springframework.data.annotation.Id;

public class Cast {
	@Id public String id;
	
	public String nconst;
	public String primaryName;
	
	public Cast() {};
	
	public Cast(String imdbId, String name) {
		this.nconst = imdbId;
		this.primaryName = name;
	}
	
    @Override
    public String toString() {
        return String.format(
                "Cast[id=%s, imdbID='%s', Name='%s']",
                id, nconst, primaryName);
    }
}
