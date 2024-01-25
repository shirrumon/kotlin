// WITH_STDLIB

// IGNORE_BACKEND_K2: JVM_IR
// FIR status:
//   1) KT-57268 K2: extra methods `remove` and/or `getOrDefault` are generated for Map subclasses with JDK 1.6 in dependencies

abstract class AMapSD : AbstractMap<String, Double>()

abstract class AMMapSD : AbstractMutableMap<String, Double>()
