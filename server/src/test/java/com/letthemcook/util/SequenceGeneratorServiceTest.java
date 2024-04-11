package com.letthemcook.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class SequenceGeneratorServiceTest {

  @Mock
  private MongoOperations mongoOperations;

  @InjectMocks
  private SequenceGeneratorService sequenceGeneratorService;

  // ######################################### Setup & Teardown #########################################

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }

  // ######################################### Sequence Number Tests #########################################

  @Test
  public void testReturnSequenceNumberWhenSequenceExists() {
    DBSequence dbSequence = new DBSequence();
    dbSequence.setId("test");
    dbSequence.setSeq(1);

    Query query = new Query(Criteria.where("id").is("test"));
    Update update = new Update().inc("seq", 1);
    FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(true);

    when(mongoOperations.findAndModify(query, update, options, DBSequence.class))
            .thenReturn(dbSequence);

    long sequenceNumber = sequenceGeneratorService.getSequenceNumber("test");

    assertEquals(1, sequenceNumber);
  }

  @Test
  public void testReturn1WhenSequenceDoesNotExist() {
    Query query = new Query(Criteria.where("id").is("test"));
    Update update = new Update().inc("seq", 1);
    FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(true);

    when(mongoOperations.findAndModify(query, update, options, DBSequence.class))
            .thenReturn(null);

    long sequenceNumber = sequenceGeneratorService.getSequenceNumber("test");

    assertEquals(1, sequenceNumber);
  }
}