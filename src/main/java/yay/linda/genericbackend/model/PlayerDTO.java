package yay.linda.genericbackend.model;

import lombok.Data;

import java.time.Instant;
import java.util.Date;

import static yay.linda.genericbackend.model.Constants.SIMPLE_DATE_FORMAT;

@Data
public class PlayerDTO implements Comparable<PlayerDTO> {

    private String username;
    private String lastActiveDate;
    private String createdDate;
    private String lastActivity;
    private Integer numWins;
    private Integer numGames;
    private Boolean canAdd;
    private String currentTimestamp;

    private PlayerDTO() {

    }

    public static PlayerDTO fromUser(User user, Boolean canAdd) {
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.username = user.getUsername();
        playerDTO.lastActiveDate = SIMPLE_DATE_FORMAT.format(user.getLastActiveDate());
        playerDTO.createdDate = SIMPLE_DATE_FORMAT.format(user.getCreatedDate());
        playerDTO.lastActivity = user.getLastActivity();
        playerDTO.numWins = user.getNumWins();
        playerDTO.numGames = user.getNumGames();
        playerDTO.canAdd = canAdd;
        playerDTO.currentTimestamp = SIMPLE_DATE_FORMAT.format(Date.from(Instant.now()));
        return playerDTO;
    }

    @Override
    public int compareTo(PlayerDTO o) {
        return this.getLastActiveDate().compareTo(o.getLastActiveDate()) * -1;
    }
}
