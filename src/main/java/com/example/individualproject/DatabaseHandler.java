package com.example.individualproject;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.sql.*;

public class DatabaseHandler extends Configs {
    Connection dbConnection;

    //function to connect to database
    public Connection getDbConnection()
            throws ClassNotFoundException, SQLException {

        String connectionString ="jdbc:mysql://" + dbHost + ":"
                + dbPort + "/" + dbName;

        Class.forName("com.mysql.cj.jdbc.Driver");

        dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);

        return dbConnection;
    }

    //request for adding information
    public void addInformation(String city, String date,
                               String maxTemp, String minTemp, String windDirection){

        String insert = "INSERT INTO " + Const.USER_TABLE + "(" +
                Const.CITY + "," + Const.DATE + "," + Const.MAX_TEMP + "," +
                Const.MIN_TEMP + "," + Const.WIND_DIRECTION + ")" +
                "VALUES(?,?,?,?,?)";

        try {
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
            preparedStatement.setString(1, city);
            preparedStatement.setString(2, date);
            preparedStatement.setString(3, maxTemp);
            preparedStatement.setString(4, minTemp);
            preparedStatement.setString(5, windDirection);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    //function to make request to database and save all information from database in txt file
    public void getAllInformation(){
        String query = "SELECT * FROM weather_information.information";

        PreparedStatement preparedStatement = null;
        int id = 0;
        String city = "";
        String date = "";
        String maxTemp = "";
        String minTemp = "";
        String windDirection = "";

        try {
            Stage stage = new Stage();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select directory for save");
            fileChooser.setInitialFileName("AllInformation");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(".txt", "*.txt"));
            File file = fileChooser.showSaveDialog(stage);
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            PrintWriter printDatum = new PrintWriter(bufferedWriter);

            preparedStatement = getDbConnection().prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery(query);
            while(resultSet.next()){
                id = resultSet.getInt(1);
                city = resultSet.getString(2);
                date = resultSet.getString(3);
                maxTemp = resultSet.getString(4);
                minTemp = resultSet.getString(5);
                windDirection = resultSet.getString(6);

                if (file != null) {
                    printDatum.println("Number of record: " + id);
                    printDatum.println("City: " + city);
                    printDatum.println("Date: " + date);
                    printDatum.println("Max temp: " + maxTemp);
                    printDatum.println("Min temp: " + minTemp);
                    printDatum.println("Wind direction: " + windDirection);
                    printDatum.println();
                }
            }
            printDatum.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
