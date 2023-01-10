// TARGET_BACKEND: JVM
// FULL_JDK
// WITH_STDLIB
// WITH_COROUTINES
// CHECK_TAIL_CALL_OPTIMIZATION
import helpers.*
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.*

fun suspendThere(v: String): suspend () -> String = {
    suspendCoroutineUninterceptedOrReturn { x ->
        TailCallOptimizationChecker.saveStackTrace(x)
        x.resume(v)
        COROUTINE_SUSPENDED
    }
}

fun builder(c: suspend () -> Unit) {
    c.startCoroutine(EmptyContinuation)
}

fun box(): String {
    var result = ""

    builder {
        result = suspendThere("OK")()
    }
    // TODO: Update tail-call optimization for suspend lambdas after IR inliner is enabled.
    TailCallOptimizationChecker.checkStateMachineIn("invokeSuspend", "suspendThere$1")
    TailCallOptimizationChecker.checkNoStateMachineIn("invoke", "suspendThere$1")

    return result
}
