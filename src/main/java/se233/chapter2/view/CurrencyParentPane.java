package se233.chapter2.view;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import se233.chapter2.model.Currency;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class CurrencyParentPane extends ScrollPane {
    private VBox subpane = new VBox();
    public CurrencyParentPane(List<Currency> currencyList) throws ExecutionException, InterruptedException {
        refreshPane(currencyList);
        this.setPadding(new Insets(0));
        this.setMaxHeight(630);
        this.setContent(subpane);
        this.setFitToWidth(true);
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
    }

    public void refreshPane(List<Currency> currencyList) throws ExecutionException, InterruptedException {
        subpane.getChildren().clear();
        for (int i = 0; i < currencyList.size(); i++) {
            CurrencyPane cp = new CurrencyPane(currencyList.get(i));
            subpane.getChildren().add(cp);
        }
    }
}
