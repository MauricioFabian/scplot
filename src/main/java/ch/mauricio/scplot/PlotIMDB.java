package ch.mauricio.scplot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.BitmapEncoder.BitmapFormat;

public class PlotIMDB {

	public void renderPlot(int startYear,int endYear){
		HashMap<Integer,Double> globalNotes= new HashMap<Integer,Double>();

    	ArrayList<Notes> notes= new ArrayList<Notes>();
    	Document doc;
		try {
			for(int i=startYear;i<endYear;i++){
				ArrayList<Double> yearNotes= new ArrayList<Double>();
				
				for(int j=1;j<1000;j=j+100){
					//http://www.imdb.com/search/title?count=250&num_votes=1000,,&production_status=released&release_date=2014,2014&start=1&title_type=feature,tv_movie&view=simple
					try{
						doc = Jsoup.connect("http://www.imdb.com/search/title?count=250&num_votes=1000,,&production_status=released&release_date="+i+","+i+"&start="+j+"&title_type=feature,tv_movie&view=detailed").get();
						Elements newsHeadlines = doc.select(".rating-rating .value");
						if(doc.select("#header #main").text().equals("No results.")){
							globalNotes.put(i, 0.0);
						}else{
							for(Element element:newsHeadlines){
								System.out.println(i+"->"+element.text());
								yearNotes.add(Double.parseDouble(element.text()));
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					}
				Double sum=0.0;
				for(Double note: yearNotes){
					sum=sum+note;
				}
				Double moy= sum/yearNotes.size();
				System.out.println(moy+":"+yearNotes.size());
				BigDecimal bd = new BigDecimal(moy);
				bd= bd.setScale(2,BigDecimal.ROUND_DOWN);
				moy = bd.doubleValue();
				globalNotes.put(i, moy);
				
				Notes note= new Notes();
				note.setYear(i);
				note.setNote(moy);
				notes.add(note);
			}
			outputFile(startYear, endYear, globalNotes);
			
			
			outputChart(globalNotes, notes);
			
			
			
		} catch (IOException e) {
			
			System.out.println(e.getCause()+"\n"+e.getMessage());
		}
    	
	}


	private void outputChart(HashMap<Integer, Double> globalNotes, ArrayList<Notes> notes)
			throws IOException {
		double[] xData = new double[globalNotes.size()];
		double[] yData = new double[globalNotes.size()];
 
		// Create Chart
		for(int i=0;i<notes.size();i++){
			Notes note= notes.get(i);
			xData[i]=new BigDecimal(note.getYear()).setScale(1, BigDecimal.ROUND_DOWN).doubleValue();
			yData[i]=note.getNote();
		}
 
		// Show it
		Chart chart = new Chart(1000, 700);
		chart.setChartTitle("Moyenne des notes par année des films entre 1990 et 2014 sur SensCritique");
		chart.setXAxisTitle("Année");
		chart.setYAxisTitle("Moyenne");
		Series series = chart.addSeries("Moyenne des notes par année", xData, yData);
		series.setMarker(SeriesMarker.CIRCLE);
 
		BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapFormat.PNG, 300);
	}

	private void outputFile(int startYear, int endYear, HashMap<Integer, Double> globalNotes) throws IOException {
		File file = new File("C:/Users/Abas/Desktop/notes.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		for(int i=startYear;i<endYear;i++){
			String content=i+": "+globalNotes.get(i);
			bw.write(content);
			bw.newLine();
		}
		bw.close();
	}
}

