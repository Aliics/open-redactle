import './style.css'
import 'scalajs:main.js'

window.Settings = {
    wsServerUrl: import.meta.env.PROD ? 'wss://server.open-redactle.com' : 'ws://localhost:8080/',
}
