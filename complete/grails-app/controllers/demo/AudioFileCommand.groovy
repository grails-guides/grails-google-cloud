package demo

import grails.validation.Validateable

import org.springframework.web.multipart.MultipartFile

class AudioFileCommand implements Validateable {
    MultipartFile audioFile

    static constraints = {
        audioFile  validator: { MultipartFile val, AudioFileCommand obj ->
            if ( val == null ) {
                return false
            }
            if ( val.empty ) {
                return false
            }

            audioFileExtensions().any { extension ->
                val.originalFilename?.toLowerCase()?.endsWith(extension)
            }
        }
    }

    static audioFileExtensions() {
        ['wav', 'mp3', 'raw']
    }
}
