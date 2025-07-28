package se233.chapter2.controller.draw;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import se233.chapter2.controller.AllEventHandlers;
import se233.chapter2.model.Currency;

import java.util.concurrent.Callable;

public class DrawTopAreaTask implements Callable<Pane> {
    private Currency currency;
    private Button watch;
    private Button delete;
    private Button unwatch;
    public DrawTopAreaTask(Currency currency) {
        this.currency = currency;
    }
    @Override
    public Pane call() {
        this.watch = new Button("Watch");
        this.delete = new Button("Delete");
        this.unwatch = new Button("Unwatch");

        this.delete.setOnAction(e -> AllEventHandlers.onDelete(currency.getShortCode()));
        this.watch.setOnAction(e -> AllEventHandlers.onWatch(currency.getShortCode()));
        this.unwatch.setOnAction(e -> AllEventHandlers.onUnwatch(currency.getShortCode()));

        HBox topArea = new HBox(10);
        topArea.setPadding(new Insets(5));
        topArea.getChildren().addAll(watch, unwatch, delete);
        ((HBox) topArea).setAlignment(Pos.CENTER_RIGHT);
        return topArea;
    }
}
