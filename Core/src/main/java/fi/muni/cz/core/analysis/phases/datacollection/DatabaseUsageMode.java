package fi.muni.cz.core.analysis.phases.datacollection;

/** @author Valtteri Valtonen valtonenvaltteri@gmail.com */
public enum DatabaseUsageMode {
  DO_NOT_USE_DATABASE,
  USE_DATABASE_IF_SNAPSHOT_AVAILABLE,
  USE_DATABASE_AND_OVERWRITE_SNAPSHOTS
}
