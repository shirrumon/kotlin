// ISSUE: KT-64222

fun box(): String {
    build {
        nullableTargetTypeBuildeeVariable = this
    }
    return "OK"
}




class TargetType

var nullableTargetTypeBuildeeVariable: Buildee<TargetType>? = null

class Buildee<TV>

fun <PTV> build(instructions: Buildee<PTV>.() -> Unit): Buildee<PTV> {
    return Buildee<PTV>().apply(instructions)
}
