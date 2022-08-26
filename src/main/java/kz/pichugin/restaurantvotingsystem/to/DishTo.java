package kz.pichugin.restaurantvotingsystem.to;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import javax.validation.constraints.PositiveOrZero;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DishTo extends NamedTo {

    @PositiveOrZero
    int price;

    public DishTo(Integer id, String name, int price) {
        super(id, name);
        this.price = price;
    }
}