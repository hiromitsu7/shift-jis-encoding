package hiromitsu.garbling;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import lombok.Data;

/**
 * テキストエンティティ
 */
@Data
@Entity(name = "MY_TEXT")
@NamedQueries({ @NamedQuery(name = "MyText.findAll", query = "SELECT m FROM MY_TEXT m") })
@NamedNativeQueries({
    @NamedNativeQuery(name = "MyText.length", query = "SELECT id, text, length(text) as length FROM MY_TEXT", resultClass = MyText.class) })
public class MyText {

  @Column
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false)
  private String text;

  private int length;
}
