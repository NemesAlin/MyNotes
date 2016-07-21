package com.example.alinnemes.mynotes.model;

/**
 * Created by alin.nemes on 12-Jul-16.
 */
public class Note {

    private int id;
    private String subject;
    private String body;
    private String photoPath;

    public Note(int id, String subject, String body, String photoPath) {
        this.id = id;
        this.subject = subject;
        this.body = body;
        this.photoPath = photoPath;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", photoPath='" + photoPath + '\'' +
                '}';
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


}
