package com.letthemcook.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;

@Document("users")
public class User implements Serializable {

  @Transient
  public static final String SEQUENCE_NAME = "users_sequence";

  @Id
  private Long id;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

}
