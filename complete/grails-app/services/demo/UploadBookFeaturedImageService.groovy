package demo

import org.grails.plugins.googlecloud.storage.GoogleCloudStorageService
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import groovy.util.logging.Slf4j
import groovy.transform.CompileStatic

@Slf4j
@CompileStatic
class UploadBookFeaturedImageService {

    BookGormService bookGormService

    GoogleCloudStorageService googleCloudStorageService

    private static String fileSuffix() {
        DateTimeFormatter dtf = DateTimeFormat.forPattern('-YYYY-MM-dd-HHmmssSSS')
        DateTime dt = DateTime.now(DateTimeZone.UTC)
        dt.toString(dtf)
    }

    Book uploadFeaturedImage(FeaturedImageCommand cmd) {
        String fileName = "${cmd.featuredImageFile.originalFilename}${fileSuffix()}"

        log.info "cloud storage file name $fileName"

        String fileUrl = googleCloudStorageService.storeMultipartFile(fileName, cmd.featuredImageFile)

        log.info "cloud storage media url $fileUrl"

        def book = bookGormService.updateFeaturedImageUrl(cmd.id, cmd.version, fileName, fileUrl)
        if ( !book || book.hasErrors() ) {
            googleCloudStorageService.deleteFile(fileName)
        }
        book
    }
}
