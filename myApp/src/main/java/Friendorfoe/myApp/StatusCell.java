package Friendorfoe.myApp;

import javafx.event.ActionEvent;
import javafx.scene.GroupBuilder;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.HyperlinkBuilder;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPaneBuilder;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.FlowPaneBuilder;
import javafx.scene.layout.HBoxBuilder;
import twitter4j.Status;
import twitter4j.User;

public class StatusCell extends ListCell<Status> {

	  Status tweet;
	  Node tweetTextNode;
	  ListView listView;
	  
	  Node createDisplayNode() {
		    listView = getListView();
		    Node displayNode = HBoxBuilder.create()
		    	      .spacing(5)
		    	      .children(
		    	    		  tweetTextNode = createTweetText()
		    	    		  )
    	    		  .build();
		    return displayNode;
	  }
	  
	  @Override public void updateItem(Status item, boolean empty) {
		    tweet = item;
		    super.updateItem(tweet, empty);
		    if (item != null) {
		      setGraphic(createDisplayNode());
		    }
		  }

	@SuppressWarnings("deprecation")
	public Node createTweetText() {
	
	    String tweetText = tweet.getText();
	    listView = getListView();
	    FlowPane flowPane = FlowPaneBuilder.create()
	      .hgap(4)
	      .vgap(0)
	      .build();
	    flowPane.setPrefWidth(listView.getScene().getWindow().getWidth() - 100);
	    
	    User user = tweet.getUser();
	    Node nameNode = new Label(user.getName());
	    Node text = new Label(tweetText);
	    flowPane.getChildren().add(nameNode);
	    flowPane.getChildren().add(text);
    
	    return BorderPaneBuilder.create()
	    	      .top(
	    	        GroupBuilder.create()
	    	          .children(
	    	            flowPane
	    	          )
	    	         .build()
	    	      )
	    	      .build();
	}
}
