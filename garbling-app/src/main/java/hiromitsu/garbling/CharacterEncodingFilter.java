package hiromitsu.garbling;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns="/MyServlet")
public class CharacterEncodingFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    request.setCharacterEncoding("Shift_JIS");
//    request.setCharacterEncoding("UTF-8");
    
    chain.doFilter(request, response);
    
    response.setCharacterEncoding("Shift_JIS");
//    response.setCharacterEncoding("UTF-8");
  }

}
