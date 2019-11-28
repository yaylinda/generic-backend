package yay.linda.genericbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yay.linda.genericbackend.model.GameConfiguration;
import yay.linda.genericbackend.model.Card;
import yay.linda.genericbackend.model.CardType;
import yay.linda.genericbackend.model.Cell;
import yay.linda.genericbackend.model.Game;
import yay.linda.genericbackend.model.GameDTO;
import yay.linda.genericbackend.model.GameStatus;
import yay.linda.genericbackend.model.GameboardCoordinate;
import yay.linda.genericbackend.model.PutCardRequest;
import yay.linda.genericbackend.model.PutCardResponse;
import yay.linda.genericbackend.repository.GameRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static yay.linda.genericbackend.util.Utilities.randomStringGenerator;

@Service
public class GameAIPlayer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameAIPlayer.class);

    @Autowired
    private GameService gameService;

    @Autowired
    private GameRepository gameRepository;

    public GameDTO nextMove(String gameId, String realUsername, String sessionToken) {

        Game game = gameService.getGameById(gameId);

        String aiUsername;

        // join game, if game is not in progress
        if (game.getStatus() == GameStatus.WAITING_PLAYER_2) {
            aiUsername = "SimpleWarAI_" + randomStringGenerator(6);

            LOGGER.info("gameId={} is WAITING_PLAYER_2, {} is joining...", gameId, aiUsername);

            game.addPlayer2ToGame(aiUsername);

            gameRepository.save(game);
            LOGGER.info("Updated gameId={} with player2={}", gameId, aiUsername);
        } else {
            aiUsername = game.getPlayer2();
        }

        LOGGER.info("{} is player2 (AI) in gameId={}", aiUsername);

        // do put card while we have energy
        while(canAffordPutCard(game.getEnergyMap().get(aiUsername), game.getCardsMap().get(aiUsername))
                && canPutInEmptySpace(game, aiUsername)) {

            PutCardRequest putCardRequest = calculatePutCardRequest(game, aiUsername, realUsername);
            LOGGER.info("Calculated putCardRequest: {}, for {}", putCardRequest, aiUsername);

            PutCardResponse putCardResponse = gameService.putCardHelper(game, aiUsername, realUsername, false, putCardRequest);
            LOGGER.info("{} successfully PUT card", aiUsername);

            game = gameService.getGameById(putCardResponse.getGame().getId());
        }

        // do end turn
        gameService.endTurnHelper(game, aiUsername, true);
        LOGGER.info("{} successfully ended turn", aiUsername);

        // return gameDTO for real player
        return gameService.getGameById(sessionToken, gameId);
    }

    /**
     *
     * @param game
     * @param username
     * @param opponent
     * @return
     */
    private PutCardRequest calculatePutCardRequest(Game game, String username, String opponent) {

        double energyRemaining = game.getEnergyMap().get(username);

        LOGGER.info("Calculating PutCardRequest for {} in gameId={}, with energyRemaining={}",
                username, game.getId(), energyRemaining);

        // calculate threat of opponent troop cards per column
        Map<Integer, Double> columnIdToOpponentTroopPointsMap = normalizeMap(
                accumulateColumnMight(game.getBoardMap().get(username), CardType.TROOP, opponent), false);

        // calculate threat of opponent wall cards per column
        Map<Integer, Double> columnIdToOpponentWallPointsMap = normalizeMap(
                accumulateColumnMight(game.getBoardMap().get(username), CardType.WALL, opponent), true);

        // combine threat scores per columns
        Map<Integer, Double> combined = new HashMap<>();
        columnIdToOpponentTroopPointsMap.keySet()
                .forEach(k -> combined.put(k, columnIdToOpponentTroopPointsMap.get(k) + columnIdToOpponentWallPointsMap.get(k)));

        LOGGER.info("Normalized TROOP+WALL opponent column threat map: {}", combined);

        // get all valid gameboard coordinates to place cards
        List<GameboardCoordinate> possibleCoordinates = new ArrayList<>();

        for (int row = game.getGameConfig().getMinTerritoryRow(); row < game.getGameConfig().getNumRows(); row++) {
            for (int col = 0; col < game.getGameConfig().getNumCols(); col++) {
                if (game.getBoardMap().get(username).get(row).get(col).getCards().isEmpty()) { // TODO - handle advanced configs
                    possibleCoordinates.add(new GameboardCoordinate(row, col, 0.0, 0.0, 0.0));
                }
            }
        }

        // set scores for placing troop and wall in valid gameboard coordinates
        for (GameboardCoordinate gameboardCoordinate : possibleCoordinates) {
            gameboardCoordinate.setPlaceTroopThreat(columnIdToOpponentTroopPointsMap.get(gameboardCoordinate.getCol()));
            gameboardCoordinate.setPlaceWallThreat(columnIdToOpponentWallPointsMap.get(gameboardCoordinate.getCol()));
            gameboardCoordinate.setCombinedThreat(combined.get(gameboardCoordinate.getCol()));
        }

        LOGGER.info("Possible coordinates and threats: {}", possibleCoordinates);

        // pick which cards are able to be placed
        List<Integer> troopIndices = calculateCardIndexToPlace(game.getCardsMap().get(username), CardType.TROOP, energyRemaining);
        List<Integer> wallIndices = calculateCardIndexToPlace(game.getCardsMap().get(username), CardType.WALL, energyRemaining);
        List<Integer> leftoverIndices = calculateCardIndexToPlace(game.getCardsMap().get(username), CardType.DEFENSE, energyRemaining);

        // synthesize info for PutCardRequest

        PutCardRequest putCardRequest;

        if (!troopIndices.isEmpty()) {

            possibleCoordinates.sort(
                    Comparator.comparing(GameboardCoordinate::getPlaceTroopThreat)
                            .reversed()
                            .thenComparing(GameboardCoordinate::getRow));

            putCardRequest = new PutCardRequest(
                    possibleCoordinates.get(0).getRow(),
                    possibleCoordinates.get(0).getCol(),
                    troopIndices.get(0),
                    game.getCardsMap().get(username).get(troopIndices.get(0)));

        } else if (!wallIndices.isEmpty()) {

            possibleCoordinates.sort(Comparator
                    .comparing(GameboardCoordinate::getPlaceWallThreat)
                    .reversed()
                    .thenComparing(GameboardCoordinate::getRow));

            putCardRequest = new PutCardRequest(
                    possibleCoordinates.get(0).getRow(),
                    possibleCoordinates.get(0).getCol(),
                    wallIndices.get(0),
                    game.getCardsMap().get(username).get(wallIndices.get(0)));

        } else {

            possibleCoordinates.sort(
                    Comparator.comparing(GameboardCoordinate::getCombinedThreat)
                            .reversed()
                            .thenComparing(GameboardCoordinate::getRow));

            putCardRequest = new PutCardRequest(
                    possibleCoordinates.get(0).getRow(),
                    possibleCoordinates.get(0).getCol(),
                    leftoverIndices.get(0),
                    game.getCardsMap().get(username).get(leftoverIndices.get(0)));
        }

        return putCardRequest;
    }

    private List<Integer> calculateCardIndexToPlace(List<Card> cards, CardType cardType, double energyRemaining) {
        List<Integer> cardIndices = new ArrayList<>();

        for(int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getCost() <= energyRemaining && cards.get(i).getType() == cardType) {
                cardIndices.add(i);
            }
        }

        Map<Integer, Integer> indexToMight = new HashMap<>();
        cardIndices.forEach(i -> indexToMight.put(i, cards.get(i).getMight()));

        LOGGER.info("Affordable {} cardIndex:might : {}", cardType, indexToMight);

        return new ArrayList<>(indexToMight.entrySet()
                .stream()
                .sorted((Map.Entry.<Integer, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
                .keySet());
        }

    /**
     *
     * @param cardOwner
     * @param cardType
     * @return
     */
    private Map<Integer, Integer> accumulateColumnMight(List<List<Cell>> gameboard, CardType cardType, String cardOwner) {

        Map<Integer, Integer> columnIdToMightMap = new HashMap<>();

        int numRows = gameboard.size();
        int numCols = gameboard.get(0).size();

        for (int columnId = 0; columnId < numCols; columnId++) {

            int columnSum = 0;
            for (List<Cell> row : gameboard) {
                for (Card card : row.get(columnId).getCards()) {
                    if (card.getOwner().equalsIgnoreCase(cardOwner) && card.getType() == cardType) {
                        columnSum += card.getMight();
                    }
                }
            }

            LOGGER.info("Cumulative {} OPPONENT might={} for column={}", cardType, columnSum, columnId);

            if (!columnIdToMightMap.containsKey(columnId)) {
                columnIdToMightMap.put(columnId, 0);
            }

            columnIdToMightMap.put(columnId, columnIdToMightMap.get(columnId) + columnSum);
        }

        return columnIdToMightMap;
    }

    /**
     *
     * @param input
     * @param inverseWeight
     * @return
     */
    private Map<Integer, Double> normalizeMap(Map<Integer, Integer> input, boolean inverseWeight) {
        Map<Integer, Double> normalized = new HashMap<>();

        Integer valueSum = input.values().stream().reduce(0, Integer::sum);

        input.keySet().forEach(k -> {
            double val = input.get(k) / (valueSum * 1.0);
            if (inverseWeight) {
                val = 1.0 - val;
            }
            normalized.put(k, val);
        });

        LOGGER.info("Normalized column threats (inverseWeight={}): {}", inverseWeight, input);
        return normalized;
    }

    private boolean canAffordPutCard(double energyRemaining, List<Card> cards) {
        for (Card card : cards) {
            if (card.getCost() <= energyRemaining) {
                LOGGER.info("canAffordPutCard: true");
                return true;
            }
        }

        LOGGER.info("canAffordPutCard: true");
        return false;
    }

    private boolean canPutInEmptySpace(Game game, String username) {
        for (int row = game.getGameConfig().getMinTerritoryRow(); row < game.getGameConfig().getNumRows(); row++) {
            for (int col = 0; col < game.getGameConfig().getNumCols(); col++) {
                if (game.getBoardMap().get(username).get(row).get(col).getCards().size() < game.getGameConfig().getMaxCardsPerCell()) {
                    LOGGER.info("canPutInEmptySpace: true");
                    return true;
                }
            }
        }

        LOGGER.info("canPutInEmptySpace: false");
        return false;
    }
}
