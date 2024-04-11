package com.letthemcook.util;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("DBSequence")
public class DBSequence {
  @Id
  private String id;
  private int seq;

  public void setId(String id) {
    this.id = id;
  }

  public void setSeq(int seq) {
    this.seq = seq;
  }

  public int getSeq() {
    return seq;
  }
}
