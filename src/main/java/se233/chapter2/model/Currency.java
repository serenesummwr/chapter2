package se233.chapter2.model;

import java.util.List;

public class Currency {
    private String shortCode;
    private CurrencyEntity current;
    private List<CurrencyEntity> historical;
    private Boolean isWatch;
    private Double watchRate;
    public Currency(String shortCode) {
        this.shortCode = shortCode;
        this.isWatch = false;
        this.watchRate = 0.0;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public CurrencyEntity getCurrent() {
        return current;
    }

    public void setCurrent(CurrencyEntity current) {
        this.current = current;
    }

    public List<CurrencyEntity> getHistorical() {
        return historical;
    }

    public void setHistorical(List<CurrencyEntity> historical) {
        this.historical = historical;
    }

    public Boolean getWatch() {
        return isWatch;
    }

    public void setWatch(Boolean watch) {
        isWatch = watch;
    }

    public Double getWatchRate() {
        return watchRate;
    }

    public void setWatchRate(Double watchRate) {
        this.watchRate = watchRate;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Currency))
            return false;
        else
            return ((Currency) o).getShortCode().equals(shortCode);
    }

}
