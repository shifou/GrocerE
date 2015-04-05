package main

import (
	"database/sql"
	"fmt"
	_ "github.com/mattn/go-sqlite3"
	"server"
)

const defaultPort = 9999

func main() {
	// Initialize the server.
	db, err := sql.Open("sqlite3", "./data.db")
	if err != nil {

		sqlStmt := `
		create table foo (id integer not null primary key, name text);
		delete from foo;
		`
		_, err = db.Exec(sqlStmt)
		if err != nil {
			log.Printf("%q: %s\n", err, sqlStmt)
			return
		}
	}
	defer db.Close()

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
