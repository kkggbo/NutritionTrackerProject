import com.nt.tracker.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Map;

@Slf4j
public class myTest {
    @Test
    void  test() {
        Map<String, Object> claims = JwtUtils.parseJwt("eyJhbGciOiJIUzI1NiJ9.eyJpZCI6NCwidXNlcm5hbWUiOiJLS0dHQk8iLCJleHAiOjE3NDgxMTkzMzB9.4p-gooI57Z_pHs3fMjqP61c1rPgTpynk7H6GjpNdqEE");
        // log.info("{}", claims);
        System.out.println(claims);
    }



}
