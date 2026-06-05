package com.fiap.restaurant_management_v2.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Enforces the Clean Architecture dependency rule: dependencies point inward only.
 * Domain knows nothing; UseCase knows Domain; Adapter knows UseCase + Domain.
 * Main (Application + config) is the outermost composition root.
 */
@AnalyzeClasses(packages = "com.fiap.restaurant_management_v2")
class CleanArchitectureTest {

    // withOptionalLayers(true) tolerates the pre-feature state where some layers
    // have no classes yet; the framework-freedom rules below use allowEmptyShould
    // for the same reason. Once code exists, violations still fail the build.
    //
    // Config is the Main component (outermost): it may wire any inner layer, but
    // nothing may depend on it. Declaring it as a layer closes the gap where
    // consideringOnlyDependenciesInLayers() would otherwise ignore Domain -> config.

    @ArchTest
    static final ArchRule layers_respect_dependency_rule = layeredArchitecture()
        .consideringOnlyDependenciesInLayers()
        .withOptionalLayers(true)
        .layer("Domain").definedBy("..domain..")
        .layer("UseCase").definedBy("..usecase..")
        .layer("Adapter").definedBy("..adapter..")
        .layer("Config").definedBy("..config..")
        .whereLayer("Domain").mayNotAccessAnyLayer()
        .whereLayer("UseCase").mayOnlyAccessLayers("Domain")
        .whereLayer("Adapter").mayOnlyAccessLayers("UseCase", "Domain")
        .whereLayer("Config").mayNotBeAccessedByAnyLayer();

    @ArchTest
    static final ArchRule domain_is_free_of_frameworks = noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat().resideInAnyPackage(
            "org.springframework..",
            "jakarta..",
            "org.hibernate..",
            "com.fasterxml.jackson..")
        .as("domain must not depend on any framework")
        .allowEmptyShould(true);

    @ArchTest
    static final ArchRule usecase_is_free_of_frameworks = noClasses()
        .that().resideInAPackage("..usecase..")
        .should().dependOnClassesThat().resideInAnyPackage(
            "org.springframework..",
            "org.hibernate..",
            "com.fasterxml.jackson..")
        .as("usecase must not depend on web/persistence frameworks")
        .allowEmptyShould(true);
}
