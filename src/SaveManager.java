import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class SaveManager {
    private static final String SAVE_FILE = "farm_save.txt";

    public static void saveGame(int money, List<Culture> cultures, List<Animal> animals) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            writer.write(money + "\n");

            // Save crops
            for (Culture culture : cultures) {
                writer.write("CULTURE," + culture.getName() + "," + culture.getPricePurchase() + "," +
                        culture.getPriceSell() + "," + culture.getPlantedTime() + "\n");
            }

            // Save animals
            for (Animal animal : animals) {
                writer.write("ANIMAL," + animal.getName() + "," + animal.getBuyPrice() + "," +
                        animal.getSellPrice() + "," + animal.getResourceType() + "," + animal.hasProduced() + "\n");
            }

            System.out.println("Game saved successfully!");
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }

    public static GameState loadGame() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            int money = Integer.parseInt(reader.readLine());
            List<Culture> cultures = new ArrayList<>();
            List<Animal> animals = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equals("CULTURE")) {
                    cultures.add(new Culture(data[1], Integer.parseInt(data[2]),
                            Integer.parseInt(data[3]), Long.parseLong(data[4])));
                } else if (data[0].equals("ANIMAL")) {
                    animals.add(new Animal(data[1], Integer.parseInt(data[2]),
                            Integer.parseInt(data[3]), 2, data[4], Integer.parseInt(data[5])));
                }
            }

            System.out.println("Game loaded successfully!");
            return new GameState(money, cultures, animals);
        } catch (IOException | NumberFormatException e) {
            System.out.println("No save file found. Starting a new game.");
            return new GameState();
        }
    }

    public static void saveGame(GameState gameState) {
    }
}
