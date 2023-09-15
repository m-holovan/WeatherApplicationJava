package com.example.individualproject;

import javafx.fxml.FXML;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField city;

    @FXML
    private Text cityInformation;

    @FXML
    private Text date;

    @FXML
    private Button getData;

    @FXML
    private Text maxTemp;

    @FXML
    private Text minTemp;

    @FXML
    private Button saveToDatabase;

    @FXML
    private Button saveFromDatabase;

    @FXML
    private Button saveToFile;

    @FXML
    private Text windDirection;
    Root root = new Root();

    @FXML
    void initialize() {
        //function for button "Check the weather"
        getData.setOnAction(event -> {
            //get name of the city from field
            String nameOfTheCity = city.getText().trim();
            //make string request for api
            String query = "https://api.weatherbit.io/v2.0/forecast/daily?city=" + nameOfTheCity + "&key=*******";
            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) new URL(query).openConnection();
                connection.setRequestMethod("GET");
                connection.setUseCaches(false);
                //set connection
                connection.connect();

                StringBuilder sb = new StringBuilder();
                //try to read json answer
                if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String line;
                    String jsonString = "";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        sb.append("\n");
                        jsonString = sb.toString();
                    }
                    ObjectMapper mapper = new ObjectMapper();
                    //deserialize information
                    root = mapper.readValue(jsonString, Root.class);
                    //show information
                    cityInformation.setText("City: " + root.city_name);
                    date.setText("Date: " + root.data.get(0).datetime);
                    maxTemp.setText("Max temp: " + root.data.get(0).max_temp);
                    minTemp.setText("Min temp: " + root.data.get(0).min_temp);
                    windDirection.setText("Wind direction: " + root.data.get(0).wind_cdir_full);
                }
            }
            catch (Throwable exception)
            {
                exception.printStackTrace();
            }
            finally
            {
                if(connection != null)
                {
                    connection.disconnect();
                }
            }
        });
        //function for button "Save to file"
        saveToFile.setOnAction(event -> {
            try
            {
                Stage stage = new Stage();
                FileChooser fileChooser = new FileChooser();
                //here we give to user choice where to save file
                fileChooser.setTitle("Select directory for save");
                fileChooser.setInitialFileName("currentWeather");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(".txt", "*.txt"));
                File file = fileChooser.showSaveDialog(stage);
                if (file != null) {
                    //write information in file
                    FileWriter fileWriter = new FileWriter(file);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    PrintWriter printDatum = new PrintWriter(bufferedWriter);
                    printDatum.print(cityInformation.getText() + "\n");
                    printDatum.print(date.getText() + "\n");
                    printDatum.print(maxTemp.getText() + "\n");
                    printDatum.print(minTemp.getText() + "\n");
                    printDatum.print(windDirection.getText() + "\n");
                    printDatum.close();
                }

            }
            catch (IOException exception)
            {
                System.out.println("Error!");
            }
        });

        DatabaseHandler databaseHandler =  new DatabaseHandler();
        //function for button "Save to database"
        saveToDatabase.setOnAction(event -> {
            //give information to database
            databaseHandler.addInformation(root.city_name, root.data.get(0).datetime, Double.toString(root.data.get(0).max_temp),
                    Double.toString(root.data.get(0).min_temp), root.data.get(0).wind_cdir_full);
        });

        //function for button "Save from database"
        saveFromDatabase.setOnAction(event ->{
            //get information from database and save to file
            databaseHandler.getAllInformation();
        });


    }
}