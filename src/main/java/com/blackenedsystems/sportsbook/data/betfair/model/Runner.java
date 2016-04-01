package com.blackenedsystems.sportsbook.data.betfair.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Alan Tibbetts
 * @since 01/04/16
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Runner {
    private String selectionId;
    private String runnerName;
    private int sortPriority;
    private MetaData metadata;

    public String getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(String selectionId) {
        this.selectionId = selectionId;
    }

    public String getRunnerName() {
        return runnerName;
    }

    public void setRunnerName(String runnerName) {
        this.runnerName = runnerName;
    }

    public int getSortPriority() {
        return sortPriority;
    }

    public void setSortPriority(int sortPriority) {
        this.sortPriority = sortPriority;
    }

    public MetaData getMetadata() {
        return metadata;
    }

    public void setMetadata(MetaData metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "Runner{" +
                "selectionId='" + selectionId + '\'' +
                ", runnerName='" + runnerName + '\'' +
                ", sortPriority=" + sortPriority +
                ", metadata=" + metadata +
                '}';
    }


    private class MetaData {
        private String runnerId;

        public String getRunnerId() {
            return runnerId;
        }

        public void setRunnerId(String runnerId) {
            this.runnerId = runnerId;
        }

        @Override
        public String toString() {
            return "MetaData{" +
                    "runnerId='" + runnerId + '\'' +
                    '}';
        }
    }
}
