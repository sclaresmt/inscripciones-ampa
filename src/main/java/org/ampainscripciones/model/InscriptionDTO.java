package org.ampainscripciones.model;

import java.time.LocalDateTime;

public class InscriptionDTO {

    private LocalDateTime timestamp;

    private String email;

    private String parent1Name;

    private Double parent1PhoneNumber;

    private String parent2Name;

    private Double parent2PhoneNumber;

    private Double ausiasChildrenNumber;

    private String ausiasChild1Name;

    private String ausiasChild1Course;

    private String ausiasChild2Name;

    private String ausiasChild2Course;

    private Double lluisChildrenNumber;

    private String lluisChild1Name;

    private String lluisChild1Course;

    private String lluisChild2Name;

    private String lluisChild2Course;

    private String paymentFileUrl;

    private String protectionDataPolicy;

    private String childrenImageAuthorization;

    private Boolean isPaymentValidated;

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

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

    public Double getParent1PhoneNumber() {
        return parent1PhoneNumber;
    }

    public void setParent1PhoneNumber(Double parent1PhoneNumber) {
        this.parent1PhoneNumber = parent1PhoneNumber;
    }

    public String getParent2Name() {
        return parent2Name;
    }

    public void setParent2Name(String parent2Name) {
        this.parent2Name = parent2Name;
    }

    public Double getParent2PhoneNumber() {
        return parent2PhoneNumber;
    }

    public void setParent2PhoneNumber(Double parent2PhoneNumber) {
        this.parent2PhoneNumber = parent2PhoneNumber;
    }

    public Double getAusiasChildrenNumber() {
        return ausiasChildrenNumber;
    }

    public void setAusiasChildrenNumber(Double ausiasChildrenNumber) {
        this.ausiasChildrenNumber = ausiasChildrenNumber;
    }

    public String getAusiasChild1Name() {
        return ausiasChild1Name;
    }

    public void setAusiasChild1Name(String ausiasChild1Name) {
        this.ausiasChild1Name = ausiasChild1Name;
    }

    public String getAusiasChild1Course() {
        return ausiasChild1Course;
    }

    public void setAusiasChild1Course(String ausiasChild1Course) {
        this.ausiasChild1Course = ausiasChild1Course;
    }

    public String getAusiasChild2Name() {
        return ausiasChild2Name;
    }

    public void setAusiasChild2Name(String ausiasChild2Name) {
        this.ausiasChild2Name = ausiasChild2Name;
    }

    public String getAusiasChild2Course() {
        return ausiasChild2Course;
    }

    public void setAusiasChild2Course(String ausiasChild2Course) {
        this.ausiasChild2Course = ausiasChild2Course;
    }

    public Double getLluisChildrenNumber() {
        return lluisChildrenNumber;
    }

    public void setLluisChildrenNumber(Double lluisChildrenNumber) {
        this.lluisChildrenNumber = lluisChildrenNumber;
    }

    public String getLluisChild1Name() {
        return lluisChild1Name;
    }

    public void setLluisChild1Name(String lluisChild1Name) {
        this.lluisChild1Name = lluisChild1Name;
    }

    public String getLluisChild1Course() {
        return lluisChild1Course;
    }

    public void setLluisChild1Course(String lluisChild1Course) {
        this.lluisChild1Course = lluisChild1Course;
    }

    public String getLluisChild2Name() {
        return lluisChild2Name;
    }

    public void setLluisChild2Name(String lluisChild2Name) {
        this.lluisChild2Name = lluisChild2Name;
    }

    public String getLluisChild2Course() {
        return lluisChild2Course;
    }

    public void setLluisChild2Course(String lluisChild2Course) {
        this.lluisChild2Course = lluisChild2Course;
    }

    public String getPaymentFileUrl() {
        return paymentFileUrl;
    }

    public void setPaymentFileUrl(String paymentFileUrl) {
        this.paymentFileUrl = paymentFileUrl;
    }

    public String getProtectionDataPolicy() {
        return protectionDataPolicy;
    }

    public void setProtectionDataPolicy(String protectionDataPolicy) {
        this.protectionDataPolicy = protectionDataPolicy;
    }

    public String getChildrenImageAuthorization() {
        return childrenImageAuthorization;
    }

    public void setChildrenImageAuthorization(String childrenImageAuthorization) {
        this.childrenImageAuthorization = childrenImageAuthorization;
    }

    public Boolean getPaymentValidated() {
        return isPaymentValidated;
    }

    public void setPaymentValidated(Boolean paymentValidated) {
        isPaymentValidated = paymentValidated;
    }

}
