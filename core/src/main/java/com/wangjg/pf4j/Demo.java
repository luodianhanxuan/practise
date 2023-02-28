package com.wangjg.pf4j;

import org.pf4j.*;

import java.util.List;

/**
 * @author wangjg
 */
public class Demo {
/*
        <!-- pf4j -->
        <dependency>
            <groupId>org.pf4j</groupId>
            <artifactId>pf4j</artifactId>
            <version>3.6.0</version>
        </dependency>

 */

    public static interface Greeting extends ExtensionPoint {
        String getGreeting();
    }

    public static class HelloPlugin extends Plugin {

        /**
         * Constructor to be used by plugin manager for plugin instantiation.
         * Your plugins have to provide constructor with this exact signature to
         * be successfully loaded by manager.
         *
         * @param wrapper
         */
        public HelloPlugin(PluginWrapper wrapper) {
            super(wrapper);
        }

        @Extension
        public static class HelloGreeting implements Greeting {

            @Override
            public String getGreeting() {
                return "Hello";
            }
        }
    }


    static class WelcomePlugin extends Plugin {
        /**
         * Constructor to be used by plugin manager for plugin instantiation.
         * Your plugins have to provide constructor with this exact signature to
         * be successfully loaded by manager.
         *
         * @param wrapper
         */
        public WelcomePlugin(PluginWrapper wrapper) {
            super(wrapper);
        }

        @Extension
        public static class WelcomeGreeting implements Greeting {

            @Override
            public String getGreeting() {
                return "Welcome";
            }
        }
    }



    public static void main(String[] args) {
        PluginManager pluginManager = new DefaultPluginManager();
        List<Greeting> greetings =
                pluginManager.getExtensions(Greeting.class);

        for (Greeting greeting : greetings) {
            System.out.println(">>> " + greeting.getGreeting());
        }
    }
}
