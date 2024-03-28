package com.letthemcook.user;

import com.letthemcook.util.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UserService {

  private final UserRepository userRepository;

  @Autowired
  private SequenceGeneratorService sequenceGeneratorService;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    newUser.setId(sequenceGeneratorService.getSequenceNumber(User.SEQUENCE_NAME));
    userRepository.save(newUser);
    return newUser;
  }

  public void loginUser(User checkUser) {
    User user = userRepository.findByEmail(checkUser.getEmail());
    //TODO: Returns 404 instead of 401 UNAUTHORIZED
    if(user == null){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The email is incorrect.");
    }
    if(!Objects.equals(user.getPassword(), checkUser.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The password is incorrect.");
    }
  }
}
