package com.springproject.core.Services;

import com.springproject.core.Entity.Token;
import com.springproject.core.Entity.User;
import com.springproject.core.Mapper.UserMapperImpl;
import com.springproject.core.Repository.UserRepository;
import com.springproject.core.dto.UserDto;
import java.security.SecureRandom;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public Long createUser(UserDto userDto, int role) {
    User user = UserMapperImpl.toUser(userDto);

    //dataVerification.login(user.getLogin());
    if (userRepository.getByLogin(user.getLogin()).isPresent()) {
      return null;
      //throw new LoginException("такой логин занят");
    }
//    dataVerification.password(user.getPassword());
//    dataVerification.isValidUsername(user.getUsername());
    SecureRandom random = new SecureRandom();
    String salt = BCrypt.gensalt(4, random);
    user.setPassword(BCrypt.hashpw(user.getPassword(), salt));

    Token token = new Token();
    token.setUser(user);
    user.setToken(token);
    if (role == 1 )
      user.setRole("ADMIN");
    else
      user.setRole("USER");

//    String uuid = Uid.getUuid();
//    Optional<User> optionalUserTemp = userRepository.getByUuid(uuid);
//    while (optionalUserTemp.isPresent()) {
//      uuid = Uid.getUuid();
//      optionalUserTemp = userRepository.getByUuid(uuid);
//    }
//    user.setUuid(uuid);

    return userRepository.save(user).getId();
  }

}
