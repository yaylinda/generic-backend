package yay.linda.genericbackend.model;

public class Move {

    private String placeholder;

    public Move(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public Move setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }
}
