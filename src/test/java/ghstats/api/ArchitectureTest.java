package ghstats.api;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

class ArchitectureTest {
    private final JavaClasses applicationClasses = new ClassFileImporter().importPackages(this.getClass().getPackageName());

    @Test
    @DisplayName("Configurations classes should not be public")
    void configurations() {
        ArchRule rule = classes()
                .that()
                .areAnnotatedWith(Configuration.class)
                .should()
                .notBePublic();
        rule.check(applicationClasses);
    }

    @Test
    @DisplayName("Query and command classes should always be public")
    void queryAndCommands() {
        ArchRule rule = classes()
                .that()
                .haveSimpleNameEndingWith("Query")
                .or()
                .haveSimpleNameEndingWith("Command")
                .should()
                .bePublic();
        rule.check(applicationClasses);
    }

    @Test
    @DisplayName("Repository classes should never be public")
    void repositories() {
        ArchRule rule = classes()
                .that()
                .areInterfaces()
                .and()
                .haveSimpleNameEndingWith("Repository")
                .should()
                .bePackagePrivate();
        rule.check(applicationClasses);
    }

    @Test
    @DisplayName("All classes in api package should be public")
    void apiPackages() {
        ArchRule rule = classes()
                .that()
                .resideInAPackage(this.getClass().getPackageName() + "..api..")
                .should()
                .bePublic();
        rule.check(applicationClasses);
    }

    @Test
    @DisplayName("All classes in web package should be private")
    void webPackages() {
        ArchRule rule = classes()
                .that()
                .resideInAPackage(this.getClass().getPackageName() + "..web..")
                .should()
                .notBePublic();
        rule.check(applicationClasses);
    }
}
