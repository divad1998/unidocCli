package org.unidocCli.parse;

import org.unidocCli.annotations.MethodDoc;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.unidocCli.core.MethodDocumentation;
import org.unidocCli.formatter.LogSetter;

import java.util.logging.Logger;

/**
 *
 * has method for setting java doc comments and removing @MethodDoc
 */
public class MethodParser extends VoidVisitorAdapter<Void> {

    // logger
    private final Logger log = Logger.getLogger(this.getClass().getName());

    /**
     * sets java doc comments generated from unidoc @MethodDoc annotation.
     * Also removes @MethodDoc org.unidocCli.annotations from source code
     *
     * @param md Method to be assessed
     * @param arg void
     */
    public void visit(MethodDeclaration md, Void arg) {
        super.visit(md, arg);
        if (md.isAnnotationPresent(MethodDoc.class)) {
            // check if method has @MethodDoc annotation
            MethodDocumentation methodDocumentation = new MethodDocumentation(md);
            md.setJavadocComment(methodDocumentation.getJavadoc());
            md.getAnnotationByClass(MethodDoc.class).ifPresent(Node::remove);
        } else {
            LogSetter logSetter = new LogSetter();
            logSetter.setLog(log);
            log.info(md.getNameAsString() + "() is not annotated with @" + MethodDoc.class.getSimpleName());
        }
    }
}
