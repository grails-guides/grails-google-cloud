package demo

import groovy.util.logging.Slf4j

@Slf4j
class LegalController {

    def index() {
	log.info "inside legal controller"
	render "Legal Terms" 
    }
}
