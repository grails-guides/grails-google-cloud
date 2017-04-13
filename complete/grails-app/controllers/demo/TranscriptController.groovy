package demo

import groovy.transform.CompileStatic
import org.grails.plugins.googlecloud.speech.GoogleCloudSpeechService

@CompileStatic
class TranscriptController {

    static allowedMethods = [index: 'GET', updloadAudio: 'POST']

    GoogleCloudSpeechService googleCloudSpeechService

    def index() {

    }

    def updloadAudio(AudioFileCommand cmd) {

        if (cmd.hasErrors()) {
            respond(cmd.errors, view: 'index')
            return
        }


        googleCloudSpeechService.transcriptBytes(cmd.audioFile.bytes)


        redirect(controller: 'transcript', action: 'index')
    }
}