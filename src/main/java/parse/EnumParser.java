package parse;

import annotations.EnumDoc;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import core.EnumDocumentation;
import formatter.LogFormatter;
import formatter.LogSetter;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;

/**
 *
 * has method for setting java doc comments and removing @EnumDoc
 */
public class EnumParser extends VoidVisitorAdapter<Void> {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    /**
     * sets java doc comments generated from unidoc @EnumDoc annotation.
     * Also removes @EnumDoc annotations from source code
     *
     * @param ed enum to be assessed
     * @param arg void
     */
    @Override
    public void visit(EnumDeclaration ed, Void arg) {
        super.visit(ed, arg);
        if (ed.isAnnotationPresent(EnumDoc.class)) {
            EnumDocumentation enumDocumentation = new EnumDocumentation(ed);
            ed.setJavadocComment(enumDocumentation.getJavadoc());
            ed.getAnnotationByClass(EnumDoc.class).ifPresent(Node::remove);
        } else {
            LogSetter logSetter = new LogSetter();
            logSetter.setLog(log);
            log.info(ed.getNameAsString() + " is not annotated with @" + EnumDoc.class.getSimpleName());
        }
    }
}
