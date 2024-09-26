package com.lostmc.hungergames.event.game;

import com.lostmc.game.event.GameEvent;
import com.lostmc.hungergames.stage.GameStage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GameStageChangeEvent extends GameEvent {

    private GameStage lastStage;
    private GameStage newStage;
}
