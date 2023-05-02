# ![idiolect icon](src%2Fmain%2Fresources%2FMETA-INF%2FpluginIcon.svg) idiolect 

[![Deploy](https://github.com/OpenASR/idiolect/workflows/Deploy/badge.svg)](https://github.com/OpenASR/idiolect/actions?query=workflow%3ABuild)
[![][plugin-repo-svg]][plugin-repo-page]
[![][plugin-download-svg]][plugin-repo-page]

A general purpose [voice user interface](https://en.wikipedia.org/wiki/Voice_user_interface) for the IntelliJ Platform, inspired by [Tavis Rudd](https://www.youtube.com/watch?v=8SkdfdXWYaI). 
Possible use cases: visually impaired and [RSI](https://en.wikipedia.org/wiki/Repetitive_strain_injury) users. Originally developed as part of a [JetBrains hackathon](https://blog.jetbrains.com/blog/2015/08/31/jetbrains-3rd-annual-hackathon-new-generation-debugger-grabs-1st-place/), it is now a community-supported project. For background information, check out [this presentation](https://speakerdeck.com/breandan/programming-java-by-voice).

## See Also

- [idiolect-azure](https://github.com/OpenASR/idiolect-azure)

## Usage

To get started, press the <img src="src/main/resources/org/openasr/idiolect/icons/start.svg" height="24" alt="Voice control"/> button in the toolbar, then speak a command, e.g. "Hi, IDEA!" Idiolect supports a simple [grammar](src/main/resources/org/openasr/idiolect/grammars/command.gram). For a complete list of commands, please refer to [the wiki](https://github.com/OpenASR/idiolect/wiki/Feature-Roadmap#features). Click the button once more to deactivate.

### Voice Commands
For a full list of all actions that can be be activated by voice ask Idiolect:

> What can I say?

or

> What can I say about "activate"?
> What can I say about "create"?
> What can I say about "generate"?
> What can I say about "new"?
> What can I say about "template"?


There are _a lot_, and some of them are not easy to say or remember, but you can [customise the phrases](#CustomPhraseRecognizer).

Some of the more useful commands are:

#### Navigation
- activate project tool window (commit, database, debug, find, gradle, run, terminal...)
- back/forward, down/up, left/right, page up/down, scroll up/down
- next/previous word
- method down/up
- find, find in project
- go to ... (class, bookmark, declaration, line, implementation...)
- close all editors
- next method
- find usages
- call hierarchy

#### Editing
- create (editor config file, grpc request action, liquibase changelog, vue single file comp)
- new class/dockerfile/element/file...
- code cleanup
- code completion
- collapse block
- extract class/function/method/etc
- generate getter/setter/test method...
- new class (or "create new class "something")
- rename (or rename to "something")
- cut / copy / paste / delete 
- cut line end/backward
- delete to (line/word) (start/end)
- toggle column mode
- reformat code
- fix it
- whoops

#### Debugging
- debug
- context debug
- context run
- coverage

#### Git Commands
- git add/pull/merge/push/stash...
- checkin files
- annotate


## Building

For Linux or macOS users:

`git clone https://github.com/OpenASR/idiolect && cd idiolect && ./gradlew runIde`

For Windows users:

`git clone https://github.com/OpenASR/idiolect & cd idiolect & gradlew.bat runIde`

Recognition works with most popular microphones (preferably 16kHz, 16-bit). For best results, minimize background noise.

## Contributing

Contributors who have IntelliJ IDEA installed can simply open the project. Otherwise, run the following command from the project's root directory:

`./gradlew runIde -PluginDev`

## Architecture

Idiolect is implemented using the [IntelliJ Platform SDK](https://www.jetbrains.org/intellij/sdk/docs/intro/welcome.html). For more information about the plugin architecture, please refer to [the wiki page](https://github.com/OpenASR/idiolect/wiki/Architecture).

### Integration with Idiolect

[plugin.xml](src/main/resources/META-INF/plugin.xml) defines a number of `<extensionPoint>`s which would allow other plugins to integrate with or extend/customise the capabilities of Idiolect.

An example of this is provided in [idiolect-azure](https://github.com/OpenASR/idiolect-azure) which implements `AsrProvider` 
and adds its own settings under **Tools/Idiolect**.

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

The Idiolect implementations use either exact-match or regular expressions on the recognized text.
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

#### org.openasr.idiolect.nlp.NlpResultListener
Any interfaces which are registered to the topic in plugin.xml under `<applicationListeners>` will be notified when

- listening state changes
- recognition is returned by the `AsrProvider`
- request is fulfilled by an `IntentHandler`
- there is a failure
- a prompt/message is provided for the user  


### Plugin Actions

[plugin.xml](src/main/resources/META-INF/plugin.xml) defines `<action>`s:

#### [`VoiceRecordControllerAction`](src/main/java/org/openasr/idiolect/VoiceRecordControllerAction.kt)
  This action is invoked when the user clicks on the <img src="src/main/resources/org/openasr/idiolect/icons/start.svg" height="24" alt="Voice control"/> button in the toolbar.
  This simply tells [`AsrService`](src/main/java/org/openasr/idiolect/asr/AsrService.kt) to activate or standby.
  When the `AsrService` is active, the [`AsrSystem`](src/main/java/org/openasr/idiolect/asr/AsrSystem.kt), 

  by default [`ASRControlLoop`][ASRControlLoop] [(see below)](#ASRControlLoop).

#### [`ExecuteActionFromPredefinedText`](src/main/java/org/openasr/idiolect/actions/ExecuteActionFromPredefinedText.kt)
  A debugging aid to use one of the [**`ActionRecognizer`**](src/main/java/org/openasr/idiolect/actions/recognition/ActionRecognizer.kt) 
  extension classes configured in `plugin.xml` to generate an [`ActionCallInfo`](src/main/java/org/openasr/idiolect/actions/recognition/ActionCallInfo.kt)
  which is then [`runInEditor()`](src/main/java/org/openasr/idiolect/actions/ExecuteActionByCommandText.kt#L25).
  
#### [`ExecuteVoiceCommandAction`](src/main/java/org/openasr/idiolect/actions/ExecuteVoiceCommandAction.kt)
  Similar to `ExecuteActionFromPredefinedText` but uses the `Idiolect.VoiceCommand.Text` data attached to the invoking `AnActionEvent`.

   
#### IDEA Actions

There are many Actions (classes which extend `AnAction`) provided by IDEA:   
   
  - [ExternalSystemActions](https://upsource.jetbrains.com/idea-ce/file/idea-ce-1d111593d9e5208b6783f381b507e34866587ec8/platform/platform-resources/src/idea/ExternalSystemActions.xml)
  - [LangActions](https://upsource.jetbrains.com/idea-ce/file/idea-ce-1d111593d9e5208b6783f381b507e34866587ec8/platform/platform-resources/src/idea/LangActions.xml)
  - [PlatformActions](https://upsource.jetbrains.com/idea-ce/file/idea-ce-1d111593d9e5208b6783f381b507e34866587ec8/platform/platform-resources/src/idea/PlatformActions.xml)
  - [VcsActions](https://upsource.jetbrains.com/idea-ce/file/idea-ce-1d111593d9e5208b6783f381b507e34866587ec8/platform/platform-resources/src/idea/VcsActions.xml)

### ASRControlLoop

When [`AsrControlLoop`][AsrControlLoop] detects an utterance, it invokes 
[`PatternBasedNlpProvider.processUtterance()`](src/main/java/org/openasr/idiolect/nlp/PatternBasedNlpProvider.kt#L43)
which typically calls `invokeAction()` and/or one or more of the methods of [`IdeService`](src/main/java/org/openasr/idiolect/ide/IdeService.kt)


## Programming By Voice

- [Interactive IDE Voice Control](https://www.youtube.com/watch?v=eARvFI7hm40)
- [Using Python to Code by Voice](https://www.youtube.com/watch?v=8SkdfdXWYaI)
- [How a Blind Developer uses Visual Studio](https://www.youtube.com/watch?v=iWXebEeGwn0)

## Maintainers

* [Breandan Considine](https://github.com/breandan/)
* [Nicholas Albion](https://github.com/nalbion)


[travis-build-status]: https://travis-ci.com/OpenASR/idiolect
[travis-status-svg]: https://travis-ci.com/OpenASR/idiolect.svg?branch=master
[plugin-repo-page]: https://plugins.jetbrains.com/plugin/20776-idiolect
[plugin-repo-svg]: https://img.shields.io/jetbrains/plugin/v/20776-idiolect.svg
[plugin-download-svg]: https://img.shields.io/jetbrains/plugin/d/20776-idiolect.svg


[AsrControlLoop]: src/main/java/org/openasr/idiolect/asr/AsrControlLoop.kt

