package com.github.fgiannesini.libsass.gradle.plugin.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import com.github.fgiannesini.libsass.gradle.plugin.extension.LibSassParameters;
import com.github.fgiannesini.libsass.gradle.plugin.installer.FrameworkPropertiesEnum;
import com.github.fgiannesini.libsass.gradle.plugin.installer.ScssFrameworkInstaller;

/**
 * Task to install Bourbon sources to the project
 */
public class InstallBourbonTask extends DefaultTask {

    @Override
    public String getDescription() {
        return "Install bourbon sources in defined directory. Old installation is not deleted.";
    }

    @TaskAction
    public void installBoubon() throws Exception {
        final Project project = this.getProject();
        LibSassParameters extension = project.getExtensions()
                .findByType(LibSassParameters.class);
        if (extension == null) {
            extension = new LibSassParameters();
        }

        final ScssFrameworkInstaller scssFrameworkInstaller = new ScssFrameworkInstaller(
                project, this.getLogger());

        scssFrameworkInstaller
                .setInstallationPath(extension.getBourbonInstallationPath());
        scssFrameworkInstaller
                .setVersionToDownload(extension.getBourbonVersion());

        scssFrameworkInstaller
                .installFramework(FrameworkPropertiesEnum.BOURBON);
    }

}