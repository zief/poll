package zief.romi.poll.security;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import zief.romi.poll.model.User;
import zief.romi.poll.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  @Autowired 
  UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
    User user = 
        userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        "User not found with username or email : " + usernameOrEmail));

    return UserPrincipal.create(user);
  }
  
  @Transactional
  public UserDetails loadUserById(Long id) {
      User user = userRepository.findById(id).orElseThrow(
          () -> new UsernameNotFoundException("User not found with id : " + id)
      );

      return UserPrincipal.create(user);
  }
}
