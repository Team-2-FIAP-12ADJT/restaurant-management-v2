package com.fiap.restaurant_management_v2.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Enforces the Clean Architecture dependency rule: dependencies point inward only.
 * Layout follows the guia-do-back-end-java reference:
 *   core/{domain,usecases,ports} (independent) -> adapters -> config (frameworks).
 * Domain knows nothing; Ports know Domain; UseCases know Domain + Ports;
 * Adapters know the core; config (Main) is the outermost composition root.
 */
@AnalyzeClasses(packages = "com.fiap.restaurant_management_v2")
class CleanArchitectureTest {

    // withOptionalLayers(true) tolerates the pre-feature state where some layers
    // have no classes yet; the framework-freedom rule below uses allowEmptyShould
    // for the same reason. Once code exists, violations still fail the build.
    //
    // Config is the outermost component: it may wire any inner layer, but nothing
    // may depend on it (mayNotBeAccessedByAnyLayer).

    @ArchTest
    static final ArchRule layers_respect_dependency_rule = layeredArchitecture()
        .consideringOnlyDependenciesInLayers()
        .withOptionalLayers(true)
        .layer("Domain").definedBy("..core.domain..")
        .layer("Ports").definedBy("..core.ports..")
        .layer("UseCases").definedBy("..core.usecases..")
        .layer("Adapters").definedBy("..adapters..")
        .layer("Config").definedBy("..config..")
        .whereLayer("Domain").mayNotAccessAnyLayer()
        .whereLayer("Ports").mayOnlyAccessLayers("Domain")
        .whereLayer("UseCases").mayOnlyAccessLayers("Domain", "Ports")
        .whereLayer("Adapters").mayOnlyAccessLayers("Domain", "UseCases", "Ports")
        .whereLayer("Config").mayNotBeAccessedByAnyLayer();

    @ArchTest
    static final ArchRule core_is_independent_of_frameworks = noClasses()
        .that().resideInAPackage("..core..")
        .should().dependOnClassesThat().resideInAnyPackage(
            "org.springframework..",
            "jakarta..",
            "org.hibernate..",
            "com.fasterxml.jackson..")
        .as("core (domain + use cases + ports) must not depend on any framework")
        .allowEmptyShould(true);
}
