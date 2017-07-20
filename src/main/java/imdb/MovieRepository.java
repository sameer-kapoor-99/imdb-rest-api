package imdb;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
//Imdb class repository
//The repository uses Spring rest api for queries
@RepositoryRestResource(collectionResourceRel = "movie", path = "movie")
public interface MovieRepository extends MongoRepository<Imdb, String>{

	List<Imdb> findByMovieIgnoreCase(@Param("movie") String movie);
}
