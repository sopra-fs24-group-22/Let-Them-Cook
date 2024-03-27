package com.letthemcook.user;

import com.letthemcook.util.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.midi.Sequence;
import java.util.List;

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
}
