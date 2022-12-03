package hello.proxy.config.v1_proxy;

import hello.proxy.app.v2.OrderControllerV2;
import hello.proxy.app.v2.OrderRepositoryV2;
import hello.proxy.app.v2.OrderServiceV2;
import hello.proxy.config.v1_proxy.concrete_proxy.OrderControllerConcreteProxy;
import hello.proxy.config.v1_proxy.concrete_proxy.OrderRepositoryConcreteProxy;
import hello.proxy.config.v1_proxy.concrete_proxy.OrderServiceConcreteProxy;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConcreteProxyConfig {

  @Bean
  public OrderControllerV2 orderController(LogTrace logTrace) {
    OrderControllerV2 orderController = new OrderControllerV2(orderService(logTrace));
    return new OrderControllerConcreteProxy(orderController, logTrace);
  }

  @Bean
  public OrderServiceV2 orderService(LogTrace logTrace) {
    OrderServiceV2 orderService = new OrderServiceV2(orderRepository(logTrace));
    return new OrderServiceConcreteProxy(orderService, logTrace);
  }

  @Bean
  public OrderRepositoryV2 orderRepository(LogTrace logTrace) {
    OrderRepositoryV2 orderRepository = new OrderRepositoryV2();
    return new OrderRepositoryConcreteProxy(orderRepository, logTrace);
  }

}
