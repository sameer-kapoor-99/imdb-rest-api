package imdb;

import org.springframework.data.mongodb.repository.MongoRepository;

//Cast repository to map imdb's nconst id to actual cast member name
public interface CastRepository extends MongoRepository<Cast, String> {
	public Cast findBynconst(String nconst);
}
