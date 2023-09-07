package fi.muni.cz.core.analysis.phases.modelfitting;

public class TrendTestResult {

    private double trendValue;
    private boolean trendFound;

    public double getTrendValue() {
        return trendValue;
    }

    public void setTrendValue(double trendValue) {
        this.trendValue = trendValue;
    }

    public boolean isTrendFound() {
        return trendFound;
    }

    public void setTrendFound(boolean trendFound) {
        this.trendFound = trendFound;
    }
}
