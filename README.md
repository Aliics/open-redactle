# open-redactle

Open Source Redactle written entirely in Scala. The frontend utilizes Laminar for reactivity and basic state management.

# Development

The webapp has hot-reloading setup, but you need to run two commands to accomplish this, because there is a small
dependency on Vite.

```shell
sbt ~fastLinkJS &
npm run dev
```

I use IntelliJ, so I just run the server through my IDE. You could do it in sbt very easily though, and the command
would probably look something like this:

```shell
sbt server/run
```
