package demo

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        // tag::homeUrlMapping[]
        "/"(controller: 'book')
        // end::homeUrlMapping[]
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
