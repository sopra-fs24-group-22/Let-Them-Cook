package com.letthemcook.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SequenceGeneratorService {
  @Autowired
  private MongoOperations mongoOperations;

  public long getSequenceNumber(String sequenceName){
    // Get and update sequence no
    Query query = new Query(Criteria.where("id").is(sequenceName));
    Update update = new Update().inc("seq", 1);
    // Modify MongoDB document
    DBSequence counter = mongoOperations
            .findAndModify(query,
                    update, FindAndModifyOptions.options().returnNew(true).upsert(true),
                    DBSequence.class);

    return !Objects.isNull(counter) ? counter.getSeq() : 1;
  }
}
