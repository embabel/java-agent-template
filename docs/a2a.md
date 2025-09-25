# A2A Support

Embabel integrates with the [A2A](https://github.com/google-a2a/A2A) protocol, allowing you to connect to other
A2A-enabled agents and
services.

> Embabel agents can be exposed to A2A with zero developer effort.

Check out the `a2a` branch of this repository to try A2A support.

You'll need the following environment variable:

- `GOOGLE_STUDIO_API_KEY`: Your Google Studio API key, which is used for Gemini.

Start the Google A2A web interface using Docker compose:

```bash
docker compose up
```

Go to the web interface running within the container at `http://localhost:12000/`.

Connect to your agent at `host.docker.internal:8080/a2a`. Note that `localhost:8080/a2a` won't work as the server
cannot access it when running in a Docker container.

Your agent will have automatically been exported to A2A. Add it in the UI, and start a chat.
You should see something like this:

<img src="images/a2a_ui.jpg" alt="A2A UI" width="600">