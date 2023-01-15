package hello.aop.pointcut;

import static org.assertj.core.api.Assertions.assertThat;

import hello.aop.member.MemberServiceImpl;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

@Slf4j
public class ExecutionTest {

  AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
  Method helloMethod;

  @BeforeEach
  public void init() throws NoSuchMethodException {
    helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
  }

  @Test
  void printMethod() {
    //public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
    log.info("helloMethod={}", helloMethod);
  }

  /**
   * 매칭 조건
   * 접근제어자?: public
   * 반환타입: String
   * 선언타입?: hello.aop.member.MemberServiceImpl
   * 메서드이름: hello
   * 파라미터: (String)
   * 예외?: 생략
   */
  @Test
  void exactMatch() {
    pointcut.setExpression(
        "execution(public java.lang.String hello.aop.member.MemberServiceImpl.hello(String))");

    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
  }

  /**
   * 매칭 조건
   * 접근제어자?: 생략
   * 반환타입: *
   * 선언타입?: 생략
   * 메서드이름: *
   * 파라미터: (..)
   */
  @Test
  void allMatch() {
    pointcut.setExpression("execution(* *(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
  }

  @Test
  void nameMatch() {
    pointcut.setExpression("execution(* hello(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
  }

  @Test
  void nameMatchStart1() {
    pointcut.setExpression("execution(* hel*(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
  }

  @Test
  void nameMatchStart2() {
    pointcut.setExpression("execution(* *hel*(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
  }

  @Test
  void nameMatchFalse() {
    pointcut.setExpression("execution(* nono(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
  }

  @Test
  void packageExactMatch1() {
    pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.hello(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
  }

  /**
   * hello.aop.member.*(1).*(2)
   * (1): 타입
   * (2): 메서드 이름
   */
  @Test
  void packageExactMatch2() {
    pointcut.setExpression("execution(* hello.aop.member.*.*(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
  }

  @Test
  void packageExactMatchFalse() {
    pointcut.setExpression("execution(* hello.aop.*.*(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
  }

  @Test
  void packageMatchSubPackage1() {
    pointcut.setExpression("execution(* hello.aop.member..*.*(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
  }

  @Test
  void packageMatchSubPackage2() {
    pointcut.setExpression("execution(* hello.aop..*.*(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
  }

  @Test
  void typeExactMatch() {
    pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
  }

  @Test
  void typeMatchSuperType() {
    pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
  }

  @Test
  void typeMatchInternal() throws NoSuchMethodException {
    pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
    Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);

    assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isTrue();
  }

  @Test
  void typeMatchNoSuperTypeMethodFalse() throws NoSuchMethodException {
    pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
    Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);

    assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isFalse();
  }

  /**
   * (String) : 정확하게 String 타입 파라미터
   * () : 파라미터가 없어야 한다.
   * (*) : 정확히 하나의 파라미터, 단 모든 타입을 허용한다.
   * (*, *) : 정확히 두 개의 파라미터, 단 모든 타입을 허용한다.
   * (..) : 숫자와 무관하게 모든 파라미터, 모든 타입을 허용한다. 참고로 파라미터가 없어도 된다. 0..* 로
   * 이해하면 된다.
   * (String, ..) : String 타입으로 시작해야 한다. 숫자와 무관하게 모든 파라미터, 모든 타입을 허용한다.
   */

  /**
   * String 타입의 파라미터 허용
   * (String)
   */
  @Test
  void argsMatch() {
    pointcut.setExpression("execution(* *(String))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
  }

  /**
   * 파라미터가 없어야 함
   * ()
   */
  @Test
  void argsMatchNoArgs() {
    pointcut.setExpression("execution(* *())");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
  }

  /**
   * 정확히 하나의 파라미터 허용, 모든 타입 허용
   * (Xxx)
   */
  @Test
  void argsMatchStar() {
    pointcut.setExpression("execution(* *(*))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
  }

  /**
   * 숫자와 무관하게 모든 파라미터, 모든 타입 허용
   * 파라미터가 없어도 됨
   * (), (Xxx), (Xxx, Xxx)
   */
  @Test
  void argsMatchAll() {
    pointcut.setExpression("execution(* *(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
  }

  /**
   * String 타입으로 시작, 숫자와 무관하게 모든 파라미터, 모든 타입 허용
   * (String), (String, Xxx), (String, Xxx, Xxx) 허용
   */
  @Test
  void argsMatchComplex() {
    pointcut.setExpression("execution(* *(String, ..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
  }

}
