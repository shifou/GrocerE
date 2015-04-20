package main

import (
	//"database/sql"
	"fmt"
	_ "github.com/mattn/go-sqlite3"
	"server"
)

const defaultPort = 9999

func main() {
	// Initialize the server.
	/*
		db, err := sql.Open("sqlite3", "./user_data.db")
		if err == nil {
			sqlStmt := `create table users (Mid text primary key, Ipaddr text, Port Integer, Stores text);`
			_, err = db.Exec(sqlStmt)
			if err != nil {
				fmt.Printf("%q: %s\n", err, sqlStmt)
				return
			}
		}
		defer db.Close()

		db2, err := sql.Open("sqlite3", "./store_data.db")
		if err == nil {
			sqlStmt := `create table stores (Name text primary key, List text);`
			_, err = db2.Exec(sqlStmt)
			if err != nil {
				fmt.Printf("%q: %s\n", err, sqlStmt)
				return
			}
		}
		defer db2.Close()
	*/
	ser := server.New()
	if ser == nil {
		fmt.Println("New() returned a nil server. Exiting...")
		return
	}

	// Start the server and continue listening for client connections in the background.
	if err := ser.Start(defaultPort); err != nil {
		fmt.Printf("Server could not be started: %s\n", err)
		return
	}

	fmt.Printf("Started on port %d...\n", defaultPort)

	// Block forever.
	select {}
}
