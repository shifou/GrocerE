// Implementation of a serverNode. Students should write their code in this file.

package server

import (
	"bufio"
	"fmt"
	"net"
	"strconv"
)

type serverNode struct {
	flag   bool
	quit   chan int
	qu     chan string
	remove chan int
	ct     chan int
	ls     net.Listener
}

// New creates and returns (but does not start) a new serverNode.
func New() Server {
	mes := new(serverNode)
	mes.flag = false
	mes.quit = make(chan int)
	mes.qu = make(chan string)
	mes.remove = make(chan int)
	mes.ct = make(chan int, 1)
	mes.clients = make(map[int]client)
	mes.ls = nil
	mes.ct <- 1
	return mes
}

func handle(mes *multiEchoServer, idd int, con net.Conn) {

}
func (mes *serverNode) Start(port int) error {
	if mes.flag == true {
		return fmt.Errorf("the Server has already started\n")
	}
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
			conn, err := ln.Accept()
			if err == nil {
				go handle(mes, conn)

			}
		}
		fmt.Println("exit waiting client")
	}()
	fmt.Println("end")
	return nil
}

func (mes *serverNode) Close() {
	select {
	case <-mes.ct:
		for _, v := range mes.clients {
			v.conn.Close()
		}
		mes.ct <- 1
	}
	mes.ls.Close()
	mes.quit <- 0
	mes.flag = false
}
