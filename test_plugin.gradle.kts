project(":data") {
    afterEvaluate {
        println("Plugins applied to :data:")
        plugins.forEach { println(" - ${it.javaClass.name}") }
    }
}
