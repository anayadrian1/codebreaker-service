package edu.cnm.deepdive.codebreaker.controller;

import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.model.entity.Match;
import edu.cnm.deepdive.codebreaker.model.entity.User;
import edu.cnm.deepdive.codebreaker.service.MatchService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //annotation spring uses to identify components that participate in injection
@RequestMapping("/matches") // set root path for everything in this class; so it handles everything that /matches...
public class MatchController {

  private final MatchService matchService;

  @Autowired
  public MatchController(MatchService matchService) {
    this.matchService = matchService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Match post(@RequestBody Match match, Authentication auth) { // when it invokes, look at the request body of the match
    return matchService.startMatch(match, (User) auth.getPrincipal());
  }

  @GetMapping(value = "/{matchId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Match get(@PathVariable UUID matchId) { // match id comes from the placeholder in the path i.e. {match_id}
    return matchService.get(matchId)
        .orElseThrow(NoSuchElementException::new); // if theres nothing with the match_id throw an exception
  }

  @GetMapping(value = "/{matchId}/games", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<Game> getGames(@PathVariable UUID matchId) {
    return matchService.getGames(matchId)
        .orElseThrow(NoSuchElementException::new);
  }

}
