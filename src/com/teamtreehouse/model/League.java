package com.teamtreehouse.model;

import java.util.*;
import java.util.stream.Collectors;

public class League {
    private static final int MAX_PLAYERS = 33;
    private static final int MAX_PLAYERS_PER_TEAM = 11;
    private final List<Team> teams = new ArrayList<>();
    private final Set<Player> allPlayers = new HashSet<>();
    private final Player[] playerDatabase;
    private final Scanner scanner;
    private final Queue<Player> waitingList = new LinkedList<>();


    public League(Player[] playerDatabase){
        this.playerDatabase = playerDatabase;
        scanner = new Scanner(System.in);
        System.out.println("Players loaded into the league.");
    }
//    Start the Menu

    public void start(){
        String option;
        do{
            System.out.println("\nMenu:");
            System.out.println("1. Create - Create a new team.");
            System.out.println("2. Add - Add a player to team.");
            System.out.println("3. Add - Add a player to waiting list.");
            System.out.println("4. Add - Add waiting list players to team.");
            System.out.println("5. Remove - Remove a player from team.");
            System.out.println("6. Report - View report by height.");
            System.out.println("7. League Balance - View the league balance report.");
            System.out.println("8. Roaster - View roaster.");
            System.out.println("9. Auto-Build Teams - Build fair teams automatically.");
            System.out.println("10. Rotate Player - Remove and replace a player from the waiting list.");
            System.out.println("11. Exit - Exit program.");
            System.out.printf("%nSelect an option: ");
            option = scanner.nextLine();

            switch (option) {
                case"1": createTeam(); break;
                case"2": addPlayerToTeam(); break;
                case"3": addToWaitingList(createNewPlayer()); break;
                case"4": addWaitingListPlayersToTeams(); break;
                case"5": removePlayerFromTeam(); break;
                case"6": teamReportByHeight(); break;
                case"7": leagueBalanceReport(); break;
                case"8": viewRoster(); break;
                case"9": buildFairTeams(); break;
                case"10": rotatePlayersInTeams(); break;
                case"11": System.out.println("Exiting ..."); break;
                default: System.out.println("Invalid option. Try again");
            }
        }while(!option.equals("11"));
    }

//    Create a new team
    private void createTeam() {
        // Calculate the maximum number of teams
        int maxTeams = MAX_PLAYERS / MAX_PLAYERS_PER_TEAM;

        // Check if we've reached the maximum number of teams
        if (teams.size() >= maxTeams) {
            System.out.println("Maximum number of teams reached. Cannot create more teams.");
            return;
        }

        // Collect team information if we haven't reached the max number of teams.
        System.out.print("Enter team name: ");
        String teamName = scanner.nextLine();

        System.out.print("Enter coach name: ");
        String coachName = scanner.nextLine();

        // Create new team
        Team newTeam = new Team(teamName, coachName);

        // Add new team to the collection of teams
        teams.add(newTeam);
        System.out.println("Team " + teamName + " coached by " + coachName + " created successfully.");
    }

    // Create new player
    private Player createNewPlayer(){
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter height in inches: ");
        int heightInInches = Integer.parseInt(scanner.nextLine());
        System.out.print("Has previous experience? (yes/no): ");
        boolean experience = scanner.nextLine().equalsIgnoreCase("yes");

        return new Player(firstName, lastName, heightInInches, experience);

    }

//    Add a player to a team
    private void addPlayerToTeam(){
        // Display a list of existing teams and prompt for selection
        Team selectedTeam = selectTeamAlphabetically();
        if(selectedTeam == null) {
            return;
        }
        // Ensure the team can not exceed 11 players
        if(selectedTeam.getTeamPlayers().size() >= 11) {
            System.out.println("This team already has the maximum of 11 players.");
            return;
        }
        //Show only available players and allow selection
        List<Player> availablePlayers = Arrays.stream(playerDatabase)
                .filter(player -> !allPlayers.contains(player))
                .collect(Collectors.toList());

        Player player = displayPlayersAlphabetically(availablePlayers);
        if(player == null) {
            return;
        }

        // Check if the selected team already has this player
        if (selectedTeam.getTeamPlayers().contains(player)) {
            System.out.println(player.getFirstName() + " " + player.getLastName() + " already exists in this team");
            return;
        }

        // Add player to the team
        selectedTeam.addPlayer(player);
        // Add to the global set to track across teams
        allPlayers.add(player);
        System.out.println(player.getFirstName() + " " + player.getLastName() + " added to team " + selectedTeam.getTeamName());
    }

//    Remove a player from a team

    private void removePlayerFromTeam(){
        // Display a list of teams and prompt for selection
        Team selectedTeam = selectTeamAlphabetically();
        if(selectedTeam == null) {
            return;
        }
        // Show players on the selected team and allow selection for removal
        List<Player> teamPlayersList = new ArrayList<>(selectedTeam.getTeamPlayers());
        Player player = displayPlayersAlphabetically(teamPlayersList); // Use players in the selected team
        if(player == null){
            return;
        }
        // Remove the selected player from the team
        if(selectedTeam.removePlayer(player)){
            System.out.println("Player " + player.getFirstName() + " " + player.getLastName() + " removed from team " + selectedTeam.getTeamName());
        } else {
            System.out.println("Player could not be removed.");
        }
    }

    // Method to remove a player from a team and replace them with the next waiting list player
    private void rotatePlayersInTeams(){
        Team selectedTeam = selectTeamAlphabetically();
        if (selectedTeam == null) return;

        // Display players and prompt for removal
        List<Player> players = new ArrayList<>(selectedTeam.getTeamPlayers());
        Player playerToRemove = displayPlayersAlphabetically(players);
        if (playerToRemove == null) return;

        // Remove the selected player from the team
        if (selectedTeam.removePlayer(playerToRemove)) {
            System.out.println("Player " + playerToRemove.getFirstName() + " " + playerToRemove.getLastName() + " removed from team " + selectedTeam.getTeamName());

            // check if there is player on the waiting list to replace the removed player
            if (!waitingList.isEmpty()) {
                Player newPlayer = waitingList.poll();
                selectedTeam.addPlayer(newPlayer);
                System.out.println("Player " + newPlayer.getFirstName() + " " + newPlayer.getLastName() + " added to team " + selectedTeam.getTeamName());
            } else {
                System.out.println("No players available on the waiting list to replace the removed player.");
            }
        } else {
            System.out.println("Failed to remove player from the team.");
        }

    }

//  Team Height Report
    private void teamReportByHeight(){
        // Prompt for team selection
        Team selectedTeam = selectTeamAlphabetically();
        if (selectedTeam == null) {
            return;
        }
        // Create a map to count players by individual height
        Map<Integer, Integer> heightCountMap = new HashMap<>();

        // Count players by height
        for (Player player : selectedTeam.getTeamPlayers()) {
            int height = player.getHeightInInches();
            heightCountMap.put(height, heightCountMap.getOrDefault(height, 0) + 1);
        }

        System.out.println("Height Report for Team: " + selectedTeam.getTeamName());

        // Define height ranges
        int[][] heightRanges = {
                {35, 40},
                {41, 46},
                {47, 52},
                {52, 58},
                {58, 64}
        };

        //  Group players by height range and display the report
        for(int[] range : heightRanges) {
            int minHeight = range[0];
            int maxHeight = range[1];

            //System.out.printf("Height range %d-%d inches:%n", minHeight, maxHeight);

            for (int height = minHeight; height <= maxHeight; height++) {
                if (heightCountMap.containsKey(height)) {
                    System.out.printf("- Height %d inches: %d player(s)%n", height, heightCountMap.get(height));
                }
            }
            selectedTeam.getTeamPlayers().stream()
                    .filter(player -> player.getHeightInInches() >= minHeight && player.getHeightInInches() <= maxHeight)
                    .forEach(player -> System.out.printf("- %s %s (%d inches)%n",
                            player.getFirstName(), player.getLastName(), player.getHeightInInches()));
        }
    }


//  League Balance Report
    private void leagueBalanceReport(){
        // Create a map to store experienced and inexperienced counts for each team
        Map<String, int[]> experienceCountMap = new HashMap<>();

        // Iterate through each team
        for(Team team : teams){
            int experiencedCount = 0;
            int inexperiencedCount = 0;

            // Count experienced and inexperienced players for the current team
            for(Player player : team.getTeamPlayers()) {
                if(player.isPreviousExperience()) {
                    experiencedCount++;
                } else {
                    inexperiencedCount++;
                }
            }

            // Store the counts in the map (index 0 for experienced, index 1 for inexperienced
            experienceCountMap.put(team.getTeamName(), new int[]{experiencedCount, inexperiencedCount});
        }

        //  Display the league balance report with experience percentage
        System.out.println("League Balance Report:");
        for(Map.Entry<String, int[]> entry : experienceCountMap.entrySet()) {
            String teamName = entry.getKey();
            int[] counts = entry.getValue();
            int totalPlayers = counts[0] + counts[1];
            double experiencePercentage = (totalPlayers > 0) ? ((double) counts[0] / totalPlayers) * 100 : 0;

            System.out.printf("Team: %s | Experienced: %d | Inexperienced: %d | Experience Percentage: %.2f%%%n", teamName, counts[0], counts[1], experiencePercentage);
        }
    }

//  Player Roster
    private void viewRoster(){
        //  Prompt for team selection
        Team selectedTeam = selectTeamAlphabetically();
        if(selectedTeam == null) {
            return;
        }

        //  Display the roster for the selected team
        System.out.println("Roster for Team: " + selectedTeam.getTeamName());
        System.out.println("Coach: " + selectedTeam.getTeamCoach());

        if(selectedTeam.getTeamPlayers().isEmpty()) {
            System.out.println("No players on this team");
            return;
        }

        // Sort players alphabetically by last name
        List<Player> sortedPlayers = new ArrayList<>(selectedTeam.getTeamPlayers());
        sortedPlayers.sort(null);

        //  Display each player's stats
        for (Player player : sortedPlayers) {
            String experience = player.isPreviousExperience() ? "experienced" : "inexperienced";
            System.out.printf("Name: %s %s, Height: %d inches, experience: %s%n",
                    player.getFirstName(), player.getLastName(), player.getHeightInInches(), experience);
        }
    }

//  Automatically Build Teams
    private void buildFairTeams() {
        System.out.print("Enter the number of teams to build: ");
        int teamCount = Integer.parseInt(scanner.nextLine());

        if(teamCount <= 0 || playerDatabase.length < teamCount) {
            System.out.println("Insufficient players or invalid team count.");
            return;
        }

        //  Separate players into experienced and inexperienced groups
        List<Player> experiencedPlayers = new ArrayList<>();
        List<Player> inexperiencedPlayers = new ArrayList<>();

        for (Player player : playerDatabase) {
            if (player.isPreviousExperience()) {
                experiencedPlayers.add(player);
            } else {
                inexperiencedPlayers.add(player);
            }
        }

        //  Create empty teams
        teams.clear();
        for (int i = 1; i <= teamCount; i++) {
            teams.add(new Team("Team " + i, "Coach " + i));
        }

        //  Distribute experienced players evenly across teams
        distributePlayersAcrossTeams(experiencedPlayers, teamCount);

        //  Distribute inexperienced players evenly across teams
        distributePlayersAcrossTeams(inexperiencedPlayers, teamCount);

        System.out.println("Teams built automatically with balanced experience");
    }

//  Player Waiting List
    private void addToWaitingList(Player player) {
        waitingList.offer(player);
        System.out.println("Player " + player.getFirstName() + " " + player.getLastName() + " added to the waiting list.");
    }

//  Rotate Players from Waiting List
    private void addWaitingListPlayersToTeams(){
        for (Team team : teams) {
            while(!waitingList.isEmpty() && team.getTeamPlayers().size() < 11) {
                Player player = waitingList.poll(); // retrieve the next player in line
                team.addPlayer(player); // add player to the team
                if (player != null) {
                    System.out.println("Player " + player.getFirstName() + " " + player.getLastName() + " added to team " + team.getTeamName());
                }
            }
        }

        if(!waitingList.isEmpty()) {
            System.out.println("Waiting list players are still waiting due to full teams.");
        } else {
            System.out.println("All players from the waiting list have been added to teams.");
        }
    }

//  Helper method to display teams alphabetically and return the selected team
    private Team selectTeamAlphabetically(){
        if(teams.isEmpty()){
            System.out.println("No teams available. Create a team first.");
            return null;
        }

        // Sort teams alphabetically by name
        List<Team> sortedTeams = new ArrayList<>(teams);
        sortedTeams.sort((team1, team2) -> team1.getTeamName().compareToIgnoreCase(team2.getTeamName()));

        // Display sorted teams and prompt for selection
        System.out.println("Select a team:");
        for(int i = 0; i < sortedTeams.size(); i++) {
            System.out.printf("%d.) %s (Coach: %s)%n", i + 1, sortedTeams.get(i).getTeamName(), sortedTeams.get(i).getTeamCoach());
        }

        System.out.print("Enter the team by number: ");
        int teamIndex = Integer.parseInt(scanner.nextLine()) - 1;

        if (teamIndex < 0 || teamIndex >= sortedTeams.size()) {
            System.out.println("Invalid team selection");
            return null;
        }
        return sortedTeams.get(teamIndex);
    }

//  Helper method to display players alphabetically with stats
    private Player displayPlayersAlphabetically(List<Player> players) {
        if (players.isEmpty()) {
            System.out.println("No players available.");
            return null;
        }

        // Sort players alphabetically by using their compareTo implementation
        List<Player> sortedPlayers = new ArrayList<>(players);
        sortedPlayers.sort(null); // Sorts based on Player's compareTo method

        //System.out.println("Select a player by number: ");
        for(int i = 0; i < sortedPlayers.size(); i++) {
            Player player = sortedPlayers.get(i);
            String experience = player.isPreviousExperience() ? "experienced" : "inexperienced";
            System.out.printf("%d.) %s %s (%d inches - %s)%n",
                    i + 1, player.getFirstName(), player.getLastName(), player.getHeightInInches(), experience);
        }

        System.out.print("Enter the player by number: ");
        int playerIndex = Integer.parseInt(scanner.nextLine()) - 1;

        if(playerIndex < 0 || playerIndex >= sortedPlayers.size()) {
            System.out.println("Invalid player selection.");
            return null;
        }
        return sortedPlayers.get(playerIndex);
    }

//    Helper method to distribute players evenly across teams
    private void distributePlayersAcrossTeams(List<Player> players, int teamCount) {
        int teamIndex = 0;
        for(Player player : players) {
            teams.get(teamIndex).addPlayer(player);
            teamIndex = (teamIndex + 1) % teamCount; // Move to the next team in a round-robin fashion
        }
    }

}
