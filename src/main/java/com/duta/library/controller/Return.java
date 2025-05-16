package com.duta.library.controller;

import com.duta.library.model.ListData;
import com.duta.library.repository.Database;
import com.duta.library.ui.CellView;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class Return {

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
    private ListView<ListData> LV_ReturnedList;

    @FXML
    private TextField TF_Code;

    @FXML
    private TextField TF_Search;

    @FXML
    private TextField TF_BookCode;

    @FXML
    private TextField TF_StudentCode;

    @FXML
    private TextField TF_WorkerCode;

    @FXML
    private TextField TF_BookName;

    @FXML
    private TextField TF_StudentName;

    @FXML
    private TextField TF_WorkerName;

    @FXML
    private TextField TF_Quantity;

    @FXML
    private TextField TF_Due;

    @FXML
    private TextField TF_Fine;

    @FXML
    private DatePicker DP_LendDate;

    @FXML
    private DatePicker DP_DueDate;

    @FXML
    private DatePicker DP_ReturnDate;

    @FXML
    private ImageView IV_BookCover;

    @FXML
    private ImageView IV_StudentPhoto;

    @FXML
    private ImageView IV_WorkerPhoto;

    @FXML
    private Button B_Add;

    private ObservableList<ListData> originalReturnList =
        FXCollections.observableArrayList();

    private Database db;

    @FXML
    public void initialize() {
        db = Database.getInstance();
        TF_Code.setOnKeyPressed(this::handleCodeSearch);
        DP_ReturnDate.setOnAction(e -> calculateFine());
        LV_ReturnedList.setCellFactory(list -> new CellView());

        loadReturnData();

        LV_ReturnedList.getSelectionModel()
            .selectedItemProperty()
            .addListener((obs, oldVal, newVal) -> {
                if (
                    newVal.getText() != null && newVal.getText().contains(",")
                ) {
                    String code = newVal.getText().split(",")[0].trim();
                    handleReturnListSelection(code);
                }
            });

        TF_Search.textProperty()
            .addListener((observable, oldValue, newValue) -> {
                ObservableList<ListData> filteredList =
                    FXCollections.observableArrayList();
                for (ListData data : originalReturnList) {
                    if (
                        data
                            .getText()
                            .toLowerCase()
                            .contains(newValue.toLowerCase())
                    ) {
                        filteredList.add(data);
                    }
                }
                LV_ReturnedList.setItems(filteredList);
            });

        B_Add.setOnAction(e -> handleAddReturn());

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

    private void handleReturnListSelection(String code) {
        try {
            ResultSet returnResult = db.read(
                "`Return`",
                "*",
                "Code = '" + code + "'"
            );
            if (returnResult != null && returnResult.next()) {
                TF_Code.setText(code);
                populateFieldsFromReturn(returnResult);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to load return data.");
        }
    }

    private void handleAddReturn() {
        String code = TF_Code.getText();
        try {
            ResultSet borrowResult = db.read(
                "Borrow",
                "*",
                "Code = '" + code + "'"
            );

            if (borrowResult != null && borrowResult.next()) {
                String bookCode = borrowResult.getString("BookCode");
                String studentCode = borrowResult.getString("StudentCode");
                String workerCode = borrowResult.getString("WorkerCode");
                int quantity = borrowResult.getInt("Quantity");
                LocalDate lendDate = borrowResult.getDate("LendDate") != null
                    ? borrowResult.getDate("LendDate").toLocalDate()
                    : null;
                LocalDate dueDate = borrowResult.getDate("DueDate") != null
                    ? borrowResult.getDate("DueDate").toLocalDate()
                    : null;
                LocalDate returnDate = DP_ReturnDate.getValue();
                long due = 0;
                long fine = 0;

                if (dueDate != null && returnDate != null) {
                    due = Math.max(
                        0,
                        ChronoUnit.DAYS.between(dueDate, returnDate)
                    );
                    fine = due * 5000;
                }

                // Confirm return with the user
                Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
                confirmationAlert.setTitle("Confirm Return");
                confirmationAlert.setHeaderText("Confirm Book Return");
                confirmationAlert.setContentText(
                    "Are you sure you want to return this book?"
                );

                Optional<ButtonType> result = confirmationAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Insert into Return table
                    String[] returnColumns = {
                        "Code",
                        "BookCode",
                        "StudentCode",
                        "WorkerCode",
                        "Quantity",
                        "LendDate",
                        "DueDate",
                        "ReturnDate",
                        "Due",
                        "Fine",
                    };
                    Object[] returnValues = {
                        code,
                        bookCode,
                        studentCode,
                        workerCode,
                        quantity,
                        lendDate != null
                            ? java.sql.Date.valueOf(lendDate)
                            : null,
                        dueDate != null ? java.sql.Date.valueOf(dueDate) : null,
                        returnDate != null
                            ? java.sql.Date.valueOf(returnDate)
                            : null,
                        due,
                        fine,
                    };
                    boolean insertResult = db.create(
                        "`Return`",
                        returnColumns,
                        returnValues
                    );

                    if (insertResult) {
                        String updateQuery =
                            "UPDATE Book SET Quantity = Quantity + " +
                            quantity +
                            " WHERE Code = '" +
                            bookCode +
                            "'";
                        boolean updateResult = db.execute(updateQuery);

                        if (updateResult) {
                            // Delete from Borrow table
                            String deleteWhere = "Code = '" + code + "'";
                            boolean deleteResult = db.delete(
                                "Borrow",
                                deleteWhere
                            );

                            if (deleteResult) {
                                showAlert(
                                    AlertType.INFORMATION,
                                    "Success",
                                    "Book returned successfully."
                                );
                                clearFields();
                                loadReturnData();
                            } else {
                                showAlert(
                                    AlertType.ERROR,
                                    "Error",
                                    "Failed to delete from Borrow table."
                                );
                            }
                        } else {
                            showAlert(
                                AlertType.ERROR,
                                "Error",
                                "Failed to update book quantity."
                            );
                        }
                    } else {
                        showAlert(
                            AlertType.ERROR,
                            "Error",
                            "Failed to add return record."
                        );
                    }
                } else {
                    // User cancelled the operation
                }
            } else {
                showAlert(
                    AlertType.WARNING,
                    "Not Found",
                    "Borrow record not found or already returned."
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(
                AlertType.ERROR,
                "Database Error",
                "Error accessing the database."
            );
        }
    }

    private void handleCodeSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            searchBorrowOrReturn();
        }
    }

    private void searchBorrowOrReturn() {
        String code = TF_Code.getText();
        clearFields();
        try {
            ResultSet borrowResult = db.read(
                "Borrow",
                "*",
                "Code = '" + code + "'"
            );
            if (borrowResult != null && borrowResult.next()) {
                populateFieldsFromBorrow(borrowResult);
                DP_ReturnDate.setDisable(false);
            } else {
                ResultSet returnResult = db.read(
                    "Return",
                    "*",
                    "Code = '" + code + "'"
                );
                if (returnResult != null && returnResult.next()) {
                    populateFieldsFromReturn(returnResult);
                    DP_ReturnDate.setDisable(true);
                } else {
                    showAlert(
                        AlertType.WARNING,
                        "Code Not Found",
                        "Borrow/Return code not found."
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(
                AlertType.ERROR,
                "Database Error",
                "Error accessing the database."
            );
        }
    }

    private void populateFieldsFromBorrow(ResultSet result)
        throws SQLException {
        TF_BookCode.setText(result.getString("BookCode"));
        TF_StudentCode.setText(result.getString("StudentCode"));
        TF_WorkerCode.setText(result.getString("WorkerCode"));
        TF_Quantity.setText(result.getString("Quantity"));
        if (result.getDate("LendDate") != null) DP_LendDate.setValue(
            result.getDate("LendDate").toLocalDate()
        );
        if (result.getDate("DueDate") != null) DP_DueDate.setValue(
            result.getDate("DueDate").toLocalDate()
        );
        loadBookData(result.getString("BookCode"));
        loadStudentData(result.getString("StudentCode"));
        loadWorkerData(result.getString("WorkerCode"));
    }

    private void populateFieldsFromReturn(ResultSet result)
        throws SQLException {
        TF_BookCode.setText(result.getString("BookCode"));
        TF_StudentCode.setText(result.getString("StudentCode"));
        TF_WorkerCode.setText(result.getString("WorkerCode"));
        TF_Quantity.setText(result.getString("Quantity"));
        if (result.getDate("LendDate") != null) DP_LendDate.setValue(
            result.getDate("LendDate").toLocalDate()
        );
        if (result.getDate("DueDate") != null) DP_DueDate.setValue(
            result.getDate("DueDate").toLocalDate()
        );
        if (result.getDate("ReturnDate") != null) DP_ReturnDate.setValue(
            result.getDate("ReturnDate").toLocalDate()
        );
        TF_Due.setText(result.getString("Due"));
        TF_Fine.setText(result.getString("Fine"));
        loadBookData(result.getString("BookCode"));
        loadStudentData(result.getString("StudentCode"));
        loadWorkerData(result.getString("WorkerCode"));
    }

    private void calculateFine() {
        LocalDate dueDate = DP_DueDate.getValue();
        LocalDate returnDate = DP_ReturnDate.getValue();
        if (dueDate != null && returnDate != null) {
            long dueDays = Math.max(
                0,
                ChronoUnit.DAYS.between(dueDate, returnDate)
            );
            TF_Due.setText(String.valueOf(dueDays));
            TF_Fine.setText(String.valueOf(dueDays * 5000));
        }
    }

    private void loadBookData(String bookCode) {
        try {
            ResultSet result = db.read(
                "Book",
                "Title, Cover",
                "Code = '" + bookCode + "'"
            );
            if (result != null && result.next()) {
                TF_BookName.setText(result.getString("Title"));
                setImage(IV_BookCover, result.getString("Cover"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadStudentData(String studentCode) {
        try {
            ResultSet result = db.read(
                "Student",
                "Name, Picture",
                "Code = '" + studentCode + "'"
            );
            if (result != null && result.next()) {
                TF_StudentName.setText(result.getString("Name"));
                setImage(IV_StudentPhoto, result.getString("Picture"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadWorkerData(String workerCode) {
        try {
            ResultSet result = db.read(
                "Worker",
                "Name, Picture",
                "Code = '" + workerCode + "'"
            );
            if (result != null && result.next()) {
                TF_WorkerName.setText(result.getString("Name"));
                setImage(IV_WorkerPhoto, result.getString("Picture"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setImage(ImageView view, String path) {
        if (path != null && !path.isEmpty()) {
            File file = new File(path);
            if (file.exists()) {
                view.setImage(new Image(file.toURI().toString()));
            } else {
                view.setImage(null);
                System.out.println("File not found: " + path);
            }
        } else {
            view.setImage(null);
        }
    }

    private void clearFields() {
        TF_Code.clear();
        TF_BookCode.clear();
        TF_StudentCode.clear();
        TF_WorkerCode.clear();
        TF_BookName.clear();
        TF_StudentName.clear();
        TF_WorkerName.clear();
        TF_Quantity.clear();
        TF_Due.clear();
        TF_Fine.clear();
        DP_LendDate.setValue(null);
        DP_DueDate.setValue(null);
        DP_ReturnDate.setValue(null);
        IV_BookCover.setImage(null);
        IV_StudentPhoto.setImage(null);
        IV_WorkerPhoto.setImage(null);
    }

    private void loadReturnData() {
        LV_ReturnedList.getItems().clear();
        originalReturnList.clear();
        ResultSet result = Database.getInstance().read("`Return`", "*", "");
        try {
            while (result.next()) {
                String code = result.getString("Code");
                String bookCode = result.getString("BookCode");
                String studentCode = result.getString("StudentCode");
                String workerCode = result.getString("WorkerCode");
                LocalDate lendDate = result.getDate("LendDate").toLocalDate();
                LocalDate dueDate = result.getDate("DueDate").toLocalDate();
                LocalDate returnDate = result
                    .getDate("ReturnDate")
                    .toLocalDate();
                int quantity = result.getInt("Quantity");
                String returnString = String.format(
                    "%s, %s, %s, %s, %s, %s, %s, %d",
                    code,
                    bookCode,
                    studentCode,
                    workerCode,
                    lendDate,
                    dueDate,
                    returnDate,
                    quantity
                );
                ListData data = new ListData(returnString, null);
                originalReturnList.add(data);
            }
            LV_ReturnedList.setItems(
                FXCollections.observableArrayList(originalReturnList)
            );
        } catch (SQLException e) {
            System.out.println(
                "Error loading returned data: " + e.getMessage()
            );
        }
    }

    private void showAlert(AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
