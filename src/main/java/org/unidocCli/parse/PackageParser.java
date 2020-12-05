package org.unidocCli.parse;

import org.unidocCli.annotations.PackageDoc;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.unidocCli.core.PackageDocumentation;
import org.unidocCli.formatter.LogSetter;

import java.util.logging.Logger;

/**
 *
 * has method for setting java doc comments and removing @PackageDoc
 */
public class PackageParser extends VoidVisitorAdapter<Void> {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    /**
     * sets java doc comments generated from unidoc @PackageDoc annotation.
     * Also removes @PackageDoc org.unidocCli.annotations from source code
     *
     * @param pd package to be assessed
     * @param arg void
     */
    @Override
    public void visit(PackageDeclaration pd, Void arg) {
        super.visit(pd, arg);
        PackageDocumentation packageDocumentation = new PackageDocumentation(pd);
        if (pd.isAnnotationPresent(PackageDoc.class)) {
            pd.setComment(packageDocumentation.getJavadoc());
            pd.getAnnotationByClass(PackageDoc.class).ifPresent(Node::remove);
        } else {
            LogSetter logSetter = new LogSetter();
            logSetter.setLog(log);
            log.info(pd.getNameAsString() + " is not annotated with @" + PackageDoc.class.getSimpleName());
        }
    }
}
