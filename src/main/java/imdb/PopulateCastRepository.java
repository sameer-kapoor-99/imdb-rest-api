package imdb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


@SpringBootApplication
public class PopulateCastRepository implements CommandLineRunner {
	
//	@Autowired
//	private CastRepository castRepo;
	
	@Autowired
	private MovieRepository movieRepo;
	
	@Autowired
	private MongoOperations mongoOperations;
	
	private Map<String, String> castMap;
	private Map<String, String> titleMap;
	private Map<String, List<String>> movieCastMap;
	private Map<String, Double> movieRatingMap;


	public static void main(String[] args) {
		SpringApplication.run(PopulateCastRepository.class, args);
	}
	
	public void run(String...args) throws Exception {
//		castRepo.deleteAll();
		movieRepo.deleteAll();
//		String line;
//		String command = "mongoimport --db test --collection cast --type tsv --fields nconst,primaryName --file /home/skapoor/Downloads/name.basics.tsv";
//		Runtime r = Runtime.getRuntime();
//		Process p = null;
		
		buildCastMap("/home/skapoor/Downloads/name.basics.tsv.gz");
		buildTitleMap("/home/skapoor/Downloads/title.basics.tsv.gz");
		buildMovieCastMap("/home/skapoor/Downloads/title.principals.tsv.gz");
		buildRatingMap("/home/skapoor/Downloads/title.ratings.tsv.gz");		

		buildMovieDatabase();
		System.out.println("Finished building movie database");
//		System.out.println(castRepo.findBynconst("nm0374658"));
		System.out.println(movieRepo.findByMovie("Big"));
		
		//System.out.println(lineList.get(0));

	}
	
	public void buildMovieDatabase() {
		System.out.println("Building Movie Database");
		
		Imdb movie;
		
		for(String id : titleMap.keySet()) {
			movie = new Imdb();
			movie.setImdbId(id);
			movie.setMovie(titleMap.get(id));
			movie.setCast(movieCastMap.get(id));
			

			if(movieRatingMap.containsKey(id))
				movie.setRating(movieRatingMap.get(id));
			else
				movie.setRating(0.0);
			movieRepo.save(movie);
		}
	}
	
	public void buildCastMap(String csvFile) {
		castMap = new HashMap<>();
		String line;
		try{
			
			//System.out.println(csvFile);
			
			//p = r.exec(command);
			File inputF = new File("/home/skapoor/Downloads/name.basics.tsv.gz");
			InputStream inputFS = new FileInputStream(inputF);
			InputStream gzipStream = new GZIPInputStream(inputFS);
			BufferedReader br = new BufferedReader(new InputStreamReader(gzipStream));
			
			br.skip(1);
			System.out.println("Mapping cast ids to names");
			
			while((line = br.readLine())!=null) {
				String[] s = line.split("\t");

				castMap.put(s[0], s[1]);
			}			
			
			br.close();
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void buildTitleMap(String titleFile) {
		String line;
		titleMap = new HashMap<>();
		
		try{
			File inputF = new File(titleFile);
			InputStream inputFS = new FileInputStream(inputF);
			InputStream gzipStream = new GZIPInputStream(inputFS);
			BufferedReader br = new BufferedReader(new InputStreamReader(gzipStream));
			
			br.readLine();
			System.out.println("Reading in Movie Titles");
			
			while((line = br.readLine())!=null) {
				String[] s = line.split("\t");
				titleMap.put(s[0], s[2]);
			}			
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
	    }
	}
	
	public void buildMovieCastMap(String principalFile) {
		String line;
		movieCastMap = new HashMap<>();
		
		try{
			File inputF = new File(principalFile);
			InputStream inputFS = new FileInputStream(inputF);
			InputStream gzipStream = new GZIPInputStream(inputFS);
			BufferedReader br = new BufferedReader(new InputStreamReader(gzipStream));
			
			br.readLine();
			
			List<String> cast;
			System.out.println("Mapping movie cast lists");
//			Query query;
			while((line = br.readLine())!=null) {
//				query = new Query();
				cast  = new ArrayList<>();
				String[] s = line.split("\t");

//				Imdb movie = new Imdb();
//				query.addCriteria(Criteria.where("imdbId").is(s[0]));
//				movie = mongoOperations.findOne(query, Imdb.class,"imdb");
				
				for(String nconst : s[1].split(",")) {

					String person = castMap.get(nconst);
					if (person != null) {
						cast.add(person);
					} else {
						System.out.println(nconst + " not found");
					}

				}
				
				movieCastMap.put(s[0], cast);
			}			
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
	    }
	}
	
	public void buildRatingMap(String ratingFile) {
		String line;
		movieRatingMap = new HashMap<>();
		
		try{
			File inputF = new File("/home/skapoor/Downloads/title.ratings.tsv.gz");
			InputStream inputFS = new FileInputStream(inputF);
			InputStream gzipStream = new GZIPInputStream(inputFS);
			BufferedReader br = new BufferedReader(new InputStreamReader(gzipStream));
			
			br.readLine();
			
//			Query query;
			
			System.out.println("Mapping movies to their ratings");
			while((line = br.readLine())!=null) {
				String[] s = line.split("\t");
				
//				query = new Query();
//				query.addCriteria(Criteria.where("imdbId").is(s[0]));
//				
//
//				Imdb movie = mongoOperations.findOne(query, Imdb.class,"imdb");
//				movie.setRating(Double.parseDouble(s[0]));
//				movieRepo.save(movie);
				
				movieRatingMap.put(s[0], Double.parseDouble(s[1]));
			}			
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
	    }
	}

	
	

}
