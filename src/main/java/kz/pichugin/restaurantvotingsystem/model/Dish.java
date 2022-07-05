package kz.pichugin.restaurantvotingsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
@Table(name = "dish", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "add_date", "restaurant_id"},
        name = "dish_unique_restaurant_name_idx")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Dish extends NamedEntity {

    @Column(name = "price", nullable = false)
    @NotNull
    private Double price;

    @Column(name = "add_date", nullable = false, updatable = false, columnDefinition = "timestamp default now()")
    @NotNull
    private LocalDate added = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    @JsonIgnore
    Restaurant restaurant;

    public Dish(String name, Double price) {
        super(null, name);
        this.price = price;
    }

    public Dish(Integer id, String name, Double price, Restaurant restaurant, LocalDate added) {
        super(id, name);
        this.price = price;
        this.restaurant = restaurant;
        this.added = added;
    }
}