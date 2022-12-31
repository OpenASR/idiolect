# ![idear icon](src%2Fmain%2Fresources%2FMETA-INF%2FpluginIcon.svg) idear 

[![][jetbrains-team-svg]][jetbrains-team-page]
[![Deploy](https://github.com/OpenASR/idear/workflows/Deploy/badge.svg)](https://github.com/OpenASR/idear/actions?query=workflow%3ABuild)
[![][teamcity-status-svg]][teamcity-build-status]
[![][plugin-repo-svg]][plugin-repo-page]
[![][plugin-download-svg]][plugin-repo-page]
<iframe width="245px" height="48px" src="https://plugins.jetbrains.com/embeddable/install/7910"></iframe>

A general purpose [voice user interface](https://en.wikipedia.org/wiki/Voice_user_interface) for the IntelliJ Platform, inspired by [Tavis Rudd](https://www.youtube.com/watch?v=8SkdfdXWYaI). 
Possible use cases: visually impaired and [RSI](https://en.wikipedia.org/wiki/Repetitive_strain_injury) users. Originally developed as part of a [JetBrains hackathon](https://blog.jetbrains.com/blog/2015/08/31/jetbrains-3rd-annual-hackathon-new-generation-debugger-grabs-1st-place/), it is now a community-supported project. For background information, check out [this presentation](https://speakerdeck.com/breandan/programming-java-by-voice).

## Usage

To get started, press the <img src="src/main/resources/org/openasr/idear/icons/start.svg" height="24" alt="Voice control"/> button in the toolbar, then speak a command, e.g. "Hi, IDEA!" Idear supports a simple [grammar](src/main/resources/org/openasr/idear/grammars/command.gram). For a complete list of commands, please refer to [the wiki](https://github.com/OpenASR/idear/wiki/Feature-Roadmap#features). Click the button once more to deactivate.

## Building

For Linux or macOS users:

`git clone https://github.com/OpenASR/idear && cd idear && ./gradlew runIde`

For Windows users:

`git clone https://github.com/OpenASR/idear & cd idear & gradlew.bat runIde`

Recognition works with most popular microphones (preferably 16kHz, 16-bit). For best results, minimize background noise.

## Contributing

Contributors who have IntelliJ IDEA installed can simply open the project. Otherwise, run the following command from the project's root directory:

`./gradlew runIde -PluginDev`

## Architecture

Idear is implemented using the [IntelliJ Platform SDK](https://www.jetbrains.org/intellij/sdk/docs/intro/welcome.html). For more information about the plugin architecture, please refer to [the wiki page](https://github.com/OpenASR/idear/wiki/Architecture).

### Integration with Idear

[plugin.xml](src/main/resources/META-INF/plugin.xml) defines a number of `<extensionPoint>`s which would allow other plugins to integrate with or extend/customise the capabilities of Idear.

#### AsrProvider
Listens for audio input, recognises speech to text and returns an `NlpRequest` with possible utterances.
Does _not_ resolve the intent.

Possible alternative implementations could:

- integrate with Windows SAPI 5 Speech API
- integrate with Dragon/Nuance API

#### NlpProvider
Processes an `NlpRequest`.
The default implementation invokes `IdeService.invokeAction(ExecuteVoiceCommandAction, nlpRequest)`
and the action is handled by `ExecuteVoiceCommandAction` and `ActionRecognizerManager.handleNlpRequest()`

#### AsrSystem
Processes audio input, recognises speech to text and executes actions.
The default implementation `AsrControlLoop` uses the `AsrProvider` and `NlpProvider`.

Some APIs such as AWS Lex implement the functionality of `AsrProvider` and `NlpProvider` in a single call.

#### IntentResolver
Processes an `NlpRequest` (utterance/alternatives) and resolves an `NlpResponse` with `intentName` and `slots`.
`ActionRecognizerManager.handleNlpRequest()` iterates through the `IntentResolver`s until it finds a match.

The Idear implementations use either exact-match or regular expressions on the recognized text.
Alternative implementations may use AI to resolve the intent.

##### CustomPhraseRecognizer
Many of the auto-generated trigger phrases are not suitable for voice activation. You can add your own easier to 
say and remember phrases in `~/.idea/phrases.properties`
 
#### IntentHandler
Fulfills an `NlpResponse` (intent + slots), performing desired actions.
`ActionRecognizerManager.handleNlpRequest()` iterates through the `IntentHandler`s until the intent is actioned.

##### TemplateIntentHandler
Handles two flavours of intent prefix: 

- `Template.id.${template.id}` eg: `Template.id.maven-dependency`
- `Template.${template.groupName}.${template.key}` eg: `Template.Maven.dep`

`template.id` is often null. 
`template.key` is the "Abbreviation" that you would normally type before pressing `TAB`.

The default trigger phrases are generated from the template description or key and are often not suitable for voice activation.
You can add your own trigger phrase -> live template mapping in `~/.idea/phrases.properties` and it will be resolved by `CustomPhraseRecognizer`.

#### ttsProvider
Reads audio prompts/feedback to the user

#### org.openasr.idear.nlp.NlpResultListener
Any interfaces which are registered to the topic in plugin.xml under `<applicationListeners>` will be notified when

- listening state changes
- recognition is returned by the `AsrProvider`
- request is fulfilled by an `IntentHandler`
- there is a failure
- a prompt/message is provided for the user  


### Plugin Actions

[plugin.xml](src/main/resources/META-INF/plugin.xml) defines `<action>`s:

#### [`VoiceRecordControllerAction`](src/main/java/org/openasr/idear/actions/VoiceRecordControllerAction.kt)
  This action is invoked when the user clicks on the <img src="src/main/resources/org/openasr/idear/icons/start.svg" height="24" alt="Voice control"/> button in the toolbar.
  This simply tells [`AsrService`](src/main/java/org/openasr/idear/asr/AsrService.kt) to activate or standby.
  When the `AsrService` is active, the [`ASRSystem`](src/main/java/org/openasr/idear/asr/ASRSystem.kt), 
  by default [`ASRControlLoop`][ASRControlLoop] [(see below)](#ASRControlLoop).

#### [`ExecuteActionFromPredefinedText`](src/main/java/org/openasr/idear/actions/ExecuteActionFromPredefinedText.kt)
  A debugging aid to use one of the [**`ActionRecognizer`**](src/main/java/org/openasr/idear/actions/recognition/ActionRecognizer.kt) 
  extension classes configured in `plugin.xml` to generate an [`ActionCallInfo`](src/main/java/org/openasr/idear/actions/recognition/ActionCallInfo.kt)
  which is then [`runInEditor()`](src/main/java/org/openasr/idear/actions/ExecuteActionByCommandText.kt#L25).
  
#### [`ExecuteVoiceCommandAction`](src/main/java/org/openasr/idear/actions/ExecuteVoiceCommandAction.kt)
  Similar to `ExecuteActionFromPredefinedText` but uses the `Idear.VoiceCommand.Text` data attached to the invoking `AnActionEvent`.

   
#### IDEA Actions

There are many Actions (classes which extend `AnAction`) provided by IDEA:   
   
  - [ExternalSystemActions](https://upsource.jetbrains.com/idea-ce/file/idea-ce-1d111593d9e5208b6783f381b507e34866587ec8/platform/platform-resources/src/idea/ExternalSystemActions.xml)
  - [LangActions](https://upsource.jetbrains.com/idea-ce/file/idea-ce-1d111593d9e5208b6783f381b507e34866587ec8/platform/platform-resources/src/idea/LangActions.xml)
  - [PlatformActions](https://upsource.jetbrains.com/idea-ce/file/idea-ce-1d111593d9e5208b6783f381b507e34866587ec8/platform/platform-resources/src/idea/PlatformActions.xml)
  - [VcsActions](https://upsource.jetbrains.com/idea-ce/file/idea-ce-1d111593d9e5208b6783f381b507e34866587ec8/platform/platform-resources/src/idea/VcsActions.xml)

### ASRControlLoop

When [`AsrControlLoop`][AsrControlLoop] detects an utterance, it invokes 
[`PatternBasedNlpProvider.processUtterance()`](src/main/java/org/openasr/idear/nlp/PatternBasedNlpProvider.kt#L43)
which typically calls `invokeAction()` and/or one or more of the methods of [`IdeService`](src/main/java/org/openasr/idear/ide/IdeService.kt)

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


[AsrControlLoop]: src/main/java/org/openasr/idear/asr/AsrControlLoop.kt
