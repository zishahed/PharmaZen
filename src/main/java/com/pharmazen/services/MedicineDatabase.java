package com.pharmazen.services;

import com.pharmazen.Medicine;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MedicineDatabase {

    private static String DB_URL = "jdbc:sqlite:db/medicines.db";

    static {
        try {
            URL resource = MedicineDatabase.class.getResource("/db/medicines.db");
            if (resource != null) {
                DB_URL = "jdbc:sqlite:" + resource.toURI().getPath();
            } else {
                System.err.println("Database file not found at /db/medicines.db");
                DB_URL = "jdbc:sqlite::memory:"; // fallback or throw exception
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e);
        }
    }

    // Load all medicines without pagination
    public static List<Medicine> loadMedicinesChunk(int offset, int limit) {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicines LIMIT ? OFFSET ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            stmt.setInt(2, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    medicines.add(createMedicineFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error message: " + e);
        }
        return medicines;
    }


    // Load medicines filtered by category
    public static List<Medicine> loadMedicinesByCategory(String category) {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE category = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    medicines.add(createMedicineFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicines;
    }

    // Search medicines by name or indication (case-insensitive)
    public static List<Medicine> searchMedicines(String query) {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE LOWER(brand_name) LIKE ? OR LOWER(dosage_form) LIKE ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String likeQuery = "%" + query.toLowerCase() + "%";
            stmt.setString(1, likeQuery);
            stmt.setString(2, likeQuery);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    medicines.add(createMedicineFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error for search: " + e);
        }
        return medicines;
    }

    public static List<Medicine> searchByGenericName(String query) {
        List<Medicine> medicines = new ArrayList<>();
        String sql_generic = "SELECT * FROM medicines WHERE LOWER(generic_name) LIKE ? OR LOWER(type) LIKE ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt_generic = conn.prepareStatement(sql_generic)) {

            String likeQuery = "%" + query.toLowerCase() + "%";
            stmt_generic.setString(1, likeQuery);
            stmt_generic.setString(2, likeQuery);

            try (ResultSet rs = stmt_generic.executeQuery()) {
                while (rs.next()) {
                    medicines.add(createMedicineFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error for search: " + e);
        }
        return medicines;
    }



    // Get total number of medicines
    public static int getTotalMedicineCount() {
        String sql = "SELECT COUNT(*) FROM medicines";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get count of medicines available in stock
    public static int getAvailableMedicineCount() {
        String sql = "SELECT COUNT(*) FROM medicines WHERE stock > 0";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Helper method to map a ResultSet row to Medicine object
    private static Medicine createMedicineFromResultSet(ResultSet rs) throws SQLException {
        Medicine medicine = new Medicine();
        // ID and Name
        medicine.setId(rs.getString("brand_id"));
        medicine.setName(rs.getString("brand_name"));
        medicine.setManufacturer(rs.getString("manufacturer"));
        medicine.setGeneric(rs.getString("generic_name"));

        // Using dosage_form as indication (can be adjusted)
        medicine.setIndication(rs.getString("dosage_form"));

        // Using type or generic_name as category (choose what suits better)
        medicine.setCategory(rs.getString("type"));

        // Is the Drug is sensitive and need Doctors approval
        medicine.setSensitive(rs.getBoolean("isSensitive"));

        // Simulated price — since DB has no price, you may use a placeholder or compute based on some logic
        medicine.setPrice(99.0); // Placeholder value

        // Simulated stock — DB has no stock column, so we'll randomly assign or default
        int stock = 10; // Placeholder
        medicine.setStock(stock);
        medicine.setAvailable(true);

        return medicine;
    }
}
