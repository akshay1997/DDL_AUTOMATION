package mypackage;


import java.io.File;
import java.net.URL;

public class Constants {
    public String src_username = "";
    public String src_password = "";
    public String src_schema = "DBDFMSDR"; // source schema considered for demo
    public String src_table = "CD_FMST_O_CC_PAYOUT_MAP"; // source table considered for demo
    public String src_url = "jdbc:db2://9.212.130.25:38362/CF02CDA1";
    public String dest_url = "jdbc:db2://9.212.133.111:50024/CMDW_PED";
    public String dest_username = "";
    public String dest_password = "";
    public String dest_schema = "TEST"; // destination demo considered for demo
    public String dest_table = "CD_FMST_O_CC_PAYOUT_MAP"; // destination table to be created using the extracted ddl
    URL url = getClass().getResource("Universal-trustore.jks"); // jks file in the same package as the java files
}
