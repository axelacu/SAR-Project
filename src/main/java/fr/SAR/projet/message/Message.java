package fr.SAR.projet.message;

public class Message extends ToSend {

    private String message;

    public Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
