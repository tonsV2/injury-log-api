package dk.fitfit.injurylog

import io.micronaut.runtime.Micronaut

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("dk.fitfit.injurylog")
                .mainClass(Application.javaClass)
                .start()
    }
}
