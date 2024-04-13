package com.letthemcook.session;


import com.letthemcook.rest.mapper.DTOSessionMapper;
import com.letthemcook.session.dto.SessionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.*;

@RestController
public class SessionController {


  private final SessionService sessionService;

  @Autowired
  public SessionController(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @GetMapping("/api/session")
  @ResponseStatus(OK)
  @ResponseBody
  public Session getSession(SessionDTO sessionDTO) {
    Session session = DTOSessionMapper.INSTANCE.convertSessionDTOToEntity(sessionDTO);
    return sessionService.getSession(session.getId());
  }

}
