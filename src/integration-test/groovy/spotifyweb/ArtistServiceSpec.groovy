package spotifyweb

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class ArtistServiceSpec extends Specification {

    ArtistService artistService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Artist(...).save(flush: true, failOnError: true)
        //new Artist(...).save(flush: true, failOnError: true)
        //Artist artist = new Artist(...).save(flush: true, failOnError: true)
        //new Artist(...).save(flush: true, failOnError: true)
        //new Artist(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //artist.id
    }

    void "test get"() {
        setupData()

        expect:
        artistService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Artist> artistList = artistService.list(max: 2, offset: 2)

        then:
        artistList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        artistService.count() == 5
    }

    void "test delete"() {
        Long artistId = setupData()

        expect:
        artistService.count() == 5

        when:
        artistService.delete(artistId)
        sessionFactory.currentSession.flush()

        then:
        artistService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Artist artist = new Artist()
        artistService.save(artist)

        then:
        artist.id != null
    }
}
