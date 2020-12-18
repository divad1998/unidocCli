package org.unidocCli.cli;

import org.unidocCli.docgen.Generator;
import picocli.CommandLine;

import java.io.IOException;
import java.util.List;

/**
 *
 * unidoc cli class
 */
@CommandLine.Command(name = "unidoc", mixinStandardHelpOptions = true, version = "unidoc 1.0", separator = " ", description = "Generates java source code documentation from unidoc annotations")
public class Main implements Runnable {

    /**
     * where output html would be stored
     */
    @CommandLine.Option(names = {"-d"}, arity = "1", description = "indicates directory of output file(s)")
    String destination;

    /**
     * path to find files
     */
    @CommandLine.Option(names = {"-sourcepath", "--source-path"}, arity = "1", split = ":", splitSynopsisLabel = ":", description = "indicates path of src path(s)")
    List<String> sourcePaths;

    /**
     * takes package and sourcefile to be documented
     */
    @CommandLine.Parameters(paramLabel = "PackageAndSourceFile", description = "package and sourcefile to document")
    List<String> packageAndSrcFileList;

    /**
     * package with subpackages to document or subpackage to document
     */
    @CommandLine.Option(names = {"-subpackages"}, arity = "1", split = ":", description = "identifies top-level subpackage or top-level package with subpackages")
    List<String> subpackages;

    /**
     * whether to document all declarations
     */
    @CommandLine.Option(names = {"-private"}, description = "indicates that all classes should be documented")
    boolean privateRequested;

    /**
     * whether to document only public declarations
     */
    @CommandLine.Option(names = {"-public"}, description = "indicates that only public declarations should be documented")
    boolean publicRequested;

    /**
     * whether to document public, protected and package declarations
     */
    @CommandLine.Option(names = {"-package"}, description = "indicates that only public, protected and package declarations should be documented")
    boolean packageRequested;

    /**
     * passes options and parameters to method that generate javadoc
     */
    @Override
    public void run() {

        Generator gen = new Generator();
        try {
            gen.generate(destination, sourcePaths, packageAndSrcFileList, subpackages, privateRequested, publicRequested, packageRequested);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Main m = new Main();
        CommandLine cmd = new CommandLine(m);
        cmd.execute(args);

    }
}
