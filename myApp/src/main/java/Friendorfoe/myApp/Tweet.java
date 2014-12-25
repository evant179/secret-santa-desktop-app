package Friendorfoe.myApp;

import twitter4j.GeoLocation;
import twitter4j.Status;
import javafx.beans.property.SimpleStringProperty;

public class Tweet {
	
	  private final SimpleStringProperty userName;
      private final SimpleStringProperty message;
      private final SimpleStringProperty location;
      private double latitude;
      private double longitude;
      
	public Tweet(Status status)
	{
		this.userName = new SimpleStringProperty(status.getUser().getScreenName());
		this.message =  new SimpleStringProperty(status.getText());
		this.location =  new SimpleStringProperty(status.getUser().getLocation());
		GeoLocation location = status.getGeoLocation();
		
		if (location != null)
		{
			latitude = status.getGeoLocation().getLatitude();
			longitude = status.getGeoLocation().getLongitude();
		}
		
	}

	public String getUserName() {
        return userName.get();
    }
	
	public String getMessage() {
        return message.get();
    }
	
	public String getLocation() {
        return location.get();
    }
}
