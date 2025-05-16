package com.duta.library.controller;

import com.duta.library.model.ListData;
import com.duta.library.repository.Database;
import com.duta.library.ui.CellView;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Borrow implements Initializable {

    @FXML
    private ListView<ListData> LV_PendingLendList;

    @FXML
    private TextField TF_Code;

    @FXML
    private TextField TF_BookCode;

    @FXML
    private TextField TF_StudentCode;

    @FXML
    private TextField TF_WorkerCode;

    @FXML
    private DatePicker DP_LendDate;

    @FXML
    private DatePicker DP_DueDate;

    @FXML
    private TextField TF_Search;

    @FXML
    private Button B_Add;

    @FXML
    private Button B_Edit;

    @FXML
    private Button B_Delete;

    @FXML
    private TextField TF_Quantity;

    @FXML
    private TextField TF_BookName;

    @FXML
    private TextField TF_StudentName;

    @FXML
    private TextField TF_WorkerName;

    @FXML
    private ImageView IV_BookCover;

    @FXML
    private ImageView IV_StudentPhoto;

    @FXML
    private ImageView IV_WorkerPhoto;

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

    private ObservableList<ListData> originalBorrowList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LV_PendingLendList.setCellFactory(param -> new CellView());

        loadBorrowData();
        originalBorrowList = FXCollections.observableArrayList(
            LV_PendingLendList.getItems()
        );

        B_Add.setOnAction(e -> {
            addBorrow();
        });

        B_Edit.setOnAction(e -> {
            editBorrow();
        });

        B_Delete.setOnAction(e -> {
            deleteBorrow();
        });

        LV_PendingLendList.getSelectionModel()
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

        TF_BookCode.textProperty()
            .addListener((obs, oldVal, newVal) -> {
                if (!newVal.isEmpty()) {
                    loadBookData(newVal);
                } else {
                    TF_BookName.clear();
                    IV_BookCover.setImage(null);
                }
            });

        TF_StudentCode.textProperty()
            .addListener((obs, oldVal, newVal) -> {
                if (!newVal.isEmpty()) {
                    loadStudentData(newVal);
                } else {
                    TF_StudentName.clear();
                    IV_StudentPhoto.setImage(null);
                }
            });

        TF_WorkerCode.textProperty()
            .addListener((obs, oldVal, newVal) -> {
                if (!newVal.isEmpty()) {
                    loadWorkerData(newVal);
                } else {
                    TF_WorkerName.clear();
                    IV_WorkerPhoto.setImage(null);
                }
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
    }

    private void loadBorrowData() {
        LV_PendingLendList.getItems().clear();
        ResultSet result = Database.getInstance().read("Borrow", "*", "");
        try {
            while (result.next()) {
                String code = result.getString("Code");
                String bookCode = result.getString("BookCode");
                String studentCode = result.getString("StudentCode");
                String workerCode = result.getString("WorkerCode");
                LocalDate lendDate = result.getDate("LendDate").toLocalDate();
                LocalDate dueDate = result.getDate("DueDate").toLocalDate();

                int quantity = result.getInt("Quantity");

                String borrowString = String.format(
                    "%s, %s, %s, %s, %s, %s, %d",
                    code,
                    bookCode,
                    studentCode,
                    workerCode,
                    lendDate.toString(),
                    dueDate.toString(),
                    quantity
                );

                ListData data = new ListData(borrowString, null);
                LV_PendingLendList.getItems().add(data);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void addBorrow() {
        String code = TF_Code.getText();
        String bookCode = TF_BookCode.getText();
        String studentCode = TF_StudentCode.getText();
        String workerCode = TF_WorkerCode.getText();
        String quantityText = TF_Quantity.getText();
        int quantity = 0;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Please enter a valid number for quantity."
            );
            return;
        }

        LocalDate lendDate = DP_LendDate.getValue();
        LocalDate dueDate = DP_DueDate.getValue();

        if (
            code.isEmpty() ||
            bookCode.isEmpty() ||
            studentCode.isEmpty() ||
            workerCode.isEmpty() ||
            lendDate == null ||
            dueDate == null ||
            quantityText.isEmpty()
        ) {
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Please fill in all fields."
            );
            return;
        }

        // Check current quantity of the book from DB
        int currentQuantity = 0;
        ResultSet bookResult = Database.getInstance()
            .read("Book", "Quantity", "Code = '" + bookCode + "'");
        try {
            if (bookResult.next()) {
                currentQuantity = bookResult.getInt("Quantity");
            } else {
                showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Book not found with code: " + bookCode
                );
                return;
            }
        } catch (SQLException e) {
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Error retrieving book quantity: " + e.getMessage()
            );
            return;
        }

        // If requested quantity is more than current quantity, fail
        if (quantity > currentQuantity) {
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Not enough books available. Current quantity: " +
                currentQuantity
            );
            return;
        }

        // Proceed to add borrow record
        Image coverImage = IV_BookCover.getImage();
        String coverPath = (coverImage != null && coverImage.getUrl() != null)
            ? coverImage.getUrl()
            : null;
        if (coverPath != null && coverPath.startsWith("file:")) {
            coverPath = coverPath.substring(5);
        }

        String[] columns = {
            "Code",
            "BookCode",
            "StudentCode",
            "WorkerCode",
            "LendDate",
            "DueDate",
            "Quantity",
            "Cover",
        };
        Object[] values = {
            code,
            bookCode,
            studentCode,
            workerCode,
            lendDate.toString(),
            dueDate.toString(),
            quantity,
            coverPath,
        };

        boolean success = Database.getInstance()
            .create("Borrow", columns, values);

        if (success) {
            // Decrease the book quantity in the database
            int newQuantity = currentQuantity - quantity;
            String[] updateColumns = { "Quantity" };
            Object[] updateValues = { newQuantity };
            String condition = "Code = '" + bookCode + "'";
            Database.getInstance()
                .update("Book", updateColumns, updateValues, condition);

            showAlert(
                Alert.AlertType.INFORMATION,
                "Success",
                "Borrow record added successfully!"
            );
            loadBorrowData();
            clearFields();
        } else {
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Failed to add borrow record."
            );
        }
    }

    private void editBorrow() {
        String code = TF_Code.getText();
        String bookCode = TF_BookCode.getText();
        String studentCode = TF_StudentCode.getText();
        String workerCode = TF_WorkerCode.getText();
        LocalDate lendDate = DP_LendDate.getValue();
        LocalDate dueDate = DP_DueDate.getValue();

        if (code.isEmpty()) {
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Please select a borrow record to edit."
            );
            return;
        }

        String[] columns = {
            "BookCode",
            "StudentCode",
            "WorkerCode",
            "LendDate",
            "DueDate",
            "Quantity", // ← Add this!
        };

        Object[] values = {
            bookCode,
            studentCode,
            workerCode,
            lendDate.toString(),
            dueDate.toString(),
            Integer.parseInt(TF_Quantity.getText()), // ← Add this too!
        };

        String condition = "Code = '" + code + "'";

        boolean success = Database.getInstance()
            .update("Borrow", columns, values, condition);

        if (success) {
            showAlert(
                Alert.AlertType.INFORMATION,
                "Success",
                "Borrow record updated successfully!"
            );
            loadBorrowData();
        } else {
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Failed to update borrow record."
            );
        }
    }

    private void deleteBorrow() {
        String code = TF_Code.getText();

        if (code.isEmpty()) {
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Please select a borrow record to delete."
            );
            return;
        }

        String condition = "Code = '" + code + "'";
        boolean success = Database.getInstance().delete("Borrow", condition);

        if (success) {
            showAlert(
                Alert.AlertType.INFORMATION,
                "Success",
                "Borrow record deleted successfully!"
            );
            loadBorrowData();
            clearFields();
        } else {
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Failed to delete borrow record."
            );
        }
    }

    private void populateFields(ListData data) {
        String[] parts = data.getText().split(", ");

        if (parts.length >= 7) {
            TF_Code.setText(parts[0]);
            TF_BookCode.setText(parts[1]);
            TF_StudentCode.setText(parts[2]);
            TF_WorkerCode.setText(parts[3]);
            DP_LendDate.setValue(LocalDate.parse(parts[4]));
            DP_DueDate.setValue(LocalDate.parse(parts[5]));
            TF_Quantity.setText(parts[6]);
        } else {
            System.out.println("Data string is not in the expected format.");
            clearFields();
        }
    }

    private void clearFields() {
        TF_Code.clear();
        TF_BookCode.clear();
        TF_StudentCode.clear();
        TF_WorkerCode.clear();
        TF_Quantity.clear();
        DP_LendDate.setValue(null);
        DP_DueDate.setValue(null);
        TF_BookName.clear();
        TF_StudentName.clear();
        TF_WorkerName.clear();
        IV_BookCover.setImage(null);
        IV_StudentPhoto.setImage(null);
        IV_WorkerPhoto.setImage(null);
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
            LV_PendingLendList.setItems(originalBorrowList);
        } else {
            ObservableList<ListData> filteredList =
                FXCollections.observableArrayList();
            for (ListData data : originalBorrowList) {
                if (
                    data
                        .getText()
                        .toLowerCase()
                        .contains(searchText.toLowerCase())
                ) {
                    filteredList.add(data);
                }
            }
            LV_PendingLendList.setItems(filteredList);
        }
    }

    private void loadBookData(String bookCode) {
        ResultSet result = Database.getInstance()
            .read("Book", "*", "Code = '" + bookCode + "'");
        try {
            if (result.next()) {
                String bookName = result.getString("Title");
                String photoPath = result.getString("Cover");

                TF_BookName.setText(bookName);

                if (photoPath != null && !photoPath.isEmpty()) {
                    File file = new File(photoPath);
                    if (file.exists()) {
                        Image image = new Image(file.toURI().toString());
                        IV_BookCover.setImage(image);
                    } else {
                        IV_BookCover.setImage(null);
                        System.out.println(
                            "Image file not found: " + photoPath
                        );
                    }
                } else {
                    IV_BookCover.setImage(null);
                }
            } else {
                TF_BookName.clear();
                IV_BookCover.setImage(null);
                System.out.println("Book not found with code: " + bookCode);
            }
        } catch (SQLException e) {
            System.out.println("Error loading book data: " + e.getMessage());
        }
    }

    private void loadStudentData(String studentCode) {
        ResultSet result = Database.getInstance()
            .read("Student", "*", "Code = '" + studentCode + "'");
        try {
            if (result.next()) {
                String studentName = result.getString("Name");
                String photoPath = result.getString("Picture");

                TF_StudentName.setText(studentName);

                if (photoPath != null && !photoPath.isEmpty()) {
                    File file = new File(photoPath);
                    if (file.exists()) {
                        Image image = new Image(file.toURI().toString());
                        IV_StudentPhoto.setImage(image);
                    } else {
                        IV_StudentPhoto.setImage(null);
                        System.out.println(
                            "Image file not found: " + photoPath
                        );
                    }
                } else {
                    IV_StudentPhoto.setImage(null);
                }
            } else {
                TF_StudentName.clear();
                IV_StudentPhoto.setImage(null);
                System.out.println(
                    "Student not found with code: " + studentCode
                );
            }
        } catch (SQLException e) {
            System.out.println("Error loading student data: " + e.getMessage());
        }
    }

    private void loadWorkerData(String workerCode) {
        ResultSet result = Database.getInstance()
            .read("Worker", "*", "Code = '" + workerCode + "'");
        try {
            if (result.next()) {
                String workerName = result.getString("Name");
                String photoPath = result.getString("Picture");

                TF_WorkerName.setText(workerName);

                if (photoPath != null && !photoPath.isEmpty()) {
                    File file = new File(photoPath);
                    if (file.exists()) {
                        Image image = new Image(file.toURI().toString());
                        IV_WorkerPhoto.setImage(image);
                    } else {
                        IV_WorkerPhoto.setImage(null);
                        System.out.println(
                            "Image file not found: " + photoPath
                        );
                    }
                } else {
                    IV_WorkerPhoto.setImage(null);
                }
            } else {
                TF_WorkerName.clear();
                IV_WorkerPhoto.setImage(null);
                System.out.println("Worker not found with code: " + workerCode);
            }
        } catch (SQLException e) {
            System.out.println("Error loading worker data: " + e.getMessage());
        }
    }
}
