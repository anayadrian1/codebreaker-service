package edu.cnm.deepdive.codebreaker.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.net.URI;
import java.net.URL;
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
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(
    name = "user_profile",
    indexes = {
        @Index(columnList = "created"),
        @Index(columnList = "connected")
    }
)
@JsonIgnoreProperties(
    value = {"id", "created", "connected", "oauthKey", "matches", "matchesWon", "matchedOriginated"},
    allowGetters = true, ignoreUnknown = true)
@JsonPropertyOrder({"id", "href", "displayName", "created", "connected"})
@Component // participates in dependency injection
public class User {

  private static EntityLinks entityLinks;

  @NonNull
  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(name = "user_id", nullable = false, updatable = false,
      columnDefinition = "CHAR(16) FOR BIT DATA")
  private UUID id;

  @NonNull
  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date created;

  @JsonIgnore
  @NonNull
  @Column(nullable = false, updatable = false, unique = true)
  private String oauthKey;

  @NonNull
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date connected;

  @NonNull
  @Column(nullable = false, unique = true)
  private String displayName;

  @JsonIgnore
  @NonNull
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "originator", cascade = CascadeType.ALL, orphanRemoval = true) //same as field; orphan is match taken out of list
  @OrderBy("started DESC")
  private final List<Match> matchesOriginated = new LinkedList<>(); // originator_id

  @JsonIgnore
  @NonNull
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "winner", cascade =
      {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @OrderBy("deadline DESC")
  private final List<Match> matchesWon = new LinkedList<>();

  @JsonIgnore
  @NonNull
  @ManyToMany(mappedBy = "players", fetch = FetchType.LAZY) // users can play a lot of games and dont want them all every time
  @OrderBy("deadline DESC")
  private final List<Match> matches = new LinkedList<>();

  // if our code is assigning a value to a field, we need a setter
  @NonNull
  public UUID getId() {
    return id;
  }

  @NonNull
  public Date getCreated() {
    return created;
  }

  @NonNull
  public String getOauthKey() {
    return oauthKey;
  }

  public void setOauthKey(@NonNull String oauthKey) {
    this.oauthKey = oauthKey;
  }

  @NonNull
  public Date getConnected() {
    return connected;
  }

  public void setConnected(@NonNull Date connected) {
    this.connected = connected;
  }

  @NonNull
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(@NonNull String displayName) {
    this.displayName = displayName;
  }

  @NonNull
  public List<Match> getMatchesOriginated() {
    return matchesOriginated;
  }

  @NonNull
  public List<Match> getMatchesWon() {
    return matchesWon;
  }

  @NonNull
  public List<Match> getMatches() {
    return matches;
  }

  public URI getHref() {
    return (id != null) ? entityLinks.linkForItemResource(User.class, id).toUri() : null;
  }

  @PostConstruct
  private void initHateoas() {
    //noinspection ResultOfMethodCallIgnored
    entityLinks.toString();
  }

  @Autowired

  public void setEntityLinks(
      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") EntityLinks entityLinks) {
    User.entityLinks = entityLinks;
  }
}
