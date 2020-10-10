package spotifyweb

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class AlbumSimplifiedServiceSpec extends Specification {

    AlbumSimplifiedService albumSimplifiedService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new AlbumSimplified(...).save(flush: true, failOnError: true)
        //new AlbumSimplified(...).save(flush: true, failOnError: true)
        //AlbumSimplified albumSimplified = new AlbumSimplified(...).save(flush: true, failOnError: true)
        //new AlbumSimplified(...).save(flush: true, failOnError: true)
        //new AlbumSimplified(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //albumSimplified.id
    }

    void "test get"() {
        setupData()

        expect:
        albumSimplifiedService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<AlbumSimplified> albumSimplifiedList = albumSimplifiedService.list(max: 2, offset: 2)

        then:
        albumSimplifiedList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        albumSimplifiedService.count() == 5
    }

    void "test delete"() {
        Long albumSimplifiedId = setupData()

        expect:
        albumSimplifiedService.count() == 5

        when:
        albumSimplifiedService.delete(albumSimplifiedId)
        sessionFactory.currentSession.flush()

        then:
        albumSimplifiedService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        AlbumSimplified albumSimplified = new AlbumSimplified()
        albumSimplifiedService.save(albumSimplified)

        then:
        albumSimplified.id != null
    }
}
