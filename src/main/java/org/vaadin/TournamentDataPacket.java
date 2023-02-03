package org.vaadin;

import java.util.ArrayList;

public class TournamentDataPacket extends StoryDataPacket{
    /*
    1.ask player to join
        1.1 Drawer gets asked first
        1.2 If only 1 player enters, is awarded 1 shield + extra
    2.All tournament players draw adventure card
    3.Pick cards they want to play
        3.1 No doubles and only 1 amour
        3.2 Can play zero cards (Only count rank)
    4.Highest power player (Not counting allies already in play) wins -> potential tie
    5.Winner receives #shields = #numPlayers who entered tourney + extra

    IF TIE OCCURS:
    1. Discard weapon cards in play
    2. Pick cards they want to play
    3. Repeat step 4
        3.1 If tie again, all players in second round receive #shields = #numPlayers
    */

    int tourneyPhase = -1;
    int activePlayerID = -1;

}
