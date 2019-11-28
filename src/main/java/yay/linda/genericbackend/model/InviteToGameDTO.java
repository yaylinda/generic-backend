package yay.linda.genericbackend.model;

import lombok.Data;

@Data
public class InviteToGameDTO {
    private String player2;
    private Boolean useAdvancedConfigs;
    private GameConfiguration advancedGameConfiguration;
}
