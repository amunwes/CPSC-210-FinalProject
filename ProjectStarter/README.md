# CPSC 210 Term Project: A Streaming Service Watchlist to Shake-Up the Viewing Experience.

## The Proposal
Have you ever found yourself watching Netflix, getting really into a series, only to end up binging the entire series in a single night and having nothing left to watch? Have you ever watched a series with a really episodic structure that was never intended to be binged, that you ended up binging because you couldn't think of something else to watch once an episode ended or better yet, couldn't be bothered to switch over to another show? Have you ever missed the experience of watching cable TV? Having the surprise of not knowing what programs would be shown next? Well I have, many times.   

And so, the project I am proposing for my term project is a more sophisticated streaming service watchlist tracker which would keep track of the status of multiple series'/movies in the user's watchlist, track what has been watched, and offer up alternate watch next suggestions by randomly selecting a series and episode from the user's watchlist breaking up the monotony and giving the user the option to break up their binge watching with some variety.

## User Stories
- As a User, I want to add shows and movies to my watchlist
- As a User, I want to remove shows and movies from my watchlist
- As a User, I want to set whether a show on my watchlist is viewed in order, or episodes are presented at random.
- As a User, I want to be recommended episodes of shows from my watchlist
- As a User, I want the app to recommend a random show from my watchlist to watch next in addition to the next episode of the show i'm currently watching.
- As a User, I want to be able to shuffle the random option in case I dont like either option.
- As a User, I want the app to remember what episodes of shows i've watched and how much of a movie i've seen.
- As a User, I want to be able to see what is on my watchlist.
- As a User, I would like the option to save my watchlist to a file.
- As a User, I would like to be able to load my watchlist from a file.

## The Functional Requirements 
For the implementation of this project to accurately reflect what I had envisioned, certain functional requirements must be met by the term project's completion. For the sake of keeping the project's scope within reason for the subject matter of this class i'm seperating the functional requirements into the following requirements which must be completed as part of this project and goals that would need fulfilling outside of the class' scope. 

### Requirements:
- A User must be able to add and remove media titles to their watchlist, ie. shows, movies.
- The application must track what media the user has watched automatically, particular episodes and movies.
- The User should be able to set whether they'd like to watch a show in order or have episodes queue up at random for each series on the watchlist. 
- The application would need to recognize when an episode has finished and offer the user a random option from their watchlist to watch next in addition to having the option to watch the next episode of the current series they're on.
- The user should be able to shuffle the random show selection if they don't like what was drawn.
- The user should be able reset the watch status of media on their watchlist, in order to re-watch their favorite shows.

### Out of Scope Goals:
- The application should be able to access a catalogue of media content that the user can choose from. Complete with meta data.
- The application would need to interface with media streams to actually play the selected media and track whether a show has been watched.

## Phase 4 Task 2:

Printing logs:
Mon Nov 25 20:05:00 PST 2024
Show movie 2 watch status updated to: true
Mon Nov 25 20:05:08 PST 2024
Show movie 1 added to watchlist
Mon Nov 25 20:05:12 PST 2024
Show movie 4 added to watchlist
Mon Nov 25 20:05:16 PST 2024
Show Series 4 added to watchlist
Mon Nov 25 20:05:20 PST 2024
Show Series 8 added to watchlist
Mon Nov 25 20:05:23 PST 2024
Series Series 4 last watched updated to: 1
Mon Nov 25 20:05:23 PST 2024
Show Series 4 Episode 1 watch status updated to: true
Mon Nov 25 20:05:25 PST 2024
Show Series 4 removed from watchlist
Mon Nov 25 20:05:26 PST 2024
Show movie 4 watch status updated to: true
Mon Nov 25 20:05:29 PST 2024
Show movie 4 removed from watchlist

## Phase 4 Task 3:

I think my design started fairly clean but as I started to work on the bulk of the features I realized a need to add more and more things unrelated to the project milestones so I compromised to keep things within scope and presentable.
If I had more time to work on the project I would probably start by refactoring the code and altering my implementation of the catalogue feature. The primary focus of the project was the implementation of the user's watchlist, but midway through I decided that I wanted a way to display options of what could be added to the watchlist. 

I implemented a datastructure in my ui with lists that was functionally very similar to the watchlist class but with less functionality, In my ui i kept 2 lists of show and series objects to represent choices the user could add to their list. It would have been a better idea to create an abstract class representing a catalogue of movies and tv and then extending that class to build the watchlist and catalogues. Making that above change i could have implemented writeable in the abstract class and had both the watchlist and catalogue states save and load keeping the object references between them synced. 



