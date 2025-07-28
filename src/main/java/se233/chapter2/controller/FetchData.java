package se233.chapter2.controller;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import se233.chapter2.model.CurrencyEntity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class FetchData {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static List<CurrencyEntity> fetchRange(String base, String symbol, int N) {
        String dateEnd = LocalDate.now().format(formatter);
        String dateStart = LocalDate.now().minusDays(N).format(formatter);
        String urlStr = String.format("https://cmu.to/SE233currencyapi?base=%s&symbol=%s&start_date=%s&end_date=%s", base, symbol, dateStart, dateEnd);
        List<CurrencyEntity> histList = new ArrayList<>();
        try {
            String retrievedJson = IOUtils.toString(new URL(urlStr), Charset.defaultCharset());
            JSONObject jsonOBJ = new JSONObject(retrievedJson).getJSONObject("rates");
            Iterator keystoCopyIterator = jsonOBJ.keys();
            while (keystoCopyIterator.hasNext()) {
                String key = (String) keystoCopyIterator.next();
                Double rate = Double.parseDouble(jsonOBJ.get(key).toString());
                histList.add(new CurrencyEntity(rate, key));
            }
            histList.sort(new Comparator<CurrencyEntity>() {
                @Override
                public int compare(CurrencyEntity o1, CurrencyEntity o2) {
                    return o1.getTimestamp().compareTo(o2.getTimestamp());
                }
            });
        } catch (MalformedURLException e) {
            System.err.println("Encountered a Malformed Url exception");
        } catch (IOException e) {
            System.err.println("Encounter an IO exception");
        } catch (JSONException e) {
             throw new RuntimeException(String.format("The exchange rate for %s->%s is unavailable.", base, symbol));
        }
        return histList;
    }

}
