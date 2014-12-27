package mergeBanks;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
 
public class VCTBankDBConnection{
	
//DB-Connection-Variabeln
private static Connection con = null;
private static String dbHost = "localhost"; // Hostname
private static String dbPort = "3306";      // Port -- Standard: 3306
private static String dbName = "vctBank";   // Datenbankname
private static String dbUser = "root";     // Datenbankuser
private static String dbPass = "";      // Datenbankpasswort

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
private static String kontoart = "Privatkonto";


 
private VCTBankDBConnection(){
    try {
        Class.forName("com.mysql.jdbc.Driver"); // Datenbanktreiber f�r JDBC Schnittstellen laden.
 
        // Verbindung zur JDBC-Datenbank herstellen.
        con = DriverManager.getConnection("jdbc:mysql://"+dbHost+":"+ dbPort+"/"+dbName+"?"+"user="+dbUser+"&"+"password="+dbPass);
    } catch (ClassNotFoundException e) {
        System.out.println("Treiber nicht gefunden");
    } catch (SQLException e) {
        System.out.println("Verbindung nicht moglich");
        System.out.println("SQLException: " + e.getMessage());
        System.out.println("SQLState: " + e.getSQLState());
        System.out.println("VendorError: " + e.getErrorCode());
    }
  }
 
private static Connection getInstance(){
    if(con == null)
        new VCTBankDBConnection();
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
          String sql = "SELECT * FROM account WHERE kundenart !='Firma'";
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
//          String info = kundenname + ", " + strassenname + ", " + plz + ", " + stadt + ", " + land;
//          System.out.println(info);

          
          //Kundenname aufteilen
          String[] part = kundenname.split("\\s");
          if(part.length == 2) {
        	  vorname = part[0];
        	  nachname = part[1];
          } else if(kundenname.contains("Dr")){
        	  vorname = part[1];
        	  nachname = part[0] + " " + part[2];
          } else if(part.length == 3) {
        	  vorname = part[0];
        	  nachname = part[1] + " " + part[2];
          }
//          System.out.println(vorname + ", " + nachname);
          
          
          //Adresse zusammenf�hren
          addresse = strassenname + ", " + plz + " " + stadt;
//          System.out.println(addresse);
          
          
          //L�ndercode erstellen
          if(land.equals("Schweiz") || land.equals("Switzerland")) {
        	  laendercode = "CH";
          } else if(land.equals("Germany")) {
        	  laendercode = "DE";
          } else if(land.equals("The Netherlands")) {
        	  laendercode = "NL";
          }
//          System.out.println(laendercode);
          
          
          //Status Bronze, Silber, Gold
          kontostand = Float.parseFloat(saldo);
          if(kontostand <= 50000) {
        	  status = "BRONZE";
          } else if(kontostand <= 500000) {
        	  status = "SIVLER";
          } else {
        	  status = "GOLD";
          }
//          System.out.println(status);          
          
          
          //Create IBAN
          iban = "CH" + "27" + kontonummer;
          
          
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