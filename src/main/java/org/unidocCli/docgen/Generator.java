package org.unidocCli.docgen;

import org.unidocCli.formatter.LogSetter;

import javax.tools.DocumentationTool;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * has method for generating javadoc html file for package cli arg
 */
public class Generator {

    private Logger log = Logger.getLogger(this.toString());

    /**
     *
     * calls method that generates javadoc html files
     *
     * @param destination where to store generated html file
     * @param sourcePathList path to files to document
     * @param packageAndSrcFileList package and sourcefiles to document
     * @param privateRequested whether to include all declarations in generated doc file
     * @param publicRequested whether to include only public declarations in generated doc file
     * @param packageRequested whether to include only public, protected and package declarations in generated doc file
     */
    public void generate(String destination, List<String> sourcePathList, List<String> packageAndSrcFileList, List<String> subpackages, boolean privateRequested, boolean publicRequested, boolean packageRequested) throws IOException {

        LogSetter logSetter = new LogSetter();
        logSetter.setLog(log);

        List<String> options = new ArrayList<>();
        List<File> srcFiles = new ArrayList<>();

        if (!(sourcePathList == null)) { //it means packageAndSrcFileList isn't empty

            Path newDirectory = Paths.get(System.getProperty("user.home") + "/" + "unidoccc");
            Files.createDirectory(newDirectory);

            sourcePathList.stream().forEach(sourcepath -> {
                    try {
                        packageAndSrcFileList.stream().forEach(resource -> { //for each package in the list

                            String finalResource = resource.replace(".", "/");
                            Path path = Paths.get(System.getProperty("user.dir") + "/" + sourcepath + "/" + finalResource);
                            try {
                                ParseHandler ph = new ParseHandler();
                                ph.handlePackage(newDirectory, finalResource, path);
                            } catch (IllegalArgumentException | IOException iae) {

                                //probably ran from a different working directory
                                String finalResource1 = resource.replace(".", "/");
                                Path path1 = Paths.get(sourcepath + "/" + finalResource1); //
                                try {
                                    ParseHandler ph = new ParseHandler();
                                    ph.handlePackage(newDirectory, finalResource1, path1); //initialFile = user/home/unidocc
                                } catch (IllegalArgumentException | IOException e) {

                                    //might be a source file. Considering curr working dir
                                    String pathToSrcFile = System.getProperty("user.dir") + resource;
                                    try {
                                        ParseHandler ph = new ParseHandler();
                                        File outputFile = ph.handleSourceFile(newDirectory, resource, pathToSrcFile);
                                        srcFiles.add(outputFile);
                                    } catch (IllegalArgumentException | IOException ee) {
                                        try {
                                          //not considering working dir
                                            ParseHandler ph = new ParseHandler();
                                            File outputFile = ph.handleSourceFile(newDirectory, resource, resource);
                                            srcFiles.add(outputFile);
                                        } catch (IllegalArgumentException | IOException ioe) {
                                            log.info("null");
                                        }
                                    }
                                }
                            }
                        });
                    } catch (NullPointerException ignored) {

                    }

                    if (subpackages != null) {

                        subpackages.stream()
                            .forEach(subPack -> { //org.testfiles

                                //considering working dir and sourcepath
                                String finalSubPack = subPack.replace(".", "/"); //org/testfiles
                                Path path = Paths.get(System.getProperty("user.dir") + "/" + sourcepath + "/" + finalSubPack); //user/dir/sourcepath/org/testfiles
                                try {
                                    ParseHandler ph = new ParseHandler();
                                    List<File> sourceFiles = ph.handleSubPackage(newDirectory, path, finalSubPack);
                                    srcFiles.addAll(sourceFiles);
                                } catch (IllegalArgumentException | IOException ioe) {
                                    //not considering curr dir
                                    String finalSubPack1 = subPack.replace(".", "/");
                                    Path path1 = Paths.get(sourcepath + "/" + finalSubPack1);
                                    try {
                                        ParseHandler ph = new ParseHandler();
                                        List<File> sourceFiles = ph.handleSubPackage(newDirectory, path1, finalSubPack1);
                                        srcFiles.addAll(sourceFiles);
                                    } catch (IllegalArgumentException | IOException e) {
                                        log.info("null");
                                    }
                                }
                            });
                    }
           });

            toolCaller(newDirectory, destination, privateRequested, publicRequested, packageRequested, srcFiles, options, packageAndSrcFileList);
            deleteNewDirs(newDirectory.toFile());

        } else {
            Path newDirectory = Paths.get(System.getProperty("user.home") + "/" + "unidoccc");
            Files.createDirectory(newDirectory);

            //document a package without -sourcepath i.e considering working directory
            try {
                packageAndSrcFileList.stream().forEach(resource -> {

                    String finalResource = resource.replace(".", "/");
                    Path path = Paths.get(System.getProperty("user.dir") + finalResource);
                    try {
                        ParseHandler ph = new ParseHandler();
                        ph.handlePackage(newDirectory, finalResource, path);
                    } catch (IllegalArgumentException | IOException e) {

                        //might be a source file
                        String pathToSrcFile = System.getProperty("user.dir") + resource;
                        try {
                            ParseHandler ph = new ParseHandler();
                            File outputFile = ph.handleSourceFile(newDirectory, resource, pathToSrcFile);
                            srcFiles.add(outputFile);
                        } catch (IOException ee) {
                            try {
                                ParseHandler ph = new ParseHandler();
                                File outputFile = ph.handleSourceFile(newDirectory, resource, resource);
                                srcFiles.add(outputFile);
                            } catch (IOException ioException) {
                                log.info("null");
                            }
                        }
                    }
                });
            } catch (NullPointerException ignored) {

            }

            if (subpackages != null) {
                subpackages.stream()
                        .forEach(subPack -> {
                            String finalSubPack = subPack.replace(".", "/");
                            Path path = Paths.get(System.getProperty("user.dir") + finalSubPack);
                            try {
                                ParseHandler ph = new ParseHandler();
                                List<File> sourceFiles = ph.handleSubPackage(newDirectory, path, finalSubPack);
                                srcFiles.addAll(sourceFiles);
                            } catch (IllegalArgumentException | IOException ii) {
                                log.info("null");
                            }
                        });
            }

            toolCaller(newDirectory, destination, privateRequested, publicRequested, packageRequested, srcFiles, options, packageAndSrcFileList);
            deleteNewDirs(newDirectory.toFile());
        }
    }

    /**
     *
     * calls javadoc tool
     *
     * @param newDirectory directory to find files
     * @param destination output file
     * @param privateRequested
     * @param publicRequested
     * @param packageRequested
     * @param sourceFiles
     * @param options
     * @param packageAndSrcFileList
     */
    public void toolCaller(Path newDirectory, String destination,
                           boolean privateRequested, boolean publicRequested,
                           boolean packageRequested, List<File> sourceFiles,
                           List<String> options, List<String> packageAndSrcFileList) {

        if (!(destination == null)) {
            options.add("-d");
            options.add(destination);
        }

        options.add("-sourcepath");
        options.add(newDirectory.toString());

        if (packageAndSrcFileList != null) {
            for (String pack : packageAndSrcFileList) {
                if (!pack.contains("/")) {
                    options.add(pack);
                }
            }
        }

        if (privateRequested) {
            options.add("-private");
        } else {
            if (publicRequested) {
                options.add("-public");
            } else {
                if (packageRequested) {
                    options.add("-package");
                }
            }
        }

        DocumentationTool tool = ToolProvider.getSystemDocumentationTool();
        StandardJavaFileManager fileManager = tool.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> javadocCompilationUnits = fileManager.getJavaFileObjectsFromFiles(sourceFiles);
        DocumentationTool.DocumentationTask task = tool.getTask(null, fileManager, null, null, options, javadocCompilationUnits);
        task.call();
    }

    /**
     *
     * delete "user.home"/unidoccc
     * @param paths path to directories to delete
     * @throws IOException
     */
    public void deleteNewDirs(File paths) throws IOException {

        try {
            for (File subFile : paths.listFiles()) {
                if(subFile.isDirectory()) {
                    deleteNewDirs(subFile);
                } else {
                    subFile.delete();
                }
            }
        } catch (NullPointerException ignored) {

        }
        paths.delete();
    }
}




