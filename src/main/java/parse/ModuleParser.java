package parse;

import annotations.ModuleDoc;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.modules.ModuleDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import core.ModuleDocumentation;
import formatter.LogFormatter;
import formatter.LogSetter;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;

/**
 *
 * has method for setting java doc comments and removing @ModuleDoc
 */

public class ModuleParser extends VoidVisitorAdapter<Void> {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    /**
     * sets java doc comments generated from unidoc @ModuleDoc annotation.
     * Also removes @ModuleDoc annotations from source code
     *
     * @param mdd module to be assessed
     * @param arg void
     */
    @Override
    public void visit(ModuleDeclaration mdd, Void arg) {
        super.visit(mdd, arg);
        if (mdd.isAnnotationPresent(ModuleDoc.class)) {
            ModuleDocumentation moduleDocumentation = new ModuleDocumentation(mdd);
            mdd.setComment(moduleDocumentation.getJavadoc());
            mdd.getAnnotationByClass(ModuleDoc.class).ifPresent(Node::remove);
        } else {
            LogSetter logSetter = new LogSetter();
            logSetter.setLog(log);
            log.info(mdd.getNameAsString() + " is not annotated with @" + ModuleDoc.class.getSimpleName());
        }
    }
}
