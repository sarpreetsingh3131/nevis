package com.sarpreetsingh.nevis.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ActiveProfilesResolver;

@SpringBootTest
@ActiveProfiles(resolver = AbstractIT.CustomActiveProfilesResolver.class)
public abstract class AbstractIT {

    static class CustomActiveProfilesResolver implements ActiveProfilesResolver {

        @Value("${spring.profiles.active}")
        String profile;

        @Override
        public String[] resolve(Class<?> testClass) {
            return new String[] { profile };
//            return new String[] { "local" };
        }
    }
}
