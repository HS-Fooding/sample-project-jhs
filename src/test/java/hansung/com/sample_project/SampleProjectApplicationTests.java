package hansung.com.sample_project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class SampleProjectApplicationTests {

	@Autowired
	private PasswordEncoder encoder;

	@Test
	void generateHashedPassword() {
		String pwd = encoder.encode("fooding");
		System.out.println(pwd);
	}
}
