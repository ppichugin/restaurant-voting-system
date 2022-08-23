package kz.pichugin.restaurantvotingsystem.to;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DishTo extends NamedTo {

    int price;

    public DishTo(Integer id, String name, int price) {
        super(id, name);
        this.price = price;
    }
}