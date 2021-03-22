# Maastricht University - Multimodal Digital Assistant
## Project 2-2 Group 9

### Quick Introduction

##### Language & Terminology

* Domain: A 'Domain' is a module responsible for handling a narrow set of queries.  (a calendar domain is responsible for handling queries related to  scheduling tasks). Every domain is also responsible for running skills.
* Skill: A 'Skill' represents an action the assistant can perform (reading the  schedule from MyUM Calendar or reading my notes, etc.). Skills are  essentially simple actions that, together, conform a set of actions that belong to a domain.
* Fallback Skill: A 'Fallback Skill' is a user-defined skill composed of a pattern and a corresponding response. When a query does not match with any domain, then it will try matching with a fallback skill.

##### Why 'multimodal'?

Our design allows multiple skills to run at the same time. In other words, the assistant can be requested to search for a file in the device while trying to perform a google search. This is a stark contrast with single-threaded versions which would require the user to wait longer.

Skills can also produce output messages (text, images, links, etc.) without interrupting back-and-forth interaction.

##### Diagram of  user-assistant interaction

<img src="Readmestuff/interaction_diagram.png" alt="Interaction Model" width="50%" height="50%">

Seeking to elucidate; this loop-like interaction is broken either by closing the window or triggering the 'Leave' domain.

### Documentation & Tutorials

[Pattern Language](./Readmestuff/pattern_lang.pdf)

[Custom Responses](Readmestuff/custom_skills_lang.pdf)

[How to build a domain?](./Readmestuff/howto_build_domain.pdf)

#### How to define custom skills?
  1) Write a text file with custom responses such as in [Custom Responses](Readmestuff/custom_skills_lang.pdf)
  2) Run the assistant and drop the text file in "drop file"

The assistant will remember previously defined custom skills, to get rid of them press "clear templates".

### Dependencies:

[OpenCV](https://opencv-java-tutorials.readthedocs.io/en/latest/01-installing-opencv-for-java.html) (OpenCV dependent skills will not run on MacOS for the time being)

### External Resources:

[Windows lnk parser](https://stackoverflow.com/questions/309495/windows-shortcut-lnk-parser-in-java)

[String similarity metric](https://gist.github.com/thotro/af2dcbcf6bd7ecd9f5fc)

### Running For The First Time:
  1) Install OpenCV (On Windows you will just need to invoke the photo skill, other OSs are not yet supported)
  2) Clone this repository
  3) Gradle > Execute Gradle Task > run

### Using Calendar Domain:

  1) For more information use the "help calendar" command or [click here](https://www.youtube.com/watch?v=Gqyd9Ot5a7s)
  2) Go to the student portal
  3) Go to the My Timetable tab
  4) Go to the calendar section and scroll to the bottom
  5) Click "Agenda connect manual" and open "General Timetables"
  6) Click the "Connect calendar" button on the top right of the page (next to the download calendar button)
  7) Click the "other" options
  8) You now can copy the link and use the command "set calendar" to set it up in the application