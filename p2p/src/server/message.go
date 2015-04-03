package server

import "fmt"

type MsgType int

const (
	MsgLogin MsgType = iota //
	MsgQuery                //
	MsgReply
	MsgExit //
)

// Message represents a message used by the LSP protocol.
type Message struct {
	Type    MsgType // One of the message types listed above.
	ipaddr  string  // Unique client-server connection ID.
	port    int     // Message sequence number.
	Payload []byte  // Data message payload.
}

// NewConnect returns a new connect message.
func NewConnect() *Message {
	return &Message{Type: MsgConnect}
}

// NewData returns a new data message with the specified connection ID,
// sequence number, and payload.
func NewReply(ip string, pt int, payload []byte) *Message {
	return &Message{
		Type:    MsgReply,
		ipaddr:  ip,
		port:    pt,
		Payload: payload,
	}
}

// String returns a string representation of this message. To pretty-print a
// message, you can pass it to a format string like so:
//     msg := NewConnect()
//     fmt.Printf("Connect message: %s\n", msg)
func (m *Message) String() string {
	var name, payload string
	switch m.Type {
	case MsgLogin:
		name = "Login"
	case MsgQuery:
		name = "query"
		payload = " " + string(m.Payload)
	case MsgReply:
		name = "reply"
		payload = " " + string(m.Payload)
	case MsgExit:
		name = "Exit"
	}
	return fmt.Sprintf("[%s %s %d%s]", name, m.ipaddr, m.port, payload)
}
