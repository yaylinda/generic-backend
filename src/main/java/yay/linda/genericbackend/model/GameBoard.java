package yay.linda.genericbackend.model;

public class GameBoard {

    public String todo = "TODO";

    public GameBoard() {
    }

    public GameBoard transpose() {
        // TODO - reverse gameboard pieces
        return new GameBoard();
    }

    public String getTodo() {
        return todo;
    }

    public GameBoard setTodo(String todo) {
        this.todo = todo;
        return this;
    }
}
