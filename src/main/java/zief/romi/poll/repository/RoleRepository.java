package zief.romi.poll.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zief.romi.poll.model.Role;
import zief.romi.poll.model.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

  Optional<Role> findByName(RoleName roleName);


	
}
