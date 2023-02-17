package com.mouken;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.junit.AnalyzeClasses;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packagesOf = MoukenApplication.class)
public class PackageDependencyTests {
    
    private static final String PARTY = "..modules.party..";
    private static final String EVENT = "..modules.event..";
    private static final String ACCOUNT = "..modules.account..";
    private static final String TAG = "..modules.tag..";
    private static final String ZONE = "..modules.zone..";
    private static final String MAIN = "..modules.main..";
    private static final String ACCESS_IP = "..modules.accessIp..";
    private static final String RESOURCE = "..modules.resource..";
    private static final String ROLE = "..modules.role..";
    private static final String SECURITY = "..modules.security..";

/*
    @ArchTest
    ArchRule modulesPackageRule = classes().that().resideInAPackage("com.mouken.modules..")
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage("com.mouken.modules..");

    @ArchTest
    ArchRule partyPackageRule = classes().that().resideInAPackage(PARTY)
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage(PARTY, EVENT, MAIN);

    @ArchTest
    ArchRule eventPackageRule = classes().that().resideInAPackage(EVENT)
            .should().accessClassesThat().resideInAnyPackage(PARTY, ACCOUNT, EVENT);

    @ArchTest
    ArchRule accountPackageRule = classes().that().resideInAPackage(ACCOUNT)
            .should().accessClassesThat().resideInAnyPackage(TAG, ZONE, ACCOUNT);
*/
    /*@ArchTest
    ArchRule cycleCheck = slices().matching("com.mouken.modules.(*)..")
            .should().beFreeOfCycles();*/
}