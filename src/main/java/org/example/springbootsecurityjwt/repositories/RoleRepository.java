package org.example.springbootsecurityjwt.repositories;


import org.example.springbootsecurityjwt.models.ERole;
import org.example.springbootsecurityjwt.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository <Role, Long> {
    Optional<Role> findByName(ERole name);
}
