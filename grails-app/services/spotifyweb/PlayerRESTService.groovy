package spotifyweb

import grails.gorm.transactions.Transactional
import grails.plugins.rest.client.RestBuilder
import org.springframework.http.HttpStatus

@Transactional
class PlayerRESTService {
    RestBuilder rest

    PlayerRESTService() {
        rest = new RestBuilder()
    }

    boolean requestSpotifyChangeDevice(deviceId, authKey) {
        try {
            def response = rest.put('https://api.spotify.com/v1/me/player') {
                header('Authorization', 'Bearer ' + authKey)
                contentType('application/json')
                body("{\"device_ids\":[\"" + deviceId + "\"]}")
            }
            if (response.statusCode == HttpStatus.NO_CONTENT) {
                Console.println('https://api.spotify.com/v1/me/player in PlayerRESTService SUCCESS')
                return true
            } else {
                Console.println('https://api.spotify.com/v1/me/player in PlayerRESTService FAILED')
            }
        } catch (Exception e) {
            Console.println('EXCEPTION requestSpotifyChangeDevice in PlayerRESTService. \n' + e.message)
        }
        return false
    }

    boolean requestSpotifyChangeShuffleStatus(state, authKey) {
        try {
            def response = rest.put('https://api.spotify.com/v1/me/player/shuffle?state=' + state) {
                header('Authorization', 'Bearer ' + authKey)
            }
            if (response.statusCode == HttpStatus.NO_CONTENT) {
                Console.println('https://api.spotify.com/v1/me/player/shuffle?state=' + state + 'in PlayerRESTService-requestSpotifyChangeShuffleStatus SUCCESS')
                return true
            } else {
                Console.println('https://api.spotify.com/v1/me/player/shuffle?state=' + state + 'in PlayerRESTService-requestSpotifyChangeShuffleStatus FAILED')
            }
        } catch (Exception e) {
            Console.println('EXCEPTION requestSpotifyChangeShuffleStatus in PlayerRESTService. \n' + e.message)
        }

        return false
    }

    def checkPlayStatus(authKey) {
        def response = rest.get('https://api.spotify.com/v1/me/player') {
            header('Authorization', 'Bearer ' + authKey)
        }
        if (response.statusCode == HttpStatus.OK) {
            Console.println('https://api.spotify.com/v1/me/player in PlayerRESTService-checkPlayStatus SUCCESS')
            def responseJson = response.getJson()
            def resultsMap = new HashMap()
            resultsMap.put('playing', responseJson.getAt('is_playing'))
            resultsMap.put('shuffle', responseJson.getAt('shuffle_state'))
            return resultsMap
        } else {
            Console.println('https://api.spotify.com/v1/me/player in PlayerRESTService-checkPlayStatus FAILED')
        }
        return null
    }
}