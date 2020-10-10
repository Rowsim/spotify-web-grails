package spotifyweb

import grails.gorm.transactions.Transactional
import grails.plugins.rest.client.RestBuilder
import org.springframework.http.HttpStatus

@Transactional
class UserFetchService {

    PlaylistRESTService playlistRESTService

    User initUser(String authKey) {
        RestBuilder rest = new RestBuilder()
        def response = rest.get('https://api.spotify.com/v1/me') {
            header('Authorization', 'Bearer '+authKey)
        }

        if (response.statusCode == HttpStatus.OK) {
            User user = new User()
            def responseJson = response.getJson()
            user.spotifyId = responseJson.getAt('id')
            user.name = responseJson.getAt('display_name')
            user.email = responseJson.getAt('email')
            user.imageUrl = responseJson.getAt('images')[0].getAt('url')
            user.playlists = playlistRESTService.getCurrentUserPlaylists(authKey)

            user.save(flush: true)
            if(user.hasErrors()) {
                Console.println("failed to save user $user: $user.errors")
            }
            return user
        } else {
            Console.println('https://api.spotify.com/v1/me IN UserFetchService FAILED')
        }

        return null
    }
}
