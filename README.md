# imdb-rest-api
Spring framework project using mongodb and rest apis to look up movie titles, cast and ratings from imdb

INSTRUCTIONS:
1) Compiling and creating a jar file:
Gradle:

./gradlew build

Maven:

./mvn clean package

The above should create a jar file called imdb-mongodb-data-rest-0.1.0.jar

2) Install MongoDB and dependencies
https://docs.mongodb.com/manual/administration/install-on-linux/

3) Configure aws credentials to download Imdb S3 data:
http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html

4) Run the jar file:
java -jar imdb-mongodb-data-rest-0.1.0.jar

A Tomcat servlet will now start and listen on port 8080 for http requests

FEATURES:
The application contains 2 main APIs
1) populateDB
	This API can be invoked to automatically download Imdb S3 data using your aws credentials. This will then parse these files and populate mongodb
2) findByMovieIgnoreCase
	This is the main REST api which can be used to search for a movie. This will return a JSON document containing the movie title, cast list and imdb rating

eg.
http://localhost:8080/movie/search/findByMovieIgnoreCase?movie=children+of+men
{
  "_embedded" : {
    "movie" : [ {
      "imdbId" : "tt0206634",
      "movie" : "Children of Men",
      "rating" : 7.9,
      "cast" : [ "Timothy J. Sexton", "Alfonso Cuarón", "Julianne Moore", "Mark Fergus", "Chiwetel Ejiofor", "Hawk Ostby", "Clive Owen", "David Arata", "Michael Caine", "P.D. James" ],
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/movie/596eb0f9584eeea49b8112e7"
        },
        "imdb" : {
          "href" : "http://localhost:8080/movie/596eb0f9584eeea49b8112e7"
        }
      }
    } ]
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/movie/search/findByMovieIgnoreCase?movie=children+of+men"
    }
  }
}


