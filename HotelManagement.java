import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class HotelManagement {
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_d";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Sathwik@2003";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                System.out.println("1. Room Management");
                System.out.println("2. Customer Management");
                System.out.println("3. Reservation Management");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        roomManagementMenu(connection, scanner);
                        break;
                    case 2:
                        customerManagementMenu(connection, scanner);
                        break;
                    case 3:
                        reservationManagementMenu(connection, scanner);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void roomManagementMenu(Connection connection, Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\nRoom Management Menu:");
            System.out.println("1. Add a new room");
            System.out.println("2. View room details");
            System.out.println("3. Update room information");
            System.out.println("4. Delete a room");
            System.out.println("5. Back to main menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addRoom(connection, scanner);
                    break;
                case 2:
                    viewRoomDetails(connection);
                    break;
                case 3:
                    updateRoom(connection, scanner);
                    break;
                case 4:
                    deleteRoom(connection, scanner);
                    break;
                case 5:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    private static void addRoom(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter room number: ");
            String roomNumber = scanner.next();
            System.out.print("Enter room type: ");
            String roomType = scanner.next();
            System.out.print("Enter price per night: ");
            double pricePerNight = scanner.nextDouble();

            String sql = "INSERT INTO rooms (room_number, type, price_per_night, status) VALUES (?, ?, ?, 'available')";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, roomNumber);
                statement.setString(2, roomType);
                statement.setDouble(3, pricePerNight);
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Room added successfully!");
                } else {
                    System.out.println("Failed to add room.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewRoomDetails(Connection connection) {
        String sql = "SELECT * FROM rooms";
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            System.out.println("Room Details:");
            while (resultSet.next()) {
                int roomId = resultSet.getInt("room_id");
                String roomNumber = resultSet.getString("room_number");
                String type = resultSet.getString("type");
                double pricePerNight = resultSet.getDouble("price_per_night");
                String status = resultSet.getString("status");
                System.out.printf("Room ID: %d, Room Number: %s, Type: %s, Price per Night: %.2f, Status: %s%n",
                        roomId, roomNumber, type, pricePerNight, status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateRoom(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter room ID to update: ");
            int roomId = scanner.nextInt();
            scanner.nextLine();

            if (!roomExists(connection, roomId)) {
                System.out.println("Room not found for the given ID.");
                return;
            }

            System.out.print("Enter new room number: ");
            String newRoomNumber = scanner.next();
            System.out.print("Enter new room type: ");
            String newRoomType = scanner.next();
            System.out.print("Enter new price per night: ");
            double newPricePerNight = scanner.nextDouble();

            String sql = "UPDATE rooms SET room_number = ?, type = ?, price_per_night = ? WHERE room_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, newRoomNumber);
                statement.setString(2, newRoomType);
                statement.setDouble(3, newPricePerNight);
                statement.setInt(4, roomId);

                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Room updated successfully!");
                } else {
                    System.out.println("Failed to update room.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteRoom(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter room ID to delete: ");
            int roomId = scanner.nextInt();

            if (!roomExists(connection, roomId)) {
                System.out.println("Room not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM rooms WHERE room_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, roomId);
                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("Room deleted successfully!");
                } else {
                    System.out.println("Failed to delete room.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean roomExists(Connection connection, int roomId) {
        try {
            String sql = "SELECT room_id FROM rooms WHERE room_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, roomId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void customerManagementMenu(Connection connection, Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\nCustomer Management Menu:");
            System.out.println("1. Register a new customer");
            System.out.println("2. View customer details");
            System.out.println("3. Update customer information");
            System.out.println("4. Delete a customer");
            System.out.println("5. Back to main menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    registerCustomer(connection, scanner);
                    break;
                case 2:
                    viewCustomerDetails(connection);
                    break;
                case 3:
                    updateCustomer(connection, scanner);
                    break;
                case 4:
                    deleteCustomer(connection, scanner);
                    break;
                case 5:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    private static void registerCustomer(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter customer name: ");
            String name = scanner.nextLine();
            System.out.print("Enter customer email: ");
            String email = scanner.nextLine();
            System.out.print("Enter customer phone number: ");
            String phone = scanner.nextLine();
            System.out.print("Enter customer address: ");
            String address = scanner.nextLine();

            String sql = "INSERT INTO customers (name, email, phone_number, address) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, name);
                statement.setString(2, email);
                statement.setString(3, phone);
                statement.setString(4, address);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Customer registered successfully!");
                } else {
                    System.out.println("Failed to register customer.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewCustomerDetails(Connection connection) {
        String sql = "SELECT * FROM customers";
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            System.out.println("Customer Details:");
            while (resultSet.next()) {
                int customerId = resultSet.getInt("customer_id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone_number");
                String address = resultSet.getString("address");
                System.out.printf("Customer ID: %d, Name: %s, Email: %s, Phone: %s, Address: %s%n",
                        customerId, name, email, phone, address);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateCustomer(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter customer ID to update: ");
            int customerId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (!customerExists(connection, customerId)) {
                System.out.println("Customer not found for the given ID.");
                return;
            }

            System.out.print("Enter new customer name: ");
            String newName = scanner.nextLine();
            System.out.print("Enter new customer email: ");
            String newEmail = scanner.nextLine();
            System.out.print("Enter new customer phone number: ");
            String newPhone = scanner.nextLine();
            System.out.print("Enter new customer address: ");
            String newAddress = scanner.nextLine();

            String sql = "UPDATE customers SET name = ?, email = ?, phone_number = ?, address = ? WHERE customer_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, newName);
                statement.setString(2, newEmail);
                statement.setString(3, newPhone);
                statement.setString(4, newAddress);
                statement.setInt(5, customerId);

                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Customer updated successfully!");
                } else {
                    System.out.println("Failed to update customer.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteCustomer(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter customer ID to delete: ");
            int customerId = scanner.nextInt();

            if (!customerExists(connection, customerId)) {
                System.out.println("Customer not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM customers WHERE customer_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, customerId);
                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("Customer deleted successfully!");
                } else {
                    System.out.println("Failed to delete customer.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void reservationManagementMenu(Connection connection, Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\nReservation Management Menu:");
            System.out.println("1. Make a reservation for a customer");
            System.out.println("2. View reservation details");
            System.out.println("3. Cancel a reservation");
            System.out.println("4. List all reservations for a specific customer");
            System.out.println("5. Back to main menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    makeReservation(connection, scanner);
                    break;
                case 2:
                    viewReservationDetails(connection);
                    break;
                case 3:
                    cancelReservation(connection, scanner);
                    break;
                case 4:
                    listReservationsForCustomer(connection, scanner);
                    break;
                case 5:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    private static void makeReservation(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter customer ID: ");
            int customerId = scanner.nextInt();
            if (!customerExists(connection, customerId)) {
                System.out.println("Customer not found for the given ID.");
                return;
            }

            System.out.print("Enter room ID: ");
            int roomId = scanner.nextInt();
            if (!roomExists(connection, roomId)) {
                System.out.println("Room not found for the given ID.");
                return;
            }

            scanner.nextLine();
            System.out.print("Enter check-in date (YYYY-MM-DD): ");
            String checkInDate = scanner.nextLine();
            System.out.print("Enter check-out date (YYYY-MM-DD): ");
            String checkOutDate = scanner.nextLine();

            String sql = "INSERT INTO reservations (room_id, customer_id, check_in_date, check_out_date, status) " +
                    "VALUES (?, ?, ?, ?, 'reserved')";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, roomId);
                statement.setInt(2, customerId);
                statement.setString(3, checkInDate);
                statement.setString(4, checkOutDate);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Reservation made successfully!");
                    updateRoomStatus(connection, roomId, "booked");
                } else {
                    System.out.println("Failed to make reservation.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewReservationDetails(Connection connection) {
        String sql = "SELECT r.reservation_id, r.check_in_date, r.check_out_date, r.status, " +
                "c.name AS guest_name, c.email AS guest_email, c.phone_number AS guest_phone, " +
                "ro.room_number, ro.type AS room_type, ro.price_per_night " +
                "FROM reservations r " +
                "JOIN customers c ON r.customer_id = c.customer_id " +
                "JOIN rooms ro ON r.room_id = ro.room_id";

        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            System.out.println("Reservation Details:");
            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                String checkInDate = resultSet.getString("check_in_date");
                String checkOutDate = resultSet.getString("check_out_date");
                String status = resultSet.getString("status");
                String guestName = resultSet.getString("guest_name");
                String guestEmail = resultSet.getString("guest_email");
                String guestPhone = resultSet.getString("guest_phone");
                String roomNumber = resultSet.getString("room_number");
                String roomType = resultSet.getString("room_type");
                double pricePerNight = resultSet.getDouble("price_per_night");

                System.out.printf("Reservation ID: %d%n", reservationId);
                System.out.printf("Guest: %s, Email: %s, Phone: %s%n", guestName, guestEmail, guestPhone);
                System.out.printf("Room: %s (%s), Price per Night: %.2f%n", roomNumber, roomType, pricePerNight);
                System.out.printf("Check-in: %s, Check-out: %s, Status: %s%n", checkInDate, checkOutDate, status);
                System.out.println("-------------------------------------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void cancelReservation(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID to cancel: ");
            int reservationId = scanner.nextInt();

            String sql = "UPDATE reservations SET status = 'canceled' WHERE reservation_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, reservationId);
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Reservation canceled successfully!");
                    int roomId = getRoomIdForReservation(connection, reservationId);
                    if (roomId != -1) {
                        updateRoomStatus(connection, roomId, "available");
                    } else {
                        System.out.println("Failed to update room status.");
                    }
                } else {
                    System.out.println("Failed to cancel reservation.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getRoomIdForReservation(Connection connection, int reservationId) {
        try {
            String sql = "SELECT room_id FROM reservations WHERE reservation_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, reservationId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("room_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static void listReservationsForCustomer(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter customer ID: ");
            int customerId = scanner.nextInt();

            String sql = "SELECT r.reservation_id, r.check_in_date, r.check_out_date, r.status, " +
                    "ro.room_number, ro.type AS room_type, ro.price_per_night " +
                    "FROM reservations r " +
                    "JOIN rooms ro ON r.room_id = ro.room_id " +
                    "WHERE r.customer_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, customerId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    System.out.println("Reservations for Customer ID " + customerId + ":");
                    while (resultSet.next()) {
                        int reservationId = resultSet.getInt("reservation_id");
                        String checkInDate = resultSet.getString("check_in_date");
                        String checkOutDate = resultSet.getString("check_out_date");
                        String status = resultSet.getString("status");
                        String roomNumber = resultSet.getString("room_number");
                        String roomType = resultSet.getString("room_type");
                        double pricePerNight = resultSet.getDouble("price_per_night");

                        System.out.printf("Reservation ID: %d, Room: %s (%s), Price per Night: %.2f, " +
                                "Check-in: %s, Check-out: %s, Status: %s%n",
                                reservationId, roomNumber, roomType, pricePerNight, checkInDate, checkOutDate, status);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateRoomStatus(Connection connection, int roomId, String status) {
        try {
            String sql = "UPDATE rooms SET status = ? WHERE room_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, status);
                statement.setInt(2, roomId);
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Room status updated successfully!");
                } else {
                    System.out.println("Failed to update room status.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean customerExists(Connection connection, int customerId) {
        try {
            String sql = "SELECT customer_id FROM customers WHERE customer_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, customerId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void exit() {
        System.out.println("Exiting Hotel Management System.");
    }
}
