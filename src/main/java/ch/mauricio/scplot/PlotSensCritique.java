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
import org.jsoup.select.Elements;

import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.BitmapEncoder.BitmapFormat;

public class PlotSensCritique {

	
	public void renderPlot(int startYear,int endYear){
		HashMap<Integer,Double> globalNotes= new HashMap<Integer,Double>();
    	HashMap<Integer,Double> perYearAvis= new HashMap<Integer,Double>();
    	ArrayList<Notes> notes= new ArrayList<Notes>();
    	Document doc;
		try {
			for(int i=startYear;i<endYear;i++){
				ArrayList<Double> yearNotes= new ArrayList<Double>();
				ArrayList<Double> yearAvis= new ArrayList<Double>();
				for(int j=1;j<4;j++){
					doc = Jsoup.connect("http://www.senscritique.com/recherche?query="+i+"&filter=movies&page="+j).get();
					Elements newsHeadlines = doc.select(".erra-ratings a");
					Elements yearHeadlines = doc.select(".elco-date");
					
					
					if(!newsHeadlines.isEmpty()){
						for(int k=0;k<newsHeadlines.size();k++){
							String text= newsHeadlines.get(k).text();
							String textYear;
							try{
								textYear=yearHeadlines.get(k).text();
							}catch(IndexOutOfBoundsException e){
								textYear="";
							}
							
							String nbrAvis= newsHeadlines.get(k).attr("title");
							String nbrAvis1= nbrAvis.replace("Note globale pondérée sur : ", "");
							String nbrAvis2= nbrAvis1.replace(" avis", "");
							if(!text.equals("-") && textYear.equals("("+i+")")){
								Double noteToAdd = Double.parseDouble(text);
								yearNotes.add(noteToAdd);
								yearAvis.add(Double.parseDouble(nbrAvis2));
								System.out.println(i+"-> "+text+">"+nbrAvis2+" avis");
							}
						}
					}
				}
				Double sum=0.0;
				for(Double note: yearNotes){
					sum=sum+note;
				}
				Double moy= sum/yearNotes.size();
				
				moy = roundDouble(moy);
				globalNotes.put(i, moy);
				
				Notes note= new Notes();
				note.setYear(i);
				note.setNote(moy);
				notes.add(note);
				
				sum=0.0;
				for(Double avis: yearAvis){
					sum=sum+avis;
				}
				moy= sum/yearNotes.size();
				moy = roundDouble(moy);
				perYearAvis.put(i, moy);
				
			}
			outputFile(startYear, endYear, globalNotes, perYearAvis);
			
			
			outputChart(globalNotes, notes);
			
			
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    	
	}

	private Double roundDouble(Double moy) {
		BigDecimal bd = new BigDecimal(moy);
		bd= bd.setScale(2,BigDecimal.ROUND_DOWN);
		moy = bd.doubleValue();
		return moy;
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

	private void outputFile(int startYear, int endYear, HashMap<Integer, Double> globalNotes,
			HashMap<Integer, Double> perYearAvis) throws IOException {
		File file = new File("C:/Users/Abas/Desktop/notes.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		for(int i=startYear;i<endYear;i++){
			String content=i+": "+globalNotes.get(i)+"( "+perYearAvis.get(i)+" avis)";
			bw.write(content);
			bw.newLine();
		}
		bw.close();
	}
}
