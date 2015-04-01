package ch.mauricio.scplot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    public static void main( String[] args )
    {
    	HashMap<Integer,Double> globalNotes= new HashMap<Integer,Double>();
    	HashMap<Integer,Double> perYearAvis= new HashMap<Integer,Double>();
    	Document doc;
		try {
			for(int i=1990;i<1992;i++){
				ArrayList<Double> yearNotes= new ArrayList<Double>();
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
							if(!text.equals("-") && textYear.equals("("+i+")")){
								Double noteToAdd = Double.parseDouble(text);
								yearNotes.add(noteToAdd);
								System.out.println(i+"-> "+text+"_>_>_>_"+nbrAvis);
							}
						}
					}
				}
				Double sum=0.0;
				for(Double note: yearNotes){
					sum=sum+note;
				}
				Double moy= sum/yearNotes.size();
				BigDecimal bd = new BigDecimal(moy);
				bd= bd.setScale(2,BigDecimal.ROUND_DOWN);
				moy = bd.doubleValue();
				globalNotes.put(i, moy);
			}
			File file = new File("C:/Users/Abas/Desktop/notes.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for( Map.Entry<Integer, Double> entry : globalNotes.entrySet() ) {
			   Integer cle = entry.getKey();
			    Double valeur = entry.getValue();
			    bw.write(cle+": "+valeur);
			    bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    	
    }
}
