package Clases;

import com.toedter.calendar.JDateChooser;
import java.io.File;
import java.io.FileInputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Marisa
 */
public class CConexion {

    Connection conectar = null;
    String usuario = "root";
    String contraseña = "";
    String bd = "bdusuarios";
    String ip = "localhost";
    String puerto = "3306";
    String cadena = "jdbc:mysql://" + ip + ":" + puerto + "/" + bd;

    public Connection estableceConexion() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conectar = DriverManager.getConnection(cadena, usuario, contraseña);
            //JOptionPane.showMessageDialog(null, "se conecto a la BD correctamente");
        } catch (Exception e) {
            //JOptionPane.showMessageDialog(null, "No se conecto a la BD correctamente");

        }
        return conectar;
    }

    public void cerrarConexion() {
        try {
            if (conectar != null && !conectar.isClosed()) {
                conectar.close();
                //JOptionPane.showMessageDialog(null, "Se cerro la conexion");
            }
        } catch (Exception e) {
            //JOptionPane.showMessageDialog(null, "No se cerro la conexion");
        }

    }
}
