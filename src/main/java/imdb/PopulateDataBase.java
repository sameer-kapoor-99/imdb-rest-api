package imdb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

//Rest controller to that pulls data from Imdb's S3 storage and populates the MovieRepository
@RestController
public class PopulateDataBase {
	
	@Autowired
	private CastRepository castRepo;
	
	@Autowired
	private MovieRepository movieRepo;
	
	@Autowired
	private MongoOperations mongoOperations;
	
	@RequestMapping("/populateDB")
	public void run(String...args) throws Exception {
		castRepo.deleteAll();
		movieRepo.deleteAll();
		//mongoTemplate.getDb().dropDatabase();
		

		message("Downloading Imdb S3 files");
		downloadS3Files();
		
		buildCastMap("name.basics.tsv.gz");
		buildTitleMap("title.basics.tsv.gz");
		buildMovieCastMap("title.principals.tsv.gz");
		buildRatingMap("title.ratings.tsv.gz");		

		System.out.println("Finished building movie database");
		

	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String message(String msg) {
		return msg;
	}

	//Method to download required data from Imdb's S3 storage
	public void downloadS3Files() throws IOException {
        AmazonS3 s3 = new AmazonS3Client();

        String bucketName = "imdb-datasets";
        
        String[] keyNames = {"name.basics.tsv.gz", "title.basics.tsv.gz", "title.principals.tsv.gz", "title.ratings.tsv.gz"};
        
		for (String keyName : keyNames) {

			System.out.println("Downloading : " + keyName);
			try {
				S3Object o = s3.getObject(new GetObjectRequest(bucketName, "documents/v1/current/" + keyName, true));
				S3ObjectInputStream s3is = o.getObjectContent();
				FileOutputStream fos = new FileOutputStream(new File(keyName));
				byte[] read_buf = new byte[1024];
				int read_len = 0;
				while ((read_len = s3is.read(read_buf)) > 0) {
					fos.write(read_buf, 0, read_len);
				}
				s3is.close();
				fos.close();
			} catch (AmazonServiceException e) {
				System.err.println(e.getErrorMessage());
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
        }
	}
	
	//Method to store the nconst<->cast member name map
	public void buildCastMap(String csvFile) {

		try{
			String command = "mongoimport --db test --collection cast --type tsv --fields nconst,primaryName --file name.basics.tsv";
			Runtime r = Runtime.getRuntime();
			r.exec("gunzip name.basics.tsv.gz");
			
			r.exec(command);

			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Method to create the imdbId<->movie title map
	public void buildTitleMap(String titleFile) {
		String line;
		
		try{
			File inputF = new File(titleFile);
			InputStream inputFS = new FileInputStream(inputF);
			InputStream gzipStream = new GZIPInputStream(inputFS);
			BufferedReader br = new BufferedReader(new InputStreamReader(gzipStream));
			
			br.readLine();
			System.out.println("Reading in Movie Titles");
			
			while((line = br.readLine())!=null) {
				Imdb movie = new Imdb();
				
				String[] s = line.split("\t");
				if(s[1].equals("movie")){
					movie.setImdbId(s[0]);
					movie.setMovie(s[2]);
					movieRepo.save(movie);
				}
				
			}			
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
	    }
	}
	
	//Method to build the imdbId<->cast list map
	//This method pull data from the cast repository to build the cast list
	public void buildMovieCastMap(String principalFile) {
		String line;
		
		try{
			File inputF = new File(principalFile);
			InputStream inputFS = new FileInputStream(inputF);
			InputStream gzipStream = new GZIPInputStream(inputFS);
			BufferedReader br = new BufferedReader(new InputStreamReader(gzipStream));
			
			br.readLine();
			
			List<String> cast;
			System.out.println("Mapping movie cast lists");
			Query query;
			while((line = br.readLine())!=null) {
				query = new Query();
				cast  = new ArrayList<>();
				String[] s = line.split("\t");

				Imdb movie = new Imdb();
				query.addCriteria(Criteria.where("imdbId").is(s[0]));
				movie = mongoOperations.findOne(query, Imdb.class,"imdb");
				
				if (movie != null) {

					for (String nconst : s[1].split(",")) {
						
						Cast person = new Cast();
						query = new Query();
						query.addCriteria(Criteria.where("nconst").is(nconst));
						person = mongoOperations.findOne(query, Cast.class, "cast");

						if (person != null) {
							cast.add(person.getPrimaryName());
						}
					}

					movie.setCast(cast);
					movieRepo.save(movie);
				}
			}			
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
	    }
	}
	
	//Method to build the imdbId<->movie rating map
	public void buildRatingMap(String ratingFile) {
		String line;
		
		try{
			File inputF = new File(ratingFile);
			InputStream inputFS = new FileInputStream(inputF);
			InputStream gzipStream = new GZIPInputStream(inputFS);
			BufferedReader br = new BufferedReader(new InputStreamReader(gzipStream));
			
			br.readLine();
			
			Query query;
			
			System.out.println("Mapping movies to their ratings");
			while((line = br.readLine())!=null) {
				String[] s = line.split("\t");
				
				query = new Query();
				query.addCriteria(Criteria.where("imdbId").is(s[0]));				

				Imdb movie = mongoOperations.findOne(query, Imdb.class,"imdb");
				
				if(movie != null) {
					movie.setRating(Double.parseDouble(s[1]));
					movieRepo.save(movie);
				}
			}			
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
	    }
	}

	
	

}
