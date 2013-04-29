/**
 * 
 */
package movierecommender;

import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import play.Play;


public class RecommendationEngine {
	
	public static enum RecommendationType {
		NON_CLUSTERED_USER_BASED("ncub", "Non Clustered User Based"),
		NON_CLUSTERED_ITEM_BASED("ncib", "Non Clustered Item Based"),
		CLUSTERED_USER_BASED("cub", "Clustered User Based"),
		CLUSTERED_ITEM_BASED("cib", "Clustered Item Based");
		
		private String val;
		private String description;
		
		private RecommendationType(String val, String desc) {
			this.val = val;
			this.description = desc;
		}
		
		public static RecommendationType getType(String val) {
			for(RecommendationType type : RecommendationType.values()) {
				if(type.val.equalsIgnoreCase(val)) {
					return type;
				}
			}
			return null;
		}

		public String getVal() {
			return val;
		}

		public String getDescription() {
			return description;
		}
	}

	private static final int NEAREST_K = 3;
	private static final int NO_OF_RECOMMENDATIONS = 10;
	
	public List<RecommendedItem> getNonClusUserBased(long userId) throws TasteException, IOException {
		DataModel model = new FileDataModel(Play.application().getFile("/app/movierecommender/u.csv"));
		
//		DataModel model = new FileDataModel(new File("G:\\sadhana\\workspace\\MiA-master\\src\\main\\java\\mia\\recommender\\ch02\\u.csv"));
		return getUserBasedRecommendationsInternal(model, userId);
	}
	
	public List<RecommendedItem> getNonClusItemBased(long userId) throws TasteException, IOException {
		DataModel model = new FileDataModel(Play.application().getFile("/app/movierecommender/u.csv"));
//		DataModel model = new FileDataModel(new File("G:\\sadhana\\workspace\\MiA-master\\src\\main\\java\\mia\\recommender\\ch02\\u.csv"));
		return getItemBasedRecommendationsInternal(model, userId);	
	}
	
	public List<RecommendedItem> getClusUserBased(long userId, String gender, int age) throws TasteException, IOException {
		DataModel model = new FileDataModel(ClusteringDataFiles.getFile(gender, age));
		return getUserBasedRecommendationsInternal(model, userId);
	}
	
	public List<RecommendedItem> getClusItemBased(long userId, String gender, int age) throws TasteException, IOException {
		DataModel model = new FileDataModel(ClusteringDataFiles.getFile(gender, age));
		return getItemBasedRecommendationsInternal(model, userId);
	}
	
	private List<RecommendedItem> getUserBasedRecommendationsInternal(DataModel model, long userId) throws TasteException {
		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
		UserNeighborhood neighborhood = new NearestNUserNeighborhood(NEAREST_K,
				similarity, model);

		Recommender recommender = new GenericUserBasedRecommender(model,
				neighborhood, similarity);

		return recommender.recommend(userId, NO_OF_RECOMMENDATIONS);
	}
	
	private List<RecommendedItem> getItemBasedRecommendationsInternal(DataModel model, long userId) throws TasteException {
		ItemSimilarity itemSimilarity = new UncenteredCosineSimilarity(model);
		GenericItemBasedRecommender itemBasedRecommender = new GenericItemBasedRecommender(model, itemSimilarity);

		List<RecommendedItem> recommendations = itemBasedRecommender.recommend(userId, NO_OF_RECOMMENDATIONS);
		return recommendations;
	}
}