package hiromitsu.garbling;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * 初期化
 */
@WebListener
public class Initializer implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent ev) {
    // jul-to-slf4jを利用する
    SLF4JBridgeHandler.install();
  }

  @Override
  public void contextDestroyed(ServletContextEvent ev) {
    // no-op
  }
}
