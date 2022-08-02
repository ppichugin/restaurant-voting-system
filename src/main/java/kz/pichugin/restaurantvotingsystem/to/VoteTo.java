package kz.pichugin.restaurantvotingsystem.to;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.time.LocalDate;

@Value
@EqualsAndHashCode
@ToString(callSuper = true)
public class VoteTo {
    LocalDate voteDate;
    int restaurantId;

    public VoteTo(LocalDate voteDate, int restaurantId) {
        this.voteDate = voteDate;
        this.restaurantId = restaurantId;
    }
}
