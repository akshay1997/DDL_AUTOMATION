package mypackage;
import java.io.*;     // JDBC classes
import java.lang.*;
import java.util.*;
import java.sql.*;


public class deployddl {
    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException{
       // try {
            Constants cnt = new Constants();
            String url = cnt.dest_url;
            String user = cnt.dest_username; //  Enter dest_username in Constants.java file to run this code
            String password = cnt.dest_password;  // Enter dest_password in Constants.java file to run this code
            String scname = cnt.dest_schema;
            String tbname = cnt.dest_table;
            Connection con;
            PreparedStatement stmt = null, stmt2 = null;
            ResultSetMetaData metadata, metadata2;
            ResultSet rs = null;

            InputStream is = new FileInputStream(tbname + ".sql");
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                line = line.replace("TIMESTMP", "TIMESTAMP");
                line = line.replace("DBDFMSDR", "TEST");
                sb.append(line);
                line = buf.readLine();
            }

            // replace dbdfmsdr by ag --- hardcoded for now

            // replace TIMESTMP by TIMESTAMP  --- TIMESTAMP in CMDW
            String sql = sb.toString();
            System.out.println(sql);


            Class.forName("com.ibm.db2.jcc.DB2Driver");
            Properties properties = new Properties();
            properties.put("user", user);
            properties.put("password", password);
            properties.put("sslConnection", "true");
            File ssl_truststore_location = new File(cnt.url.getPath());
            properties.put("sslTrustStoreLocation", ssl_truststore_location.toString());
            con = DriverManager.getConnection (url, properties);
            con.setAutoCommit(false);
            System.out.println("Created a JDBC connection to the data source");
            stmt = con.prepareCall(sql);
            stmt.execute();
            System.out.println("query executed");

            stmt.close();
            con.commit();
            con.close();
            System.out.println("Disconnected from data source");


    }
    /*
        catch(IOException e){
            e.printStackTrace();
        }
        catch(ClassNotFoundException e){
            System.err.println("Could not load JDBC driver");
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
        catch(SQLException ex) {
            System.err.println("SQLException information");
            while (ex != null) {
                System.err.println("Error msg: " + ex.getMessage());
                System.err.println("SQLSTATE: " + ex.getSQLState());
                System.err.println("Error code: " + ex.getErrorCode());
                ex.printStackTrace();
                ex = ex.getNextException(); // For drivers that support chained exceptions
            }
        }

     */
    }

