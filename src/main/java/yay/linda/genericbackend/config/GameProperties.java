package yay.linda.genericbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("application.game")
public class GameProperties {
    private int numRows;
    private int numCols;
    private int numCardsInHand;
    private int maxPoints;
    private int numTerritoryRows;
}
