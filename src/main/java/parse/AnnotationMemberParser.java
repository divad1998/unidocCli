package parse;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import annotations.AnnotationMemberDoc;
import core.AnnotationMemberDocumentation;
import formatter.LogFormatter;
import formatter.LogSetter;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;

/**
 *
 * has method for setting java doc comments and removing @AnnotationMemberDoc
 */
public class AnnotationMemberParser extends VoidVisitorAdapter<Void> {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    /**
     * sets java doc comments generated from unidoc @AnnotationMemberDoc annotation.
     * Also removes @AnnotationMemberDoc annotations from source code
     *
     * @param aed annotationMember to be assessed
     * @param arg void
     */
    @Override
    public void visit(AnnotationMemberDeclaration aed, Void arg) {
        super.visit(aed, arg);
        if (aed.isAnnotationPresent(AnnotationMemberDoc.class)) {
            AnnotationMemberDocumentation annotationMemberDocumentation = new AnnotationMemberDocumentation(aed);
            aed.setJavadocComment(annotationMemberDocumentation.getJavadoc());
            aed.getAnnotationByClass(AnnotationMemberDoc.class).ifPresent(Node::remove);
        } else {
            LogSetter logSetter = new LogSetter();
            logSetter.setLog(log);
            log.info(aed.getNameAsString() + " is not annotated with @" + AnnotationMemberDoc.class.getSimpleName());
        }
    }
}
