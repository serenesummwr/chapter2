package se233.chapter2.view;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import se233.chapter2.controller.draw.DrawCurrencyInfoTask;
import se233.chapter2.controller.draw.DrawGraphTask;
import se233.chapter2.controller.draw.DrawTopAreaTask;
import se233.chapter2.model.Currency;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;


public class CurrencyPane extends BorderPane {
    private Currency currency;
    public CurrencyPane(Currency currency) {
        this.setPadding(new Insets(0));
        this.setPrefSize(840, 300);
        this.setStyle("-fx-border-color: black");
        try {
            this.refreshPane(currency);
        } catch (ExecutionException e) {
            System.out.println("Encountered an execution exception.");
        } catch (InterruptedException e) {
            System.out.println("Encountered an interrupted exception.");
        }
    }

    public void refreshPane(Currency currency) throws ExecutionException, InterruptedException {
        this.currency = currency;
        FutureTask<Pane> drawCurrencyInfoTask = new FutureTask<Pane>(new DrawCurrencyInfoTask(currency));
        FutureTask<VBox> drawGraphTask = new FutureTask<VBox>(new DrawGraphTask(currency));
        FutureTask<Pane> drawTopAreaTask = new FutureTask<Pane>(new DrawTopAreaTask(currency));

        System.out.println("Creating ExecutorService for currency: " + currency.getShortCode());
        try (ExecutorService executor = Executors.newSingleThreadExecutor();) {
            System.out.println("ExecutorService created and running");
            executor.execute(drawCurrencyInfoTask);
            System.out.println("Task submitted: DrawCurrencyInfoTask");
            executor.execute(drawGraphTask);
            System.out.println("Task submitted: DrawGraphTask");
            executor.execute(drawTopAreaTask);
            System.out.println("Task submitted: DrawTopAreaTask");
            System.out.println("All tasks submitted to ExecutorService");
        }
        System.out.println("ExecutorService shutdown successfully");
        VBox currencyGraph = (VBox) drawGraphTask.get();
        Pane topArea = (Pane) drawTopAreaTask.get();
        Pane currencyInfo = (Pane) drawCurrencyInfoTask.get();

        this.setTop(topArea);
        this.setLeft(currencyInfo);
        this.setCenter(currencyGraph);
    }
}
