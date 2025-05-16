package com.duta.library.controller;

import com.duta.library.model.ListData;
import com.duta.library.repository.Database;
import com.duta.library.ui.CellView;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Listing implements Initializable {

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
    private TextField TF_Search;

    @FXML
    private TextField TF_Code;

    @FXML
    private TextField TF_Title;

    @FXML
    private TextField TF_Writer;

    @FXML
    private TextField TF_Publisher;

    @FXML
    private TextField TF_Year;

    @FXML
    private TextField TF_Quantity;

    @FXML
    private Button B_Add;

    @FXML
    private Button B_Edit;

    @FXML
    private Button B_Remove;

    @FXML
    private ListView<ListData> LV_BookList;

    @FXML
    private ImageView IV_BookCover;

    @FXML
    private Text T_CoverFilename;

    @FXML
    private Button B_ChangeCover;

    private File selectedCoverFile;
    private ObservableList<ListData> originalBookList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LV_BookList.setCellFactory(param -> new CellView());
        loadBookData();

        originalBookList = FXCollections.observableArrayList(
            LV_BookList.getItems()
        );

        B_ChangeCover.setOnAction(e -> {
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
            selectedCoverFile = fileChooser.showOpenDialog(
                B_ChangeCover.getScene().getWindow()
            );
            if (selectedCoverFile != null) {
                Image image = new Image(selectedCoverFile.toURI().toString());
                IV_BookCover.setImage(image);
                T_CoverFilename.setText(selectedCoverFile.getName());
            }
        });

        B_Add.setOnAction(e -> {
            addBook();
        });

        B_Edit.setOnAction(e -> {
            editBook();
        });

        B_Remove.setOnAction(e -> {
            removeBook();
        });

        LV_BookList.getSelectionModel()
            .selectedItemProperty()
            .addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateFields(newSelection);
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

        TF_Search.setOnKeyPressed(event -> {
            filterList();
            if (event.getCode() == KeyCode.ESCAPE) {
                resetFilter();
            }
        });
    }

    private void loadBookData() {
        LV_BookList.getItems().clear();
        ResultSet result = Database.getInstance().read("Book", "*", "");
        try {
            while (result.next()) {
                String code = result.getString("Code");
                String title = result.getString("Title");
                String writer = result.getString("Writer");
                String publisher = result.getString("Publisher");
                String year = result.getString("Year");
                String quantity = result.getString("Quantity");

                String bookString = String.format(
                    "%s, %s, %s, %s, %s, %s",
                    code,
                    title,
                    writer,
                    publisher,
                    year,
                    quantity
                );

                String coverPath = null;
                try {
                    coverPath = result.getString("Cover");
                } catch (SQLException ex) {
                    System.out.println(
                        "Error reading cover path from database: " +
                        ex.getMessage()
                    );
                }

                ListData data = new ListData(bookString, coverPath);
                LV_BookList.getItems().add(data);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void addBook() {
        String code = TF_Code.getText();
        String title = TF_Title.getText();
        String writer = TF_Writer.getText();
        String publisher = TF_Publisher.getText();
        String year = TF_Year.getText();
        String quantity = TF_Quantity.getText();

        if (
            code.isEmpty() ||
            title.isEmpty() ||
            writer.isEmpty() ||
            publisher.isEmpty() ||
            year.isEmpty() ||
            quantity.isEmpty()
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
            "Title",
            "Writer",
            "Publisher",
            "Year",
            "Quantity",
            "Cover",
        };
        Object[] values = { code, title, writer, publisher, year, quantity, null };

        if (selectedCoverFile != null) {
            values[6] = selectedCoverFile.getAbsolutePath();
        }

        boolean success = Database.getInstance()
            .create("Book", columns, values);

        if (success) {
            showAlert(
                Alert.AlertType.INFORMATION,
                "Success",
                "Book added successfully!"
            );
            loadBookData();
            clearFields();
            resetFilter();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add book.");
        }
    }

    private void editBook() {
        String code = TF_Code.getText();
        String title = TF_Title.getText();
        String writer = TF_Writer.getText();
        String publisher = TF_Publisher.getText();
        String year = TF_Year.getText();
        String quantity = TF_Quantity.getText();

        if (code.isEmpty()) {
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Please select a book to edit."
            );
            return;
        }

        String[] columns = { "Title", "Writer", "Publisher", "Year", "Quantity", "Cover" };
        Object[] values = { title, writer, publisher, year, quantity, null };

        if (selectedCoverFile != null) {
            values[5] = selectedCoverFile.getAbsolutePath();
        }

        String condition = "Code = '" + code + "'";

        boolean success = Database.getInstance()
            .update("Book", columns, values, condition);

        if (success) {
            showAlert(
                Alert.AlertType.INFORMATION,
                "Success",
                "Book updated successfully!"
            );
            loadBookData();
            resetFilter();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update book.");
        }
    }

    private void removeBook() {
        String code = TF_Code.getText();

        if (code.isEmpty()) {
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Please select a book to remove."
            );
            return;
        }

        String condition = "Code = '" + code + "'";
        boolean success = Database.getInstance().delete("Book", condition);

        if (success) {
            showAlert(
                Alert.AlertType.INFORMATION,
                "Success",
                "Book removed successfully!"
            );
            loadBookData();
            clearFields();
            resetFilter();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to remove book.");
        }
    }

    private void populateFields(ListData data) {
        String[] parts = data.getText().split(", ");

        if (parts.length >= 6) {
            TF_Code.setText(parts[0]);
            TF_Title.setText(parts[1]);
            TF_Writer.setText(parts[2]);
            TF_Publisher.setText(parts[3]);
            TF_Year.setText(parts[4]);
            TF_Quantity.setText(parts[5]);
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
                    IV_BookCover.setImage(image);
                    T_CoverFilename.setText(file.getName());
                    selectedCoverFile = file;
                } else {
                    IV_BookCover.setImage(null);
                    T_CoverFilename.setText("Image file not found");
                    selectedCoverFile = null;
                }
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
                IV_BookCover.setImage(null);
                T_CoverFilename.setText("Error loading image");
                selectedCoverFile = null;
            }
        } else {
            IV_BookCover.setImage(null);
            T_CoverFilename.setText("No cover image");
            selectedCoverFile = null;
        }
    }

    private void clearFields() {
        TF_Code.clear();
        TF_Title.clear();
        TF_Writer.clear();
        TF_Publisher.clear();
        TF_Year.clear();
        TF_Quantity.clear();
        IV_BookCover.setImage(null);
        T_CoverFilename.setText("");
        selectedCoverFile = null;
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

    private void filterList() {
        String codeFilter = TF_Search.getText().toLowerCase();

        ObservableList<ListData> filteredList =
            FXCollections.observableArrayList();

        for (ListData data : originalBookList) {
            String bookString = data.getText().toLowerCase();
            if (bookString.contains(codeFilter)) {
                filteredList.add(data);
            }
        }

        LV_BookList.setItems(filteredList);
    }

    private void resetFilter() {
        TF_Search.clear();
        TF_Code.clear();
        TF_Title.clear();
        TF_Writer.clear();
        TF_Publisher.clear();
        TF_Year.clear();
        TF_Quantity.clear();
        LV_BookList.setItems(originalBookList);
    }
}
