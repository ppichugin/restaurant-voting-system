package kz.pichugin.restaurantvotingsystem.util;

import kz.pichugin.restaurantvotingsystem.model.Vote;
import kz.pichugin.restaurantvotingsystem.to.VoteTo;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@UtilityClass
public class VoteUtil {
    public static VoteTo getVoteTo(Vote vote) {
        return new VoteTo(vote.getVoteDate(), vote.getSelectedRestaurant().id());
    }

    public static List<VoteTo> getVoteTos(Collection<Vote> votes) {
        List<VoteTo> voteTos = new ArrayList<>();
        for (Vote vote : votes) {
            voteTos.add(new VoteTo(vote.getVoteDate(), vote.getSelectedRestaurant().id()));
        }
        voteTos.sort(Comparator.comparing(VoteTo::getVoteDate).reversed());
        return voteTos;
    }
}
