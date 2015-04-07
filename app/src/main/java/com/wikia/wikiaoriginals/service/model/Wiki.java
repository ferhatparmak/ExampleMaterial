package com.wikia.wikiaoriginals.service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Model of the result from 'getDetails' service request. Service returns a map. This is for each item.
 * Ignored some(or many) fields.
 *
 * It implements serializable because it will be bundled in between activity states(orientation change)
 *
 * Ex.
 * {id: 159,
 *  wordmark: "http://images.wikia.com/lotr/images/8/89/Wiki-wordmark.png",
 *  title: "The One Wiki to Rule Them All",
 *  url: "http://lotr.wikia.com/",
 *  image: "http://vignette1.wikia.nocookie.net/wikiaglobal/images/a/aa/Wikia-Visualization-Main%2Clotr.png/revision/latest?cb=20140530091625"}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Wiki implements Serializable {
    private int id;
    private String title;
    private String url;
    private String image;

    public Wiki(){} //Jackson Only

    public Wiki(int id, String title, String url, String image) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getImage() {
        return image;
    }
}
