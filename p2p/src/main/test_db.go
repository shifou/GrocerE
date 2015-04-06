package main

import (
	"database/sql"
	"fmt"
	_ "github.com/mattn/go-sqlite3"
	"log"
	"os"
)

type User struct {
	Mid    string
	Ipaddr string
	Port   int
	Stores string
}

type Store struct {
	Name string
	List string
}

var logger *log.Logger

func checkErr(sql string, err error) {
	if err != nil {
		logger.Panic(err)
	} else {
		logger.Println("exec: ", sql)
	}
}
func main() {
	fileName := "test_db.log"
	logFile, err := os.Create(fileName)
	defer logFile.Close()
	if err != nil {
		logger.Fatalln("open file error !")
	}
	logger = log.New(logFile, "[INFO]", log.Lshortfile)
	logger.SetFlags(logger.Flags() | log.LstdFlags)
	logger.Println("==========start===========")
	db, err := sql.Open("sqlite3", "./data.db")
	defer db.Close()
	sql := `create table users (Mid text primary key, Ipaddr text, Port Integer, Stores text);`
	_, err = db.Exec(sql)
	if err != nil {
		sql := `drop table users;create table users (Mid text primary key, Ipaddr text, Port Integer, Stores text);`
		_, err = db.Exec(sql)
		checkErr(sql, err)
	}
	sql = `insert into users(Mid,Ipaddr,Port,Stores) values(1,'127.0.0.1',80,"Giant Eagle,Best Buy");`
	_, err = db.Exec(sql)
	checkErr(sql, err)
	sql = `insert into users(Mid,Ipaddr,Port,Stores) values(22,'127.0.0.44',80,"Giant Eagle,Best Buy");`
	_, err = db.Exec(sql)
	checkErr(sql, err)
	sql = `insert into users(Mid,Ipaddr,Port,Stores) values(2555,'127.0.0.55',80,"ABC,DEF");`
	_, err = db.Exec(sql)
	checkErr(sql, err)
	sql = `select * from users;`
	rows, err := db.Query(sql)
	checkErr(sql, err)
	defer rows.Close()
	var users []User = make([]User, 0)
	for rows.Next() {
		var u User
		rows.Scan(&u.Mid, &u.Ipaddr, &u.Port, &u.Stores)
		users = append(users, u)
	}
	fmt.Println(users)
}
