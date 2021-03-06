package edu.cnm.deepdive.codebreaker.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(
    name = "tournament",
    indexes = {
        @Index(columnList = "codeLength"),
        @Index(columnList = "started,deadline")
    }
)
@JsonIgnoreProperties(
    value = {"id", "started", "originator", "winner", "players", "games"},
    allowGetters = true, ignoreUnknown = true
)
@Component
@JsonInclude(Include.NON_NULL)
public class Match {

  private static EntityLinks entityLinks;

  @NonNull
  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(name = "match_id", nullable = false, updatable = false,
      columnDefinition = "CHAR(16) FOR BIT DATA")
  private UUID id;

  @NonNull
  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date started;

  @Column(updatable = false)
  private int codeLength;

  @NonNull //nullable in code
  @Column(nullable = false) //nullable in the database
  private String pool;

  @Transient // if youre writing an object of this type, dont bother writing the value of this field
  private int gameCount; //room doesn't translate name

  @Column(nullable = false, updatable = false)
  private Criterion criterion;

  @NonNull
  @Column(nullable = false, updatable = false)
  private Date deadline;

  @NonNull
  @ManyToOne(fetch = FetchType.EAGER, optional = false) // @Foreign key to reference keys that are not the primary key
  @JoinColumn(name = "originator_id", nullable = false, updatable = false) // join foreign
  private User originator;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "winner_id")
  private User winner;

  @NonNull
  @ManyToMany(fetch = FetchType.EAGER,
      cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinTable(joinColumns = {@JoinColumn(name = "match_id")},
      inverseJoinColumns = {@JoinColumn(name = "user_id")}) // join column refers to match, inverse refers to joining
  @OrderBy("displayName ASC")
  private final List<User> players = new LinkedList<>();

  @NonNull
  @OneToMany(mappedBy = "match", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true) // if we delete match, delete all games associated; never do on Many to Many
  @JsonIgnore
  private final List<Game> games = new LinkedList<>(); // when looking at one to many or many2many and you have an empty list as an initial value, you only need a getter

  @NonNull
  public UUID getId() {
    return id;
  }

  @NonNull
  public Date getStarted() {
    return started;
  }

  public int getCodeLength() {
    return codeLength;
  }

  public void setCodeLength(int codeLength) {
    this.codeLength = codeLength;
  }

  @NonNull
  public String getPool() {
    return pool;
  }

  public void setPool(@NonNull String pool) {
    this.pool = pool;
  }

  public int getGameCount() {
    return gameCount;
  }

  public void setGameCount(int gameCount) {
    this.gameCount = gameCount;
  }

  public Criterion getCriterion() {
    return criterion;
  }

  public void setCriterion(Criterion criterion) {
    this.criterion = criterion;
  }

  @NonNull
  public Date getDeadline() {
    return deadline;
  }

  public void setDeadline(@NonNull Date deadline) {
    this.deadline = deadline;
  }

  @NonNull
  public User getOriginator() {
    return originator;
  }

  public void setOriginator(@NonNull User originator) {
    this.originator = originator;
  }

  public User getWinner() {
    return winner;
  }

  public void setWinner(User winner) {
    this.winner = winner;
  }

  @NonNull
  public List<User> getPlayers() {
    return players;
  }

  @NonNull
  public List<Game> getGames() {
    return games;
  }

  public URI getHref() {
    return (id != null) ? entityLinks.linkForItemResource(Match.class, id).toUri() : null;
  }

  @PostConstruct
  private void initHateoas() {
    //noinspection ResultOfMethodCallIgnored
    entityLinks.toString();
  }

  @Autowired

  public void setEntityLinks(
      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") EntityLinks entityLinks) {
    Match.entityLinks = entityLinks;
  }

  public enum Criterion {
    GUESSES_TIME, TIME_GUESSES
  }


}
