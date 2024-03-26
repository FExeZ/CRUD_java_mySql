/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JDayChooser;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Marisa
 */
public class CUsuario {
    
    int idSexo;
    
    public void establecerIdSexo(int idSexo) {
        this.idSexo = idSexo;
    }
    
    public void MostrarSexoCombo(JComboBox comboSexo) {
        
        Clases.CConexion objetoConexion = new Clases.CConexion();
        String sql = "select * from sexo";
        Statement st;
        
        try {
            st = objetoConexion.estableceConexion().createStatement();
            ResultSet rs = st.executeQuery(sql);
            comboSexo.removeAllItems();
            
            while (rs.next()) {
                String nombreSexo = rs.getString("sexo");
                this.establecerIdSexo(rs.getInt("id"));
                comboSexo.addItem(nombreSexo);
                comboSexo.putClientProperty(nombreSexo, idSexo);
                
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar Sexo: " + e.toString());
        } finally {
            objetoConexion.cerrarConexion();
        }
    }
    
    public void agregarUsuario(JTextField nombres, JTextField apellidos, JComboBox combosexo, JTextField edad, JDateChooser fnacimiento, File foto) {
        
        CConexion objetoConexion = new CConexion();
        String consulta = "insert into usuarios (nombre, apellido, fksexo, edad, fnacimiento, foto) values (?,?,?,?,?,?);";
        try {
            FileInputStream fis = new FileInputStream(foto);
            CallableStatement cs = objetoConexion.estableceConexion().prepareCall(consulta);
            cs.setString(1, nombres.getText());
            cs.setString(2, apellidos.getText());
            int idSexo = (int) combosexo.getClientProperty(combosexo.getSelectedItem());
            cs.setInt(3, idSexo);
            cs.setInt(4, Integer.parseInt(edad.getText()));
            Date fechaSeleccionada = fnacimiento.getDate();
            java.sql.Date fechaSQL = new java.sql.Date(fechaSeleccionada.getTime());
            cs.setDate(5, fechaSQL);
            cs.setBinaryStream(6, fis, (int) foto.length());
            cs.execute();
            JOptionPane.showMessageDialog(null, "Se guardo el usuario correctamente");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.toString() + " No se guardo el usuario");
        } finally {
            objetoConexion.cerrarConexion();
        }
    }
    
    public void mostrarUsuarios(JTable tablaTotalUsuarios) {
        Clases.CConexion objetoConexion = new Clases.CConexion();
        DefaultTableModel modelo = new DefaultTableModel();
        String sql = "";
        modelo.addColumn("ID");
        modelo.addColumn("Nombres");
        modelo.addColumn("Apellidos");
        modelo.addColumn("Sexo");
        modelo.addColumn("Edad");
        modelo.addColumn("Nacimiento");
        modelo.addColumn("Foto");
        
        tablaTotalUsuarios.setModel(modelo);
        
        sql = "select usuarios.id,usuarios.nombre,usuarios.apellido,sexo.sexo, usuarios.edad,usuarios.fnacimiento,usuarios.foto from usuarios inner join sexo on usuarios.fksexo = sexo.id;";
        try {
            Statement st = objetoConexion.estableceConexion().createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                String id = rs.getString("id");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String sexo = rs.getString("sexo");
                String edad = rs.getString("edad");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                java.sql.Date fechaSQL = rs.getDate("fnacimiento");
                String nuevaFecha = sdf.format(fechaSQL);
                byte[] imageBytes = rs.getBytes("foto");
                Image foto = null;
                if (imageBytes != null) {
                    try {
                        ImageIcon imageIcon = new ImageIcon(imageBytes);
                        foto = imageIcon.getImage();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Error: " + e.toString());
                    }
                    
                    modelo.addRow(new Object[]{id, nombre, apellido, sexo, edad, nuevaFecha, foto});
                }
                
                tablaTotalUsuarios.setModel(modelo);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.toString());
        } finally {
            objetoConexion.cerrarConexion();
        }
    }
    
    public void seleccionar(JTable totalUsuarios, JTextField id, JTextField nombre, JTextField apellido, JComboBox sexo, JTextField edad, JDateChooser fnacimiento, JLabel foto) {
        int fila = totalUsuarios.getSelectedRow();
        if (fila >= 0) {
            id.setText(totalUsuarios.getValueAt(fila, 0).toString());
            nombre.setText(totalUsuarios.getValueAt(fila, 1).toString());
            apellido.setText(totalUsuarios.getValueAt(fila, 2).toString());
            sexo.setSelectedItem(totalUsuarios.getValueAt(fila, 3).toString());
            edad.setText(totalUsuarios.getValueAt(fila, 4).toString());
            String fechaString = totalUsuarios.getValueAt(fila, 5).toString();
            Image imagen = (Image) totalUsuarios.getValueAt(fila, 6);
            ImageIcon originalIcon = new ImageIcon(imagen);
            int lblanchura = foto.getWidth();
            int lblaltura = foto.getHeight();
            Image scaledImage = originalIcon.getImage().getScaledInstance(lblanchura, lblaltura, Image.SCALE_SMOOTH);
            foto.setIcon(new ImageIcon(scaledImage));
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date fechaDate = sdf.parse(fechaString);
                fnacimiento.setDate(fechaDate);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.toString());
            }
        }
        
    }
    
    public void modificarUsuario(JTextField id, JTextField nombres, JTextField apellidos, JComboBox combosexo, JTextField edad, JDateChooser fnacimiento, File foto) {
        CConexion objetoConexion = new CConexion();
        String consulta = "update usuarios set usuarios.nombre=?, usuarios.apellido=?, usuarios.fksexo=?,usuarios.edad=?,usuarios.fnacimiento=?,usuarios.foto=? where usuarios.id=?";
        try {
            FileInputStream fis = new FileInputStream(foto);
            CallableStatement cs = objetoConexion.estableceConexion().prepareCall(consulta);
            cs.setString(1, nombres.getText());
            cs.setString(2, apellidos.getText());
            int idSexo = (int) combosexo.getClientProperty(combosexo.getSelectedItem());
            cs.setInt(3, idSexo);
            cs.setInt(4, Integer.parseInt(edad.getText()));
            Date fechaSeleccionada = fnacimiento.getDate();
            java.sql.Date fechaSQL = new java.sql.Date(fechaSeleccionada.getTime());
            cs.setDate(5, fechaSQL);
            cs.setBinaryStream(6, fis, (int) foto.length());
            cs.setInt(7, Integer.parseInt(id.getText()));
            cs.execute();
            JOptionPane.showMessageDialog(null, "Se modifico correctamente");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No se modifico correctamente. Error: " + e.toString());
        } finally {
            objetoConexion.cerrarConexion();
        }
    }
    
    public void eliminarUsuario(JTextField id) {
        CConexion objetoConexion = new CConexion();
        String consulta = "delete from usuarios where usuarios.id=?;";
        try {
            CallableStatement cs = objetoConexion.estableceConexion().prepareCall(consulta);
            cs.setInt(1, Integer.parseInt(id.getText()));
            cs.execute();
            JOptionPane.showMessageDialog(null, "Se elimino correctamente");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No se elimino el registro. Error: " + e.toString());
        } finally {
            objetoConexion.cerrarConexion();
        }
    }
    
    public void limpiarCampos(JTextField id, JTextField nombre, JTextField apellido, JTextField edad, JDateChooser nacimiento, JTextField rutaImagen, JLabel imagenContenido) {
        id.setText("");
        nombre.setText("");
        apellido.setText("");
        edad.setText("");
        Calendar calendario = Calendar.getInstance();
        nacimiento.setDate(calendario.getTime());
        rutaImagen.setText("");
        imagenContenido.setIcon(null);
    }
}
