package mergeBanks;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.text.WordUtils;
 
public class BankVCT{
	
//DB-Connection-Variabeln
private static Connection con = null;
private static String dbHost = "localhost";	// Hostname
private static String dbPort = "3306";		// Port -- Standard: 3306
private static String dbName = "vctBank";	// Datenbankname
private static String dbUser = "root";		// Datenbankuser
private static String dbPass = "";			// Datenbankpasswort


//Zielsystem-Variabeln
//Kunde
private static String vorname;
private static String nachname;
private static String addresse;
private static String laendercode;
private static String status;

//Konto
private static String iban;
private static float kontostand;
private static String kontoart = "Kontokorrent";


 
private BankVCT(){
    try {
        Class.forName("com.mysql.jdbc.Driver"); // Datenbanktreiber f�r JDBC Schnittstellen laden.
 
        // Verbindung zur JDBC-Datenbank herstellen.
        con = DriverManager.getConnection("jdbc:mysql://"+dbHost+":"+ dbPort+"/"+dbName+"?"+"user="+dbUser+"&"+"password="+dbPass);

    } catch (ClassNotFoundException e) {
        System.out.println("Treiber nicht gefunden");
    } catch (SQLException e) {
        System.out.println("Verbindung nicht m�glich");
        System.out.println("SQLException: " + e.getMessage());
        System.out.println("SQLState: " + e.getSQLState());
        System.out.println("VendorError: " + e.getErrorCode());
    }
  }
 
private static Connection getInstance(){
    if(con == null)
        new BankVCT();
    return con;
}
 
  //Gebe Tabelle in die Konsole aus
  public static void getVCTDatas(){
      con = getInstance();
 
      if(con != null){
      // Abfrage-Statement erzeugen.
      Statement query;
      try {
          query = con.createStatement();
 
          // Tabelle anzeigen
          String sql = "SELECT * FROM Account WHERE kundenart !='Firma'";
          ResultSet result = query.executeQuery(sql);
 
        // Ergebnisstabelle durchforsten
          while (result.next()) {
          String kundenname = result.getString("kundenname");
          String strassenname = result.getString("strassenname");
          String plz = result.getString("plz");
          String stadt = result.getString("stadt");
          String land = result.getString("land");
          String kontonummer = result.getString("kontonummer");
          String saldo = result.getString("saldo");

          
          //Kundenname aufteilen
          String[] part = kundenname.split("\\s");
          if(part.length == 2) {
        	  vorname = part[0];
        	  nachname = part[1];
          } else if(kundenname.contains("Dr")){
        	  vorname = part[1];
        	  nachname = part[2];
          } else if(kundenname.contains("van") || kundenname.contains("von")) {
        	  vorname = part[0];
        	  nachname = part[1] + " " + part[2];
          }
          else if(part.length == 3 & part[0].length() <= 2 & part[1].length() <= 2) {
        	  vorname = part[0] + " " + part[1];
        	  nachname = part[2];
          }else if(part.length == 3 & part[0].length() > 2 & part[1].length() > 2){
        	  vorname = part[0];
        	  nachname = part[1] + " " + part[2];
          }
          vorname = vorname.replace("\u00FC", "ue");
          vorname = vorname.replace("\u00E4", "ae");
          vorname = vorname.replace("\u00F6", "oe");
          vorname = WordUtils.capitalizeFully(vorname);
          nachname = nachname.replace("\u00FC", "ue");
          nachname = nachname.replace("\u00E4", "ae");
          nachname = nachname.replace("\u00F6", "oe");
          nachname = WordUtils.capitalizeFully(nachname);
          nachname = nachname.replace("Van", "van");
          nachname = nachname.replace("Von", "von");
          
          
          //Adresse zusammenf�hren
          addresse = strassenname + ", " + plz + " " + stadt;
          
          
          //L�ndercode erstellen
          if(land.equals("Schweiz") || land.equals("Switzerland")) {
        	  laendercode = "CH";
          } else if(land.equals("Germany")) {
        	  laendercode = "DE";
          } else if(land.equals("The Netherlands")) {
        	  laendercode = "NL";
          }

          
          //Kontostand
          kontostand = Float.parseFloat(saldo);         
          
          
          // IBAN f�r Kontokorrent generieren
          String nullen = new String();
          // Einf�gen von Nullen damit IBAN L�nge von 21 erreicht wird
          for(int y = kontonummer.length();  y < 12; y++){
              nullen += "0";
          }
          iban = "CH" + "27" + "00261" + nullen + kontonummer;
          
          
          MergeBanks.KundenArray.add(new Kunde(MergeBanks.kundenidcnt, vorname, nachname, addresse, laendercode, status));
          MergeBanks.KontenArray.add(new Konto(MergeBanks.kundenidcnt, iban, kontostand, kontoart));
          MergeBanks.kundenidcnt++;
          
          
          
          
          }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
}