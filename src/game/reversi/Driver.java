package game.reversi;

import game.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.regex.Pattern;

public class Driver {

    private static final Pattern COORD_PATTERN = Pattern.compile("\\((d+),(d+)\\)");

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        Player white = new ReversiPlayer("white", 'W');
        Player black = new ReversiPlayer("black", 'B');
        ReversiState state = new ReversiState(white, black);

        Set<ReversiAction> availableActions = state.getAvailableActions();

        while (!availableActions.isEmpty()) {
            System.out.println(state);
            System.out.println("Enter move as (x,y) coordinate: ");
            String nextMove = in.readLine().trim();

            boolean found = false;
            for(ReversiAction action: availableActions) {
                System.out.println(action.getLabel());
                if (action.getLabel().equals(nextMove)) {
                    state = action.getResultingState();
                    availableActions = state.getAvailableActions();
                    found = true;
                }
            }
            if (!found) throw new IllegalArgumentException("WRONG!");
        }



    }
}
