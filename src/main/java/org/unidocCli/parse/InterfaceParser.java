package org.unidocCli.parse;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.unidocCli.core.InterfaceDocumentation;
import org.unidocCli.annotations.InterfaceDoc;
import org.unidocCli.formatter.LogSetter;

import java.util.Optional;
import java.util.logging.Logger;

/**
 *
 * has method for setting java doc comments and removing @InterfaceDoc
 */
public class InterfaceParser extends VoidVisitorAdapter<Void> {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    /**
     * sets java doc comments generated from unidoc @InterfaceDoc annotation.
     * Also removes @InterfaceDoc org.unidocCli.annotations from source code
     *
     * @param id interface to be assessed
     * @param arg void
     */
    @Override
    public void visit(ClassOrInterfaceDeclaration id, Void arg) {
        super.visit(id, arg);
        Optional<AnnotationExpr> expr = id.getAnnotationByClass(InterfaceDoc.class);
        if (expr.isPresent()) {
            InterfaceDocumentation interfaceDocumentation = new InterfaceDocumentation(id);
            id.setJavadocComment(interfaceDocumentation.getJavadoc());
            expr.get().remove();
        } else {
            LogSetter logSetter = new LogSetter();
            logSetter.setLog(log);
            log.info(id.getNameAsString() + ", is not annotated with @" + InterfaceDoc.class.getSimpleName());
        }
    }
}
