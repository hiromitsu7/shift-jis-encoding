package hiromitsu.garbling;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class MyServlet
 */
@WebServlet(urlPatterns = { "/MyServlet" })
public class MyServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  private static Logger logger = LoggerFactory.getLogger(MyServlet.class);

  @PersistenceContext
  private EntityManager em;
  
  @Inject
  private MyTextService myTextService;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public MyServlet() {
    super();
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
    List<MyText> list = myTextService.findAllAndLength();
    logger.info(list.toString());
    
    request.setAttribute("list", list);
    
    request.getRequestDispatcher("db-access.jsp").forward(request, response);
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
    String text = request.getParameter("text");
    MyText myText = new MyText();
    myText.setText(text);
    logger.info(myText.toString());

    myTextService.create(myText);
    logger.info(myText.toString());
    
    doGet(request, response);
  }

}
