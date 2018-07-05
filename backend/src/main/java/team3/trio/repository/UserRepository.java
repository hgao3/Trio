package team3.trio.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team3.trio.model.User;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByLastName(@Param("lastName") String lastName);

    List<User> findByFirstName(@Param("firstName") String firstName);
    
    List<User> findByEmail(@Param("email") String email);
}
