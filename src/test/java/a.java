import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

public interface a {

  default void speak(){
    System.out.println("呀哈哈哈");
  }
}

class b implements a{

}

@Slf4j
class c {
  @Test
  void aVoid(){
    b b = new b();
    b.speak();

    for (int i = 0; i < 10; i++) {
      repeat(i,()-> System.out.println("countdown"));
      speak(i,()->log.info("你真的很牛逼哦"));
    }
  }

  private static void repeat(int n, Runnable action){
    for(int i = 0;i<n;i++)action.run();
  }

  private void speak(int n,Runnable action){
    for (int i = 0; i<n; i++)action.run();
  }
}