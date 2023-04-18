# JavaFx Client App

How to run the project?

Here are the steps to download and set up JavaFX in IntelliJ IDEA (it is the easiest and most pleasure way):

1) Download JavaFX SDK: Go to the JavaFX website (https://openjfx.io/) and download the JavaFX SDK for your platform. Make sure to download the version that matches the version of Java that you are using in your project.
2) Extract JavaFX SDK: Extract the contents of the downloaded JavaFX SDK file to a directory on your computer.
3) Open our JavaFX project with IntelliJ IDEA (in this case).
4) Configure project settings: In the project settings, go to the "Modules" section and add the JavaFX SDK to the project's dependencies. To do this, click on the "Dependencies" tab and then click on the "+" icon to add a new dependency. Select "JARs or directories" and then navigate to the "lib" directory in the extracted JavaFX SDK directory. Select all the JAR files in this directory and click "OK".
5) Set VM options: In order to run a JavaFX application, you need to set the VM options for the run configuration. Go to the run configuration for your project and add the VM options.

Helpful link: https://openjfx.io/openjfx-docs/

There is a Dockerfile uploaded, but due to configuration issues related to JavaFX, it is partially not working. Just Java...

Results:
