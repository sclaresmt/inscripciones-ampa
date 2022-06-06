package org.ampainscripciones.model;

import java.time.LocalDateTime;

public class InscriptionDTO {

    private String email;

    private String parent1Name;

    private String parent2Name;

    private String child1Name;

    private String child2Name;

    private String ausiasChild1Name;

    private String ausiasChild2Name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getParent1Name() {
        return parent1Name;
    }

    public void setParent1Name(String parent1Name) {
        this.parent1Name = parent1Name;
    }

    public String getParent2Name() {
        return parent2Name;
    }

    public void setParent2Name(String parent2Name) {
        this.parent2Name = parent2Name;
    }

    public String getChild1Name() {
        return child1Name;
    }

    public void setChild1Name(String child1Name) {
        this.child1Name = child1Name;
    }

    public String getChild2Name() {
        return child2Name;
    }

    public void setChild2Name(String child2Name) {
        this.child2Name = child2Name;
    }

    public String getAusiasChild1Name() {
        return ausiasChild1Name;
    }

    public void setAusiasChild1Name(String ausiasChild1Name) {
        this.ausiasChild1Name = ausiasChild1Name;
    }

    public String getAusiasChild2Name() {
        return ausiasChild2Name;
    }

    public void setAusiasChild2Name(String ausiasChild2Name) {
        this.ausiasChild2Name = ausiasChild2Name;
    }
}
