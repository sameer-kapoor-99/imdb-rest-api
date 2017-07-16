package imdb;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CastRepository extends MongoRepository<Cast, String> {
	public Cast findBynconst(String nconst);
}
