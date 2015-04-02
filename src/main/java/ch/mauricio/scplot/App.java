package ch.mauricio.scplot;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Hello world!
 *
 */
public class App 
{
	
	private static int startYear=1940;
	private static int endYear=2015;
    public static void main( String[] args ) throws IOException
    {
    	Document doc;
    	boolean test=false;
    	if(test){
    		int i=1900;
    		int j=1;
    		doc = Jsoup.connect("http://www.imdb.com/search/title?count=250&num_votes=1000,,&production_status=released&release_date="+i+","+i+"&start="+j+"&title_type=feature,tv_movie&view=detailed").get();
			Elements newsHeadlines = doc.select(".rating-rating .value");
			for(Element element:newsHeadlines){
				System.out.println(element.text());
			}
    	}else{
    		//PlotSensCritique plot= new PlotSensCritique();
    		PlotIMDB plot = new PlotIMDB();
        	plot.renderPlot(startYear, endYear);
    	}
    	
    }
}
