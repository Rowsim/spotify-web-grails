package spotifyweb

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class TrackServiceSpec extends Specification {

    TrackService trackService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Track(...).save(flush: true, failOnError: true)
        //new Track(...).save(flush: true, failOnError: true)
        //Track track = new Track(...).save(flush: true, failOnError: true)
        //new Track(...).save(flush: true, failOnError: true)
        //new Track(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //track.spotifyPlaylistId
    }

    void "test get"() {
        setupData()

        expect:
        trackService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Track> trackList = trackService.list(max: 2, offset: 2)

        then:
        trackList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        trackService.count() == 5
    }

    void "test delete"() {
        Long trackId = setupData()

        expect:
        trackService.count() == 5

        when:
        trackService.delete(trackId)
        sessionFactory.currentSession.flush()

        then:
        trackService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Track track = new Track()
        trackService.save(track)

        then:
        track.spotifyId != null
    }
}
