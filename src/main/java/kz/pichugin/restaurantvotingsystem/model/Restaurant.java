package kz.pichugin.restaurantvotingsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Set;

@Entity
@Table(name = "restaurant", uniqueConstraints = @UniqueConstraint(columnNames = {"name"},
        name = "restaurant_unique_name_idx"))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Restaurant extends NamedEntity {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "restaurant")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OrderBy("added DESC")
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Set<Dish> menu;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "selectedRestaurant")
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Set<Vote> votes;

    public Restaurant(Integer id, String name) {
        super(id, name);
    }
}