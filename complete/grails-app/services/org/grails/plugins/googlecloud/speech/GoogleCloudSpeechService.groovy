package org.grails.plugins.googlecloud.speech

import com.google.cloud.speech.spi.v1.SpeechClient;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;
import groovy.transform.CompileStatic

@CompileStatic
class GoogleCloudSpeechService {

    SpeechClient speech = SpeechClient.create()

    void transcriptBytes(byte[] data, String languageCode = 'en-US') {
        ByteString audioBytes = ByteString.copyFrom(data);

        // Builds the sync recognize request
        RecognitionConfig config =  RecognitionConfig.newBuilder()
                .setEncoding(AudioEncoding.LINEAR16)
                .setSampleRateHertz(16000)
                .setLanguageCode(languageCode)
                .build()

        RecognitionAudio audio = RecognitionAudio.newBuilder()
                .setContent(audioBytes)
                .build()

        // Performs speech recognition on the audio file
        RecognizeResponse response = speech.recognize(config, audio)
        List<SpeechRecognitionResult> results = response.resultsList

        log.info "Resulsts ${results.size()}"

        for (SpeechRecognitionResult result: results) {
            List<SpeechRecognitionAlternative> alternatives = result.alternativesList
            for (SpeechRecognitionAlternative alternative: alternatives) {
                log.info "Transcription: ${alternative.transcript}"
            }
        }
        speech.close()
    }
}