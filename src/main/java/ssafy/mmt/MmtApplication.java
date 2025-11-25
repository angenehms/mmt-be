package ssafy.mmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MmtApplication {

	public static void main(String[] args) {
		SpringApplication.run(MmtApplication.class, args);
	}

}
