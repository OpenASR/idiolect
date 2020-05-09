# idear
[![][jetbrains-team-svg]][jetbrains-team-page]
[![][travis-status-svg]][travis-build-status]
[![][teamcity-status-svg]][teamcity-build-status]
[![][plugin-repo-svg]][plugin-repo-page]
[![][plugin-download-svg]][plugin-repo-page]

A general purpose [voice user interface](https://en.wikipedia.org/wiki/Voice_user_interface) for the IntelliJ Platform, inspired by [Tavis Rudd](https://www.youtube.com/watch?v=8SkdfdXWYaI). Possible use cases: visually impaired and [RSI](https://en.wikipedia.org/wiki/Repetitive_strain_injury) users. Originally developed as part of a [JetBrains hackathon](https://blog.jetbrains.com/blog/2015/08/31/jetbrains-3rd-annual-hackathon-new-generation-debugger-grabs-1st-place/), it is now a community-supported project. For background information, check out [this presentation](https://speakerdeck.com/breandan/programming-java-by-voice).

## Features

Idear supports a [simple grammar](src/main/resources/org.openasr.idear/grammars/command.gramsrc/main/resources/org.openasr.idear/grammars/command.gram). For a complete list of commands, please refer to [the wiki](https://github.com/OpenASR/idear/wiki/Feature-Roadmap).

# Building

For Linux or Mac OS users:

`git clone https://github.com/OpenASR/idear && cd idear && ./gradlew runIde`

For Windows users:

`git clone https://github.com/OpenASR/idear & cd idear & gradlew.bat runIde`

Recognition works with most popular microphones (preferably 16kHz, 16-bit). For best results, minimize background noise.

# Contributing

Run the following command from the project root directory:

`./gradlew runIde -PluginDev`

For more information about the plugin architecture, please refer to [the wiki page](https://github.com/OpenASR/idear/wiki/Architecture).

# Programming By Voice

- [Interactive IDE Voice Control](https://www.youtube.com/watch?v=eARvFI7hm40)
- [Using Python to Code by Voice](https://www.youtube.com/watch?v=8SkdfdXWYaI)
- [How a Blind Developer uses Visual Studio](https://www.youtube.com/watch?v=iWXebEeGwn0)

# Maintainers

* [Breandan Considine](https://github.com/breandan/)
* [Nicolas Albion](https://github.com/nalbion)

<!-- Badges -->
[jetbrains-team-page]: https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub
[jetbrains-team-svg]: http://jb.gg/badges/team.svg
[travis-build-status]: https://travis-ci.com/OpenASR/idear
[travis-status-svg]: https://travis-ci.com/OpenASR/idear.svg?branch=master
[teamcity-build-status]: https://teamcity.jetbrains.com/viewType.html?buildTypeId=idear_buildplugin&guest=1
[teamcity-status-svg]: https://teamcity.jetbrains.com/app/rest/builds/buildType:idear_buildplugin/statusIcon.svg
[plugin-repo-page]: https://plugins.jetbrains.com/plugin/7910-idear
[plugin-repo-svg]: https://img.shields.io/jetbrains/plugin/v/7910-idear.svg
[plugin-download-svg]: https://img.shields.io/jetbrains/plugin/d/7910-idear.svg