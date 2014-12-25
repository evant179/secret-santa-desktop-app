package Friendorfoe.myApp;

import java.net.URL;
import java.text.Collator;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import twitter4j.Location;
import twitter4j.ResponseList;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/*
 * Profile controller that controls elements on the profile tab
 */
public class Profile implements Initializable, ChangeListener{
	
	@FXML private Label followerCount;
	@FXML private Label followingCount;
	@FXML private TextField tweet;
	@FXML private Button submit;
	@FXML private Label name;
	@FXML private ImageView userImage;
	@FXML private ListView<String> trends;
	@FXML private ComboBox<String> trendLocations;
	private Twitter twitter = TwitterFactory.getSingleton();
	
	private ResponseList<Location> locations;
	private HashMap<String,Location> nameMap = new HashMap<String, Location>();
	

	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		User thisUser = null;
		ObservableList<String> list= 
				FXCollections.observableArrayList();
		try {
			thisUser = twitter.verifyCredentials();
		
		
		//sets name label
		name.setText(thisUser.getScreenName());
		
		//sets follower count
		followerCount.setText(Integer.toString(thisUser.getFollowersCount()));
		
		//sets following count
		followingCount.setText(Integer.toString(thisUser.getFriendsCount()));
		
		//sets the user image
		Image image = new Image(thisUser.getProfileImageURL());
		userImage.setImage(image);
		
		//adds action listener to combo box to which gets trends 
		//based on selected location
		trendLocations.valueProperty().addListener(this);
		
		//returns a list of locations with available trends
		locations = twitter.getAvailableTrends();
		
		//iterates through all location
		for (Location currentLocation : locations)
		{
			list.add(currentLocation.getName());
			nameMap.put(currentLocation.getName(), currentLocation);
		}
		
		
		
		trendLocations.setItems(new SortedList<String>(list, Collator.getInstance()));
		new AutoCompleteComboBoxListener<String>(trendLocations);
		trendLocations.getSelectionModel().select("United States");
		
	
		getTrends();
		} catch (TwitterException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void getTrends() throws TwitterException
	{
		
		ObservableList<String> list2 = 
				FXCollections.observableArrayList();
		
		String selectedCountry = trendLocations.getValue();
		
		Trends placeTrends = 
				twitter.getPlaceTrends(nameMap.get(selectedCountry).getWoeid());
		
		Trend[] dailyTrends = placeTrends.getTrends();
		
		for(int i = 0; i < 10; i++)
		{
			list2.add(dailyTrends[i].getName());
		}
		
		trends.setItems(list2);
		
	}
	//Sends a tweet
	public void tweet() throws TwitterException
	{
		twitter.updateStatus(tweet.getText());
		tweet.setText("");
	}


	public void changed(ObservableValue arg0, Object arg1, Object arg2) {
		
		try {
			getTrends();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
