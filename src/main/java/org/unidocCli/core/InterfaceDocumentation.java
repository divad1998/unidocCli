package org.unidocCli.core;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.description.JavadocDescription;
import org.unidocCli.annotations.InterfaceDoc;
import org.unidocCli.blockTagSetter.JavadocBlocktagSetter;

import java.util.Optional;

/**
 * calls and passes required parameters to methods that transform @InterfaceDoc org.unidocCli.annotations to java doc comments
 */
public class InterfaceDocumentation {

    private NodeList<MemberValuePair> pairs;

    private Javadoc javadoc;

    private JavadocBlocktagSetter javadocBlocktagSetter = new JavadocBlocktagSetter();

    private ClassOrInterfaceDeclaration id;

    private AnnotationExpr annotationExpr;

    /**
     * assigns value to pairs
     * @param id interface declaration
     */
    public InterfaceDocumentation(ClassOrInterfaceDeclaration id) {
        this.id = id;
        Optional<AnnotationExpr> annotationByClass = id.getAnnotationByClass(InterfaceDoc.class);
        annotationByClass.ifPresent(annotationExpr -> {
            this.pairs = annotationExpr.asNormalAnnotationExpr().getPairs();
            this.annotationExpr = annotationExpr;
        });
    }

    /**
     * sets description of class
     * @return javadoc description
     */
    private JavadocDescription description() {
        return javadocBlocktagSetter.setDescription(pairs);
    }

    /**
     * sets javadoc @author tag
     */
    public void authorTag() {
        javadocBlocktagSetter.setAuthorTag(javadoc, pairs);
    }

    /**
     * sets javadoc @version tag
     */
    public void versionTag() {
        javadocBlocktagSetter.setVersionTag(javadoc, pairs);
    }

    /**
     * sets javadoc @param tag.
     */
    private void paramTag() {
        javadocBlocktagSetter.setInterfaceParamTag(id, annotationExpr, javadoc);
    }

    /**
     * sets javadoc @see tag
     */
    public void seeTag() {
        javadocBlocktagSetter.setSeeTag(javadoc, pairs);
    }

    /**
     * sets javadoc @since tag
     */
    public void sinceTag() {
        javadocBlocktagSetter.setSinceTag(javadoc, pairs);
    }

    /**
     * sets javadoc @serial tag
     */
    private void serialFieldTag() {
        javadocBlocktagSetter.setSerialFieldTag(javadoc, pairs);
    }

    /**
     * sets javadoc @hidden tag
     */
    private void hiddenTag() {
        javadocBlocktagSetter.setHiddenTag(javadoc, pairs);
    }

    /**
     * sets javadoc @deprecated tag
     */
    private void deprecatedTag() {
        javadocBlocktagSetter.setDeprecatedTag(javadoc, pairs);
    }

    /**
     * calls methods for setting javadoc comments.
     * @return javadoc
     */
    public Javadoc getJavadoc() {
        javadoc = new Javadoc(description());
        authorTag();
        versionTag();
        paramTag();
        seeTag();
        sinceTag();
        serialFieldTag();
        hiddenTag();
        deprecatedTag();
        return javadoc;
    }
}
