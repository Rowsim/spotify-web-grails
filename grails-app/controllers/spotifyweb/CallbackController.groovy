package spotifyweb

import grails.config.Config
import grails.plugins.rest.client.RestBuilder
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

class CallbackController {
    Config config = grailsApplication.config
    UserFetchService userFetchService
    UserService userService
    def index() {
        if (params.error != null) {
            redirect(view: '/error')
        } else if (params.state == config.getProperty('spotify_auth_state')) {
            fetchAccessToken(params.code)
        }
    }

    def fetchAccessToken(String code) {
        RestBuilder rest = new RestBuilder();
        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>()
        form.add("client_id", config.getProperty('spotify_client_id'))
        form.add("client_secret", config.getProperty('spotify_client_secret'))
        form.add("grant_type", "authorization_code")
        form.add("code", code)
        form.add("redirect_uri", config.getProperty('redirect_uri'))

        def response = rest.post('https://accounts.spotify.com/api/token') {
            accept("application/json")
            contentType("application/x-www-form-urlencoded")
            body(form)
        }

        def responseJson = response.getJson()
        session.setAttribute('authKey', responseJson.getAt('access_token'))
        session.setAttribute('refreshToken', responseJson.getAt('refresh_token'))
        session.setAttribute('isAuth', true)

        if (session.getAttribute('authKey') != null) {
            Console.println('Access Token acquired, expires in: ' + responseJson.getAt('expires_in') + 's')
            def currentUser = userFetchService.initUser(session.getAttribute('authKey'))
            session.setAttribute('user', currentUser)
        }

        redirect controller: "home", action: 'index'
    }
}
