FullStack Multiplayer Card Game Web Application: Quests Of The Round Table

Carleton University Winter 2021 - COMP 3004 Object-Oriented Software Engineering

Contributers: Chris Boyd, Tristan Demers, Henry Nguyen, Brandon Atkins

Tech Stack: Java, Vaadin, Maven, Spring Boot, Jira

Back-End Design Patterns: Mediator Pattern, Factory Pattern, Strategy Pattern, Observer Pattern

Relevent Code can be found in: /src/main/java/org/vaadin/

How to use program:

	-Run main application called Application.java
	(This will start up the application)

	-Access localhost:8080 on your browser of choice
	(The current implementation of the application creates and initializes a 
	game with 4 players for testing purposes)

	-Navigate to localhost:8080/gameStart/[playerId] (between 0-3)

	-Click the load page button to access the game board

	-Utilize the application


If you would like to create and initialize players comment in code for:
	
	-init.joinGame() function
	-init.getPlayer().readyUp() function
	-init.startGame()

This will allow you to create new players in different instances of a browser.

	-Head to localhost:8080/ to create a player
	-This will redirect you to localhost:8080/Player/[playerId] (between 0-3)
	-The game will ask for you to ready up (by clicking the ready up button)
	-Then press join game (join game button) once all players to be redirected to localhost:8080/gameStart/[playerId]
	-Click the load page button to access the game board
