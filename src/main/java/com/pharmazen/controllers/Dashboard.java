package com.pharmazen.controllers;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.pharmazen.Medicine;
import com.pharmazen.services.MedicineDatabase;
import com.pharmazen.services.AuthenticationService;
import javafx.event.ActionEvent;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Dashboard implements Initializable {

    public ScrollPane medicineScrollPane;
    @FXML
    private AnchorPane dashboardPane;
    @FXML
    private Text welcomeText;
    @FXML
    private TextField searchField;
    @FXML
    private Button loadMoreButton, profileButton, authorizeButton, cartButton, logoutButton, refreshButton;
    @FXML
    private Button allCategoryButton, painReliefButton, antibioticsButton, vitaminsButton, coldFluButton, diabetesButton;
    @FXML
    private Tile totalMedicinesTile, cartItemsTile, totalSpentTile, availableMedicinesTile, recentOrdersTile;
    @FXML
    private TilePane medicineGrid;

    private List<String> authorizedGenerics = new ArrayList<>();
    private Date authorizationExpiry;

    private final List<Medicine> cartItems = new ArrayList<>();
    private String currentCategory = "All Medicines";
    private double totalSpent = 0.0;

    private String currentUserUid;
    private String currentUserName;
    private String currentUserEmail;
    private boolean currentUserProf;

    private static final int PAGE_SIZE = 100;
    private int currentOffset = 0;
    private boolean allLoaded = false;

    @FXML
    private void handleCategoryFilter(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        currentCategory = clickedButton.getText();

        // Remove 'active-category' from all category buttons
        List<Button> categoryButtons = Arrays.asList(
                allCategoryButton, painReliefButton, antibioticsButton, vitaminsButton, coldFluButton, diabetesButton
        );
        for (Button btn : categoryButtons) {
            btn.getStyleClass().remove("active-category");
            if (!btn.getStyleClass().contains("category-button")) {
                btn.getStyleClass().add("category-button");
            }
        }

        // Add 'active-category' to the clicked one
        if (!clickedButton.getStyleClass().contains("active-category")) {
            clickedButton.getStyleClass().add("active-category");
        }
    }

    @FXML
    private void handleProfile() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pharmazen/profilePage.fxml"));
        Parent root = loader.load();

        Profile controller = loader.getController();
        controller.setUserData(currentUserName, currentUserEmail, currentUserProf, currentUserUid);
        System.out.println("Current Profession: " + currentUserProf);
        Stage stage = new Stage();
        stage.getIcons().add(new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/pharmazen/images/icon.png"))));
        stage.setScene(new Scene(root));
        stage.setTitle("User Profile");
        stage.show();
    }

    @FXML
    void handleAuthorization(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pharmazen/authorization.fxml"));
            Parent root = loader.load();

            Authorization authorizationController = loader.getController();
            authorizationController.setCurrentDoctorUid(currentUserUid);

            Stage stage = new Stage();
            stage.setTitle("Authorization Panel");

            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/pharmazen/images/icon.png")));
            stage.getIcons().add(icon);

            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error message: " + e);
        }
    }

    @FXML
    private void handleCart() {
        System.out.println("Cart clicked");
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pharmazen/loginPage.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) dashboardPane.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/pharmazen/css/loginPage.css")).toExternalForm());

            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/pharmazen/images/icon.png")));
            stage.getIcons().add(icon);

            stage.setTitle("PharmaZen Login");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            System.out.println("Error message: " + e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupStatisticsTiles();
        setupEventHandlers();
        updateWelcomeText();
        activateCategoryButton(allCategoryButton);
    }

    private void preloadAuthorization() {
        try {
            Firestore db = FirestoreClient.getFirestore();
            List<QueryDocumentSnapshot> docs = db.collection("authorizations").whereEqualTo("authorizedTo", currentUserUid).get().get().getDocuments();

            authorizedGenerics.clear(); // reset
            authorizationExpiry = null;

            Date now = new Date();

            System.out.println("Checking authorization for userUid=" + currentUserUid);
            System.out.println("Authorization docs found: " + docs.size());

            for (DocumentSnapshot doc : docs) {
                Date expiry = doc.getDate("validUntil");
                String generic = doc.getString("genericName");

                System.out.println("Doc: " + doc.getId() + ", generic=" + generic + ", expiry=" + expiry);

                if (generic != null && expiry != null && now.before(expiry)) {
                    authorizedGenerics.add(generic.trim().toLowerCase());
                    if (authorizationExpiry == null || expiry.after(authorizationExpiry)) {
                        authorizationExpiry = expiry;
                    }
                }
            }

            System.out.println("Authorized generics for user " + currentUserUid + ": " + authorizedGenerics);
            System.out.println("Authorization expiry: " + authorizationExpiry);

        } catch (Exception e) {
            System.out.println("Failed to preload authorization: " + e.getMessage());
        }
    }



    private void loadMoreMedicines() {
        if (allLoaded) {
            loadMoreButton.setDisable(true);
            loadMoreButton.setText("No more medicines");
            return;
        }

        List<Medicine> medicinesChunk = MedicineDatabase.loadMedicinesChunk(currentOffset, PAGE_SIZE);

        if (medicinesChunk.isEmpty()) {
            allLoaded = true;
            loadMoreButton.setDisable(true);
            loadMoreButton.setText("No more medicines");
            return;
        }

        for (Medicine med : medicinesChunk) {
            VBox card = createMedicineCard(med);
            medicineGrid.getChildren().add(card);
        }

        currentOffset += medicinesChunk.size();

        updateStatistics();
    }

    private void activateCategoryButton(Button button) {
        List<Button> categoryButtons = Arrays.asList(
                allCategoryButton, painReliefButton, antibioticsButton, vitaminsButton, coldFluButton, diabetesButton
        );

        for (Button btn : categoryButtons) {
            btn.getStyleClass().remove("active-category");
            if (!btn.getStyleClass().contains("category-button")) {
                btn.getStyleClass().add("category-button");
            }
        }

        if (!button.getStyleClass().contains("active-category")) {
            button.getStyleClass().add("active-category");
        }
    }


    private void setupStatisticsTiles() {
        totalMedicinesTile = TileBuilder.create().skinType(Tile.SkinType.NUMBER).title("Total Medicines").unit("items").value(0).textColor(Color.web("#2196F3")).backgroundColor(Color.web("#FFFFFF")).build();
        cartItemsTile = TileBuilder.create().skinType(Tile.SkinType.NUMBER).title("Cart Items").unit("items").value(0).textColor(Color.web("#FF9800")).backgroundColor(Color.web("#FFFFFF")).build();
        totalSpentTile = TileBuilder.create().skinType(Tile.SkinType.NUMBER).title("Total Spent").unit("₹").value(0).textColor(Color.web("#4CAF50")).backgroundColor(Color.web("#FFFFFF")).build();
        availableMedicinesTile = TileBuilder.create().skinType(Tile.SkinType.NUMBER).title("Available").unit("in stock").value(0).textColor(Color.web("#00BCD4")).backgroundColor(Color.web("#FFFFFF")).build();
        recentOrdersTile = TileBuilder.create().skinType(Tile.SkinType.NUMBER).title("Recent Orders").unit("this month").value(0).textColor(Color.web("#9C27B0")).backgroundColor(Color.web("#FFFFFF")).build();
    }

    private void setupEventHandlers() {
        searchField.setOnAction(e -> handleSearch());
        allCategoryButton.getStyleClass().add("active-category");
    }

    private void loadAllMedicines() {
        medicineGrid.getChildren().clear();
        List<Medicine> medicines;

        if (!"All Medicines".equals(currentCategory)) {
            medicines = MedicineDatabase.loadMedicinesByCategory(currentCategory);
        } else {
            medicines = MedicineDatabase.loadMedicinesChunk(currentOffset, PAGE_SIZE);
            System.out.println("Total medicine: " + medicines.size());
        }

        updateMedicineGrid(medicines);
        updateStatistics();
    }

    private void updateMedicineGrid(List<Medicine> medicines) {
        for (Medicine medicine : medicines) {
            VBox card = createMedicineCard(medicine);
            medicineGrid.getChildren().add(card);
        }
    }

    private VBox createMedicineCard(Medicine medicine) {
        VBox card = new VBox(10);
        card.getStyleClass().add("medicine-card"); // apply overall card styling
        card.setPadding(new Insets(15));

        Label nameLabel = new Label(medicine.getName());
        nameLabel.getStyleClass().add("brand-name");

        Label genericLabel = new Label(medicine.getGeneric());
        genericLabel.getStyleClass().add("generic-name");

        Label manufactureLabel = new Label(medicine.getManufacturer());
        manufactureLabel.getStyleClass().add("manufacturer");

        Label typeLabel = new Label(medicine.getCategory());
        typeLabel.getStyleClass().add("category-info");

        Label indicationLabel = new Label(medicine.getIndication());

        Label priceLabel = new Label("৳" + String.format("%.2f", medicine.getPrice()));
        priceLabel.getStyleClass().add("price");

        Label stockLabel = new Label(medicine.isAvailable() ? "In Stock (" + medicine.getStock() + ")" : "Out of Stock");
        stockLabel.getStyleClass().add("stock");

        Button addToCartBtn = new Button("Add to Cart");
        Button buyNowBtn = new Button("Buy Now");

        // Sensitive label (conditionally added)
        Label sensitiveLabel = null;
        if ((medicine.getSensitive() && !isAuthorizeToUser(medicine))) {
            sensitiveLabel = new Label("Need Doctor's Approval");
            sensitiveLabel.getStyleClass().add("sensitive-label");
            addToCartBtn.setVisible(false);
            buyNowBtn.setVisible(false);
        }

        addToCartBtn.setOnAction(e -> addToCart(medicine));
        buyNowBtn.setOnAction(e -> buyNow(medicine));

        if (!medicine.isAvailable() || medicine.getStock() <= 0) {
            addToCartBtn.setDisable(true);
            buyNowBtn.setDisable(true);
        }

        HBox buttonBox = new HBox(10, addToCartBtn, buyNowBtn);

        // Add all components to the card
        card.getChildren().addAll(nameLabel, genericLabel, manufactureLabel, typeLabel, indicationLabel, priceLabel, stockLabel);

        if (sensitiveLabel != null) {
            card.getChildren().add(sensitiveLabel);
        }

        card.getChildren().add(buttonBox);

        return card;
    }


    private void updateStatistics() {
        int totalCount = MedicineDatabase.getTotalMedicineCount();
        totalMedicinesTile.setValue(totalCount);
        cartItemsTile.setValue(cartItems.size());
        totalSpentTile.setValue(totalSpent);
        availableMedicinesTile.setValue(MedicineDatabase.getAvailableMedicineCount());
        recentOrdersTile.setValue(5);
    }

    private void addToCart(Medicine medicine) {
        cartItems.add(medicine);
        updateStatistics();
    }

    private void buyNow(Medicine medicine) {
        // TODO: Implement buy now flow
    }

    public void setCurrentUserUid(String uid) throws ExecutionException, InterruptedException {
        this.currentUserUid = uid;
        this.currentUserName = AuthenticationService.getName(uid);
        this.currentUserEmail = AuthenticationService.getEmail(uid);
        this.currentUserProf = AuthenticationService.getProf(uid);

        updateWelcomeText();

        authorizeButton.setVisible(currentUserProf);

        new Thread(() -> {
            preloadAuthorization();
            javafx.application.Platform.runLater(this::loadInitialMedicines);
        }).start();
        loadInitialMedicines();
    }

    public void setCurrentUser(String name, String email) {
        this.currentUserName = name;
        this.currentUserEmail = email;
        updateWelcomeText();
    }

    public void setTotalSpent(double amount) {
        this.totalSpent = amount;
        updateStatistics();
    }

    private void updateWelcomeText() {
        if (currentUserName != null && !currentUserName.isEmpty()) {
            welcomeText.setText("Welcome back, " + currentUserName + "!");
        } else {
            welcomeText.setText("Welcome to PharmaZen!");
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadAllMedicines();
            return;
        }
        medicineGrid.getChildren().clear();
        List<Medicine> results = MedicineDatabase.searchMedicines(query);
        updateMedicineGrid(results);
        updateStatistics();
    }

    @FXML
    private void handleGenericSearch(ActionEvent actionEvent) {
        String query = searchField.getText().trim();
        if(query.isEmpty()){
            loadAllMedicines(); return;
        }
        medicineGrid.getChildren().clear();
        List<Medicine> results_generic = MedicineDatabase.searchByGenericName(query);
        updateMedicineGrid(results_generic);
        updateStatistics();
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        currentCategory = "All Medicines";
        allCategoryButton.getStyleClass().add("active-category");
        painReliefButton.getStyleClass().remove("active-category");
        antibioticsButton.getStyleClass().remove("active-category");
        vitaminsButton.getStyleClass().remove("active-category");
        coldFluButton.getStyleClass().remove("active-category");
        diabetesButton.getStyleClass().remove("active-category");

        searchField.clear();
        loadAllMedicines();
    }

    private boolean isAuthorizeToUser(Medicine medicine) {
//        System.out.println("Checking authorization for userProf=" + currentUserProf);
//        System.out.println("Authorization expiry: " + authorizationExpiry);
//        System.out.println("Authorized generics: " + authorizedGenerics);
        if (currentUserProf) return true;
        if (authorizationExpiry == null || new Date().after(authorizationExpiry)) return false;
        boolean contains = authorizedGenerics.contains(medicine.getGeneric().toLowerCase());
        System.out.println("Medicine generic: " + medicine.getGeneric().toLowerCase() + ", authorized? " + contains);
        return contains;
    }

    private void loadInitialMedicines() {
        currentOffset = 0;
        allLoaded = false;
        medicineGrid.getChildren().clear();
        loadMoreButton.setDisable(false);
        loadMoreButton.setText("Load More");
        loadMoreMedicines();
    }
}