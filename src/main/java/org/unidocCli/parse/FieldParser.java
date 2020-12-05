package org.unidocCli.parse;

import org.unidocCli.annotations.FieldDoc;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.unidocCli.core.FieldDocumentation;
import org.unidocCli.formatter.LogSetter;

import java.util.Optional;
import java.util.logging.Logger;

/**
 *
 * has method for setting java doc comments and removing @FieldDoc
 */
public class FieldParser extends VoidVisitorAdapter<Void> {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    /**
     * sets java doc comments generated from unidoc @FieldDoc annotation.
     * Also removes @FieldDoc org.unidocCli.annotations from source code
     *
     * @param fd field to be assessed
     * @param arg void
     */
    @Override
    public void visit(FieldDeclaration fd, Void arg) {
        super.visit(fd, arg);
        Optional<AnnotationExpr> expr = fd.getAnnotationByClass(FieldDoc.class);
        if (expr.isPresent()) {
            FieldDocumentation fieldDocumentation = new FieldDocumentation(fd);
            fd.setJavadocComment(fieldDocumentation.javadoc());
            expr.get().remove();
        } else {
            fd.getVariables().forEach(variableDeclarator -> {
                LogSetter logSetter = new LogSetter();
                logSetter.setLog(log);
                log.info("Variable: " + variableDeclarator.getNameAsString() + ", is not annotated with @" + FieldDoc.class.getSimpleName());
            });
        }
    }
}
