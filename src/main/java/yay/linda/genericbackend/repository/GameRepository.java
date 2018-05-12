package yay.linda.genericbackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import yay.linda.genericbackend.model.Game;

import java.util.List;

public interface GameRepository extends MongoRepository<Game, String> {

    List<Game> findGamesByPlayer1(String player1);

    List<Game> findGamesByPlayer2(String player2);

    List<Game> findGamesByStatusOrderByCreatedDate(String status);
}
