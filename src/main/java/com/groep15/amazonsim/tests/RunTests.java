package com.groep15.amazonsim.tests;

import org.reflections.Reflections;

import java.util.Set;

public class RunTests {
    private static Set<Class<? extends ITest>> classes = new Reflections("com.groep15.amazonsim.tests").getSubTypesOf(ITest.class);

    private static final String RED   = "\033[0;31m";
    private static final String GREEN = "\033[0;32m";
    private static final String WHITE = "\033[0;37m";

    public static void main(String[] args) {
        for (Class<? extends ITest> testcls : classes) {
            try {
                System.out.println(GREEN + "Running test " + testcls.getSimpleName());
                System.out.print(WHITE);

                ITest test = testcls.newInstance();
                boolean result = test.run();

                if (result) {
                    System.out.println(GREEN + "Test " + testcls.getSimpleName() + " completed successfully.");
                } else {
                    System.out.println(RED + "Test " + testcls.getSimpleName() + " failed!");
                }
                System.out.print(WHITE);
            } catch (Exception e) {
                System.out.print("Test " + testcls.getSimpleName() + " could not be ran: " + e.getMessage());
                System.out.print(WHITE);
            }
        }
    }
}
