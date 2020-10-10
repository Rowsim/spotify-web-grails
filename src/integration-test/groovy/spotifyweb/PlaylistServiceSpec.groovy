package spotifyweb

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class PlaylistServiceSpec extends Specification {

    PlaylistService playlistService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Playlist(...).save(flush: true, failOnError: true)
        //new Playlist(...).save(flush: true, failOnError: true)
        //Playlist playlist = new Playlist(...).save(flush: true, failOnError: true)
        //new Playlist(...).save(flush: true, failOnError: true)
        //new Playlist(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //playlist.spotifyPlaylistId
    }

    void "test get"() {
        setupData()

        expect:
        playlistService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Playlist> playlistList = playlistService.list(max: 2, offset: 2)

        then:
        playlistList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        playlistService.count() == 5
    }

    void "test delete"() {
        Long playlistId = setupData()

        expect:
        playlistService.count() == 5

        when:
        playlistService.delete(playlistId)
        sessionFactory.currentSession.flush()

        then:
        playlistService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Playlist playlist = new Playlist()
        playlistService.save(playlist)

        then:
        playlist.spotifyPlaylistId != null
    }
}
