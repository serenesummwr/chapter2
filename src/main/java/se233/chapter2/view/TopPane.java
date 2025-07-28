package se233.chapter2.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import se233.chapter2.Launcher;
import se233.chapter2.controller.AllEventHandlers;

import java.time.LocalDateTime;

public class TopPane extends FlowPane {
    private Button refresh;
    private Button add;
    private Label update;
    private Button change;

    public TopPane() {
        this.setPadding(new Insets(10));
        this.setHgap(10);
        this.setPrefSize(640, 20);
        add = new Button("Add");
        refresh = new Button("Refresh");
        change = new Button("Base Currency: ");
        refresh.setOnAction(e -> AllEventHandlers.onRefresh());
        add.setOnAction(e -> AllEventHandlers.onAdd());
        change.setOnAction(e -> AllEventHandlers.onChange());
        update = new Label();
        refreshPane();
        this.getChildren().addAll(refresh, add, change, update);
    }
    public void refreshPane() {
        update.setText(String.format("Last update: %s", LocalDateTime.now().toString()));
        change.setText(String.format("Base Currency: %s", Launcher.getBase()));
    }
}
