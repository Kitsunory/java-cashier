package com.duta.library.controller;

import com.duta.library.model.Gender;
import com.duta.library.model.ListData;
import com.duta.library.repository.Database;
import com.duta.library.ui.CellView;
import java.io.File;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Worker implements Initializable {

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
    private ListView<ListData> LV_WorkerList;

    @FXML
    private TextField TF_Code;

    @FXML
    private TextField TF_Name;

    @FXML
    private ComboBox<Gender> CB_Gender;

    @FXML
    private TextField TF_Address;

    @FXML
    private TextField TF_Department;

    @FXML
    private Button B_Add;

    @FXML
    private Button B_Edit;

    @FXML
    private Button B_Delete;

    @FXML
    private ImageView IV_WorkerPhoto;

    @FXML
    private TextField TF_Search;

    @FXML
    private TextField TF_Phone;

    @FXML
    private TextField TF_WorkingHours;

    private File selectedPhotoFile;
    private ObservableList<ListData> originalWorkerList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LV_WorkerList.setCellFactory(param -> new CellView());

        CB_Gender.getItems().addAll(Gender.Male, Gender.Female);

        loadWorkerData();
        originalWorkerList = FXCollections.observableArrayList(
            LV_WorkerList.getItems()
        );

        IV_WorkerPhoto.setOnMouseClicked(e -> {
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
                IV_WorkerPhoto.getScene().getWindow()
            );
            if (selectedPhotoFile != null) {
                Image image = new Image(selectedPhotoFile.toURI().toString());
                IV_WorkerPhoto.setImage(image);
            }
        });

        B_Add.setOnAction(e -> {
            addWorker();
        });

        B_Edit.setOnAction(e -> {
            editWorker();
        });

        B_Delete.setOnAction(e -> {
            deleteWorker();
        });

        B_Listing.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/duta/library/Listing.fxml"));
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/duta/library/Student.fxml"));
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/duta/library/Worker.fxml"));
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/duta/library/Borrow.fxml"));
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/duta/library/Return.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) B_Return.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        LV_WorkerList.getSelectionModel()
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

    private void loadWorkerData() {
        LV_WorkerList.getItems().clear();
        ResultSet result = Database.getInstance().read("Worker", "*", "");
        try {
            while (result.next()) {
                String code = result.getString("Code");
                String name = result.getString("Name");
                String gender = result.getString("Gender");
                String department = result.getString("Department");
                String phone = result.getString("Phone");
                String address = result.getString("Address");
                String workingHours = result.getString("WorkingHours");

                String workerString = String.format(
                    "%s, %s, %s, %s, %s, %s, %s",
                    code,
                    name,
                    gender,
                    department,
                    phone,
                    address,
                    workingHours
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

                ListData data = new ListData(workerString, photoPath);
                LV_WorkerList.getItems().add(data);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void addWorker() {
        String code = TF_Code.getText();
        String name = TF_Name.getText();
        Gender gender = CB_Gender.getValue();
        String department = TF_Department.getText();
        String phone = TF_Phone.getText();
        String address = TF_Address.getText();
        String workingHours = TF_WorkingHours.getText();

        if (
            code.isEmpty() ||
            name.isEmpty() ||
            gender == null ||
            department.isEmpty() ||
            phone.isEmpty() ||
            address.isEmpty() ||
            workingHours.isEmpty()
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
            "Department",
            "Phone",
            "Address",
            "WorkingHours",
            "Picture",
        };
        Object[] values = {
            code,
            name,
            gender.toString(),
            department,
            phone,
            address,
            workingHours,
            null,
        };

        if (selectedPhotoFile != null) {
            values[7] = selectedPhotoFile.getAbsolutePath();
        }

        boolean success = Database.getInstance()
            .create("Worker", columns, values);

        if (success) {
            showAlert(
                Alert.AlertType.INFORMATION,
                "Success",
                "Worker added successfully!"
            );
            loadWorkerData();
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add worker.");
        }
    }

    private void editWorker() {
        String code = TF_Code.getText();
        String name = TF_Name.getText();
        Gender gender = CB_Gender.getValue();
        String department = TF_Department.getText();
        String phone = TF_Phone.getText();
        String address = TF_Address.getText();
        String workingHours = TF_WorkingHours.getText();

        if (code.isEmpty()) {
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Please select a worker to edit."
            );
            return;
        }

        String[] columns = {
            "Name",
            "Gender",
            "Department",
            "Phone",
            "Address",
            "WorkingHours",
            "Picture",
        };
        Object[] values = {
            name,
            gender.toString(),
            department,
            phone,
            address,
            workingHours,
            null,
        };

        if (selectedPhotoFile != null) {
            values[6] = selectedPhotoFile.getAbsolutePath();
        }

        String condition = "Code = '" + code + "'";

        boolean success = Database.getInstance()
            .update("Worker", columns, values, condition);

        if (success) {
            showAlert(
                Alert.AlertType.INFORMATION,
                "Success",
                "Worker updated successfully!"
            );
            loadWorkerData();
        } else {
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Failed to update worker."
            );
        }
    }

    private void deleteWorker() {
        String code = TF_Code.getText();

        if (code.isEmpty()) {
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Please select a worker to delete."
            );
            return;
        }

        String condition = "Code = '" + code + "'";
        boolean success = Database.getInstance().delete("Worker", condition);

        if (success) {
            showAlert(
                Alert.AlertType.INFORMATION,
                "Success",
                "Worker deleted successfully!"
            );
            loadWorkerData();
            clearFields();
        } else {
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Failed to delete worker."
            );
        }
    }

    private void populateFields(ListData data) {
        String[] parts = data.getText().split(", ");

        if (parts.length >= 7) {
            TF_Code.setText(parts[0]);
            TF_Name.setText(parts[1]);

            try {
                CB_Gender.setValue(Gender.valueOf(parts[2]));
            } catch (IllegalArgumentException e) {
                CB_Gender.setValue(null);
                System.out.println("Invalid gender value in data: " + parts[2]);
            }

            TF_Department.setText(parts[3]);
            TF_Phone.setText(parts[4]);
            TF_Address.setText(parts[5]);
            TF_WorkingHours.setText(parts[6]);
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
                    IV_WorkerPhoto.setImage(image);
                    selectedPhotoFile = file;
                } else {
                    IV_WorkerPhoto.setImage(null);
                    selectedPhotoFile = null;
                    System.out.println("Image file not found: " + imagePath);
                }
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
                IV_WorkerPhoto.setImage(null);
                selectedPhotoFile = null;
            }
        } else {
            IV_WorkerPhoto.setImage(null);
            selectedPhotoFile = null;
        }
    }

    private void clearFields() {
        TF_Code.clear();
        TF_Name.clear();
        CB_Gender.setValue(null);
        TF_Department.clear();
        TF_Phone.clear();
        TF_Address.clear();
        TF_WorkingHours.clear();
        IV_WorkerPhoto.setImage(null);
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
            LV_WorkerList.setItems(originalWorkerList);
        } else {
            ObservableList<ListData> filteredList =
                FXCollections.observableArrayList();
            for (ListData data : originalWorkerList) {
                if (
                    data
                        .getText()
                        .toLowerCase()
                        .contains(searchText.toLowerCase())
                ) {
                    filteredList.add(data);
                }
            }
            LV_WorkerList.setItems(filteredList);
        }
    }
}
