/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Annos;


/**
 *
 * @author anitapenttila
 */
public class AnnosDao implements Dao<Annos, Integer> {
    
    private Database database;

    public AnnosDao(Database database) {
        this.database = database;
    }

    @Override
    public Annos findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Annos WHERE id = ?");
        stmt.setInt(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");

        Annos a = new Annos(id, nimi);

        rs.close();
        stmt.close();
        connection.close();

        return a;
    }

    @Override
    public List<Annos> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Annos");

        ResultSet rs = stmt.executeQuery();
        List<Annos> annokset = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");

            annokset.add(new Annos(id, nimi));
        }

        rs.close();
        stmt.close();
        connection.close();

        return annokset;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Annos WHERE id = ?");
        stmt.setInt(1, key);
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }
    public Annos saveOrUpdate(Annos object) throws SQLException {

        if (object.getId()== null) {
            return save(object);
        } else {
            return update(object);
        }
    }
    private Annos save(Annos annos) throws SQLException {
        
        String nimi = annos.getNimi();
        String[] palat = nimi.split(" ");
        String siistittyNimi = "";
        for (String palat1 : palat) {
            siistittyNimi += siistiMerkkijono(palat1.trim()) + " ";
        }
        annos.setNimi(siistittyNimi.trim());
        
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Annos"
                + " WHERE nimi = ?");
        stmt.setString(1, annos.getNimi());

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            stmt.close();
            return null;
        }
        stmt.close();

        stmt = conn.prepareStatement("INSERT INTO Annos"
                + " (nimi)"
                + " VALUES (?)");
        stmt.setString(1, annos.getNimi());

        stmt.executeUpdate();
        stmt.close();

        stmt = conn.prepareStatement("SELECT * FROM Annos"
                + " WHERE nimi = ?");
        stmt.setString(1, annos.getNimi());

        rs = stmt.executeQuery();
        rs.next(); 

        Annos a = new Annos(rs.getInt("id"), rs.getString("nimi"));

        stmt.close();
        rs.close();

        conn.close();

        return a;
    }

    private Annos update(Annos annos) throws SQLException {

        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE Annos SET"
                + " nimi = ? WHERE id = ?");
        stmt.setString(1, annos.getNimi());
        stmt.setInt(2, annos.getId());

        stmt.executeUpdate();

        stmt.close();
        conn.close();

        return annos;
    }
    
    public Annos findByName(String key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Annos WHERE nimi = ?");
        stmt.setString(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");

        Annos a = new Annos(id, nimi);

        rs.close();
        stmt.close();
        connection.close();

        return a;
    }
    private String siistiMerkkijono (String merkkijono) {
        if (merkkijono.length() == 0) {
            return "";
        }
        if (merkkijono.length() == 1) {
            return merkkijono.toUpperCase();
        }

        return merkkijono.substring(0,1).toUpperCase() + merkkijono.substring(1).toLowerCase();
    }
    
}
