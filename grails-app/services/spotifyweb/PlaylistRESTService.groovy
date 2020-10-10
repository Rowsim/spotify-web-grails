package spotifyweb

import grails.gorm.transactions.Transactional
import grails.plugins.rest.client.RestBuilder
import org.springframework.http.HttpStatus

@Transactional
class PlaylistRESTService {

    TrackRESTService trackRESTService

    Playlist[] getCurrentUserPlaylists(String authKey) {
        RestBuilder rest = new RestBuilder()
        def response = rest.get('https://api.spotify.com/v1/me/playlists') {
            header('Authorization', 'Bearer ' + authKey)
        }

        if (response.statusCode == HttpStatus.OK) {
            def responseJson = response.getJson()
            def playlistObjects = responseJson.getAt('items')

            List<Playlist> playlistList = new ArrayList()
            playlistObjects.eachWithIndex { key, value ->
                def tracksLink = key.getAt('tracks').getAt('href')
                def image = key.getAt('images')[0].getAt('url')
                if (image == null) {
                    image = "https://postmediawindsorstar2.files.wordpress.com/2017/08/big-duck.jpg?quality=80&strip=all&w=840&h=630&crop=1"
                }

                Playlist playlist = new Playlist(name: key.getAt('name'),
                        spotifyPlaylistId: key.getAt('id'),
                        totalTracks: key.getAt('tracks').getAt('total') as Integer,
                        tracksLink: tracksLink,
                        tracks: getPlaylistTracks(authKey, tracksLink, 0),
                        imageUrl: image).save()

                playlistList.add(playlist)
            }
            return playlistList
        } else {
            Console.println('https://api.spotify.com/v1/me/playlists IN PlaylistRESTService FAILED')
        }
        return null
    }

    Track[] getPlaylistTracks(String authKey, playlistTracksUrl, initialOffset) {
        RestBuilder rest = new RestBuilder()
        def response = rest.get(playlistTracksUrl + '?offset=' + initialOffset) {
            header('Authorization', 'Bearer ' + authKey)
        }

        List<Track> tracks = new ArrayList<>()

        trackRESTService.buildTrackListFromPagingObject(response, tracks)

        if (tracks.size() > 99) {
            def secondResponse = rest.get(playlistTracksUrl + '?offset=' + initialOffset + 100) {
                header('Authorization', 'Bearer ' + authKey)
            }
            trackRESTService.buildTrackListFromPagingObject(secondResponse, tracks)
        }

        if (tracks.size() > 199) {
            def thirdResponse = rest.get(playlistTracksUrl + '?offset=' + initialOffset + 200) {
                header('Authorization', 'Bearer ' + authKey)
            }
            trackRESTService.buildTrackListFromPagingObject(thirdResponse, tracks)
        }
        tracks.removeAll(Collections.singleton(null))
        return tracks
    }
}