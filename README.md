# Project 2-2 Group 9
Maastricht University

Structure proposal
Things to consider:
The virtual assistant should allow for multiple tasks to run at the same time.
The user should be able to query even if another task is running at the moment.
The program should be modular, where each module is responsible for a specific thing.

Observe the following diagram:

![Diagram](http://alessandrocorvi.me/ita/project_2_2_diagram.PNG)

GUI: We all know what this module is. A simple design must contain at least a textbox for queries, and a textbox for outputs. (an error of mine, this should rather be UI module because in the future it will take care of other non graphical demands regarding human-computer interface - detecting a person in front of the computer, for instance)

Query Stack: This module is simply a thread safe stack for the queries.

Task Selector: This module pulls a query from the Query Stack and then goes through the entire list of tasks. For each task, the Task Selector provides the query parsed in tokens. Then, each task returns a weight between 0 and 1 (inclusive). This value intends to represent how sure each task is that the query corresponds to them. The Task Selector will then select the task with the maximum weight. In case of a tie, the task selection must print the list of tasks in tie and tell the user to rephrase (this can be done through prompts or through the GUI module, we should discuss). Once the task is selected, it starts processing in a different thread and the task object is pushed to the next module.

Tasks Running: This is a set of tasks running. Akin to a thread pool.
Task: (Implicitly stated) This is an object that runs a subroutine in a separate thread. When the subroutine is finished, the Task pushes the output to the Output Stack.

Output Stack: Same as the Query Stack. Outputs get pulled by the GUI module periodically.

Notes:
Since each task is akin to a small program, nothing stops them from opening their own GUI or running other programs.
This way tasks are self contained and the assistant is simply a task manager.
The reason we don't parse the query entirely (recognizing sentence structure and such) at the beginning (it is implied that this is done by each task individually) is because each task will require different parse and search techniques. There is no single NLP algorithm to fit all needs.
Adding new tasks would not require modifications in the assistant.

Other suggestions:
To detect if a person is in front of the camera, we can use Viola-Jones object detection framework. It is very easy to train and is also very fast. It was proposed in 2001 and now most phone selfie cameras use it.
