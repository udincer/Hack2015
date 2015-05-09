package hack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

/**
 * Created by vincekyi on 5/8/15.
 */
public class PopulateDiseaseTables {
    private static Connection con = null;


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

    public static boolean insertIntoSpecific(int id, String name){
        String insert = "INSERT INTO specific_disease (mimNumber, name) "+
                "values (?, ?)";
        PreparedStatement st = null;
        try {
            st = con.prepareStatement(insert);
            st.setInt(1, id);
            st.setString(2, name);

            st.execute();
            return true;

        } catch (SQLException ex) {


        } finally {
            try {
                if (st != null) {
                    st.close();
                }

            } catch (SQLException ex) {
                return false;
            }
        }
        return false;
    }

    public static boolean insertIntoBroad(String id, String name, int num){
        String insert = "INSERT INTO broad_disease (psNumber, name, numberOfGenes) "+
                "values (?, ?, ?)";
        PreparedStatement st = null;
        try {
            st = con.prepareStatement(insert);
            st.setString(1, id);
            st.setString(2, name);
            st.setInt(3, num);

            st.execute();
            return true;

        } catch (SQLException ex) {


        } finally {
            try {
                if (st != null) {
                    st.close();
                }

            } catch (SQLException ex) {
                return false;
            }
        }
        return false;
    }

    public static boolean insertIntoMapping(int mimNumber, String psNumber){
        String insert = "INSERT INTO broad_specific (psNumber, mimNumber) "+
                "values (?, ?)";
        PreparedStatement st = null;
        try {
            st = con.prepareStatement(insert);
            st.setString(1, psNumber);
            st.setInt(2, mimNumber);

            st.execute();
            return true;

        } catch (SQLException ex) {


        } finally {
            try {
                if (st != null) {
                    st.close();
                }

            } catch (SQLException ex) {
                return false;
            }
        }
        return false;
    }

    public static boolean readAndInsert(String file){
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            for(String line; (line = br.readLine()) != null; ) {
                // process the line.
                String[] tokens = line.split("\t");

                int mimNumber = Integer.parseInt(tokens[0]);
                String psNumber = tokens[1];
                String name = tokens[2];
                int num = Integer.parseInt(tokens[3]);
                insertIntoBroad(psNumber, name, num);
                insertIntoMapping(mimNumber, psNumber);
            }
            return true;
            // line is not visible here.
        }catch(IOException ex){
            ex.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        PopulateDiseaseTables.createConnection();

        PopulateDiseaseTables.readAndInsert("./src/main/resources/OMIM.tsv");

        PopulateDiseaseTables.closeConnection();


    }
}
