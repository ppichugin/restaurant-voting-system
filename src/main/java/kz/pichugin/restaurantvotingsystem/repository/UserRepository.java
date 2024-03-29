package kz.pichugin.restaurantvotingsystem.repository;

import kz.pichugin.restaurantvotingsystem.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends BaseRepository<User> {

    @Query("SELECT u FROM User u WHERE u.email =:email")
    Optional<User> getByEmail(String email);
}