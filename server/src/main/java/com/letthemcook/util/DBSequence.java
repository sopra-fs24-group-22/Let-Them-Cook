package com.letthemcook.util;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("DBSequence")
public class DBSequence {
  @Id
  private String id;
  private int seq;

  public int getSeq() {
    return seq;
  }
}
