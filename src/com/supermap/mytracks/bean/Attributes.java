package com.supermap.mytracks.bean;

public class Attributes {
    private String description = null;
    private String title = null;
    private boolean updated = true;
    
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public boolean isUpdated() {
        return updated;
    }
    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

}
