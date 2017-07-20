package imdb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
//Cast class containing mapping of nconst id to cast member name
//This class is stored as a document in the database
@Document
public class Cast {
	@Id private String id;
	@Indexed(unique=true)
	private String nconst;
	private String primaryName;
	
	public Cast() {};
	
	public Cast(String imdbId, String name) {
		this.nconst = imdbId;
		this.primaryName = name;
	}
	
	


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNconst() {
		return nconst;
	}

	public void setNconst(String nconst) {
		this.nconst = nconst;
	}

	public String getPrimaryName() {
		return primaryName;
	}

	public void setPrimaryName(String primaryName) {
		this.primaryName = primaryName;
	}

	@Override
    public String toString() {
        return String.format(
                "Cast[id=%s, imdbID='%s', Name='%s']",
                id, nconst, primaryName);
    }
}
