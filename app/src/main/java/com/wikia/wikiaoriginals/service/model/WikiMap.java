package com.wikia.wikiaoriginals.service.model;

import java.util.Map;

/**
 * This model for putting @Wiki items in a map which returned by 'getDetails' service method.
 *
 */
public class WikiMap {
    private Map<Integer, Wiki> items;

    public Map<Integer, Wiki> getItems() {
        return items;
    }
}
