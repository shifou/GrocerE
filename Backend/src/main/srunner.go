package main

import (
	"fmt"
	"server"
)

const defaultPort = 9999

func main() {
	// Initialize the server.
	ser := server.New()
	if ser == nil {
		fmt.Println("New() returned a nil server. Exiting...")
		return
	}

	// Start the server and continue listening for client connections in the background.
	if err := server.Start(defaultPort); err != nil {
		fmt.Printf("Server could not be started: %s\n", err)
		return
	}

	fmt.Printf("Started on port %d...\n", defaultPort)

	// Block forever.
	select {}
}
