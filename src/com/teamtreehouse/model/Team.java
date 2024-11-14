package com.teamtreehouse.model;

import java.util.*;

public class Team {
    private final String mTeamName;
    private final String mTeamCoach;
    //private final List<Player> mPlayers;
    private final Set<Player> mTeamPlayers; //set collection avoids duplicates

//  Constructor to initialize team name and coach

    public Team(String teamName, String teamCoach) {
        mTeamName = teamName;
        mTeamCoach = teamCoach;
        //  mPlayers = new ArrayList<Player>();
        mTeamPlayers = new HashSet<>();
    }

//  Getter Methods

    public String getTeamName() {
        return mTeamName;
    }

    public String getTeamCoach() {
        return mTeamCoach;
    }

    public Set<Player> getTeamPlayers() {
        return mTeamPlayers;
    }

//  Add player
    public void addPlayer(Player player) {
        // Check if player is already in the team
        if (mTeamPlayers.contains(player)) {
            return;
        }
        // Add player
        mTeamPlayers.add(player);
    }

//  Remove player
    public boolean removePlayer(Player player) {
        if (mTeamPlayers.contains(player)) {
            mTeamPlayers.remove(player);
            return true;
        }
        return false;
    }


//  Display Team
//    public void displayTeam() {
//        System.out.println("Team: " + mTeamName);
//        for (Player player : mTeamPlayers) {
//            System.out.printf("First Name: %s, Last Name: %s, Height: %d, Experience: %b%n",
//                    player.getFirstName(), player.getLastName(), player.getHeightInInches(), player.isPreviousExperience());
//        }
//    }

//  Sort and display players by height
//    public void displayPlayersByHeight() {
//        mTeamPlayers.stream()
//                .sorted((p1, p2) -> Double.compare(p1.getHeightInInches(), p2.getHeightInInches()))
//                .forEach(player -> System.out.printf("First Name: %s, Last Name: %s, Height: %d",
//                        player.getFirstName(), player.getLastName(), player.getHeightInInches()));
//    }
}
