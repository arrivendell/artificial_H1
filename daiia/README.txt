
Welcome !

Here you will find the guide to make things work/testable.

First, it is important to launch the project with some arguments to the compiler :
-gui profiler:agent.profiler.ProfilerAgent;tourguide1:agent.tourGuide.TourGuideAgent;tourguide2:agent.tourGuide.TourGuideAgent;curator:agent.curator.CuratorAgent

This will create 4 agents ( 1 profiler, 2 tour guide, 1 curator).

Agents are ready to communicate, but to simulate a profiler entering a network of agents already set,
there is 5 secs of delay before the first action that will involve you.
So, wait 5sec, then the program should write to you some tracking data ( elementary one ), with the name of the agent 
creating them at the beginning like this "<curator> : Sending message to tourguide"
If you just want to check at the user important things, then you will have to look at the lines with some "*"
The important lines to read contain " **** " after the name of the agent.
You should then read some lines telling you that 2 agents have been found to answer the service the profiler is looking for.
The instructions ask you to enter a number ( an int positive ) corresponding to the agent you want to communicate with.

Example : 
> <profiler>: **** Service 0 found : ( agent-identifier :name tourguide2@213.103.193.67:1099/JADE
> <profiler>: **** Service 1 found : ( agent-identifier :name tourguide1@213.103.193.67:1099/JADE 
-----------------------------------------------------------------------
Enter the agent you want to contact using the number given in the list

Once you are here, enter 0 or 1, the number you want that corresponds to the service number.
A confirmation message appears, and a tour is requested.

Some tracking data is written to the console, until few seconds were the tour begins.
Every 6 seconds, you will go in front of a new artwork, and the curator will then send you the detail of it :

Example :
<profiler>: **** ArtWork name is : Guernica
<profiler>: **** The creator is : Picasso
<profiler>: **** Description : a beautiful paint 

Until the tour is over !
Then, you can choose again an agent to ask for a tour.

You can create a tourguide agent while your doing your first tour, THE IMPORTANT THING is that its name must contain "tourguide"
Two things will then appear : a notification that an agent as register a service we subscribed to, and this agent will now appear the next time you will have too choose in the list of agents.

These steps show that the whole thing is working !

