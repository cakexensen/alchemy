# alchemy

alchemy will tentatively be a fast-paced, team-based couch game for 1 to 8 players. however, i first need to flesh out some of the functional-oriented architecture it will be using.

## architecture

the game architecture is divided into a few large pieces. the two biggest are 'game' and 'gui', which respectively handle the core game logic and the rendering. additionally i might add components for networking, physics, and other separable processes.

all of these main components run on their own threads concurrently. each will loop until the program ends, and perform setup/cleanup around the loop. the loop structure typically looks like this:

* retrieve new messages from the message buffer, and process them
* process any local or shared data (often the 'game state')
* wait until the next time this process should execute
* recur the loop with any 'modified' data, or quit

all processes share access to a 'messaging system', which is initialized at the program start. at any time, a process can send a message to another by using its mailbox and the recipient's address. although i try to use as much pure functional code as possible, this system requires mutation of shared data. the simple locking behavior of an atom is sufficient; we don't care if a process has to wait an extra frame to receive a message so long as messages aren't lost.

game and gui also share access to the game state (usually just 'state'). this should contain most of the data needed throughout the game, such as information about the current screen, the scene and all entities, save data, and more. only game can modify the shared state atom, gui is only permitted to read it each frame. because game and gui run concurrently, it's possible that game might process more quickly than gui, and gui will skip frames worth of state data. i think frame loss is preferrable to keep the gui constantly rendering the most recent data.

when gui needs to communicate back to game (typically when inputs are received), we can't allow game to skip any of that data. therefore gui sends inputs to game through the messaging system, forcing game to process them all at the beginning of its loop. any data that game requires gui to process should also be sent as a message so that gui can't skip over it.

gui also has its own local state for tracking rendering data, such as textures and keeping track of the data stored on the graphics card. other processes can keep their own local data, such as the networking process maintaining ip address information.

the processing components will typically have different behaviors at different times, and this is handled with 'directors'. a director is a function that performs the 'body' of the processing loop. this is really just an alternative to a chain of "if condition do this else do that" in the loop. this makes it easier to change behavior between (for example) playing the game and watching a cutscene, as control of entities can flip from player-controlled to ai-controlled instantly just by changing the active director.

## functional attributes

because of the emphasis on pure functional code, much of the game state must be recreated every time the game loop runs. to reduce the overhead costs of constantly adjusting positions and other attributes, entities will typically store their data as functions of time. for example if the player begins pressing forward, the player entity could change its position function to move forward. each frame, the game will input the current time to calculate the current entity position, without having to modify or recreate any state data. when the player changes input, or when physics detects that the entity must change its positional vector, a new function is created for the position calculation. in addition to reducing memory consumption, this also should make the game logic cleaner because it can just ask entities to calculate their positions.

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
