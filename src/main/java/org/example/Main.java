package org.example;
import org.example.models.Expense;
import java.sql.*;
import java.util.Scanner;

public class Main {
    public static final String EXPENSE_TABLE = "expenses";

    public static void main(String[] args) {
        String url = "jdbc:sqlite:expenseTracker.db";
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " +
                EXPENSE_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "description TEXT, " +
                "amount REAL, " +
                "category TEXT)";

        String insertRecordSQL = "INSERT INTO " + EXPENSE_TABLE +
                "(description, amount, category) VALUES (?, ?, ?)";

        String selectData = "SELECT * FROM " + EXPENSE_TABLE;
        String updateRecordSQL = "UPDATE " + EXPENSE_TABLE +
                " SET description=?, amount=?, category=? WHERE id=?";
        String deleteRecordSQL = "DELETE FROM " + EXPENSE_TABLE + " WHERE id=?";

        try (Connection connection = DriverManager.getConnection(url)) {
            System.out.println("Connected");

            Statement statement = connection.createStatement();
            statement.execute(createTableSQL);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Choose operation: 1 - Add Expense, 2 - Update Expense, 3 - Delete Expense, 0 - Exit");
                int choice = scanner.nextInt();

                if (choice == 0) {
                    break;
                }

                switch (choice) {
                    case 1:
                        System.out.println("Enter expense description:");
                        String description = scanner.next();
                        System.out.println("Enter expense amount:");
                        double amount = scanner.nextDouble();
                        System.out.println("Enter expense category:");
                        String category = scanner.next();

                        Expense newExpense = new Expense(0, description, amount, category);
                        try (PreparedStatement preparedStatement = connection.prepareStatement(insertRecordSQL)) {
                            preparedStatement.setString(1, newExpense.getDescription());
                            preparedStatement.setDouble(2, newExpense.getAmount());
                            preparedStatement.setString(3, newExpense.getCategory());
                            preparedStatement.executeUpdate();
                            System.out.println("Expense inserted.");
                        }
                        break;

                    case 2:
                        System.out.println("Enter expense ID to update:");
                        int updateExpenseId = scanner.nextInt();
                        System.out.println("Enter new description:");
                        String updatedDescription = scanner.next();
                        System.out.println("Enter new amount:");
                        double updatedAmount = scanner.nextDouble();
                        System.out.println("Enter new category:");
                        String updatedCategory = scanner.next();

                        try (PreparedStatement updateStatement = connection.prepareStatement(updateRecordSQL)) {
                            updateStatement.setString(1, updatedDescription);
                            updateStatement.setDouble(2, updatedAmount);
                            updateStatement.setString(3, updatedCategory);
                            updateStatement.setInt(4, updateExpenseId);
                            updateStatement.executeUpdate();
                            System.out.println("Expense updated.");
                        }
                        break;

                    case 3:
                        System.out.println("Enter expense ID to delete:");
                        int deleteExpenseId = scanner.nextInt();

                        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteRecordSQL)) {
                            deleteStatement.setInt(1, deleteExpenseId);
                            deleteStatement.executeUpdate();
                            System.out.println("Expense deleted.");
                        }
                        break;

                    default:
                        System.out.println("Invalid choice.");
                        break;
                }


            }
        } catch (SQLException e) {

            System.out.println("Error");
            e.printStackTrace();
        }
    }
}