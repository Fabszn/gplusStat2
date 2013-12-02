package model

import play.api.libs.json.{JsValue, Writes}
import play.api.libs.json._


case class Article(title: String, googleId: String, content: String, author: String, plusone: Long, shared: Long, current: Boolean)

/*
* private String title;
    private String googleId;
    private String content;
    private String author;
    private long publicationDate;
    private long insertionDate;
    private long plusOne;
    private long shared;
    private boolean current = false;
    private String url;
* */


object Article {




}