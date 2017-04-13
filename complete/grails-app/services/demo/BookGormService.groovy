package demo

import grails.transaction.Transactional
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

@Transactional
@CompileStatic
class BookGormService {

    @Transactional(readOnly = true)
    List list(Map params) {
        [Book.list(params), Book.count()]
    }

    Book updateFeaturedImageUrl(Long id, Long version, String fileName, String featuredImageUrl) {
        Book book = Book.get(id)
        if ( !book ) {
            return null
        }
        book.version = version
        book.fileName = fileName
        book.featuredImageUrl = featuredImageUrl
        book.save()
    }

    @CompileDynamic
    Book save(NameCommand cmd) {
        def book = new Book()
        book.properties = cmd.properties
        book.save()
    }

    @CompileDynamic
    Book update(NameUpdateCommand cmd) {
        Book book = Book.get(cmd.id)
        book.properties = cmd.properties
        book.save()
    }

    void deleteById(Long bookId) {
        def book = Book.get(bookId)
        book?.delete()
    }
}
