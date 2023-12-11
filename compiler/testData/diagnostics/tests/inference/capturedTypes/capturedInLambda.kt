// FIR_IDENTICAL
// FILE: StubBasedPsiElement.java
public interface StubBasedPsiElement<Stub extends StubElement> extends PsiElement {
    Stub getStub();
}

// FILE: StubElement.java
public interface StubElement<T extends PsiElement> {
    <E extends PsiElement> E @org.jetbrains.annotations.NotNull [] getChildrenByType(final E[] array);
}

// FILE: KtStringTemplateExpression.java
public class KtStringTemplateExpression implements PsiElement {}

// FILE: test.kt
interface PsiElement

val STRING_TEMPLATE_EMPTY_ARRAY = emptyArray<KtStringTemplateExpression>()

fun test(e: StubBasedPsiElement<*>): Array<KtStringTemplateExpression>? {
    e.stub?.let {
        return it.getChildrenByType(STRING_TEMPLATE_EMPTY_ARRAY)
    }
    return null
}