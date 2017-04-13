package demo

import com.google.cloud.storage.Acl
import com.google.cloud.storage.BlobId
import groovy.transform.CompileStatic


import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import org.springframework.web.multipart.MultipartFile

@CompileStatic
class GoogleCloudStorageService implements GrailsConfigurationAware {
    // Google Cloud Platform project ID.
    String projectId

    // Cloud Storage Bucket
    String bucket

    Storage storage = StorageOptions.getDefaultInstance().getService()

    String storeMultipartFile(String fileName, MultipartFile multipartFile) {
        BlobInfo blobInfo =
                storage.create(
                        BlobInfo.newBuilder(bucket, fileName)
                        // Modify access list to allow all users with link to read file
                        .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
                                .build(),
                        multipartFile.inputStream)
        blobInfo.mediaLink
    }

    boolean deleteFile(String fileName) {
        BlobId blobId = BlobId.of(bucket, fileName)
        storage.delete(blobId)
    }

    @Override
    void setConfiguration(Config co) {
        projectId = co.getRequiredProperty('googlecloud.projectid', String)
        bucket = co.getProperty('googlecloud.cloudStorage.bucket', String, projectId)
    }
}
