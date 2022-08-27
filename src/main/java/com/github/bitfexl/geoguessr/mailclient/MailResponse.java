package com.github.bitfexl.geoguessr.mailclient;

public class MailResponse {
    private boolean read;
    private boolean expanded;
    private boolean forwarded;
    private boolean repliedTo;
    private String sentDate;
    private String sentDateFormatted;
    private String sender;
    private String from;
    private String subject;
    private String bodyPlainText;
    private String bodyHtmlContent;
    private String bodyPreview;
    private String id;

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isForwarded() {
        return forwarded;
    }

    public void setForwarded(boolean forwarded) {
        this.forwarded = forwarded;
    }

    public boolean isRepliedTo() {
        return repliedTo;
    }

    public void setRepliedTo(boolean repliedTo) {
        this.repliedTo = repliedTo;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public String getSentDateFormatted() {
        return sentDateFormatted;
    }

    public void setSentDateFormatted(String sentDateFormatted) {
        this.sentDateFormatted = sentDateFormatted;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBodyPlainText() {
        return bodyPlainText;
    }

    public void setBodyPlainText(String bodyPlainText) {
        this.bodyPlainText = bodyPlainText;
    }

    public String getBodyHtmlContent() {
        return bodyHtmlContent;
    }

    public void setBodyHtmlContent(String bodyHtmlContent) {
        this.bodyHtmlContent = bodyHtmlContent;
    }

    public String getBodyPreview() {
        return bodyPreview;
    }

    public void setBodyPreview(String bodyPreview) {
        this.bodyPreview = bodyPreview;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
