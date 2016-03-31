package com.github.fgiannesini.libsass.gradle.plugin.tasks;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Consumer;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

import com.github.fgiannesini.libsass.gradle.plugin.compiler.LibSassCompiler;
import com.github.fgiannesini.libsass.gradle.plugin.compiler.PluginInputSyntax;
import com.github.fgiannesini.libsass.gradle.plugin.compiler.PluginOutputStyle;
import com.github.fgiannesini.libsass.gradle.plugin.extension.LibSassParameters;

import io.bit3.jsass.CompilationException;
import lombok.Getter;

public class CompileLibSassTaskDelegate {

    private final Project project;

    private final LibSassCompiler libSassCompiler;

    @Getter
    private File inputFile;

    private File outputFile;

    @Getter
    private Path watchedDirectoryPath;

    public CompileLibSassTaskDelegate(final Project project,
            final Logger logger) {
        this.project = project;
        this.libSassCompiler = new LibSassCompiler(logger);
        this.init();
    }

    private void init() {
        LibSassParameters extension = this.project.getExtensions()
                .findByType(LibSassParameters.class);
        if (extension == null) {
            extension = new LibSassParameters();
        }

        this.inputFile = this.getMandatoryFile(extension.getInputFilePath(),
                "inputFilePath");
        if (!this.inputFile.exists()) {
            throw new IllegalArgumentException(
                    this.inputFile.getPath() + " does not exist");
        }

        this.outputFile = this.getMandatoryFile(extension.getOutputFilePath(),
                "outputFilePath");

        this.manageBoolean(extension.getSourceMapContents(),
                this.libSassCompiler::setSourceMapContents);

        this.manageBoolean(extension.getSourceMapEmbed(),
                this.libSassCompiler::setSourceMapEmbed);

        this.manageBoolean(extension.getSourceComments(),
                this.libSassCompiler::setSourceComments);

        this.manageBoolean(extension.getOmitSourceMappingURL(),
                this.libSassCompiler::setOmitSourceMappingURL);

        Optional.ofNullable(extension.getInputSyntax()).map(String::toUpperCase)
                .map(PluginInputSyntax::valueOf)
                .ifPresent(this.libSassCompiler::setInputSyntax);

        Optional.ofNullable(extension.getOutputStyle()).map(String::toUpperCase)
                .map(PluginOutputStyle::valueOf)
                .ifPresent(this.libSassCompiler::setOutputStyle);

        Optional.ofNullable(extension.getPrecision())
                .ifPresent(this.libSassCompiler::setPrecision);

        Optional.ofNullable(extension.getIncludePaths())
                .map(path -> path.split(File.pathSeparator))
                .ifPresent(this.libSassCompiler::addIncludePaths);

        Optional.ofNullable(extension.getSourceMapFilePath())
                .map(this::buildFile)
                .ifPresent(this.libSassCompiler::setSourceMapFile);

        final Optional<Path> watchDirectoryPathOptional = Optional
                .ofNullable(extension.getWatchedDirectoryPath())
                .map(this::buildAbsolutePath);
        if (watchDirectoryPathOptional.isPresent()) {
            this.watchedDirectoryPath = watchDirectoryPathOptional.get();
        }
    }

    private File buildFile(final String filePath) {
        return this.buildAbsolutePath(filePath).toFile();
    }

    private Path buildAbsolutePath(final String filePath) {
        return Paths.get(this.project.getRootDir().getAbsolutePath(), filePath);
    }

    private File getMandatoryFile(final String filePath,
            final String parameterName) {
        if (filePath == null) {
            throw new IllegalArgumentException(
                    "Parameter " + parameterName + " should be set");
        }
        return this.buildFile(filePath);
    }

    private void manageBoolean(final Boolean booleanValue,
            final Consumer<Boolean> booleanConsumer) {
        Optional.ofNullable(booleanValue).map(Boolean::booleanValue)
                .ifPresent(booleanConsumer);
    }

    public void compile() throws CompilationException {
        this.libSassCompiler.compileFile(this.inputFile, this.outputFile);
    }
}