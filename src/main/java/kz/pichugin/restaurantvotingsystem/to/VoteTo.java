package kz.pichugin.restaurantvotingsystem.to;

import kz.pichugin.restaurantvotingsystem.HasId;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VoteTo extends BaseTo implements HasId {
    @NotNull
    LocalDate voteDate;

    int restaurantId;

    public VoteTo(Integer id, LocalDate voteDate, int restaurantId) {
        super(id);
        this.voteDate = voteDate;
        this.restaurantId = restaurantId;
    }
}
