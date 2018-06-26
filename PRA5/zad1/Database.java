package zad1;

import java.sql.*;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Database {

	private Connection conn;
	private Statement stmt;
	private String url;
	private TravelData travelData;

	public Database(String url, TravelData travelData) {
		this.url=url;
		this.travelData=travelData;
	}

	public void create()
	{
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

			conn = DriverManager.getConnection("jdbc:derby:" + url + ";create=true");
			stmt = conn.createStatement();

		}catch (Exception exc)  {
			System.out.println(exc);
			System.exit(1);
		}
		String createTable="CREATE TABLE TRAVEL(ID INTEGER, LOCALE VARCHAR(5), COUNTRY VARCHAR(20), DEPARTURE VARCHAR(20),ARRIVAL VARCHAR(20),PLACE VARCHAR(20),PRICE VARCHAR(20),CURRENCY VARCHAR(20) )";
		try {
			//			stmt.executeUpdate("DROP TABLE TRAVEL");	
			//			stmt.executeUpdate(createTable);

			try {
				stmt.executeUpdate(createTable);}
			catch(SQLException e) {
				stmt.executeUpdate("DROP TABLE TRAVEL");	
				stmt.executeUpdate(createTable);
			}	

			for(int i=0;i<travelData.locale.size();i++){
				int ind =i+1;
				String addRecord = "INSERT INTO TRAVEL VALUES("+ ind +",'"+ 
						travelData.locale.get(i) + "','"+
						travelData.country.get(i) + "','"+
						travelData.departure.get(i) + "','"+
						travelData.arrival.get(i) + "','"+
						travelData.place.get(i) + "','"+
						travelData.price.get(i) + "','"+
						travelData.currency.get(i) +
						"')";
				stmt.executeUpdate(addRecord);
			}
//			String query = "Select * from travel";
//			ResultSet rs = stmt.executeQuery(query);
//			ResultSetMetaData rsmd = rs.getMetaData();
//			int columnCount =rsmd.getColumnCount();
//			for(int i=1; i<=columnCount; i++) {
//				System.out.format("%20s", rsmd.getColumnName(i) + " | ");
//			}
//			while(rs.next()) {
//				System.out.println("");
//				for(int i=1; i<=columnCount; i++) {
//					System.out.format("%20s", rs.getString(i) + " | ");
//				}
//			}

		} catch (SQLException sqlExc) {
			sqlExc.printStackTrace();
			System.out.println(sqlExc.getMessage());
		} finally {
			try { 
				stmt.close();
				conn.close();
			} catch(SQLException exc) {
				System.out.println(exc.getMessage());
				System.exit(1);
			}
		}
	}
	
	public Map<String, List<String>> queryDB() {
		Map<String, List<String>> data = new HashMap<String, List<String>>();
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

			conn = DriverManager.getConnection("jdbc:derby:" + url + ";create=true");
			stmt = conn.createStatement();

		}catch (Exception exc)  {
			System.out.println(exc);
			System.exit(1);
		}

		String query = "Select * from travel";
		try {
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			
			for(int i=1; i<=columnCount; i++) {
				data.put(rsmd.getColumnName(i).toLowerCase(), new ArrayList<String>());
			}
			while(rs.next()) {
				for(int i=1; i<=columnCount; i++) {
					data.get(rsmd.getColumnName(i).toLowerCase()).add(rs.getString(i));
				}
			}

		} catch (SQLException sqlExc) {
			sqlExc.printStackTrace();
			System.out.println(sqlExc.getMessage());
		} finally {
			try { 
				stmt.close();
				conn.close();
				
			} catch(SQLException exc) {
				System.out.println(exc.getMessage());
				System.exit(1);
			}
			
		}
		return data;
		
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
	
	
	public List<String> getOffersFromDBList(Map<String, List<String>> data,String loc, String dateFormat) {
		ArrayList<String> offerList = new ArrayList<String>();

		String [] localeData = loc.split("_");
		SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateInstance();
		df.applyPattern(dateFormat);
		ResourceBundle translator = ResourceBundle.getBundle("OfferInfo", new Locale(localeData[0]));



		for(int i=0;i<data.get("locale").size();i++){
			NumberFormat formator = NumberFormat.getInstance(new Locale(localeData[0], localeData[1]));

			String offerLocale = data.get("locale").get(i).split("_")[0];
			String country = getCountryNameInLanguage(data.get("country").get(i), offerLocale, localeData[0]);
			String place = offerLocale.equals(localeData[0]) ? data.get("place").get(i):translator.getString(data.get("place").get(i));
			String price = formator.format(Double.valueOf(data.get("price").get(i)));
			String departure = data.get("departure").get(i);
			String arrival = data.get("arrival").get(i);

			try {
				Calendar c = Calendar.getInstance();
				Date depart = c.getTime();
				Date arrive = c.getTime();
				arrive = df.parse(data.get("arrival").get(i));
				depart = df.parse(data.get("departure").get(i));
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
					data.get("currency").get(i);
			offerList.add(offer);
		}
		return offerList;

	}

	public void showGui() {

		JFrame frame = new JFrame("InputDialog");

		String countryLoc = JOptionPane.showInputDialog(frame, "Podaj lokalizację pl_PL lub en_GB");
		if(!countryLoc.equals("en_GB")) {
			countryLoc="pl_PL";
		}
		Map<String, List<String>> data = queryDB();
		List<String> offerList = getOffersFromDBList(data,countryLoc,"yyyy-MM-dd");
		
		JListGUI mainFrame = new JListGUI(offerList);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);      
		mainFrame.pack();
		mainFrame.setLocationByPlatform(true);
		mainFrame.setVisible(true);

	}

}
