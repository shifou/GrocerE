// Implementation of a serverNode. Students should write their code in this file.

package server

import (
	"bufio"
	"database/sql"
	_ "github.com/mattn/go-sqlite3"
	"fmt"
	"net"
	"strconv"
)

type serverNode struct {
	quit chan int
	ls   net.Listener
	ct   int
	flag bool
}

// New creates and returns (but does not start) a new serverNode.
func New() Server {
	mes := new(serverNode)
	mes.quit = make(chan int)
	mes.ls = nil
	flag = false
	return mes
}

func handle(mes *serverNode, con net.Conn) {
	var wr = bufio.NewWriter(con)
	var re = bufio.NewReader(con)
	var rebuf = [2000]byte
	num, err:= re.Read(rebuf[0:])
	if err != nil {
		return fmt.Errorf("read buf error: ", err, "\n")
	}
}
func (mes *serverNode) Start(port int) error {
	ln, err := net.Listen("tcp", ":"+strconv.Itoa(port))
	if err != nil {
		return fmt.Errorf("Error on listen: ", err, "\n")
	}
	mes.ls = ln
	flag = true
	fmt.Println("begin")
	go func() {
		for {
			//fmt.Println("Waiting for a connection via Accept:",connNumber)
			select {
			case <-mes.quit:
				Close()
				fmt.Errorf("Close the Server\n")
				return
				// wait for the new client
			case conn, err := ln.Accept():
				if err == nil {
					go handle(mes, conn)
				}
			}
		}
		fmt.Println("exit waiting client")
	}()
	fmt.Println("end")
	return nil
}

func (mes *serverNode) Close() {

}
