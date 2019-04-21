package hiromitsu.garbling;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@ApplicationScoped
@Transactional
public class MyTextService {

  @PersistenceContext
  private EntityManager em;
  
  /**
   * 登録する
   * @param myText
   * @return 自動採番された主キー(id)
   */
  public Long create(MyText myText) {
    em.persist(myText);
    em.flush();
    return myText.getId();
  }
  
  /**
   * 全件検索する
   * @return 検索結果
   */
  public List<MyText> findAll() {
    TypedQuery<MyText> query = em.createNamedQuery("MyText.findAll", MyText.class);
    return query.getResultList();
  }

  public List<MyText> findAllAndLength() {
    TypedQuery<MyText> query = em.createNamedQuery("MyText.length", MyText.class);
    return query.getResultList();
  }
}
