package zad1;
import java.io.*;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TravelData {
	ArrayList<String> locale = new ArrayList<String>();
	ArrayList<String> country = new ArrayList<String>();
	ArrayList<String> departure = new ArrayList<String>();
	ArrayList<String> arrival = new ArrayList<String>();
	ArrayList<String> place = new ArrayList<String>();
	ArrayList<Double> price = new ArrayList<Double>();
	ArrayList<String> currency = new ArrayList<String>();


	public TravelData(File dataDir) {
		File[] files=dataDir.listFiles();
		NumberFormat format;
		int number = files.length;
		int i = 0;
		while(number>0){
			Scanner sc;
			try {
				sc = new Scanner(files[i]);
				while(sc.hasNext()){
					sc.useDelimiter("\t|\n");		
					locale.add(sc.next());
					String [] localeData = locale.get(i).split("_");
					country.add(sc.next());
					departure.add(sc.next());
					arrival.add(sc.next());
					place.add(sc.next());
					format = NumberFormat.getInstance(new Locale(localeData[0]));
					try {
						price.add(format.parse(sc.next()).doubleValue());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					currency.add(sc.next());
					i++;
					number--;
				}
				sc.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("No files in data folder: "+e);
				e.printStackTrace();
			}
		}

	}

	
	public String getCountryNameInLanguage(String country, String localeInTag, String localeOutTag) {
		if(localeInTag.equals(localeOutTag)) {
			return country;
		}
		Locale outLocale = Locale.forLanguageTag(localeOutTag);
		Locale inLocale = Locale.forLanguageTag(localeInTag);

		for (Locale l : Locale.getAvailableLocales()) {
			if (l.getDisplayCountry(inLocale).equals(country)) {
				return l.getDisplayCountry(outLocale);
			}
		}
		return country;
	}
	

	
	public List<String> getOffersDescriptionsList(String loc, String dateFormat) {
		ArrayList<String> offerList = new ArrayList<String>();

		String [] localeData = loc.split("_");
		SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateInstance();
		df.applyPattern(dateFormat);
		ResourceBundle translator = ResourceBundle.getBundle("OfferInfo", new Locale(localeData[0]));



		for(int i=0;i<this.locale.size();i++){
			NumberFormat formator = NumberFormat.getInstance(new Locale(localeData[0], localeData[1]));

			String offerLocale = this.locale.get(i).split("_")[0];
			String country = getCountryNameInLanguage(this.country.get(i), offerLocale, localeData[0]);
			String place = offerLocale.equals(localeData[0]) ? this.place.get(i):translator.getString(this.place.get(i));
			String price = formator.format(this.price.get(i));
			String departure = this.departure.get(i);
			String arrival = this.arrival.get(i);

			try {
				Calendar c = Calendar.getInstance();
				Date depart = c.getTime();
				Date arrive = c.getTime();
				arrive = df.parse(this.arrival.get(i));
				depart = df.parse(this.departure.get(i));
				departure = df.format(depart);
				arrival = df.format(arrive);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			String offer = ""+ 
					country + " "+
					departure + " "+
					arrival + " "+
					place + " "+
					price + " "+
					this.currency.get(i);
			offerList.add(offer);
		}
		return(offerList);

	}
}

