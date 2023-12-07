package fi.muni.cz.models.leastsquaresolver;

/** @author Valtteri Valtonen, valtonenvaltteri@gmail.com */
public class SolverResult {
  private double[] parameters;
  private Double aic;
  private Double bic;
  private Double pseudoRSquared;

  public double[] getParameters() {
    return parameters;
  }

  public void setParameters(double[] parameters) {
    this.parameters = parameters;
  }

  public Double getAic() {
    return aic;
  }

  public void setAic(Double aic) {
    this.aic = aic;
  }

  public Double getBic() {
    return bic;
  }

  public void setBic(Double bic) {
    this.bic = bic;
  }

  public Double getPseudoRSquared() {
    return pseudoRSquared;
  }

  public void setPseudoRSquared(Double pseudoRSquared) {
    this.pseudoRSquared = pseudoRSquared;
  }
}
