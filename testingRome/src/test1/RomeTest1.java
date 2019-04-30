package test1;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import java.io.InputStreamReader;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class RomeTest1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		URL feedUrl;
		try {
			feedUrl = new URL("https://www.headroyce.org/cf_calendar/feed.cfm?type=ical&feedID=8F8307D7AE2D4331B2CD77224183ED84");
			SyndFeedInput input = new SyndFeedInput();
			try {
				SyndFeed feed = input.build(new XmlReader(feedUrl));
				//TODO: STUFF HERE
				
				
				
			} catch (IllegalArgumentException | FeedException | IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Feed Reader Error: Caught Error Below: ");
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			System.out.println("Malformed URL. Error Below: ");
			e.printStackTrace();
		}
		
		
		
		

	}

}
