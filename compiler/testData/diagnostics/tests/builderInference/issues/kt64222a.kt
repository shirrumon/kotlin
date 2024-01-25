// ISSUE: KT-64222
// FIR_IDENTICAL
// CHECK_TYPE_WITH_EXACT

fun test() {
    val buildee = build {
        nullableTargetTypeBuildeeVariable = this
    }
    // exact type equality check — turns unexpected compile-time behavior into red code
    // considered to be non-user-reproducible code for the purposes of these tests
    checkExactType<Buildee<TargetType>>(buildee)
}




class TargetType

var nullableTargetTypeBuildeeVariable: Buildee<TargetType>? = null

class Buildee<TV>

fun <PTV> build(instructions: Buildee<PTV>.() -> Unit): Buildee<PTV> {
    return Buildee<PTV>().apply(instructions)
}
