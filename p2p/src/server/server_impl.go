// Implementation of a serverNode. Students should write their code in this file.

package server

import (
	"bufio"
	"database/sql"
	"encoding/json"
	"fmt"
	_ "github.com/mattn/go-sqlite3"
	"log"
	"net"
	"os"
	"strconv"
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

var dbU *sql.DB
var dbS *sql.DB
var logger *log.Logger

func checkConError(err error) int {
	if err != nil {
		if err.Error() == "EOF" {
			fmt.Println("client exit")
			return 0
		}
		log.Fatal("an error!", err.Error())
		return -1
	}
	return 1
}
func checkDbErr(sql string, err error) {
	if err != nil {
		logger.Panic(err)
	} else {
		logger.Println("exec: ", sql)
	}
}

type serverNode struct {
	quit chan int
	ls   net.Listener
	ct   int
	flag bool
}

// New creates and returns (but does not start) a new serverNode.
func New() Server {
	fileName := "db.log"
	logFile, err := os.Create(fileName)
	defer logFile.Close()
	if err != nil {
		logger.Fatalln("open file error !")
	}
	logger = log.New(logFile, "[INFO]", log.Lshortfile)
	logger.SetFlags(logger.Flags() | log.LstdFlags)
	logger.Println("==========start===========")

	var dberr error
	dbU, dberr = sql.Open("sqlite3", "./user_data.db")
	if dberr != nil || dbU == nil {
		fmt.Println(dberr)
		return nil
	}
	errr := dbU.Ping()
	if errr != nil {
		log.Fatalf("Error on opening user database connection: %s", err.Error())
	}
	defer dbU.Close()

	dbS, dberr = sql.Open("sqlite3", "./store_data.db")
	if dberr != nil || dbS == nil {
		fmt.Println(dberr)
		return nil
	}
	errr = dbS.Ping()
	if errr != nil {
		log.Fatalf("Error on opening user database connection: %s", err.Error())
	}
	defer dbS.Close()

	mes := new(serverNode)
	mes.quit = make(chan int)
	mes.ls = nil
	mes.flag = false
	return mes
}

func handle(mes *serverNode, con net.Conn) {
	//var wr = bufio.NewWriter(con)
	var re = bufio.NewReader(con)
	var rebuf [2000]byte
	for {
		num, err := re.Read(rebuf[0:])
		flag := checkConError(err)
		if flag == 0 {
			break
		}
		msg := &Message{}
		err = json.Unmarshal(rebuf[0:num], msg)
		if err == nil {
			fmt.Println("###Client ###: :received" + msg.String())
			switch msg.Type {
			case MsgLogin:

			case MsgQuery:
			case MsgReply:
			case MsgExit:
			default:
				fmt.Println("unknow query type name")
			}
		} else {
			fmt.Print(string(rebuf[:]))
		}
	}
	//db, err := sql.Open("sqlite3", "./list.db")
	//sql = `insert into users(Mid,Ipaddr,Port,Stores) values(1,'127.0.0.1',80,"Giant Eagle,Best Buy");`
	//_, err = db.Exec(sql)
}
func (mes *serverNode) Start(port int) error {
	ln, err := net.Listen("tcp", ":"+strconv.Itoa(port))
	if err != nil {
		return fmt.Errorf("Error on listen: ", err, "\n")
	}
	mes.ls = ln
	mes.flag = true
	fmt.Println("begin")
	go func() {
		for {
			//fmt.Println("Waiting for a connection via Accept:",connNumber)
			/*select {
			case <-mes.quit:
				Close()
				fmt.Errorf("Close the Server\n")
				return
				// wait for the new client
			case conn, err := ln.Accept():
				if err == nil {
					go handle(mes, conn)
				}
			*/
			conn, err := ln.Accept()
			if err == nil {
				fmt.Println("received from: " + conn.RemoteAddr().String())
				go handle(mes, conn)
			}
		}
		fmt.Println("exit waiting client")
	}()
	fmt.Println("end")
	return nil
}

func (mes *serverNode) Close() {

}
