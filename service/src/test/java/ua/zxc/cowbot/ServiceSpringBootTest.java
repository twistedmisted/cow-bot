package ua.zxc.cowbot;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

@Profile("test")
@ActiveProfiles("test")
@SpringBootTest
public abstract class ServiceSpringBootTest {
}
