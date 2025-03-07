import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameController {
    @FXML private Label moneyLabel;
    @FXML private GridPane farmGrid;
    @FXML private Label selectedSeedLabel;
    @FXML private ComboBox<String> animalSelector;
    private static final int FARM_SIZE = 5;
    private Plot[][] farm;
    private Player player;
    private AnimalPen animalPen;
    private List<Culture> availableCultures;
    private List<Animal> availableAnimals;
    private String selectedSeed = "Wheat";
    private final HashMap<String, Integer> inventory = new HashMap<>();
    private final HashMap<String, Integer> seedInventory = new HashMap<>();
    private static final int CELL_SIZE = 150;
    private String selectedAnimal = null;
    private HashMap<String, String> animalEmojis = new HashMap<>() {{
        put("Cow", "🐄");
        put("Chicken", "🐔");
        put("Sheep", "🐑");
    }};


    public void initialize() {

        financialDashboard = new FinancialDashboard();
        player = new Player(500);
        farm = new Plot[FARM_SIZE][FARM_SIZE];
        animalPen = new AnimalPen();
        availableCultures = new ArrayList<>();
        availableAnimals = new ArrayList<>();

        for (int i = 0; i < FARM_SIZE; i++) {
            for (int j = 0; j < FARM_SIZE; j++) {
                farm[i][j] = new Plot();
                Button cellButton = new Button("🟩 Empty");
                cellButton.getStyleClass().add("grid-cell");
                cellButton.getStyleClass().add("empty-cell");
                final int row = i, col = j;
                cellButton.setOnAction(e -> handleCellClick(row, col, cellButton));
                farmGrid.add(cellButton, j, i);
            }
        }
        seedInventory.put("Wheat", 0);
        seedInventory.put("Corn", 0);
        seedInventory.put("Carrot", 0);
        animalSelector.setOnAction(event -> {
            selectedAnimal = animalSelector.getValue();
            if (selectedAnimal != null) {
                selectedSeed = null;
                selectedSeedLabel.setText("🌱 Selected Seed: None");
            }
            System.out.println("Animal sélectionné : " + selectedAnimal);
        });
        updateFarmDisplay();
    }

    private void handleCellClick(int i, int j, Button cellButton) {
        Plot plot = farm[i][j];

        if (selectedAnimal != null && !plot.isOccupied()) {
            handleAnimalClick(i, j, cellButton);
            selectedAnimal = null;
        } else if (selectedSeed != null && !plot.isOccupied()) {
            if (seedInventory.containsKey(selectedSeed) && seedInventory.get(selectedSeed) > 0) {
                plant(i, j);
            }
        } else if (plot.getAnimal() != null) {
            cellButton.getContextMenu().show(cellButton,
                    javafx.stage.Screen.getPrimary().getVisualBounds().getMinX(),
                    javafx.stage.Screen.getPrimary().getVisualBounds().getMinY());
        } else if (plot.getCulture() != null && plot.getCulture().isMature()) {
            harvest(i, j);
        }
    }


    private Culture getSelectedSeed() {
        switch (selectedSeed) {
            case "Wheat":
                return new Culture("Wheat", 10, 30, 10000);  // 10 secondes
            case "Corn":
                return new Culture("Corn", 20, 50, 15000);   // 15 secondes
            case "Carrot":
                return new Culture("Carrot", 15, 40, 8000);  // 8 secondes
            default:
                return null;
        }
    }

    private void startGrowthTimer(final Plot[][] farm, final int i, final int j, final Button cellButton, final long growthTime) {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Plot plot = farm[i][j];
                if (plot != null && plot.getCulture() != null) {
                    Culture culture = plot.getCulture();
                    long timeLeft = growthTime - (System.currentTimeMillis() - culture.getPlantedTime());

                    if (timeLeft <= 0) {
                        Platform.runLater(() -> {
                            culture.setMature(true);
                            cellButton.setText("🌾 Récolter");
                            cellButton.getStyleClass().clear();
                            cellButton.getStyleClass().add("grid-cell");
                            // Ajouter la classe spécifique pour la récolte
                            switch (culture.getName().toLowerCase()) {
                                case "wheat":
                                    cellButton.getStyleClass().add("wheat-harvest");
                                    break;
                                case "corn":
                                    cellButton.getStyleClass().add("corn-harvest");
                                    break;
                                case "carrot":
                                    cellButton.getStyleClass().add("carrot-harvest");
                                    break;
                            }
                        });
                        timer.cancel();
                    } else {
                        Platform.runLater(() -> {
                            cellButton.setText("🌱 " + (timeLeft / 1000) + "s");
                            cellButton.getStyleClass().clear();
                            cellButton.getStyleClass().add("grid-cell");

                            switch (culture.getName().toLowerCase()) {
                                case "wheat":
                                    cellButton.getStyleClass().add("wheat-growing");
                                    break;
                                case "corn":
                                    cellButton.getStyleClass().add("corn-growing");
                                    break;
                                case "carrot":
                                    cellButton.getStyleClass().add("carrot-growing");
                                    break;
                            }
                        });
                    }
                } else {
                    timer.cancel();
                }
            }
        }, 0, 1000);
    }
    @FXML
    private void chooseSeed() {
        final Stage seedStage = new Stage();
        VBox seedLayout = new VBox(10);
        final ListView<String> seedList = new ListView<>();
        for (String seed : seedInventory.keySet()) {
            if (seedInventory.get(seed) > 0) {
                seedList.getItems().add(seed);
            }
        }
        Button selectButton = new Button("Select");
        selectButton.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String selected = seedList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    selectedSeed = selected.trim();
                    selectedSeedLabel.setText("🌱 Selected Seed: " + selectedSeed);
                    System.out.println("✔ Selected seed: " + selectedSeed);
                    seedStage.close();
                }
            }
        });
        seedLayout.getChildren().addAll(seedList, selectButton);
        seedStage.setScene(new Scene(seedLayout, 300, 200));
        seedStage.setTitle("Choose Seed");
        seedStage.show();
    }

    @FXML
    public void openSeedStore() {
        final Stage seedStoreStage = new Stage();
        VBox seedLayout = new VBox(10);
        seedLayout.setPadding(new Insets(10));

        final ListView<String> seedList = new ListView<>();
        final ComboBox<Integer> quantitySelector = new ComboBox<>(); // Renamed from quantityBox


        quantitySelector.getItems().addAll(1, 5, 10, 20);
        quantitySelector.setValue(1);

        List<String> availableSeedsList = new ArrayList<>();
        availableSeedsList.add("Wheat - 10💰");
        availableSeedsList.add("Corn - 20💰");
        availableSeedsList.add("Carrot - 15💰");
        seedList.getItems().addAll(availableSeedsList);


        Label quantityLabel = new Label("Quantité:");
        HBox quantityContainer = new HBox(10); // Renamed from quantityBox
        quantityContainer.getChildren().addAll(quantityLabel, quantitySelector);

        Button buyButton = new Button("Buy");
        buyButton.setOnAction(e -> {
            String selectedSeedItem = seedList.getSelectionModel().getSelectedItem();
            if (selectedSeedItem != null) {
                String seedName = selectedSeedItem.split(" - ")[0];
                processSeedPurchase(seedName, quantitySelector.getValue());
                seedStoreStage.close();
            }
        });

        seedLayout.getChildren().addAll(
                new Label("🌱 Seed Store"),
                seedList,
                quantityContainer,
                buyButton
        );

        Scene scene = new Scene(seedLayout, 300, 250);
        seedStoreStage.setScene(scene);
        seedStoreStage.setTitle("Seed Store");
        seedStoreStage.show();
    }


    private void processSeedPurchase(String seed, int quantity) {
        int unitPrice = switch (seed) {
            case "Wheat" -> 10;
            case "Corn" -> 20;
            case "Carrot" -> 15;
            default -> 0;
        };

        int totalPrice = unitPrice * quantity;

        if (player.spendMoney(totalPrice)) {
            seedInventory.put(seed, seedInventory.getOrDefault(seed, 0) + quantity);
            financialDashboard.addExpense(seed + " seeds x" + quantity, totalPrice);
            selectedSeed = seed;
            selectedSeedLabel.setText("🌱 Selected Seed: " + selectedSeed);
            System.out.println("✔ Purchased " + quantity + " " + selectedSeed + " seeds");
        } else {
            System.out.println("❌ Not enough money to buy " + quantity + " " + seed + "!");
        }
        updateFarmDisplay();
    }

    private void updateFarmDisplay() {
        farmGrid.getChildren().clear();
        moneyLabel.setText("Money: " + player.getMoney() + "💰");

        for (int i = 0; i < FARM_SIZE; i++) {
            for (int j = 0; j < FARM_SIZE; j++) {
                Plot plot = farm[i][j];
                Button btn = new Button();
                btn.setMinSize(CELL_SIZE, CELL_SIZE);
                btn.setPrefSize(CELL_SIZE, CELL_SIZE);
                btn.setMaxSize(CELL_SIZE, CELL_SIZE);
                btn.getStyleClass().add("grid-cell");

                final int row = i, col = j;
                if (plot.getAnimal() != null) {
                    Animal animal = plot.getAnimal();
                    btn.setText(animalEmojis.get(animal.getName()));
                    btn.getStyleClass().add("animal-cell");

                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem collectItem = new MenuItem("Collecter les ressources");
                    MenuItem removeItem = new MenuItem("Retirer l'animal");

                    collectItem.setOnAction(e -> collectResources(row, col));
                    removeItem.setOnAction(e -> removeAnimal(row, col));

                    contextMenu.getItems().addAll(collectItem, removeItem);
                    btn.setContextMenu(contextMenu);
                } else if (plot.getCulture() != null) {
                    Culture culture = plot.getCulture();
                    String harvestEmoji = "";
                    switch (culture.getName().toLowerCase()) {
                        case "corn":
                            harvestEmoji = "🌽";
                            break;
                        case "wheat":
                            harvestEmoji = "🌾";
                            break;
                        case "carrot":
                            harvestEmoji = "🥕";
                            break;
                        default:
                            harvestEmoji = "🌾";
                    }

                    if (culture.isMature()) {
                        btn.setText(harvestEmoji + " Harvest");
                        btn.getStyleClass().add("harvest-cell");
                        btn.setOnAction(e -> harvest(row, col));
                    } else {
                        btn.setText("🌱 Growing");
                        btn.getStyleClass().add("growing-cell");
                    }
                } else {
                    btn.setText("🟩 Empty");
                    btn.getStyleClass().add("empty-cell");
                    btn.setOnAction(e -> handleCellClick(row, col, btn));
                }
                farmGrid.add(btn, col, row);
            }
        }
    }

    private void harvest(int row, int col) {
        Plot plot = farm[row][col];
        if (plot.getCulture() != null && plot.getCulture().isMature()) {
            String cropName = plot.getCulture().getName();
            inventory.put(cropName, inventory.getOrDefault(cropName, 0) + 1);
            plot.harvest();
            updateFarmDisplay();
            System.out.println("✔ Harvested: " + cropName + " added to inventory.");
        } else {
            System.out.println("❌ No mature crop to harvest at (" + row + ", " + col + ").");
        }
    }

    private void plant(int row, int col) {
        Plot plot = farm[row][col];
        if (!plot.isOccupied()) {
            if (seedInventory.containsKey(selectedSeed) && seedInventory.get(selectedSeed) > 0) {
                Culture cropToPlant = getSelectedSeed();
                if (cropToPlant != null) {
                    plot.plant(cropToPlant);
                    seedInventory.put(selectedSeed, seedInventory.get(selectedSeed) - 1);
                    Button cellButton = (Button) farmGrid.getChildren().get(row * FARM_SIZE + col);
                    startGrowthTimer(farm, row, col, cellButton, cropToPlant.getGrowthTime());
                    updateFarmDisplay();
                    System.out.println("✔ Planted: " + selectedSeed + " at (" + row + ", " + col + ").");
                }
            } else {
                System.out.println("❌ No " + selectedSeed + " seeds available in stock!");
            }
        } else {
            System.out.println("❌ Plot at (" + row + ", " + col + ") is already occupied.");
        }
    }

    @FXML
    public void openInventory(ActionEvent actionEvent) {
        Stage inventoryStage = new Stage();
        VBox inventoryLayout = new VBox(10);
        final ListView<String> inventoryList = new ListView<>();
        for (String item : inventory.keySet()) {
            inventoryList.getItems().add(item + " : " + inventory.get(item));
        }
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> inventoryStage.close());
        inventoryLayout.getChildren().addAll(inventoryList, closeButton);
        inventoryStage.setScene(new Scene(inventoryLayout, 300, 200));
        inventoryStage.setTitle("Inventory");
        inventoryStage.show();
    }

    @FXML
    public void openAnimalStore(ActionEvent actionEvent) {
        Stage animalStoreStage = new Stage();
        VBox animalStoreLayout = new VBox(10);
        final ListView<String> animalList = new ListView<>();
        List<String> animalOptions = new ArrayList<>();
        animalOptions.add("Cow - 100💰");
        animalOptions.add("Chicken - 50💰");
        animalOptions.add("Sheep - 75💰");
        animalList.getItems().addAll(animalOptions);
        Button buyButton = new Button("Buy");
        buyButton.setOnAction(e -> {
            String selectedAnimal = animalList.getSelectionModel().getSelectedItem();
            if (selectedAnimal != null) {
                processAnimalPurchase(selectedAnimal);
                animalStoreStage.close();
            }
        });
        animalStoreLayout.getChildren().addAll(animalList, buyButton);
        animalStoreStage.setScene(new Scene(animalStoreLayout, 300, 200));
        animalStoreStage.setTitle("Animal Store");
        animalStoreStage.show();
    }

    private void processAnimalPurchase(String selectedAnimal) {
        int price = 0;
        String animalName = "";
        if (selectedAnimal.contains("Cow")) {
            price = 100;
            animalName = "Cow";
        } else if (selectedAnimal.contains("Chicken")) {
            price = 50;
            animalName = "Chicken";
        } else if (selectedAnimal.contains("Sheep")) {
            price = 75;
            animalName = "Sheep";
        }

        if (!animalName.isEmpty() && player.spendMoney(price)) {
            Animal newAnimal = new Animal(animalName, price, price + 20, 2, animalName + " Product", 5);
            animalPen.addAnimal(newAnimal);
            financialDashboard.addExpense(animalName + " purchase", price);
            selectedAnimal = animalName;
            System.out.println("✔ Purchased: " + animalName + " added to the farm!");
        } else {
            System.out.println("❌ Not enough money to buy " + animalName + "!");
        }
        updateFarmDisplay();
    }

    @FXML
    public void openSellStore() {
        Stage sellStage = new Stage();
        VBox sellLayout = new VBox(10);
        final ListView<String> sellList = new ListView<>();
        final ComboBox<Integer> quantityBox = new ComboBox<>();

        // Ajouter les quantités possibles
        quantityBox.setValue(1);

        for (String item : inventory.keySet()) {
            if (inventory.get(item) > 0) {
                sellList.getItems().add(item + " : " + inventory.get(item));
                // Mettre à jour les quantités possibles en fonction du stock
                quantityBox.getItems().clear();
                for (int i = 1; i <= inventory.get(item); i++) {
                    quantityBox.getItems().add(i);
                }
            }
        }

        Label quantityLabel = new Label("Quantité à vendre:");
        HBox quantityHBox = new HBox(10);
        quantityHBox.getChildren().addAll(quantityLabel, quantityBox);

        Button sellButton = new Button("Sell");
        sellButton.setOnAction(e -> {
            String selectedItem = sellList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                String product = selectedItem.split(" : ")[0];
                sellProduct(product, quantityBox.getValue());
                sellStage.close();
            }
        });

        sellLayout.getChildren().addAll(sellList, quantityHBox, sellButton);
        sellStage.setScene(new Scene(sellLayout, 300, 250));
        sellStage.setTitle("Sell Store");
        sellStage.show();
    }

    private void sellProduct(String product, int quantity) {
        int unitPrice = switch (product) {
            case "Wheat" -> 30;
            case "Corn" -> 50;
            case "Carrot" -> 40;
            default -> 0;
        };

        if (inventory.getOrDefault(product, 0) >= quantity) {
            int totalPrice = unitPrice * quantity;
            inventory.put(product, inventory.get(product) - quantity);
            player.earnMoney(totalPrice);
            financialDashboard.addIncome(product + " sale x" + quantity, totalPrice);
            updateFarmDisplay();
            System.out.println("✔ Sold " + quantity + " " + product + " for " + totalPrice + "💰!");
        } else {
            System.out.println("❌ Not enough " + product + " in stock!");
        }
    }
    public void saveGame() {
        // Example: Save game logic
        GameState gameState = new GameState(player.getMoney(), availableCultures, availableAnimals);
        SaveManager.saveGame(gameState);
        System.out.println("Game saved!");
    }

    @FXML
    public void feedAnimals(ActionEvent actionEvent) {
        if (animalPen != null && !animalPen.getAnimals().isEmpty()) {
            if (animalPen.feedAnimals(10)) { //
                System.out.println("✔ Animals have been fed!");
            } else {
                System.out.println("❌ Not enough food!");
            }
            updateFarmDisplay();
        } else {
            System.out.println("❌ No animals to feed!");
        }
    }
    private void handleAnimalClick(int i, int j, Button cellButton) {
        Plot plot = farm[i][j];

        if (selectedAnimal != null && !plot.isOccupied()) {
            // Placer un animal
            Animal animal = new Animal(selectedAnimal, 0, 0, 2, "Product", 5);
            plot.placeAnimal(animal);
            cellButton.setText(animalEmojis.get(selectedAnimal));
            cellButton.getStyleClass().clear();
            cellButton.getStyleClass().addAll("grid-cell", "animal-cell");

            // Configuration du menu contextuel
            ContextMenu contextMenu = new ContextMenu();
            MenuItem collectItem = new MenuItem("Collecter les ressources");
            MenuItem removeItem = new MenuItem("Retirer l'animal");

            collectItem.setOnAction(e -> collectResources(i, j));
            removeItem.setOnAction(e -> removeAnimal(i, j));

            contextMenu.getItems().addAll(collectItem, removeItem);
            cellButton.setContextMenu(contextMenu);
        }
    }

    private void collectResources(int i, int j) {
        Plot plot = farm[i][j];
        if (plot.getAnimal() != null) {
            Animal animal = plot.getAnimal();
            if (animal.hasProduced()) {
                int resources = animal.collectResources();
                String resourceType = animal.getName() + " Product";
                inventory.put(resourceType, inventory.getOrDefault(resourceType, 0) + resources);
                // Ajouter au tableau de bord comme revenu
                financialDashboard.addIncome(resourceType + " collection", resources * 5); // Prix arbitraire par ressource
                System.out.println("Ressources collectées: " + resources + " " + resourceType);
            } else {
                System.out.println("L'animal n'a pas encore produit de ressources");
            }
        }
        updateFarmDisplay();
    }

    private void removeAnimal(int i, int j) {
        Plot plot = farm[i][j];
        if (plot.getAnimal() != null) {
            Animal removedAnimal = plot.removeAnimal();
            System.out.println("Animal retiré: " + removedAnimal.getName());
        }
        updateFarmDisplay();
    }
    private FinancialDashboard financialDashboard = new FinancialDashboard();

    @FXML
    public void openFinancialDashboard() {
        Stage dashboardStage = new Stage();
        VBox dashboardLayout = new VBox(10);
        dashboardLayout.setPadding(new Insets(10));
        dashboardLayout.setStyle("-fx-background-color: white;");

        // Titre
        Label titleLabel = new Label("📊 Tableau de Bord Financier");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Indicateurs principaux
        HBox mainIndicators = new HBox(20);
        VBox incomeBox = new VBox(5);
        incomeBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5;");
        Label incomeTitleLabel = new Label("💰 Revenus Totaux");
        incomeTitleLabel.setStyle("-fx-font-size: 14px;");
        Label incomeValueLabel = new Label(financialDashboard.getTotalIncome() + "💰");
        incomeValueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        incomeBox.getChildren().addAll(incomeTitleLabel, incomeValueLabel);

        VBox expensesBox = new VBox(5);
        expensesBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5;");
        Label expensesTitleLabel = new Label("💸 Dépenses Totales");
        expensesTitleLabel.setStyle("-fx-font-size: 14px;");
        Label expensesValueLabel = new Label(financialDashboard.getTotalExpenses() + "💰");
        expensesValueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        expensesBox.getChildren().addAll(expensesTitleLabel, expensesValueLabel);

        VBox profitBox = new VBox(5);
        profitBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5;");
        Label profitTitleLabel = new Label("📈 Bénéfices");
        profitTitleLabel.setStyle("-fx-font-size: 14px;");
        Label profitValueLabel = new Label(financialDashboard.getProfit() + "💰");
        profitValueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        profitBox.getChildren().addAll(profitTitleLabel, profitValueLabel);

        mainIndicators.getChildren().addAll(incomeBox, expensesBox, profitBox);


        Label salesTitle = new Label("Historique des Ventes");
        salesTitle.setStyle("-fx-font-weight: bold;");
        ListView<String> salesList = new ListView<>();
        HashMap<String, Integer> sales = new HashMap<>(financialDashboard.getSalesHistory());
        for (Map.Entry<String, Integer> entry : sales.entrySet()) {
            salesList.getItems().add(entry.getKey() + ": " + entry.getValue() + "💰");
        }


        Label expensesTitle = new Label("Historique des Dépenses");
        expensesTitle.setStyle("-fx-font-weight: bold;");
        ListView<String> expensesList = new ListView<>();
        HashMap<String, Integer> expenses = new HashMap<>(financialDashboard.getExpensesHistory());
        for (Map.Entry<String, Integer> entry : expenses.entrySet()) {
            expensesList.getItems().add(entry.getKey() + ": " + entry.getValue() + "💰");
        }

        Button closeButton = new Button("Fermer");
        closeButton.setOnAction(e -> dashboardStage.close());

        dashboardLayout.getChildren().addAll(
                titleLabel,
                mainIndicators,
                salesTitle,
                salesList,
                expensesTitle,
                expensesList,
                closeButton
        );

        Scene scene = new Scene(dashboardLayout, 400, 600);
        dashboardStage.setScene(scene);
        dashboardStage.setTitle("Tableau de Bord Financier");
        dashboardStage.show();
    }

}


