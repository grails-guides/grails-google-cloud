package demo

class BigTableController {

    AppEngineService appEngineService

    def index() {
        String result = appEngineService.doHelloWorld()
        render(result)
    }
}
