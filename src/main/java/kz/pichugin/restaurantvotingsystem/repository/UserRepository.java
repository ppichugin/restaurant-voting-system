package kz.pichugin.restaurantvotingsystem.repository;

import kz.pichugin.restaurantvotingsystem.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends BaseRepository<User> {
    Optional<User> getByEmail(String email);
}