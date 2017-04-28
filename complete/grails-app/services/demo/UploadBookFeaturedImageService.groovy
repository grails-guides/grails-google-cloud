package demo

import grails.transaction.Transactional
import groovy.transform.CompileStatic
import org.grails.plugins.googlecloud.storage.GoogleCloudStorageService
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
@Transactional
class UploadBookFeaturedImageService {

    BookGormService bookGormService

    GoogleCloudStorageService googleCloudStorageService

    Book uploadFeaturedImage(FeaturedImageCommand cmd) {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("-YYYY-MM-dd-HHmmssSSS");
        DateTime dt = DateTime.now(DateTimeZone.UTC);
        String dtString = dt.toString(dtf);
        final String fileName = cmd.featuredImageFile.originalFilename + dtString;

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
