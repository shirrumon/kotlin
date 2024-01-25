// ISSUE: KT-64222

interface BaseTest {
    fun test(): Buildee<TargetType>
}

class DerivedTest: BaseTest {
    // subtyping check (Buildee<TypeVariable(PTV)> <: Buildee<TargetType>)
    // in context of an override with implicit return type
    override fun test() = build {
        nullableTargetTypeBuildeeVariable = this
    }
}

fun box(): String {
    DerivedTest().test()
    return "OK"
}




class TargetType

var nullableTargetTypeBuildeeVariable: Buildee<TargetType>? = null

class Buildee<TV>

fun <PTV> build(instructions: Buildee<PTV>.() -> Unit): Buildee<PTV> {
    return Buildee<PTV>().apply(instructions)
}
