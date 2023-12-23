# [@NotEnoughRoomBot](https://t.me/NotEnoughRoomBot)

This project is a Telegram bot that helps users easily find room occupancy. With some added commands :D. 
It is made using the [java-telegram-bot-api](https://github.com/pengrad/java-telegram-bot-api) sdk

## Commands

- /search - Search for available rooms using the user's list
- /building - Show buildings info. A message appear with all the buildings as buttons
- /room - Show room info. It gives all the infos about a specific room
- /mylist - Show the user's list of rooms.
- /gethash - Get a hash the user's list. It is used to share list with other users. Since the hash is associated with 
 the list content, if the original list is modified, the hash will change. But as long as there is a list that 
  has the same values, the /copyhash will still work
- /copyhash - Copy the list with the same hash.
- /addall - Add all the rooms to the user's list. Not recommended since there are also rooms outside of Lausanne
- /create - Create the user's list. It's always one of the first commands to use
- /delete - Delete the user's list.
- /reset - Reset the user's list
- /addroom - Add a room to the user's list. You can use it to add a lot of rooms at once (\n is used as separator)
- /addbuilding - Add a building to the user's list
- /removeroom, /deleteroom - Remove a room from the user's list
- /removebuilding, /deletebuilding - Remove a building from the user's list
- /allbuildings - Show all the buildings. This command is deprecated since it's better to use /building

## Usage

- (USE THE RELEASE TO AVOID THIS STEP THAT TAKES A LOT OF TIMES) 
  Set up the project using the `EntireProjectSetup` class to get different ressources.
- Run the main method in the `TelegramBotForOccupancy` class to start the bot.