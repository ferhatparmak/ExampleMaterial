package com.wikia.wikiaoriginals.service.model;

import java.util.List;

/**
 * This model is for putting @WikiSummary's in a list which is returned from the 'getList' service method
 */
public class WikiSummaryList {
    private List<WikiSummary> items;
    private int next;
    private int total;
    private int batches;
    private int currentBatch;

    public List<WikiSummary> getItems() {
        return items;
    }

    public int getNext() {
        return next;
    }

    public int getTotal() {
        return total;
    }

    public int getBatches() {
        return batches;
    }

    public int getCurrentBatch() {
        return currentBatch;
    }
}
