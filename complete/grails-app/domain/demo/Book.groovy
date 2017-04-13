package demo

import groovy.transform.CompileStatic

@CompileStatic
class Book {
    String name
    static constraints = {
        name unique: true
    }
}
