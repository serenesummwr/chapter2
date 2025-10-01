package se233.chapter2.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se233.chapter2.Launcher;
import se233.chapter2.model.Currency;
import se233.chapter2.model.CurrencyEntity;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.UnaryOperator;

public class AllEventHandlers {
    private static final Logger logger = LogManager.getLogger(AllEventHandlers.class);

    public static void onRefresh() {
        try {
            Launcher.refreshPane();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static TextInputDialog createTextInputDialog(String title, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setContentText(content);
        dialog.setHeaderText(null);
        dialog.setGraphic(null);

        return dialog;
    }

    public static Optional<String> inputCurrencyCode(String title, String content) throws IOException {
        TextInputDialog dialog = createTextInputDialog(title, content);
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (change.isContentChange()) {
                //change.setText(change.getText().toUpperCase());
                String newText = change.getControlNewText();
                if (newText.matches("[a-zA-Z]*") && newText.length() <= 3) {
                    return change;
                }
            }
            return null;
        };
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        dialog.getEditor().setTextFormatter(formatter);
        Optional<String> shortCode = dialog.showAndWait();
        if (shortCode.isPresent()) {
            if (shortCode.get().isEmpty()) {
                throw new IOException("Currency code cannot be empty");
            }
        }
        return shortCode;
    }

    public static Optional<String> inputDouble(String title, String content) throws IOException {
        TextInputDialog dialog = createTextInputDialog(title, content);
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (change.isContentChange()) {
                change.setText(change.getText().toUpperCase());
                String newText = change.getControlNewText();
                if (newText.matches("[0-9]*\\.?[0-9]*")) {
                    return change;
                }
            }
            return null;
        };
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        dialog.getEditor().setTextFormatter(formatter);
        Optional<String> n = dialog.showAndWait();
        if (n.isPresent()) {
            if (n.get().isEmpty()) {
                throw new IOException("Input cannot be empty");
            }
        }
        return n;
    }

    public static void onAdd() {
        try {
            Optional<String> code = inputCurrencyCode("Add Currency", "Currency code:");
            if (code.isPresent()) {
                List<Currency> currencyList = Launcher.getCurrencyList();
                Currency c = new Currency(code.get().toUpperCase());
                if (currencyList.contains(c)) {
                    Alert alert = new Alert(Alert.AlertType.NONE, String.format("Currency already exists with this code: %s", c.getShortCode()), ButtonType.OK);
                    alert.showAndWait();
                    logger.warn("Attempted to add duplicate currency: {}", c.getShortCode());
                    return;
                }
                List<CurrencyEntity> cList = FetchData.fetchRange(Launcher.getBase(), c.getShortCode(), Launcher.getN());
                c.setHistorical(cList);
                c.setCurrent(cList.get(cList.size() - 1));
                currencyList.add(c);
                Launcher.setCurrencyList(currencyList);
                Launcher.refreshPane();
                logger.info("Added currency: {} (current rate: {})", c.getShortCode(), c.getCurrent() != null ? c.getCurrent().getRate() : "n/a");
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error while adding currency (thread/exec issue)", e);
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("Invalid currency code input while adding", e);
            Alert alert = new Alert(Alert.AlertType.NONE, e.getMessage(), new ButtonType("Try again"));
            alert.setTitle("Invalid Currency Code");
            alert.showAndWait();
            onAdd();
        }
    }

    public static void onDelete(String code) {
        try {
            List<Currency> currencyList = Launcher.getCurrencyList();
            int index = -1;
            for (int i = 0; i < currencyList.size(); i++) {
                if (currencyList.get(i).getShortCode().equals(code)) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                currencyList.remove(index);
                Launcher.setCurrencyList(currencyList);
                Launcher.refreshPane();
                logger.info("Deleted currency: {}", code);
            } else {
                logger.warn("Attempted to delete non-existing currency: {}", code);
            }
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error while deleting currency: {}", code, e);
            e.printStackTrace();
        }
    }

    public static void onWatch(String code) {
        try {
            List<Currency> currencyList = Launcher.getCurrencyList();
            int index = -1;
            for (int i = 0; i < currencyList.size(); i++) {
                if (currencyList.get(i).getShortCode().equals(code)) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                Optional<String> retrievedRate = inputDouble("Add Watch","Rate:");
                if (retrievedRate.isPresent()) {
                    double rate = Double.parseDouble(retrievedRate.get());
                    currencyList.get(index).setWatch(true);
                    currencyList.get(index).setWatchRate(rate);
                    Launcher.setCurrencyList(currencyList);
                    Launcher.refreshPane();
                }
                Launcher.setCurrencyList(currencyList);
                Launcher.refreshPane();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.NONE, e.getMessage(), new ButtonType("Try again"));
            alert.setTitle("Invalid Input");
            alert.showAndWait();
            onWatch(code);
        }
    }

    public static void onUnwatch(String code) {
        try {
            List<Currency> currencyList = Launcher.getCurrencyList();
            int index = -1;
            for (int i = 0; i < currencyList.size(); i++) {
                if (currencyList.get(i).getShortCode().equals(code)) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                currencyList.get(index).setWatch(false);
                currencyList.get(index).setWatchRate(0.0);
                Launcher.setCurrencyList(currencyList);
                Launcher.refreshPane();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void onChange() {
        try {
            Optional<String> code = inputCurrencyCode("Change Base Currency", "Base Currency Code:");
            if (code.isPresent()) {
                List<Currency> currencyList = Launcher.getCurrencyList();
                String base = code.get().toUpperCase();
                for (int i = 0; i < currencyList.size(); i++) {
                    if (currencyList.get(i).getShortCode().equals(base)) {
                        currencyList.set(i, new Currency(Launcher.getBase()));
                    }
                    List<CurrencyEntity> cList = FetchData.fetchRange(base, currencyList.get(i).getShortCode(), Launcher.getN());
                    currencyList.get(i).setHistorical(cList);
                    currencyList.get(i).setCurrent(cList.get(cList.size() - 1));
                    currencyList.get(i).setWatch(false);
                    currencyList.get(i).setWatchRate(0.0);
                }
                Launcher.setCurrencyList(currencyList);
                Launcher.setBase(base);
                Launcher.refreshPane();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.NONE, e.getMessage(), new ButtonType("Try again"));
            alert.setTitle("Invalid Currency Code");
            alert.showAndWait();
            onChange();
        }
    }
}
