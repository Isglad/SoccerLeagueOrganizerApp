import com.teamtreehouse.model.Player;
import com.teamtreehouse.model.Players;
import com.teamtreehouse.model.League;

public class LeagueManager {

  public static void main(String[] args) {
    Player[] players = Players.load();
    System.out.printf("%nThere are currently %d registered players.%n", players.length);
    League league = new League(players);
    league.start();
    // Your code here!
  }

}
