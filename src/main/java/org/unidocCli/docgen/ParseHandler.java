package org.unidocCli.docgen;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;
import org.unidocCli.formatter.LogSetter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ParseHandler {

    private Logger log = Logger.getLogger(this.toString());

    public void handlePackage(Path newDir, String finalResource, Path path) throws IOException, IllegalArgumentException {

        LogSetter logSetter = new LogSetter();
        logSetter.setLog(log);

            SourceRoot sourceRoot = new SourceRoot(path);
            sourceRoot.tryToParse();
            List<CompilationUnit> cu = sourceRoot.getCompilationUnits();

            String[] newDirComponents = finalResource.split("/");

            Path newDirectory = newDir;
            for (String dir : newDirComponents) {
                newDirectory = Path.of(newDirectory + "/" + dir);
                try {
                    Files.createDirectory(newDirectory);
                } catch (IOException ignored) {

                }
            }

            Path finalNewDirectory = newDirectory;
            cu.stream().forEach(compilationUnit -> {

                File outputFile = new File(finalNewDirectory + "/" + compilationUnit.getStorage().get().getFileName());
                UnitParser up = new UnitParser();
                try {
                    up.parser(outputFile, compilationUnit);
                } catch (IOException ignored) {

                }
            });

    }

    public File handleSourceFile(Path newDir, String resource, String pathToSrcFile) throws IOException {

        String finalResource = resource.replace(".", "/");
        List<String> newDirComponents = Arrays.asList(finalResource.split("/"));

        Path newDirectory = newDir;
        for (String dir : newDirComponents) {
            newDirectory = Path.of(newDirectory + "/" + dir);
            try {
                Files.createDirectory(newDirectory);
            } catch (IOException ignored) {

            }
        }
        CompilationUnit cu = StaticJavaParser.parse(new File(pathToSrcFile));
        File outputFile = new File(newDirectory + "/" + cu.getStorage().get().getFileName());
        UnitParser up = new UnitParser();
        up.parser(outputFile, cu);
        return outputFile;
    }

    public List<File> handleSubPackage(Path newDirectory, Path path, String finalSubPack) throws IOException, IllegalArgumentException {

        List<File> sourceFiles = new ArrayList<>();

            SourceRoot sourceRoot = new SourceRoot(path);
            sourceRoot.tryToParse();
            List<CompilationUnit> cu = sourceRoot.getCompilationUnits(); //gets all sourcefiles

            String[] newDirComponents = finalSubPack.split("/"); //org testfiles

            Path newDir = newDirectory;
            for (String dir : newDirComponents) {
                newDir = Path.of(newDir + "/" + dir);
                try {
                    Files.createDirectory(newDir);
                } catch (IOException ignored) {

                }
            }
            Path NewDir = newDir;
            cu.stream()
                    .forEach(compilationUnit -> { //each source file
                        String s = compilationUnit.getStorage().get().getPath().toString(); //get source file path
                        String newS = s.replace(".", "/");
                        String[] ss = newS.split("/");

                        Path finalNewDir = NewDir;
                        for (String dir : ss) {
                            finalNewDir = Path.of(finalNewDir + "/" + dir);
                            try {
                                Files.createDirectory(finalNewDir);
                            } catch (IOException ignored) {

                            }
                        }
                        try {

                            File outputFile = new File(finalNewDir + "/" + compilationUnit.getStorage().get().getFileName());
                            UnitParser up = new UnitParser();
                            up.parser(outputFile, compilationUnit);
                            sourceFiles.add(outputFile);
                        } catch (IOException ignored) {

                        }
                    });

        return sourceFiles;

    }
}
