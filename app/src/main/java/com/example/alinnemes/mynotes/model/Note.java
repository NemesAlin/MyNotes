package com.example.alinnemes.mynotes.model;

/**
 * Created by alin.nemes on 12-Jul-16.
 */
public class Note {

    private int id;
    private String subject;
    private String body;
    private String dateCreated;
    private String locationCreated;
    private String photoPath;
    private String audioPath;
    private String videoPath;

    public Note(int id, String subject, String body, String photoPath, String audioPath, String videoPath, String dateCreated, String locationCreated) {
        this.id = id;
        this.subject = subject;
        this.body = body;
        this.dateCreated = dateCreated;
        this.locationCreated = locationCreated;
        this.photoPath = photoPath;
        this.audioPath = audioPath;
        this.videoPath = videoPath;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", locationCreated='" + locationCreated + '\'' +
                ", photoPath='" + photoPath + '\'' +
                ", audioPath='" + audioPath + '\'' +
                ", videoPath='" + videoPath + '\'' +
                '}';
    }

    public String getLocationCreated() {
        return locationCreated;
    }

    public void setLocationCreated(String locationCreated) {
        this.locationCreated = locationCreated;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
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
