package org.vaadin.card;


public abstract class Card {
    protected String type;
    protected String name;
    protected String path;
    protected  boolean facedown = false;

    //functions

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getPathToImage() {
        if (facedown) {
            return "images/default.png";
        } else {
            return this.path;
        }
    }

    public boolean getFacedown() {
        return facedown;
    }

    public void setFacedown() {
        facedown = true;
    }

    public void setFaceup() {
        facedown = false;
    }

}
