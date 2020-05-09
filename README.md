# idear
[![][jetbrains-team-svg]][jetbrains-team-page]
[![][travis-status-svg]][travis-build-status]
[![][teamcity-status-svg]][teamcity-build-status]
[![][plugin-repo-svg]][plugin-repo-page]
[![][plugin-download-svg]][plugin-repo-page]

A general purpose [voice user interface](https://en.wikipedia.org/wiki/Voice_user_interface) for the IntelliJ Platform, inspired by [Tavis Rudd](https://www.youtube.com/watch?v=8SkdfdXWYaI). Possible use cases: visually impaired and [RSI](https://en.wikipedia.org/wiki/Repetitive_strain_injury) users. Originally developed as part of a [JetBrains hackathon](https://blog.jetbrains.com/blog/2015/08/31/jetbrains-3rd-annual-hackathon-new-generation-debugger-grabs-1st-place/), it is now a community-supported project. For background information, check out [this presentation](https://speakerdeck.com/breandan/programming-java-by-voice).

## Usage

To get started, press the ![Voice control](src/main/resources/org.openasr.idear/icons/start.png) button in the toolbar, then speak a command, e.g. "Hi, IDEA!" Idear supports a simple [grammar](src/main/resources/org.openasr.idear/grammars/command.gram). For a complete list of commands, please refer to [the wiki](https://github.com/OpenASR/idear/wiki/Feature-Roadmap#features). Click the button once more to deactivate.

## Building

For Linux or Mac OS users:

`git clone https://github.com/OpenASR/idear && cd idear && ./gradlew runIde`

For Windows users:

`git clone https://github.com/OpenASR/idear & cd idear & gradlew.bat runIde`

Recognition works with most popular microphones (preferably 16kHz, 16-bit). For best results, minimize background noise.

## Contributing

Contributors who have IntelliJ IDEA installed can simply open the project. Otherwise, run the following command from the project's root directory:

`./gradlew runIde -PluginDev`

## Architecture

Idear is implemented using the [IntelliJ Platform SDK](https://www.jetbrains.org/intellij/sdk/docs/intro/welcome.html). For more information about the plugin architecture, please refer to [the wiki page](https://github.com/OpenASR/idear/wiki/Architecture).

### Plugin Actions

[plugin.xml](src/main/resources/META-INF/plugin.xml) defines `<action>`s:

#### [`VoiceRecordControllerAction`](src/main/java/org/openasr/idear/VoiceRecordControllerAction.kt)
  This action is invoked when the user clicks on the ![Voice control](src/main/resources/org.openasr.idear/icons/start.png) button in the toolbar.
  This simply tells [`ASRService`](src/main/java/org/openasr/idear/asr/ASRService.kt) to activate or standby.
  When the `ASRService` is active, the [`ASRSystem`](src/main/java/org/openasr/idear/asr/ASRSystem.kt), 
  by default [`ASRControlLoop`][ASRControlLoop] [(see below)](#ASRControlLoop).

#### [`ExecuteActionFromPredefinedText`](src/main/java/org/openasr/idear/actions/ExecuteActionFromPredefinedText.kt)
  A debugging aid to use one of the [**`ActionRecognizer`**](src/main/java/org/openasr/idear/actions/recognition/ActionRecognizer.kt) 
  extension classes configured in `plugin.xml` to generate an [`ActionCallInfo`](src/main/java/org/openasr/idear/actions/recognition/ActionCallInfo.kt)
  which is then [`runInEditor()`](src/main/java/org/openasr/idear/actions/ExecuteActionByCommandText.kt#L25).
  
#### [`ExecuteVoiceCommandAction`](src/main/java/org/openasr/idear/actions/ExecuteVoiceCommandAction.kt)
  Similar to `ExecuteActionFromPredefinedText` but uses the `Idear.VoiceCommand.Text` data attached to the invoking `AnActionEvent`.

#### [`WhereAmIAction`](src/main/java/org/openasr/idear/actions/WhereAmIAction.kt)
   
#### IDEA Actions

There are many Actions (classes which extend `AnAction`) provided by IDEA:   
   
  - [ExternalSystemActions](https://upsource.jetbrains.com/idea-ce/file/idea-ce-1d111593d9e5208b6783f381b507e34866587ec8/platform/platform-resources/src/idea/ExternalSystemActions.xml)
  - [LangActions](https://upsource.jetbrains.com/idea-ce/file/idea-ce-1d111593d9e5208b6783f381b507e34866587ec8/platform/platform-resources/src/idea/LangActions.xml)
  - [PlatformActions](https://upsource.jetbrains.com/idea-ce/file/idea-ce-1d111593d9e5208b6783f381b507e34866587ec8/platform/platform-resources/src/idea/PlatformActions.xml)
  - [VcsActions](https://upsource.jetbrains.com/idea-ce/file/idea-ce-1d111593d9e5208b6783f381b507e34866587ec8/platform/platform-resources/src/idea/VcsActions.xml)

### ASRControlLoop

When [`ASRControlLoop`][ASRControlLoop] detects an utterance, it invokes 
[`PatternBasedNlpProvider.processUtterance()`](src/main/java/org/openasr/idear/nlp/PatternBasedNlpProvider.kt#L43)
which typically calls `invokeAction()` and/or one or more of the methods of [`IDEService`](src/main/java/org/openasr/idear/ide/IDEService.kt)

## Programming By Voice

- [Interactive IDE Voice Control](https://www.youtube.com/watch?v=eARvFI7hm40)
- [Using Python to Code by Voice](https://www.youtube.com/watch?v=8SkdfdXWYaI)
- [How a Blind Developer uses Visual Studio](https://www.youtube.com/watch?v=iWXebEeGwn0)

## Maintainers

* [Breandan Considine](https://github.com/breandan/)
* [Nicholas Albion](https://github.com/nalbion)

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


[ASRControlLoop]: src/main/java/org/openasr/idear/asr/ASRControlLoop.kt