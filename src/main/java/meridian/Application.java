package main.java.meridian;
import java.util.HashSet;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import main.java.meridian.Feed;
import main.java.meridian.FeedParser;
import main.java.meridian.FirebaseWrapper;
import main.java.meridian.GeoParser;
import main.java.meridian.Json;
import main.java.meridian.MapInfo;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
    	SpringApplication.run(Application.class, args);
    	
    	
    	FirebaseWrapper firebase = new FirebaseWrapper("https://wherethingshappened.firebaseio.com/locations");
    	FeedParser parser = new FeedParser("http://feeds.bbci.co.uk/news/rss.xml?edition=int");
    	Feed feed = parser.readFeed();
    	System.out.println(feed);
        for (FeedMessage message : feed.getMessages()) {
        	HashSet<String> parseResults = GeoParser.parse(message.getTitle(), message.getDescription());
        	if(!parseResults.isEmpty()){
        		MapInfo info = new MapInfo(parseResults, message.getTitle(), message.getDescription());
        		HashSet<Json> jsons = info.toJSON();
        		for(Json json : jsons){
        			firebase.post(json.key, json.data);	
        		}
        	}
        }
        
    }
}
