package yay.linda.genericbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("application.game")
public class GameProperties {

    private int numRows;
    private int numCols;
    private int numCardsInHand;

    public int getNumRows() {
        return numRows;
    }

    public GameProperties setNumRows(int numRows) {
        this.numRows = numRows;
        return this;
    }

    public int getNumCols() {
        return numCols;
    }

    public GameProperties setNumCols(int numCols) {
        this.numCols = numCols;
        return this;
    }

    public int getNumCardsInHand() {
        return numCardsInHand;
    }

    public GameProperties setNumCardsInHand(int numCardsInHand) {
        this.numCardsInHand = numCardsInHand;
        return this;
    }
}
