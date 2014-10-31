# Agricola: All Creatures Big and Small for PC

Welcome to computer implementation of "Agricola: All Creatures Big and Small" by Uwe Rosenberg (published by Lookout Games, 2012).
This readme file will not explain the rules, just the most important facts about this PC version. 
To learn more about the original board game visit Board Game Geek (http://www.boardgamegeek.com/boardgame/119890/agricola-all-creatures-big-and-small).

## General 

The game uses Java6 (will also work if you have greater). Start it by double-click on AnimalAgricola.jar file.
There is no AI or networking present. Two players play on single computer.

## Controls

 - Use mouse to select actions.
 - Left click on farm to build or unbuild fences/troughs/buildings.
 - Farm spots available for interaction are marked with player-colored overlay.
 - Some actions need to be confirmed using green 'tick' button.
 
### Manipulating animals

 - Acquired animals are waiting below your farm for you to place them.
 - Possible animal locations are marked with player-colored circles.
 - Any unplaced animals will run away at the end of your turn.
 
 - Left-click on pasture/building to add one animal of particular type.
 - Right-click on pasture/building to remove one animal.
 - Use left/right double-click to add/remove all available animals (works accros the whole pasture).
 - Alternatively, use mouse wheel to add/remove animals (this is the most convenient way - try it!).
 
### Extra actions

 - When a special building gives you an extra action:
 
 	- it may be invoked immediately (e.g. "Fence Manufacturer")
 	- you may be prompted later, when the action can be performed (e.g. "Home Workshop")
 	- a button may appear besides your farm to activate the action whenever you want (e.g. "Assembly Hall")

### Undo/Redo

 - There is no limit - you can undo any number of actions/turns.
 - Undo/redo does not remember exact placements of your animals.
 
 	- Game will maintain the correct number of animals, but their positions may change.
 	- You will need to reposition your animals manually after redoing an animal-related action.
 	
## Displays

 - Actions tab shows action board.
 - Buildings tab shows bigger building tiles.
 - Animals tab shows bonus points scoring tables.
 - Score tab shows current scores of both players.
 
 - Building panel (in the bottom) can be toggled using toolbar.
 - Board layout can be switched from two farm to single farm using toolbar.
 
 - Buildings on your farm may be decorated with small "House" symbol in top right corner,
   this notifies you that the building can grant you an extra action, ability or victory points later,
   so you won't forget it.
 
## Version history

1.2.1
 - tabbed layout, screen capture
1.2
 - added "Even More Buildings Big and Small" expansion
 - possible to save scores
1.1
 - added "More Buildings Big and Small" expansion
1.0
 - first release, available in English, German, Czech and Slovak

by Jakub Ukrop, October 2014