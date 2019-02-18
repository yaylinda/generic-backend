package yay.linda.genericbackend.model;

import lombok.Data;

import static yay.linda.genericbackend.model.Constants.SIMPLE_DATE_FORMAT;

@Data
public class PlayerDTO {

    private String username;
    private String lastActiveDate;
    private String lastActivity;
    private Integer numWins;
    private Integer numPlayed;

    private PlayerDTO() {

    }

    public static PlayerDTO fromUser(User user) {
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.username = user.getUsername();
        playerDTO.lastActiveDate = SIMPLE_DATE_FORMAT.format(user.getLastActiveDate());
        playerDTO.lastActivity = user.getLastActivity();
        playerDTO.numWins = user.getNumWins();
        playerDTO.numPlayed = user.getNumPlayed();
        return playerDTO;
    }
}
