package org.unidocCli.parse;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.unidocCli.annotations.ClassDoc;
import org.unidocCli.core.ClassDocumentation;
import org.unidocCli.formatter.LogSetter;

import java.util.Optional;
import java.util.logging.Logger;

/**
 *
 * has method for setting java doc comments and removing @ClassDoc
 */

public class ClassParser extends VoidVisitorAdapter<Void> {

    private Logger log = Logger.getLogger(this.getClass().getName());





    /**
     * sets java doc comments generated from unidoc @ClassDoc annotation.
     * Also removes @ClassDoc org.unidocCli.annotations from source code
     *
     * @param cd class to be accessed
     * @param arg void
     */
    @Override
    public void visit(ClassOrInterfaceDeclaration cd, Void arg) {
        super.visit(cd, arg);
        Optional<AnnotationExpr> expr = cd.getAnnotationByClass(ClassDoc.class);
        if (expr.isPresent()) {
            ClassDocumentation classDocumentation = new ClassDocumentation(cd);
            cd.setJavadocComment(classDocumentation.getJavadoc());
            expr.get().remove();
        } else {

            LogSetter logSetter = new LogSetter();
            logSetter.setLog(log);
            log.info(cd.getNameAsString() + ", is not annotated with @" + ClassDoc.class.getSimpleName());
        }
    }
}
