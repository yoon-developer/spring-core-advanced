package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Slf4j
@Aspect
public class AspectV6Advice {

  //hello.aop.order 패키지와 하위 패키지 이면서 클래스 이름 패턴이 *Service
  @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
  public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
    try {

      log.info("[around][트랜잭션 시작] {}", joinPoint.getSignature());
      //@Before

      Object result = joinPoint.proceed();

      log.info("[around][트랜잭션 커밋] {}", joinPoint.getSignature());
      //@AfterReturning
      return result;
    } catch (Exception e) {
      //@AfterThrowing
      log.info("[around][트랜잭션 롤백] {}", joinPoint.getSignature());
      throw e;

    } finally {
      //@After
      log.info("[around][리소스 릴리즈] {}", joinPoint.getSignature());
    }
  }

  @Before("hello.aop.order.aop.Pointcuts.orderAndService()")
  public void doBefore(JoinPoint joinPoint) {
    log.info("[before] {}", joinPoint.getSignature());
  }

  @AfterReturning(value = "hello.aop.order.aop.Pointcuts.orderAndService()", returning = "result")
  public void doReturn(JoinPoint joinPoint, Object result) {
    log.info("[return] {} return={}", joinPoint.getSignature(), result);
  }

  @AfterThrowing(value = "hello.aop.order.aop.Pointcuts.orderAndService()", throwing = "ex")
  public void doThrowing(JoinPoint joinPoint, Exception ex) {
    log.info("[ex] {} message={}", joinPoint.getSignature(), ex.getMessage());
  }

  @After("hello.aop.order.aop.Pointcuts.orderAndService()")
  public void doAfter(JoinPoint joinPoint) {
    log.info("[after] {}", joinPoint.getSignature());
  }

}
