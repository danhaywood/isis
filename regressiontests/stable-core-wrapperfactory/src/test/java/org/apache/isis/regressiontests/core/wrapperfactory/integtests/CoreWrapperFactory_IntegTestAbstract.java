package org.apache.isis.regressiontests.core.wrapperfactory.integtests;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.test.context.ActiveProfiles;

import org.apache.isis.core.config.presets.IsisPresets;
import org.apache.isis.core.runtimeservices.IsisModuleCoreRuntimeServices;
import org.apache.isis.persistence.jdo.datanucleus.IsisModulePersistenceJdoDatanucleus;
import org.apache.isis.security.bypass.IsisModuleSecurityBypass;
import org.apache.isis.testdomain.wrapperfactory.Counter;
import org.apache.isis.testdomain.wrapperfactory.CounterRepository;
import org.apache.isis.testdomain.wrapperfactory.WrapperTestFixtures;
import org.apache.isis.testing.fixtures.applib.IsisIntegrationTestAbstractWithFixtures;
import org.apache.isis.testing.fixtures.applib.IsisModuleTestingFixturesApplib;

@SpringBootTest(
        classes = CoreWrapperFactory_IntegTestAbstract.AppManifest.class
)
@ActiveProfiles("test")
public abstract class CoreWrapperFactory_IntegTestAbstract extends IsisIntegrationTestAbstractWithFixtures {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({
            IsisModuleCoreRuntimeServices.class,
            IsisModuleSecurityBypass.class,
            IsisModulePersistenceJdoDatanucleus.class,
            IsisModuleTestingFixturesApplib.class,

            WrapperTestFixtures.class,
    })
    @PropertySources({
            @PropertySource(IsisPresets.H2InMemory_withUniqueSchema),
            @PropertySource(IsisPresets.DatanucleusAutocreateNoValidate),
            @PropertySource(IsisPresets.DatanucleusEagerlyCreateTables),
            @PropertySource(IsisPresets.UseLog4j2Test),
    })
    public static class AppManifest {
    }

    @BeforeAll
    static void beforeAll() {
        IsisPresets.forcePrototyping();
    }

    protected Counter newCounter(final String name) {
        return Counter.builder().name(name).build();
    }


    @Inject protected CounterRepository counterRepository;
}
