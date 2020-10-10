package spotifyweb

import grails.converters.JSON

class PlayerController {

    PlayerRESTService playerRESTService
    String authKey

    def changeDevice(String deviceId) {
        authKey = session.getAttribute('authKey')
        def model = [success: playerRESTService.requestSpotifyChangeDevice(deviceId, authKey)]
        render model as JSON
    }

    def changeSpotifyShuffleStatus(Boolean state) {
        authKey = session.getAttribute('authKey')
        def model = [success: playerRESTService.requestSpotifyChangeShuffleStatus(state, authKey)]
        render model as JSON
    }

    def checkPlayStatus() {
        authKey = session.getAttribute('authKey')
        def resultMap = playerRESTService.checkPlayStatus(authKey)
        if (resultMap != null) {
            def model = [playing: resultMap.get('playing'), shuffle: resultMap.get('shuffle')]
            render model as JSON
        }
    }
}
