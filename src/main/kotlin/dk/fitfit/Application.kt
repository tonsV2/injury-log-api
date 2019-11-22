package dk.fitfit

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("dk.fitfit")
                .mainClass(Application.javaClass)
                .start()
    }
}