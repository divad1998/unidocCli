package parse;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import annotations.AnnotationDoc;
import core.AnnotationDocumentation;
import formatter.LogFormatter;
import formatter.LogSetter;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;

/**
 *
 * has method for setting java doc comments and removing @AnnotationDoc
 */
public class AnnotationParser extends VoidVisitorAdapter<Void> {

    private Logger log = Logger.getLogger(this.getClass().getName());

    /**
     * sets java doc comments generated from unidoc @AnnotationDoc annotation.
     * Also removes @AnnotationDoc annotations from source code
     *
     * @param ad annotation to be accessed
     * @param arg void
     */
    @Override
    public void visit(AnnotationDeclaration ad, Void arg) {
        super.visit(ad, arg);
        if (ad.isAnnotationPresent(AnnotationDoc.class)) {
            AnnotationDocumentation annotationDocumentation = new AnnotationDocumentation(ad);
            ad.setJavadocComment(annotationDocumentation.getJavadoc());
            ad.getAnnotationByClass(AnnotationDoc.class).ifPresent(Node::remove);
        } else {
            LogSetter logSetter = new LogSetter();
            logSetter.setLog(log);
            log.info(ad.getNameAsString() + " is not annotated with @" + AnnotationDoc.class.getSimpleName());
        }
    }
}
