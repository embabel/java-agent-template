package com.embabel.template;

import com.embabel.template.injected.InjectedDemo;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
record DemoShell(InjectedDemo injectedDemo) {

    @ShellMethod("Invent an animal")
    String animal() {
        return injectedDemo.inventAnimal().toString();
    }
}
