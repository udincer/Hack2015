package hack;

import java.sql.*;

/**
 * Created by vincekyi on 5/8/15.
 */
public class AccessDiseaseDB {

    private static Connection con = null;

    public static class PSDisease{
        public String psNumber;
        public String name;
    }

    public static boolean createConnection(){


        String url = "jdbc:mysql://104.236.142.82:3306/copa";
        String user = "root";
        String password = "peptidemsms";

        try {

            if(con!=null && !con.isClosed()) {
                return true;
            }
            con = DriverManager.getConnection(url, user, password);
            return true;
        } catch (SQLException ex) {

            return false;
        }

    }

    public static boolean closeConnection(){

        try {
            if (con != null) {
                con.close();
                return true;
            }

        } catch (SQLException ex) {
            return false;
        }

        return false;

    }

    public static PSDisease getPSNumber(int mimNumber){
        String query = "SELECT b.psNumber, b.name FROM broad_specific a JOIN broad_disease b " +
                "on a.psNumber=b.psNumber WHERE "+
                "a.mimNumber=";
        Statement st = null;
        ResultSet rs = null;
        try {
            st = con.createStatement();

            rs = st.executeQuery(query+Integer.toString(mimNumber));

            if(rs.next()) {
                PSDisease d = new PSDisease();
                d.psNumber = rs.getString(1);
                d.name = rs.getString(2);

                return d;
            }

        } catch (SQLException ex) {


        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }

            } catch (SQLException ex) {
                return null;
            }
        }
        return null;
    }

    public static int getNumGenes(String psNumber){
        String query = "SELECT numberOfGenes FROM broad_disease WHERE "+
                "psNumber=\"";
        Statement st = null;
        ResultSet rs = null;
        try {
            st = con.createStatement();
        //Todo need to sanitize sql query
            rs = st.executeQuery(query+psNumber+"\"");

            if(rs.next())
                return rs.getInt(1);

        } catch (SQLException ex) {


        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }

            } catch (SQLException ex) {
                return -1;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        AccessDiseaseDB.createConnection();
        PSDisease d = AccessDiseaseDB.getPSNumber(609782);
        System.out.println(d.psNumber+" "+d.name);
        System.out.println(AccessDiseaseDB.getNumGenes("PS100070"));
        AccessDiseaseDB.closeConnection();

        AccessDiseaseDB.createConnection();
        PSDisease e = AccessDiseaseDB.getPSNumber(609782);
        System.out.println(e.psNumber+" "+e.name);
        System.out.println(AccessDiseaseDB.getNumGenes("PS100070"));
        AccessDiseaseDB.closeConnection();
    }
}
