package edu.bu.repository;

import edu.bu.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}