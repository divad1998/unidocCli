package parse;

import annotations.ConstructorDoc;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import core.ConstructorDocumentation;
import formatter.LogFormatter;
import formatter.LogSetter;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;

/**
 *
 * has method for setting java doc comments and removing @ConstructorDoc
 */
public class ConstructorParser extends VoidVisitorAdapter<Void> {

    // logger
    private final Logger log = Logger.getLogger(this.getClass().getName());

    /**
     * sets java doc comments generated from unidoc @ConstructorDoc annotation.
     * Also removes @ConstructorDoc annotations from source code
     *
     * @param cd constructor to be accessed
     * @param arg void
     */
    public void visit(ConstructorDeclaration cd, Void arg) {
        super.visit(cd, arg);
        if (cd.isAnnotationPresent(ConstructorDoc.class)) {
            ConstructorDocumentation constructorDocumentation = new ConstructorDocumentation(cd);
            cd.setJavadocComment(constructorDocumentation.getJavadoc());
            cd.getAnnotationByClass(ConstructorDoc.class).ifPresent(Node::remove);
        } else {
            LogSetter logSetter = new LogSetter();
            logSetter.setLog(log);
            log.info(cd.getNameAsString() + "() is not annotated with @" + ConstructorDoc.class.getSimpleName());
        }
    }
}
