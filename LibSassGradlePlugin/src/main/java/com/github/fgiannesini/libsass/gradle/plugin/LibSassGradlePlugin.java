package com.github.fgiannesini.libsass.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

import com.github.fgiannesini.libsass.gradle.plugin.extension.LibSassParameters;
import com.github.fgiannesini.libsass.gradle.plugin.tasks.CompileLibSassTask;
import com.github.fgiannesini.libsass.gradle.plugin.tasks.CompileLibSassWithWatchTask;

/**
 * Gradle plugin declaration
 */
public class LibSassGradlePlugin implements Plugin<Project> {

    @Override
    public void apply(final Project project) {
        project.getExtensions().create("libSassParameters",
                LibSassParameters.class);
        final TaskContainer tasks = project.getTasks();
        tasks.create("compileLibSass", CompileLibSassTask.class);
        tasks.create("compileLibSassWithWatch",
                CompileLibSassWithWatchTask.class);
    }

}