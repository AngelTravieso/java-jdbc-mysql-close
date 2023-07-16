package org.atravieso.java.jdbc.repository;

import org.atravieso.java.jdbc.models.Categoria;
import org.atravieso.java.jdbc.models.Producto;
import org.atravieso.java.jdbc.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepositoryImpl implements Repository<Producto> {

    private Connection getConnection() throws SQLException {
        return ConexionBD.getInstance();
    }

    @Override
    public List<Producto> listar() {
        List<Producto> productos = new ArrayList<>();

        try (
                Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT p.*, c.nombre AS categoria FROM productos AS p INNER JOIN categorias AS c ON (p.categoria_id = c.id)")) {

            while(rs.next()) {
                Producto producto = crearProducto(rs);
                productos.add(producto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

    @Override
    public Producto porId(Long id) {

        Producto producto = null;

        try(
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT p.*, c.nombre AS categoria FROM productos AS p INNER JOIN categorias AS c ON (p.categoria_id = c.id) WHERE p.id = ?")) {
            stmt.setLong(1, id); // indice, valor

            try (ResultSet res = stmt.executeQuery()) {
                if (res.next()) {
                    producto = crearProducto(res);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return producto;
    }

    @Override
    public void guardar(Producto producto) {

        String sql;

        // Si hay un registro con ese ID actualizalo
        if (producto.getId() != null && producto.getId() > 0) {
            sql = "UPDATE productos SET nombre=?, precio=?, categoria_id=? WHERE id = ?";
        } else {
            // Si no hay registro, inserta uno nuevo
            sql = "INSERT INTO productos(nombre, precio, categoria_id, fecha_registro) VALUES (?,?,?, ?)";
        }

        try(
                Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Pasar parámetros
            stmt.setString(1, producto.getNombre());
            stmt.setLong(2, producto.getPrecio());
            stmt.setLong(3, producto.getCategoria().getId());

            if (producto.getId() != null && producto.getId() > 0) {
                stmt.setLong(4, producto.getId());
            } else {
                stmt.setDate(4, new Date(producto.getFechaRegistro().getTime()));
            }

            // Ejecutar sentencia
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void eliminar(Long id) {
        try(
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM productos WHERE id = ?")
        ) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private static Producto crearProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        Categoria categoria = new Categoria();
        producto.setId(rs.getLong("ID"));
        producto.setNombre(rs.getString("nombre"));
        producto.setPrecio(rs.getInt("precio"));
        producto.setFechaRegistro(rs.getDate("fecha_registro"));
        categoria.setId(rs.getLong("categoria_id"));

        // viene del alias de "listar" c.nombre AS categoria
        categoria.setNombre(rs.getString("categoria"));

        // relación producto con categoría
        producto.setCategoria(categoria);
        return producto;
    }
}
