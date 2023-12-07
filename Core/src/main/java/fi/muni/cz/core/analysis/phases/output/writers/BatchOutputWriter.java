package fi.muni.cz.core.analysis.phases.output.writers;

import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import java.util.List;

/** @author Valtteri Valtonen, valtonenvaltteri@gmail.com */
public interface BatchOutputWriter {

  /**
   * Write batch analysis output data to a file. A batch analysis involves multiple data sources.
   *
   * @param dtoList data to write
   */
  void writeBatchOutputDataToFile(List<ReliabilityAnalysisDto> dtoList);
}
