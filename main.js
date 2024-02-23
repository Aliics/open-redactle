import './style.css'
import 'scalajs:main.js'

window.Config = {
    wsServerUrl: import.meta.env.PROD ? 'wss://server.open-redactle.com' : 'ws://localhost:8080/',
}
