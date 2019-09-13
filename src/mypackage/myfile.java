package mypackage;
import java.io.*;     // JDBC classes
import java.lang.*;
import java.util.*;
import java.sql.*;

public class myfile{
    public static String bracket_or_no_bracket(int flag, int ptr){
        if(flag == 1){
            return "";
        }
        if(ptr == 1){
            return "(";
        }
        return ")";
    }

    public static String null_or_not(String st){
        //System.out.print(st);
        if(st.equals("N")){
            return "NOT NULL";
        }
        return "";
    }

    //public static void add_primary_key(Connection con) throws SQLException {

    //}

//    public static String add_indexes(Connection con){

//    }


    public static String isColtype(Map<?, ? >hmap, String type){
        if(type.contains("DECIMAL")){
            String l = ", " + hmap.get("SCALE");
            return l;
        }
        return "";
    }
    public static String istime(Map<?, ? >hmap, String s){
        if(s.contains("TIMESTMP")){
            return "";
        }
        return (String) hmap.get("LENGTH");
    }

    public static void main(String[] args) {

        String val;
        Connection con;
        Constants cnt = new Constants();
        PreparedStatement stmt = null, stmt2 = null;
        ResultSetMetaData metadata, metadata2;
        ResultSet rs = null;
        String scname = cnt.src_schema;
        String tbname = cnt.src_table;
        File file = new File(tbname+".sql");
        try{
            Class.forName("com.ibm.db2.jcc.DB2Driver");
            Properties properties = new Properties();
            properties.put("user", cnt.src_username); // Enter src_username in Constants.java file to run this code
            properties.put("password", cnt.src_password); // Enter src_password in Constants.java file to run this code
            properties.put("sslConnection", "true");
            File ssl_truststore_location = new File(cnt.url.getPath());
            properties.put("sslTrustStoreLocation", ssl_truststore_location.toString());
            //properties.put("sslTrustStoreLocation", "C:\\Users\\AkshayKhanna\\Downloads\\Universal-trustore.jks");
            con = DriverManager.getConnection (cnt.src_url, properties);
            con.setAutoCommit(false);
            System.out.println("Created a JDBC connection to the data source");
            System.out.println("Created JDBC Statement object");
            String sql = "SELECT NAME, COLNO, COLTYPE, LENGTH, SCALE, NULLS FROM \"SYSIBM\".SYSCOLUMNS WHERE TBNAME = '" + cnt.src_table + "'";

            stmt = con.prepareCall(sql);
            stmt.execute();
            metadata = stmt.getMetaData();
            rs = stmt.getResultSet();
            int columnCount = metadata.getColumnCount();
            try{
                FileWriter fileWriter = new FileWriter(file);

                fileWriter.write("CREATE TABLE " + scname + "." + tbname + " ( " );
                while(rs.next()) {
                    int flag = 0;
                    Map<String, String> map = new HashMap<String, String>();
                    for (int i = 1; i <= columnCount; i++) {
                        map.put(metadata.getColumnName(i), rs.getString(i));
                    }
                    if(map.get("COLTYPE").contains("INTEGER") || map.get("COLTYPE").contains("TIMESTMP")){
                        flag=1;
                    }
                    //System.out.print(map.get("NULLS"));
                    fileWriter.write(map.get("NAME") + " " + map.get("COLTYPE").stripTrailing() + "" + bracket_or_no_bracket(flag, 1) + istime(map, map.get("COLTYPE")) + isColtype(map, map.get("COLTYPE")) + bracket_or_no_bracket(flag, 2)
                            + " " + null_or_not(map.get("NULLS")) + ", \n");
                }
                fileWriter.write("CONSTRAINT "+tbname+"_PK "+"PRIMARY KEY (");
                sql = "SELECT NAME FROM SYSIBM.SYSCOLUMNS WHERE TBNAME = '" + cnt.src_table +"' AND  TBCREATOR = '" + cnt.src_schema +"' AND   KEYSEQ > 0 ORDER BY KEYSEQ ASC;";
                stmt = con.prepareCall(sql);
                //System.out.println("after function call");
                stmt.execute();

                metadata = stmt.getMetaData();
                rs = stmt.getResultSet();
                String pkey = "", pkey2;
                while(rs.next()){
                    pkey += rs.getString(1)+",";
                }
                pkey2 = pkey.substring(0, pkey.length() -1);
               fileWriter.write(pkey2+"))");
               fileWriter.flush();
               fileWriter.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }

            stmt.close();
            con.commit();
            con.close();
            System.out.println("Disconnected from data source");
        }
        catch(ClassNotFoundException e){
            System.err.println("Could not load JDBC driver");
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
        catch(SQLException ex){
            System.err.println("SQLException information");
            while(ex!=null) {
                System.err.println ("Error msg: " + ex.getMessage());
                System.err.println ("SQLSTATE: " + ex.getSQLState());
                System.err.println ("Error code: " + ex.getErrorCode());
                ex.printStackTrace();
                ex = ex.getNextException(); // For drivers that support chained exceptions
            }
        }
    }
}

