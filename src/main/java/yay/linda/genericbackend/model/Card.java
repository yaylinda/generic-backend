package yay.linda.genericbackend.model;

public class Card {

    private CardType type;
    private int might;
    private int movement;
    private double cost;
    private String owner;
    private String specialAbility; // TODO v2

    public Card() {
    }

    public Card(CardType type, int might, int movement, double cost, String owner, String specialAbility) {
        this.type = type;
        this.might = might;
        this.movement = movement;
        this.cost = cost;
        this.owner = owner;
        this.specialAbility = specialAbility;
    }

    public CardType getType() {
        return type;
    }

    public Card setType(CardType type) {
        this.type = type;
        return this;
    }

    public int getMight() {
        return might;
    }

    public Card setMight(int might) {
        this.might = might;
        return this;
    }

    public int getMovement() {
        return movement;
    }

    public Card setMovement(int movement) {
        this.movement = movement;
        return this;
    }

    public double getCost() {
        return cost;
    }

    public Card setCost(double cost) {
        this.cost = cost;
        return this;
    }

    public String getOwner() {
        return owner;
    }

    public Card setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public String getSpecialAbility() {
        return specialAbility;
    }

    public Card setSpecialAbility(String specialAbility) {
        this.specialAbility = specialAbility;
        return this;
    }

    @Override
    public String toString() {
        return "Card{" +
                "type=" + type +
                ", might=" + might +
                ", movement=" + movement +
                ", cost=" + cost +
                ", owner='" + owner + '\'' +
                ", specialAbility='" + specialAbility + '\'' +
                '}';
    }
}
