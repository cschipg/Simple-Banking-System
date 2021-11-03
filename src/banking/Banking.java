package banking;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.Scanner;

class Banking {
    public static void checkCC(long creditCardNum) {
        int[] arr = new int[15];
        for(int i = 0; i < arr.length; i++) {
            arr[i] = Math.toIntExact(creditCardNum % 10);
            creditCardNum /= 10;
        }
        int sum = 0;
        for(int i = 0; i < arr.length; i += 2) {
            arr[i] = arr[i] * 2;
            if (arr[i] > 9) {
                arr[i] -= 9;
            }
            sum += arr[i];
        }
        int last = 10 - (sum % 10);
    }

    public static long generateCC(long number) {
        long numberCopy = number;
        int[] arr = new int[15];
        for(int i = 0; i < arr.length; i++) {
            arr[i] = Math.toIntExact(number % 10);
            number /= 10;
        }
        int sum = 0;
        for(int i = 0; i < arr.length; i+= 2) {
            arr[i] = arr[i] * 2;
            if (arr[i] > 9) {
                arr[i] -= 9;
            }
            sum += arr[i];
        }
        for(int i = 1; i < arr.length; i += 2) {
            sum += arr[i];
        }
        int last = 10 - (sum % 10);

        long creditCardNum = Long.valueOf(Long.toString(numberCopy) + String.valueOf(last == 10 ? 0 : last));
        return creditCardNum;
    }

    private Connection connectDB() {
        // SQLite connection string
        String url = "jdbc:sqlite:card.s3db";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    public void createDBTable() {
        String sql = "CREATE TABLE card (id INTEGER, number TEXT, pin TEXT, balance INTEGER DEFAULT 0);";
        try(Connection connect = this.connectDB();
            PreparedStatement query = connect.prepareStatement(sql)) {
            query.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertCCIntoTable(int id, String number, String pin) {
        String sql = "INSERT INTO card VALUES (?, ?, ?, ?)";
        try(Connection connect = this.connectDB();
            PreparedStatement query = connect.prepareStatement(sql)) {
            query.setInt(1, id);
            query.setString(2, number);
            query.setString(3, pin);
            query.setInt(4, 0);
            query.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Banking app = new Banking();
        app.createDBTable();
        Scanner ask = new Scanner(System.in);
        Random random = new Random();

        long lower_range = 400000000000000L;
        long upper_range = 400001000000000L;
        int pin_upper = 10000;
        int pin_lower = 1000;
        long card = lower_range + (long)(random.nextDouble() * (upper_range - lower_range));
        int pin = random.nextInt(10000);
        long creditCardNum = generateCC(card);
        int id = 1;

        while(true) {
            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");
            int input = ask.nextInt();
            switch (input) {
                case 1:
                    System.out.println();
                    System.out.println("Your card has been created");
                    System.out.println("Your card number:");
                    card = lower_range + (long)(random.nextDouble() * (upper_range - lower_range));
                    pin = random.nextInt(pin_upper - pin_lower) + pin_lower;
                    cc = generateCC(card);

                    System.out.println(cc);
                    System.out.println("Your card PIN:");
                    System.out.println(pin);
                    System.out.println();

                    app.insertCCIntoTable(id, Long.toString(cc), Long.toString(pin));
                    id++;
                    break;
                case 2:
                    System.out.println("Enter your card number:");
                    long cardTry = ask.nextLong();
                    System.out.println("Enter your PIN:");
                    int pinTry = ask.nextInt();
                    if (cc != cardTry || pin != pinTry) {
                        System.out.println("Wrong card number or PIN!");
                        break;
                    }
                    System.out.println("You have successfully logged in!");
                    int input2;
                    do {
                        System.out.println("1. Balance");
                        System.out.println("2. Add income");
                        System.out.println("3. Do transfer");
                        System.out.println("4. Close account");
                        System.out.println("5. Log out");
                        System.out.println("0. Exit");
                        input2 = ask.nextInt();
                        if(input2 == 1) {
                            System.out.println("Balance: 0");
                        }
                        if (input2 == 0) {
                            return;
                        }
                    } while(input2 != 2);
                    if (input2 == 2) {
                        System.out.println("You have successfully logged out!");
                        break;
                    }
                case 0:
                    System.out.println("Bye!");
                    return;
            }
        }
    }
}