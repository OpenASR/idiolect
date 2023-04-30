package org.openasr.idiolect.recognizer

import org.junit.Test

class CustomMicrophoneTest {
    /** Enable this test if you need to test your microphone or record files for recognition testing */
    @Test
    fun testRecordFromMic() {
        if (false) {
            val mic = CustomMicrophone()

            mic.open()
            println("Recording...")

            /*
"Hey IDE, open up my project called 'my-app'."
"Navigate to the 'src' folder."
"Create a new file named 'utils.ts'."
"Add a function that takes in two numbers and returns their sum."
"Call the function 'addNumbers'."
"Create a React component. Create a new file named 'LoginForm.tsx'."
"Add the code to create a functional component with the name 'LoginForm' that returns a div with some text inside."
"Go to 'App.tsx' and add 'import LoginForm from './LoginForm'.'"
"Add the 'LoginForm' inside the 'App' component's render method."
"write a bash command to run our tests. Open up a new terminal window. Change to the project directory."
"Type in 'npm run test' to execute all the tests."
"Go to 'utils.ts' and add a comment above the 'addNumbers' function explaining what it does."
"create a new branch called feature/ID-123. Type 'git checkout -b my-feature' in the terminal window."
"Add the 'utils.ts' file to the staged set with the command 'git add utils.ts'."
"Commit the changes with the message 'Add utils.ts file' using the command 'git commit -m 'Add utils.ts file''."
"push the changes to the master repository with the command 'git push origin my-feature'."
"Okay, let's switch back to the main branch. Type 'git checkout main'."
"Pull in the latest changes with 'git pull'."
"Merge the 'my-feature' branch with the command 'git merge my-feature'."
"Push the changes to the remote repository with 'git push'."
function multiplyNumbers(a: number, b: number): number {
return a * b;
}

Create a new class called Person with properties name and age
Add a constructor to the Person class:

constructor(name: string, age: number) {
this.name = name;
this.age = age;
}

Explain what the multiplyNumbers() function does
Create a for loop that iterates over an array of numbers
Add a comment above the for loop that says "Iterating over numbers array"
Add a test case for the addNumbers() function
Create a new Git branch called feature/new-feature
Add MyComponent.js and utils.ts to the Git staging area
Commit the changes with the message "Added new feature"
Push the changes to the remote repository with the command "git push origin feature/new-feature"
Navigate to the file index.html
Create a new React component called MyButton
Add the following code to the MyButton component:
<button onClick={handleClick}>Click me</button>
Create a new TypeScript interface called PersonInterface with properties name and age
Navigate to the terminal
Execute the command "ls"
Navigate to the directory src/utils
Execute the command "npm install"
Create a new Kotlin class called Rectangle with properties width and height
Add the following function to the Rectangle class:

fun getArea(): Int {
return width * height
}

Create a new file called test-utils.ts
Add a new test case for the multiplyNumbers() function
Create a new bash script called deploy.sh
Add the following code to the deploy.sh script:
#!/bin/bash
echo "Deploying application..."
Execute the command "npm start"
Add a new comment to the MyButton component that says "This is a button component"
Create a new folder called test
Navigate to the folder test
Create a new file called Person.test.ts
Add a new unit test for the Person class
             */

            val file = mic.recordFromMic("src/test/resources/extended-headphones.wav", 5 * 60000)

//            val file = mic.recordFromMic("src/test/resources/laptop-noise.wav",5000)

//            // Create a new class called authorisation service
//            val file = mic.recordFromMic("src/test/resources/create-class.wav",5000)

            // Switch to the readme
//        val file = mic.recordFromMic("src/test/resources/switch-readme.wav",5000)
//
//        // go back to the previous file
//        val file = mic.recordFromMic("src/test/resources/go-back-previous.wav",5000)
//
//        // commit all files with a comment updated documentation
//        val file = mic.recordFromMic("src/test/resources/commit-all.wav",5000)
//
//        // create a test for this method
//        val file = mic.recordFromMic("src/test/resources/create-test.wav",5000)

//        // I'm wearing my headphones around my neck
//        val file = mic.recordFromMic("src/test/resources/headphones-neck.wav",5000)

//        // I'm wearing my headphones on my ears
//        val file = mic.recordFromMic("src/test/resources/headphones-ears.wav",5000)

//            // I'm talking to my laptop microphone array and its fan is noisy
//            val file = mic.recordFromMic("src/test/resources/talking-to-noisy-laptop.wav", 5000)

            mic.close()
            println("Recorded to " + file.absoluteFile)
        }
    }
}
