package controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Movie;
import models.RecommendedItemWrapper;
import models.User;
import movierecommender.RecommendationEngine;
import movierecommender.RecommendationEngine.RecommendationType;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {
	private static final String USER_ID = "userId";
	private static Map<String, Movie> movieMap = null;
	private static Map<Long, User> userMap = null;
	private static final long MAX_USER_ID_FOR_RECOMMENDATIONS = 943L;
	
	private static final class MovieFileParser {
		public static void parse(File file) throws IOException {
			Reader in = new FileReader(file);
			Iterable<CSVRecord> parser = CSVFormat.newBuilder().withDelimiter('|').parse(in);

			movieMap = new HashMap<String, Movie>();
			 for(CSVRecord r : parser) {
				 movieMap.put(r.get(0), new Movie(r.get(0), r.get(1), r.get(4)));
			 }
		}
	}
	
	private static final class UserFileParser {
		public static void parse(File file) throws IOException {
			Reader in = new FileReader(file);
			Iterable<CSVRecord> parser = CSVFormat.newBuilder().withDelimiter('|').parse(in);
			
			userMap = new LinkedHashMap<Long, User>();
			 for(CSVRecord r : parser) {
				 userMap.put(Long.parseLong(r.get(0)), new User(Long.parseLong(r.get(0)), Integer.parseInt(r.get(1)), r.get(2)));
			 }
		}
	}
	
	private static boolean checkMovieMapAndUserSession() throws IOException {
		if(movieMap == null) {
			doParseMovies();
		}
		return checkUserInSession();
	}

	private static boolean checkUserInSession() {
		// check user session
		if(userMap == null || !userMap.containsKey(Long.parseLong(session(USER_ID)))) {
    		return false;
    	}
		return true;
	}
	
	private static boolean checkUserExists(String userId) {
		// check user session
		if(userMap == null || !userMap.containsKey(Long.parseLong(userId))) {
    		return false;
    	}
		return true;
	}
	
	private static boolean isNewUser(long userId) {
		return userId > MAX_USER_ID_FOR_RECOMMENDATIONS;
	}
  
    public static Result index() throws IOException {
        return ok(index.render("Your new application is ready."));
    }
  
    public static Result home() throws IOException {
    	return renderHome();
    }

	private static Result renderHome() throws IOException {
		// load movies list and existing users into memory when home page is visited
    	doParseMovies();
    	doParseUsers();
		// remove if any session data
		session().remove(USER_ID);
        return ok(views.html.home.render());
	}

	private static void doParseUsers() throws IOException {
		if(userMap == null || userMap.isEmpty()) {
    		UserFileParser.parse(Play.application().getFile("/datafiles/u.user"));
    	}
	}

	private static void doParseMovies() throws IOException {
		if(movieMap == null || movieMap.isEmpty()) {
    		MovieFileParser.parse(Play.application().getFile("/datafiles/u.item"));
    	}
	}
	
	private static long createNewUser(int age, String gender) {
		long newUserId = -1;
		
		try {
			if(userMap != null) {
				Set<Long> keySet = userMap.keySet();
				//get max user id from user map
				Long[] keys = new Long[keySet.size() - 1];
				keys = keySet.toArray(keys);
				long currMaxUserId = keys[keys.length - 1];
				newUserId = currMaxUserId + 1;
				
				// now update the users file to save this user to file
				updateUserFile(newUserId, age, gender);
				// saved to user file, now update user map in memory as well
				userMap.put(newUserId, new User(newUserId, age, gender));
			}	
		} catch (Exception e) {
			return -1;
		}
		return newUserId;
	}
	
	private static void updateUserFile(long newUserId, int age, String gender) throws IOException {
		FileWriter writer = null;
		BufferedWriter bw = null;
		try {
			writer = new FileWriter(Play.application().getFile("/datafiles/u.user"), true);
			bw = new BufferedWriter(writer);
			bw.write(newUserId + "|" + age + "|" + gender.toUpperCase() + "|engineer|" + System.currentTimeMillis());
			writer.flush();			
		} finally {
			writer.close();			
		}
	}
    
    public static Result login() throws IOException {
        return ok(views.html.login.render(""));
    }
    
    public static Result renderSignupPage() throws IOException {
        return ok(views.html.signup.render(""));
    }
    
    public static Result newUser() throws IOException {
    	// hard coded user's age and gender for now
    	long userId = createNewUser(27, "M");
    	if(userId == -1) {
    		// unable to create a new user; some error
    		return badRequest(views.html.signup.render("Couldn't create a new user. Try again"));
    	}
        return ok(views.html.login.render("Signup successful. Your user id is: " + userId));
    }
    
    private static boolean isUserIdValid(String userId) {
    	try {
    		// user id should be a number
    		Long.parseLong(userId);
    	} catch (Exception e) {
    		return false;
    	}
    	return true;
    }
    
    public static Result selectionPage(String userId) throws IOException, TasteException {
    	if(!isUserIdValid(userId) || !checkUserExists(userId)) {
    		// user id doesn't exist
    		return badRequest(views.html.login.render("User id not found. Please enter a valid id"));
    	}
    	// successful login. set user session
    	session(USER_ID, userId);
    	
    	long uId = Long.parseLong(userId);
    	
    	if(isNewUser(uId)) {
    		// if this is a new user who signed up recently, then show top movies as no data is available for this user
    		List<RecommendedItemWrapper> returnList = wrapRecommendedItems(new RecommendationEngine().getRecommendationsForNewUser());
    		// sort by highest rating
    		Collections.sort(returnList);
    		return ok(views.html.recommendations.render(returnList, "", ""));
    	} else {
    		// show recommendation selection page
    		return ok(views.html.selection.render());    		
    	}
    }
    
    public static Result getRecommendations(String type) throws Exception {
    	if(session(USER_ID) == null || !checkMovieMapAndUserSession()) {
    		// user id doesn't exist
    		return badRequest(views.html.login.render("User session not found. Please login again"));
    	}
    	
    	List<RecommendedItem> list = null;
    	RecommendationType rType = RecommendationType.getType(type);
    	Long userId = Long.parseLong(session(USER_ID));
    	User user = userMap.get(userId);
    	
		switch (rType) {
		case NON_CLUSTERED_USER_BASED:
			list = new RecommendationEngine().getNonClusUserBased(userId);
			break;
		case NON_CLUSTERED_ITEM_BASED:
			list = new RecommendationEngine().getNonClusItemBased(userId);
			break;
		case CLUSTERED_USER_BASED:
			list = new RecommendationEngine().getClusUserBased(userId, user.getGender(), user.getAge());
			break;
		case CLUSTERED_ITEM_BASED:
			list = new RecommendationEngine().getClusItemBased(userId, user.getGender(), user.getAge());
			break;
		default:
			break;
		}
    	
		String message = "";
		if(list == null || list.isEmpty()) {
			message = "No recommendations found";
		}
		
		List<RecommendedItemWrapper> returnList = wrapRecommendedItems(list);
		if(returnList == null) {
			// no recommendations found, return an empty list
			returnList = new ArrayList<RecommendedItemWrapper>();
		}
        return ok(views.html.recommendations.render(returnList, rType.getDescription(), message));
    }

	private static List<RecommendedItemWrapper> wrapRecommendedItems(
			List<RecommendedItem> list) {
		if(list == null || list.isEmpty()) {
			return null;
		}
		List<RecommendedItemWrapper> returnList = new ArrayList<RecommendedItemWrapper>(list.size());
    	for(RecommendedItem item : list) {
    		Movie movie = movieMap.get(item.getItemID()+"");
    		returnList.add(new RecommendedItemWrapper(item.getValue(),  movie));
    	}
		return returnList;
	}
}
