# Mobile Automation Framework - Appium with Cucumber (Java)

This repository contains an automated testing framework for mobile applications using Appium, Cucumber BDD, and Java. The framework supports testing Android applications and is designed to be easily extensible for additional tests.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Setting up the Environment](#setting-up-the-environment)
- [Project Structure](#project-structure)
- [Creating New Tests](#creating-new-tests)
- [Updating Existing Tests](#updating-existing-tests)
- [Running the Tests](#running-the-tests)
- [Reports and Screenshots](#reports-and-screenshots)

---

## Prerequisites

Before you begin, ensure that the following tools are installed:

1. **Java 17+**

   - [Download Java](https://adoptopenjdk.net/)
   - Set up `JAVA_HOME` environment variable.
   - Ensure that Java is available in your terminal/command prompt by running `java -version`.

2. **Maven**

   - [Download Maven](https://maven.apache.org/download.cgi)
   - Set up `MAVEN_HOME` environment variable.
   - Ensure that Maven is available in your terminal/command prompt by running `mvn -v`.

3. **Android SDK**

   - [Install Android Studio](https://developer.android.com/studio) (Android SDK comes with it).
   - Set up `ANDROID_HOME` environment variable to point to the SDK directory.

4. **Appium**

   - [Install Appium](http://appium.io/docs/en/about-appium/intro/)
   - Install Node.js and Appium globally using `npm install -g appium`.
   - Ensure Appium is running using `appium` in your terminal/command prompt.

5. **IntelliJ IDEA or Eclipse**

   - [Download IntelliJ IDEA](https://www.jetbrains.com/idea/download/) or Eclipse.
   - Use these IDEs for running and debugging tests.

---

## Setting up the Environment

1. Clone the repository to your local machine:

```bash
git clone <repository_url>
cd <repository_folder>

```

In order to run the tests, you need to set up the environment variables as follows:

```bash
export ANDROID_HOME=/path/to/android/sdk
export JAVA_HOME=/path/to/java/jdk
export MAVEN_HOME=/path/to/maven
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$JAVA_HOME/bin:$MAVEN_HOME/bin
```

2. Install the required dependencies using Maven:

```bash
mvn install
```

3. Run the tests using Maven:

```bash
mvn test
```

4. Running single scenario:

```bash
mvn test -Dcucumber.filter.tags="@guest"
```

5. Check the reports and screenshots in the `target` directory.

Start Appium server:

```bash
appium server --port 4723 --allow-insecure=adb_shell
```

## Install Allure Report:

1. Install Allure Report using Maven:

```bash
brew install allure
```

2. Generate Allure Report
3. Open the Allure Report in your browser:

TO run Locally bundled build

4. Run the tests using Maven:
   Launch the App

Run the tests using Maven:

```bash
mvn clean test
```

```bash
allure serve target/allure-results
```

```bash
allure generate allure-results --clean -o allure-report
```

```bash
allure open allure-report
```

```bash
npx expo run android 
```

```bash
#Make the Run script executable
chmod +x run-tests.sh
#Run the script
./run-tests.sh
```