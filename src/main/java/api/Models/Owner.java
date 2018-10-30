package api.Models;

/*import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;*/
import javax.persistence.*;//backup

import org.hibernate.annotations.GenericGenerator;

@Entity // This tells Hibernate to make a table out of this class
public class Owner{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")
  private Integer owner_id;

  @ManyToOne(fetch = FetchType.EAGER, optional=false)
  @JoinColumn(name = "person_id", nullable = false)
  private Person person;

  private String status = "active";

  //GETTERS AND SETTERS
  public Integer getId() {
  	return owner_id;
  }
  public void setId(Integer owner_id) {
  	this.owner_id = owner_id;
  }

  public Integer getPersonId() {
  return person.getId();
  }
  public void setPersonId(Person person) {
  this.person = person;
  }

  public String getStatus() {
  return status;
  }
  public void setStatus(String status) {
  this.status = status;
  }
}

//SQL:
/*
CREATE TABLE OWNER
(
  owner_id INTEGER NOT NULL,
  person_id INTEGER NOT NULL,
  status VARCHAR(64) DEFAULT 'Active',
  PRIMARY KEY (owner_id),
  FOREIGN KEY (person_id) REFERENCES PERSON(person_id)
);
*/