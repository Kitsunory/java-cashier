package com.duta.library.ui;

import com.duta.library.model.ListData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class CellView extends ListCell<ListData> {

    private HBox hbox;
    private ImageView imageView;
    private Label label;

    public CellView() {
        super();
        hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(20, 20, 20, 20));

        imageView = new ImageView();
        imageView.setFitWidth(50);
        imageView.setFitHeight(58);

        label = new Label();
        label.setStyle("-fx-text-fill: white;");
        label.setAlignment(Pos.CENTER_LEFT);
        label.prefHeightProperty().bind(imageView.fitHeightProperty());
        label.setPadding(new Insets(0, 20, 0, 20));

        hbox.getChildren().addAll(imageView, label);
    }

    @Override
    protected void updateItem(ListData item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            Image image = new Image("file:" + item.getImagePath());
            imageView.setImage(image);
            label.setText(item.getText());
            setGraphic(hbox);
        }
        setStyle("-fx-background-color: #202020;");
    }
}
