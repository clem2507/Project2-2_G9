# Maastricht University (Multimodal Digital Assistant)
## Project 2-2 Group 9

### Quick Introduction

##### Language & Terminology

* Domain: A 'Domain' is a module responsible for handling a narrow set of queries.  (a calendar domain is responsible for handling queries related to  scheduling tasks). Every domain is also responsible for running skills.
* Skill: A 'Skill' represents an action the assistant can perform (reading the  schedule from MyUM Calendar or reading my notes, etc.). Skills are  essentially simple actions that, together, conform a set of actions that belong to a domain.
* Fallback Skill: A 'Fallback Skill' is a user-defined skill composed of a pattern and a corresponding response. When a query does not match with any domain, then it will try matching with a fallback skill.

##### Why 'multimodal'?

Our design allows multiple skills to run at the same time and each one of them can output messages to the user. In other words, the assistant can be requested to search for a file in the device while trying to perform a google search. This is a stark contrast with single-threaded versions which would require the user to wait longer.

Skills can also produce output messages (text, images, links, etc.) without interrupting back-and-forth interaction.

##### Diagram of  user-assistant interaction

![Interaction Model](DKE Multimodal Assistant Execution Diagram.png)

Seeking to elucidate; this loop-like interaction is broken either by closing the window or triggering the 'Leave' domain.

### Documentation & Tutorials

[Pattern Language](./Readmestuff/Project 2-2 Group 9 (Pattern Language Doc).pdf)

[Custom Responses](./Readmestuff/Fallback Custom Skills_Skill Editor.pdf)

[How to build a domain?](./Readmestuff/Project 2-2 Group 9 - Building A Domain.pdf)

### Dependencies:

[OpenCV](https://opencv-java-tutorials.readthedocs.io/en/latest/01-installing-opencv-for-java.html)

[Windows lnk parser](https://stackoverflow.com/questions/309495/windows-shortcut-lnk-parser-in-java)

[String similarity metric](https://gist.github.com/thotro/af2dcbcf6bd7ecd9f5fc)

### Using Calendar Domain:
