package kz.pichugin.restaurantvotingsystem.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "dish", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "add_date", "restaurant_id"},
        name = "uk_dish"))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Dish extends NamedEntity {

    @Column(name = "price", nullable = false)
    @NotNull
    private Double price;

    @Column(name = "add_date", nullable = false, updatable = false, columnDefinition = "date default now()")
    @NotNull
    private LocalDate addDate = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    private Restaurant restaurant;

    public Dish(Dish dish) {
        super(dish.getId(), dish.getName());
        this.price = dish.getPrice();
        this.addDate = dish.getAddDate();
        this.restaurant = dish.getRestaurant();
    }

    public Dish(String name, Double price) {
        super(null, name);
        this.price = price;
    }

    public Dish(Integer id, String name, Double price) {
        super(id, name);
        this.price = price;
    }

    public Dish(Integer id, String name, Double price, Restaurant restaurant, LocalDate addDate) {
        super(id, name);
        this.price = price;
        this.restaurant = restaurant;
        this.addDate = addDate;
    }
}