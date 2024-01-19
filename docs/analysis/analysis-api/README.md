# Core features

---

## Symbols

Symbols are used to retrieve different types of semantic information about PSI elements.

* visibility - [visibility modifiers](https://kotlinlang.org/docs/visibility-modifiers# Core features

---

## Symbols

Symbols are used to retrieve different types of semantic information about PSI elements.

* visibility - [visibility modifiers](https://kotlinlang.org/docs/visibility-modifiers.html) information of the corresponding declaration.
* modality - when and where it is possible to extend/override a class/member.

```
open class Parent {
    protected open val name = "parent"      // parentProp: KtProperty
}

class Child : Parent() {
    public override val name = "child"      // childProp: KtProperty
}

parentProp.getSymbolOfType<KtKotlinPropertySymbol>().modality   // OPEN
parentProp.getSymbolOfType<KtKotlinPropertySymbol>().visibility // protected

childProp.getSymbolOfType<KtKotlinPropertySymbol>().isOverride  // true
childProp.getSymbolOfType<KtKotlinPropertySymbol>().visibility  // public
```

## Calls

Calls are used to understand when and where a function call was resolved, what arguments the function has.

```
class Human {
    val name = "Heisenberg"                 // nameProp: KtProperty

    fun sayMyName(): String {
        return name                         // nameRef: KtReferenceExpression
    }
}

```

## Types

## Scopes.html) information of the corresponding declaration.
* modality - when and where it is possible to extend/override a class/member.

```
open class Parent {
    protected open val name = "parent"      // parentProp: KtProperty
}

class Child : Parent() {
    public override val name = "child"      // childProp: KtProperty
}

parentProp.getSymbolOfType<KtKotlinPropertySymbol>().modality   // OPEN
parentProp.getSymbolOfType<KtKotlinPropertySymbol>().visibility // protected

childProp.getSymbolOfType<KtKotlinPropertySymbol>().isOverride  // true
childProp.getSymbolOfType<KtKotlinPropertySymbol>().visibility  // public
```

## Calls

Calls are used to understand when and where a function call was resolved, what arguments the function has.

```
class Human {
    val name = "Heisenberg"                 // nameProp: KtProperty

    fun sayMyName(): String {
        return name                         // nameRef: KtReferenceExpression
    }
}


```

## Types

## Scopes