package main

import (
	"database/sql"
	"fmt"
	_ "github.com/mattn/go-sqlite3"
	"log"
	"os"
)

type User struct {
	mid    string
	ipaddr string
	port   int
	stores []string
}
type Users struct {
	UserId int
	Uname  string
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
	sql := `create table users (userId integer, uname text);`
	_, err = db.Exec(sql)
	if err != nil {
		sql := `drop table users;create table users (userId integer, uname text);`
		_, err = db.Exec(sql)
		checkErr(sql, err)
	}
	sql = `insert into users(userId,uname) values(1,'Mike');`
	_, err = db.Exec(sql)
	checkErr(sql, err)
	sql = `insert into users(userId,uname) values(2,'Mike');`
	_, err = db.Exec(sql)
	checkErr(sql, err)
	sql = `insert into users(userId,uname) values(3,'MMMMMMMike');`
	_, err = db.Exec(sql)
	checkErr(sql, err)
	sql = `select * from users;`
	rows, err := db.Query(sql)
	checkErr(sql, err)
	defer rows.Close()
	var users []Users = make([]Users, 0)
	for rows.Next() {
		var u Users
		rows.Scan(&u.UserId, &u.Uname)
		users = append(users, u)
	}
	fmt.Println(users)
}
