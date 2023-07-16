package org.atravieso.java.jdbc;

import org.atravieso.java.jdbc.models.Producto;
import org.atravieso.java.jdbc.repository.ProductoRepositoryImpl;
import org.atravieso.java.jdbc.repository.Repository;
import org.atravieso.java.jdbc.util.ConexionBD;

import java.sql.Connection;
import java.sql.SQLException;

public class EjemploJdbcDelete {
    public static void main(String[] args) {

        Repository<Producto> repository = new ProductoRepositoryImpl();
        System.out.println("========================== LISTAR ==================");

        repository.listar().forEach(System.out::println);

        System.out.println("=================== OBTENER POR ID ==================");
        System.out.println(repository.porId(3L));

        System.out.println("================= ELIMINAR PRODUCTO ===========");
        repository.eliminar(3L);
        System.out.println("Producto eliminado con éxito");
        repository.listar().forEach(System.out::println);
    }
}