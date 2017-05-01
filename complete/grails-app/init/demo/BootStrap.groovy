package demo

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class BootStrap {

    def init = { servletContext ->
        Book.withTransaction {
            [
                    'Grails 3: A Practical Guide to Application Development',
                    'Falando de Grails',
                    'The Definitive Guide to Grails 2',
                    'Grails in Action',
                    'Grails 2: A Quick-Start Guide',
                    'Programming Grails',
            ].each {
                new Book(name: it).save()
            }
        }
    }
    def destroy = {
    }
}
