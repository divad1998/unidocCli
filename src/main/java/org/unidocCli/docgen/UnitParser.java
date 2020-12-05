package org.unidocCli.docgen;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.unidocCli.formatter.LogSetter;
import org.unidocCli.parse.*;

import javax.tools.DocumentationTool;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * has methods for parsing packages and classes(cli args)
 */
public class UnitParser {

    private Logger log = Logger.getLogger(this.toString());

    /**
     *
     * parses compilation units in package cli arg
     *
     * @param opts options list with -d and its' value, plus -sourcepath without its' value
     * @param compilationUnit a class in the package
     * @param privateRequested was -private used
     * @param publicRequested was -public used
     * @param packageRequested was -package used
     */
    public void packageGenParser(List<String> opts, CompilationUnit compilationUnit, boolean privateRequested, boolean publicRequested, boolean packageRequested) throws IOException {

        LogSetter logSetter = new LogSetter();
        logSetter.setLog(log);
        List<String> options = opts;

        VoidVisitorAdapter<Void> declarations = new PackageParser();
        declarations.visit(compilationUnit, null);
        declarations = new ClassParser();
        declarations.visit(compilationUnit, null);
        declarations = new InterfaceParser();
        declarations.visit(compilationUnit, null);
        declarations = new EnumParser();
        declarations.visit(compilationUnit, null);
        declarations = new AnnotationParser();
        declarations.visit(compilationUnit, null);
        declarations = new AnnotationMemberParser();
        declarations.visit(compilationUnit, null);
        declarations = new ConstructorParser();
        declarations.visit(compilationUnit, null);
        declarations = new FieldParser();
        declarations.visit(compilationUnit, null);
        declarations = new MethodParser();
        declarations.visit(compilationUnit, null);

        List<ImportDeclaration> finalImportList = new ArrayList<>();
        NodeList<ImportDeclaration> importDeclarations = compilationUnit.getImports();
        importDeclarations.stream()
                .filter(importDeclaration -> !importDeclaration.getNameAsString().contains("unidoc.annotations"))
                .forEach(importDeclaration -> finalImportList.add(importDeclaration));
        compilationUnit.getImports().clear();
        for (ImportDeclaration importinList : finalImportList) {
            compilationUnit.addImport(importinList);
        }
        File newFile = new File(compilationUnit.getStorage().get().getFileName());
        FileOutputStream fileOutputStream = null;
        if (newFile.createNewFile()) {
            try {
                fileOutputStream = new FileOutputStream(newFile);
                //fileOutputStream = new FileOutputStream(new File(compilationUnit.getStorage().get().getPath().toString()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assert fileOutputStream != null;
                fileOutputStream.write(compilationUnit.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
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
                } else {
                    log.info("default access used -> -protected");
                }
            }
        }

        //File file = new File(compilationUnit.getStorage().get().getPath().toString());

        DocumentationTool tool = ToolProvider.getSystemDocumentationTool();

        StandardJavaFileManager fileManager = tool.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> javadocCompilationUnit = fileManager.getJavaFileObjects(newFile);

        DocumentationTool.DocumentationTask task = tool.getTask(null, fileManager, null, null, options, javadocCompilationUnit);
        task.call();

        newFile.delete();

        switch (options.size()) {
            //when an access option is used in absence of -sourcepath
            case 3:
                options.remove(2);
                break;
                //when no access option is used
            case 4:
                options.remove(3);
                break;
                //when an access option is used
            case 5:
                options.remove(4);
                options.remove(3);
                break;
        }
    }


    /**
     *
     * parses class cli arg
     *
     * @param opts options list with -d and its' value
     * @param compilationUnit class to document
     * @param privateRequested was -private used
     * @param publicRequested was -public used
     * @param packageRequested was -package used
     */
    public void classGenParser(List<String> opts, CompilationUnit compilationUnit, boolean privateRequested, boolean publicRequested, boolean packageRequested) throws IOException {

        LogSetter logSetter = new LogSetter();
        logSetter.setLog(log);
        List<String> options = opts;

        VoidVisitorAdapter<Void> declarations = new ClassParser();
        declarations.visit(compilationUnit, null);
        declarations = new PackageParser();
        declarations.visit(compilationUnit, null);
        declarations = new InterfaceParser();
        declarations.visit(compilationUnit, null);
        declarations = new EnumParser();
        declarations.visit(compilationUnit, null);
        declarations = new AnnotationParser();
        declarations.visit(compilationUnit, null);
        declarations = new AnnotationMemberParser();
        declarations.visit(compilationUnit, null);
        declarations = new ConstructorParser();
        declarations.visit(compilationUnit, null);
        declarations = new FieldParser();
        declarations.visit(compilationUnit, null); // field declaration
        declarations = new MethodParser();
        declarations.visit(compilationUnit, null); // method declaration

        List<ImportDeclaration> finalImportList = new ArrayList<>();
        NodeList<ImportDeclaration> importDeclarations = compilationUnit.getImports();
        importDeclarations.stream()
                .filter(importDeclaration -> !importDeclaration.getNameAsString().contains("unidoc.annotations"))
                .forEach(importDeclaration -> finalImportList.add(importDeclaration));
        compilationUnit.getImports().clear();
        for (ImportDeclaration importinList : finalImportList) {
            compilationUnit.addImport(importinList);
        }

        System.out.println(compilationUnit.getStorage().get().getFileName());
        File newFile = new File(compilationUnit.getStorage().get().getFileName());
        System.out.println(newFile.getAbsolutePath());
        FileOutputStream fileOutputStream = null;

        if (newFile.createNewFile()) {
            try {
                fileOutputStream = new FileOutputStream(newFile);
                //fileOutputStream = new FileOutputStream(new File(compilationUnit.getStorage().get().getPath().toString()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assert fileOutputStream != null;
                fileOutputStream.write(compilationUnit.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
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
                } else {
                    log.info("default access used -> -protected");
                }
            }
        }

        //File file = new File(compilationUnit.getStorage().get().getPath().toString());

        DocumentationTool tool = ToolProvider.getSystemDocumentationTool();

        StandardJavaFileManager fileManager = tool.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> javadocCompilationUnit = fileManager.getJavaFileObjects(newFile);
        DocumentationTool.DocumentationTask task = tool.getTask(null, fileManager, null, null, options, javadocCompilationUnit);
        task.call();
        newFile.delete();
    }
}
