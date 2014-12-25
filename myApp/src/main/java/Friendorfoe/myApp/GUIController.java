package Friendorfoe.myApp;

import java.net.URL;
import java.util.ResourceBundle;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.Callback;



public class GUIController extends Control implements Initializable{
	
	@FXML private TextField searchTerms;
	@FXML private TextField numResponse;
	@FXML private TableView<Tweet> tableView;
	@FXML private TableColumn user;
	@FXML private TableColumn location;
	@FXML private TableColumn message;
	@FXML private Parent profile;
	@FXML private Profile profileController;
	private Twitter twitter = TwitterFactory.getSingleton();

	/*
	 * Description: GetList populates the tableview on tab two of 
	 * the application.
	 */
	public void getList()
	{
		
		//observable list to add returned tweets to
		ObservableList<Tweet> tweets = FXCollections.observableArrayList();
		
		//sets the search query
		Query query = new Query(searchTerms.getText());

		//sets the amount of tweets to retrieve
		query.setCount(Integer.parseInt(numResponse.getText()));
		
		//searches twitter for relevant tweets
		try {
			QueryResult result = twitter.search(query);
			
			for (Status status : result.getTweets())
			{
				Tweet currentTweet = 
						new Tweet(status);
				
				tweets.add(currentTweet);
				
			}
			
			tableView.setItems(tweets);
			
			
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	//sets table cells to found values
	@SuppressWarnings("unchecked")
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
		//gets the username from the Tweet object and places it in the 
		//"user" column
		user.setCellValueFactory(
			    new PropertyValueFactory<Tweet,String>("userName")
			);
		user.setCellFactory(cellFactMethod());
		
		//gets the username from the Tweet object and places it in the 
		//"location" column
		location.setCellValueFactory(
			    new PropertyValueFactory<Tweet,String>("location")
			);
		location.setCellFactory(cellFactMethod());
		
		//gets the message/tweet from the Tweet object and places it in the 
		//tweet column
		message.setCellValueFactory(
			    new PropertyValueFactory<Tweet,String>("message")
			);
		
		message.setCellFactory(cellFactMethod());
	}
	
	public Callback cellFactMethod()
	{
		return new Callback<TableColumn<Tweet,String>, TableCell<Tweet,String>>() {
            public TableCell<Tweet, String> call( TableColumn<Tweet, String> param) {
                final TableCell<Tweet, String> cell = new TableCell<Tweet, String>() {
                     private Text text;
                     @Override
                     public void updateItem(String item, boolean empty) {
                          super.updateItem(item, empty);
                          if (!isEmpty()) {
                               text = new Text(item.toString());
                               // Setting the wrapping width to the Text
                               text.wrappingWidthProperty().bind(getTableColumn().widthProperty());
                               
                               setGraphic(text);
                          }
                     }
                };
                return cell;
           }
		};
	}

}
