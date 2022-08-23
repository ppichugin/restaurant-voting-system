package kz.pichugin.restaurantvotingsystem.to;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RestaurantTo extends NamedTo {

    List<DishTo> dishTos;

    public RestaurantTo(Integer id, String name, List<DishTo> dishTos) {
        super(id, name);
        this.dishTos = dishTos;
    }
}
