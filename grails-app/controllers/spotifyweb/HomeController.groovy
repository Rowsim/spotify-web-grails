package spotifyweb

import grails.config.Config

class HomeController {
    Config config = grailsApplication.config
    def index() {
        /* If we have an auth key -- TODO: Change to check if we have valid auth key */
        if (session.getAttribute('authKey') != null) {
            session.setAttribute('isAuth', true)
        } else {
            session.setAttribute('isAuth', false)
        }
        respond([isAuth : session.getAttribute('isAuth'),
                 authKey: session.getAttribute('authKey') ?: 'nokey',
                 user   : session.getAttribute('user') ?: 'nouser'])
    }

    def authenticateSpotify() {
        def refreshToken = session.getProperty('refreshToken')
        if (refreshToken != null) {
            redirect controller: "callback", action: 'fetchAccessToken', params: [refreshToken]
        } else {
            String authUrl = 'https://accounts.spotify.com/authorize/?client_id=' +
                    config.getProperty('spotify_client_id') +
                    '&response_type=code' +
                    '&redirect_uri=' + config.getProperty('redirect_uri') +
                    '&scope=user-read-private%20user-read-birthdate%20user-read-email%20' +
                    'playlist-modify-private%20playlist-read-private%20playlist-read-collaborative%20playlist-modify-public%20' +
                    'user-follow-read%20' +
                    'app-remote-control%20streaming%20' +
                    'user-read-currently-playing%20user-modify-playback-state%20user-read-playback-state%20' +
                    'user-library-modify%20user-library-read%20' +
                    'user-read-recently-played%20user-top-read' +
                    '&state=' + config.getProperty('spotify_auth_state')
            redirect(url: authUrl)
        }
    }
}
