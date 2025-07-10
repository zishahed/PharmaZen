package com.pharmazen;

import java.time.LocalDateTime;

public class Medicine {
    private String id;
    private String name;
    private String category;
    private String indication;
    private double price;
    private int stock;
    private boolean available;
    private String manufacturer;
    private String description;
    private LocalDateTime lastUpdated;
    private String imageUrl;
    private double rating;
    private int reviewCount;
    private String batchNumber;
    private LocalDateTime expiryDate;
    private String prescription;
    private String sideEffects;
    private String generic;
    private boolean Sensitive;

    // Default constructor
    public Medicine() {
        this.lastUpdated = LocalDateTime.now();
        this.available = true;
        this.rating = 0.0;
        this.reviewCount = 0;
    }

    // Constructor with basic fields
    public Medicine(String name, String generic, String category, String indication, double price, int stock, boolean available) {
        this();
        this.name = name;
        this.category = category;
        this.indication = indication;
        this.price = price;
        this.stock = stock;
        this.available = available;
        this.generic = generic;
    }

    // Constructor with all fields
    public Medicine(String id, String name, String category, String indication, double price,
                    int stock, boolean available, String manufacturer, String description,
                    String imageUrl, String batchNumber, LocalDateTime expiryDate,
                    String prescription, String sideEffects, String generic) {
        this();
        this.id = id;
        this.name = name;
        this.category = category;
        this.indication = indication;
        this.price = price;
        this.stock = stock;
        this.available = available;
        this.manufacturer = manufacturer;
        this.description = description;
        this.imageUrl = imageUrl;
        this.batchNumber = batchNumber;
        this.expiryDate = expiryDate;
        this.prescription = prescription;
        this.sideEffects = sideEffects;
        this.generic = generic;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIndication() {
        return indication;
    }

    public void setIndication(String indication) {
        this.indication = indication;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
        this.available = stock > 0;
    }

    public boolean isAvailable() {
        return available && stock > 0;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getSideEffects() {
        return sideEffects;
    }

    public void setSideEffects(String sideEffects) {
        this.sideEffects = sideEffects;
    }

    public String getGeneric() {
        return generic;
    }

    public void setGeneric(String generic) {
        this.generic = generic;
    }

    public boolean getSensitive(){
        return Sensitive;
    }

    public void setSensitive(boolean Sensitive){
        this.Sensitive = Sensitive;
    }

    // Utility methods
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDateTime.now());
    }

    public boolean needsPrescription() {
        return prescription != null && prescription.equalsIgnoreCase("required");
    }

    public String getFormattedPrice() {
        return String.format("₹%.2f", price);
    }

    public String getStockStatus() {
        if (stock <= 0) {
            return "Out of Stock";
        } else if (stock <= 5) {
            return "Low Stock (" + stock + " left)";
        } else {
            return "In Stock (" + stock + " available)";
        }
    }

    public String getRatingStars() {
        StringBuilder stars = new StringBuilder();
        int fullStars = (int) rating;
        boolean hasHalfStar = (rating - fullStars) >= 0.5;

        for (int i = 0; i < fullStars; i++) {
            stars.append("★");
        }

        if (hasHalfStar) {
            stars.append("☆");
        }

        while (stars.length() < 5) {
            stars.append("☆");
        }

        return stars.toString();
    }

    // Method to decrease stock (when purchased)
    public boolean decreaseStock(int quantity) {
        if (stock >= quantity) {
            stock -= quantity;
            if (stock <= 0) {
                available = false;
            }
            lastUpdated = LocalDateTime.now();
            return true;
        }
        return false;
    }

    // Method to increase stock (when restocked)
    public void increaseStock(int quantity) {
        stock += quantity;
        if (stock > 0) {
            available = true;
        }
        lastUpdated = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", indication='" + indication + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", available=" + available +
                ", manufacturer='" + manufacturer + '\'' +
                ", rating=" + rating +
                ", reviewCount=" + reviewCount +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Medicine medicine = (Medicine) obj;
        return id != null ? id.equals(medicine.id) : medicine.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}