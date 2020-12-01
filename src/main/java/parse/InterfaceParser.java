package parse;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import core.InterfaceDocumentation;
import annotations.InterfaceDoc;
import core.InterfaceDocumentation;
import formatter.LogFormatter;
import formatter.LogSetter;

import java.util.Optional;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;

/**
 *
 * has method for setting java doc comments and removing @InterfaceDoc
 */
public class InterfaceParser extends VoidVisitorAdapter<Void> {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    /**
     * sets java doc comments generated from unidoc @InterfaceDoc annotation.
     * Also removes @InterfaceDoc annotations from source code
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
