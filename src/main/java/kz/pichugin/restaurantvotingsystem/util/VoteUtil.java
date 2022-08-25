package kz.pichugin.restaurantvotingsystem.util;

import kz.pichugin.restaurantvotingsystem.model.Vote;
import kz.pichugin.restaurantvotingsystem.to.VoteTo;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class VoteUtil {
    @NotNull
    @Contract("_ -> new")
    public static VoteTo createVoteTo(@NotNull Vote vote) {
        return new VoteTo(vote.getVoteDate(), vote.getSelectedRestaurant().id());
    }

    public static List<VoteTo> getVoteTos(@NotNull Collection<Vote> votes) {
        return votes.stream()
                .map(VoteUtil::createVoteTo)
                .collect(Collectors.toList());
    }
}
