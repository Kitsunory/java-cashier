package com.duta.library.controller;

import com.duta.library.model.Gender;
import com.duta.library.model.ListData;
import com.duta.library.repository.Database;
import com.duta.library.ui.CellView;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Student implements Initializable {

    @FXML
    private ListView<ListData> LV_StudentList;

    @FXML
    private TextField TF_Code;

    @FXML
    private TextField TF_Major;

    @FXML
    private TextField TF_Name;

    @FXML
    private ComboBox<Gender> CB_Gender;

    @FXML
    private TextField TF_Address;

    @FXML
    private TextField TF_Year;

    @FXML
    private TextField TF_Search;

    @FXML
    private Button B_Add;

    @FXML
    private Button B_Edit;

    @FXML
    private Button B_Delete;

    @FXML
    private Button B_Listing;

    @FXML
    private Button B_Student;

    @FXML
    private Button B_Worker;

    @FXML
    private Button B_Borrow;

    @FXML
    private Button B_Return;

    @FXML
    private ImageView IV_StudentPhoto;

    private File selectedPhotoFile;
    private ObservableList<ListData> originalStudentList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LV_StudentList.setCellFactory(param -> new CellView());

        CB_Gender.getItems().addAll(Gender.Male, Gender.Female);

        loadStudentData();
        originalStudentList = FXCollections.observableArrayList(
            LV_StudentList.getItems()
        );

        IV_StudentPhoto.setOnMouseClicked(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image File");
            fileChooser
                .getExtensionFilters()
                .addAll(
                    new FileChooser.ExtensionFilter(
                        "Image Files",
                        "*.png",
                        "*.jpg",
                        "*.jpeg",
                        "*.gif"
                    )
                );
            selectedPhotoFile = fileChooser.showOpenDialog(
                IV_StudentPhoto.getScene().getWindow()
            );
            if (selectedPhotoFile != null) {
                Image image = new Image(selectedPhotoFile.toURI().toString());
                IV_StudentPhoto.setImage(image);
            }
        });

        B_Add.setOnAction(e -> {
            addStudent();
        });

        B_Edit.setOnAction(e -> {
            editStudent();
        });

        B_Delete.setOnAction(e -> {
            deleteStudent();
        });

        B_Listing.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/duta/library/Listing.fxml")
                );
                Parent root = loader.load();
                Stage stage = (Stage) B_Listing.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        B_Student.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/duta/library/Student.fxml")
                );
                Parent root = loader.load();
                Stage stage = (Stage) B_Student.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        B_Worker.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/duta/library/Worker.fxml")
                );
                Parent root = loader.load();
                Stage stage = (Stage) B_Worker.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        B_Borrow.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/duta/library/Borrow.fxml")
                );
                Parent root = loader.load();
                Stage stage = (Stage) B_Borrow.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        B_Return.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/duta/library/Return.fxml")
                );
                Parent root = loader.load();
                Stage stage = (Stage) B_Return.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        LV_StudentList.getSelectionModel()
            .selectedItemProperty()
            .addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateFields(newSelection);
                }
            });

        TF_Search.textProperty()
            .addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(
                        ObservableValue<? extends String> observable,
                        String oldValue,
                        String newValue
                    ) {
                        filterList(newValue);
                    }
                }
            );
    }

    private void loadStudentData() {
        LV_StudentList.getItems().clear();
        ResultSet result = Database.getInstance().read("Student", "*", "");
        try {
            while (result.next()) {
                String code = result.getString("Code");
                String name = result.getString("Name");
                String gender = result.getString("Gender");
                String major = result.getString("Major");
                String year = result.getString("Year");
                String address = result.getString("Address");

                String studentString = String.format(
                    "%s, %s, %s, %s, %s, %s",
                    code,
                    name,
                    gender,
                    major,
                    year,
                    address
                );

                String photoPath = null;
                try {
                    photoPath = result.getString("Picture");
                } catch (SQLException ex) {
                    System.out.println(
                        "Error reading picture path from database: " +
                        ex.getMessage()
                    );
                }

                ListData data = new ListData(studentString, photoPath);
                LV_StudentList.getItems().add(data);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void addStudent() {
        String code = TF_Code.getText();
        String name = TF_Name.getText();
        Gender gender = CB_Gender.getValue();
        String major = TF_Major.getText();
        String year = TF_Year.getText();
        String address = TF_Address.getText();

        if (
            code.isEmpty() ||
            name.isEmpty() ||
            gender == null ||
            major.isEmpty() ||
            year.isEmpty() ||
            address.isEmpty()
        ) {
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Please fill in all fields."
            );
            return;
        }

        String[] columns = {
            "Code",
            "Name",
            "Gender",
            "Major",
            "Year",
            "Address",
            "Picture",
        };
        Object[] values = {
            code,
            name,
            gender.toString(),
            major,
            year,
            address,
            null,
        };

        if (selectedPhotoFile != null) {
            values[6] = selectedPhotoFile.getAbsolutePath();
        }

        boolean success = Database.getInstance()
            .create("Student", columns, values);

        if (success) {
            showAlert(
                Alert.AlertType.INFORMATION,
                "Success",
                "Student added successfully!"
            );
            loadStudentData();
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add student.");
        }
    }

    private void editStudent() {
        String code = TF_Code.getText();
        String name = TF_Name.getText();
        Gender gender = CB_Gender.getValue();
        String major = TF_Major.getText();
        String year = TF_Year.getText();
        String address = TF_Address.getText();

        if (code.isEmpty()) {
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Please select a student to edit."
            );
            return;
        }

        String[] columns = {
            "Name",
            "Gender",
            "Major",
            "Year",
            "Address",
            "Picture",
        };
        Object[] values = {
            name,
            gender.toString(),
            major,
            year,
            address,
            null,
        };

        if (selectedPhotoFile != null) {
            values[5] = selectedPhotoFile.getAbsolutePath();
        }

        String condition = "Code = '" + code + "'";

        boolean success = Database.getInstance()
            .update("Student", columns, values, condition);

        if (success) {
            showAlert(
                Alert.AlertType.INFORMATION,
                "Success",
                "Student updated successfully!"
            );
            loadStudentData();
        } else {
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Failed to update student."
            );
        }
    }

    private void deleteStudent() {
        String code = TF_Code.getText();

        if (code.isEmpty()) {
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Please select a student to delete."
            );
            return;
        }

        String condition = "Code = '" + code + "'";
        boolean success = Database.getInstance().delete("Student", condition);

        if (success) {
            showAlert(
                Alert.AlertType.INFORMATION,
                "Success",
                "Student deleted successfully!"
            );
            loadStudentData();
            clearFields();
        } else {
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Failed to delete student."
            );
        }
    }

    private void populateFields(ListData data) {
        String[] parts = data.getText().split(", ");

        if (parts.length >= 6) {
            TF_Code.setText(parts[0]);
            TF_Name.setText(parts[1]);

            try {
                CB_Gender.setValue(Gender.valueOf(parts[2]));
            } catch (IllegalArgumentException e) {
                CB_Gender.setValue(null);
                System.out.println("Invalid gender value in data: " + parts[2]);
            }

            TF_Major.setText(parts[3]);
            TF_Year.setText(parts[4]);
            TF_Address.setText(parts[5]);
        } else {
            System.out.println("Data string is not in the expected format.");
            clearFields();
        }

        String imagePath = data.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File file = new File(imagePath);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    IV_StudentPhoto.setImage(image);
                    selectedPhotoFile = file;
                } else {
                    IV_StudentPhoto.setImage(null);
                    selectedPhotoFile = null;
                    System.out.println("Image file not found: " + imagePath);
                }
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
                IV_StudentPhoto.setImage(null);
                selectedPhotoFile = null;
            }
        } else {
            IV_StudentPhoto.setImage(null);
            selectedPhotoFile = null;
        }
    }

    private void clearFields() {
        TF_Code.clear();
        TF_Name.clear();
        CB_Gender.setValue(null);
        TF_Major.clear();
        TF_Year.clear();
        TF_Address.clear();
        IV_StudentPhoto.setImage(null);
        selectedPhotoFile = null;
    }

    private void showAlert(
        Alert.AlertType alertType,
        String title,
        String message
    ) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void filterList(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            LV_StudentList.setItems(originalStudentList);
        } else {
            ObservableList<ListData> filteredList =
                FXCollections.observableArrayList();
            for (ListData data : originalStudentList) {
                if (
                    data
                        .getText()
                        .toLowerCase()
                        .contains(searchText.toLowerCase())
                ) {
                    filteredList.add(data);
                }
            }
            LV_StudentList.setItems(filteredList);
        }
    }
}
