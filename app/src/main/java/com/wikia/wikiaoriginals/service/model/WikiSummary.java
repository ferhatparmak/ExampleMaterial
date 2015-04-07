package com.wikia.wikiaoriginals.service.model;

/**
 * Model of the result from 'getList' service request. Service returns a list. This model is for each item.
 *
 * Ex.
 * {id: 410,
 *  name: "Yu-Gi-Oh!",
 *  hub: "Gaming",
 *  language: "en",
 *  topic: null,
 *  domain: "yugioh.wikia.com"}
 */
public class WikiSummary {
    private int id;
    private String name;
    private String hub;
    private String language;
    private String topic;
    private String domain;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHub() {
        return hub;
    }

    public String getLanguage() {
        return language;
    }

    public String getTopic() {
        return topic;
    }

    public String getDomain() {
        return domain;
    }
}
