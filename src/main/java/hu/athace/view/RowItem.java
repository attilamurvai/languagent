package hu.athace.view;

public class RowItem {
    private final String word;
    private final Long globalCount;
    private final Integer globalRank;
    private final int localCount;

    public RowItem(String word, Long globalCount, Integer globalRank, int localCount) {
        this.word = word;
        this.globalCount = globalCount;
        this.globalRank = globalRank;
        this.localCount = localCount;
    }

    public String getWord() {
        return word;
    }

    public Long getGlobalCount() {
        return globalCount;
    }

    public Integer getGlobalRank() {
        return globalRank;
    }

    public int getLocalCount() {
        return localCount;
    }

}
