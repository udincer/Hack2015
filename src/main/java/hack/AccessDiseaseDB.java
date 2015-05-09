package hack;

import java.sql.*;

/**
 * Created by vincekyi on 5/8/15.
 */
public class AccessDiseaseDB {

    private static Connection con = null;


    public static boolean createConnection(){

        if(con!=null)
            return true;

        String url = "jdbc:mysql://104.236.142.82:3306/copa";
        String user = "root";
        String password = "peptidemsms";

        try {
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

    public static String getPSNumber(int mimNumber){
        String query = "SELECT psNumber FROM broad_specific WHERE "+
                "mimNumber=";
        Statement st = null;
        ResultSet rs = null;
        try {
            st = con.createStatement();

            rs = st.executeQuery(query+Integer.toString(mimNumber));

            if(rs.next())
                return rs.getString(1);

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

        System.out.println(AccessDiseaseDB.getPSNumber(609782));
        System.out.println(AccessDiseaseDB.getNumGenes("PS100070"));
        AccessDiseaseDB.closeConnection();
    }
}
